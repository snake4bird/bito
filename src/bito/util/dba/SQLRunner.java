package bito.util.dba;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bito.util.logger.Log;
import bito.util.textparser.Parser;

/**
 * ��̬SQL�ű�ִ����
 * 
 * 1. ֧�ֻ�����SQL���
 *   1.1 SQL����Էֺ�";"��β
 *   1.2 �ֺ�";"�����н���Ϊע��
 *   1.3 USE DB �����ݿ�������dbname�� �������л����ݿ�����
 *   1.4 ���ݿ�������Ϣ������ config.properties �ļ��У�����������ʱͨ������ -Dconfig.file=�������ļ�·���� ָ��
 *   1.5 ���ݿ�������Ϣ������
 *     1.5.1 dbname.driver= JDBC driver
 *     1.5.2 dbname.url= ���ݿ�JDBC����URL
 *     1.5.3 dbname.username=
 *     1.5.4 dbname.password=
 *     1.5.5 dbname.maxconnect=0 �����������0Ϊ������
 *     1.5.6 dbname.maxidleseconds=600 �������ʱ�䣬ȱʡΪ600�룬��������10���Ӳ��þ��Զ��Ͽ�
 *   1.6 ON ERROR CONTINUE ������SQL�����쳣ʱ������ִ����һ�䣬�쳣��Ϣ������־
 *   1.7 ON ERROR BREAK ������SQL�����쳣ʱ���ж����нű����У��׳��쳣��Ϣ��ȱʡ����ʽΪBREAK
 *   1.8 TRANSACTION BEGIN ��ʼ������
 *   1.9 TRANSACTION END ���������������Ҫ�ع���ֻ��Ҫ�ٴ�ִ�� TRANSACTION BEGIN
 *   1.10 �κ�һ�� SQL ���֮ǰ���������ӱ�ǩ
 *     1.8.1 ��ʽ label_name : SQL��� ;
 * 
 * 2. ֧����Ƕ��JavaScript
 *   2.1 �ԡ�{����ʼ����}������
 *   2.2 ��Ƕ�ű����ܷ���SQL�����ַ������ʽ�У�ֻ����Ƕ������SQL�����ַ������ʽ֮�䣬�ű��������Զ��������ǰ���ַ�������Ϊһ�������ַ���
 *     2.2.1 ���磺''{"abc"}'*'{123}'' == 'abc*123'
 *   2.3 ��Ƕ�ű����﷨������ο�JavaScript����ĵ�
 *   2.4 ��Ƕ�ű��п���ֱ��ʹ��Java�ࡢ����
 *   2.5 ��Ƕ�ű������շ���ֵ�����ǻ����������� String��Number ���������� String[]��Number[]���������ͽ���ǿ��ת��Ϊ�ַ��� String ����
 *   2.6 ��Ƕ�ű��е� runner �����ṩ����Ҫ�����У�
 *       2.6.1 ��ȡ��ǰʹ�õ����ݿ⣬curDB() : DBTool
 *       2.6.2 ָ��Ҫʹ�õ����ݿ⣬useDB(DBTool)
 *       2.6.3 ���� label ��Ӧ�����ݣ�putData(String label, Map[] datas)
 *       2.6.4 ��ȡ label ��Ӧ�����ݣ�getData(String label) : Map[]
 *       2.6.5 ִ�ж�̬ SQL �ű���execSQLText(String sqltext)
 *       2.6.6 ���ļ���loadFile(filename)
 *       2.6.7 д�ļ���saveFile(filename, content)
 *   2.7 ��Ƕ�ű��е� log ���������ͬ����־���
 *       log �����ṩ����Ҫ�����У�debug(msg), info(msg), warn(msg), error(msg)
 *   2.8 ��Ƕ�ű��е� exit(return_code) �����������˳���ǰ SQLRunner ִ�й���
 *   2.9 ��Ƕ�ű��е� goto(label) ������������ת�� label ָ�� SQL �ı���
 *   2.10 ��Ƕ�ű��е� sleep(ms) �����������ͷ�CPUʱ��Ƭ ms ����
 *   2.11 ��Ƕ�ű��е� config(key) ���������ڻ�ȡ������Ϣ
 *   2.12 ��Ƕ�ű��е� include(sqlfile) ����������ִ��asql�ļ�
 * 
 * 3. ֧��SQL����ж�̬�����滻
 *   3.1 ��SELECT���ǰ���ӱ�ǩ���ɽ���ѯ����������ڱ�����Ϊ label_name����������Ϊ Map ���飨Object Array�� ��ȫ�ֱ�����
 *     3.1.1 ��ʽ label_name : SELECT ID,NAME FROM TABLE1
 *   3.2 �ں���SQL��ʹ�øñ�ǩ�����Ľ����
 *     3.2.1 ��ʽ INSERT INTO TABLE2 (ID,NAME) VALUES ({lable_name.ID}, ''{lable_name.NAME}'')
 *       3.2.1.1 label_name.ID ��������ʽ���ؽ�����е�ID�ֶ�����
 *       3.2.1.2 �������Զ�ѭ��n�Σ�nΪ������еļ�¼����nΪ0ʱ������䲻�ᱻִ��
 *       3.2.1.3 ���������ʵ��������Ƕ�ű�����ѭ��ǶJavaScript����
 *     3.2.2 ��ʽ DELETE FROM TABLE1 WHERE ID={lable_name[0].ID}
 *       3.2.2.1 label_name[0].ID ���ؽ�����е�һ�������е�ID�ֶΣ�������ݲ����ڣ�����䲻�ᱻִ��
 *       3.2.2.1 label_name[0].ID ����ֵ����Ϊ null
 *     3.2.3 ��ʽ DELETE FROM TABLE1 WHERE ID={lable_name.ID[0]}
 *       3.2.3.1 label_name.ID[0] ���ؽ�����е�һ�������е�ID�ֶΣ�������ݲ����ڣ����ؿ��ַ���
 *       3.2.3.2 label_name.ID[0] ����ֵ����Ϊ null
 *   3.3 ��ǩ��һ����Ƕ�ű��еı�������������Ϊ Map ���飨Object Array��
 *   3.4 ���һ��SELECT��䣬��ѯ������ܻḲ�ǵ�""������ı�ǩ�У�
 *       ����Ƕ�ű��п���ͨ�� this[""] ����ʽ���ʣ��� {this[""].ID[0]}
 *   3.5 һ��SQL�п���{label_name.ID}��ʽ���ö����ǩ�Ľ�����������ѭ������Ϊ����������¼���ĳ˻�
 * 
 * 
 * 4. ֧���ı���������
 *   4.1 ����Ƕ�ű��е����ı���������
 *       { text_parser_result = parse(parser_script_name, text_file_name); };
 *     4.1.1 parser_script_nameΪ�ı���������ű�����
 *     
 *           * ϵͳ����ͨ��������Ϣ��ȡ�ű����ƶ�Ӧ�Ľű��ļ�
 *           * parser.script.parser_script_name=�ű��ļ���
 *           * ϵͳ����ͨ��ϵͳ������Ϣ��ȡ�ű����ƶ�Ӧ�Ľű����ݣ���Ӧ������Ϣ��"{"��ʼ,"}"����
 *           * parser.script.parser_script_name={ �ű����� }
 *           * parser_script_nameҲ������ֱ�ӵĽű����ݣ���"{"��ʼ,"}"����
 *
 *           �ڽű��ж������Ľ���������
 *               function parseLine(line, number) {
 *                   line   Ϊ��ǰ������
 *                   number Ϊ��ǰ�к�
 *                   ����ֵ����ǰ�п��Ա�ʶ�𲢽���ʱ������ me�������ַ���""��
 *                           ��ǰ������Ƕ�����ݣ����Ա������ű�����ʱ�����ظýű�����
 *                           ��ǰ�в���ʶ��ʱ�����ؿգ�null��
 *                   ��֪��ǰ��Ϊ���������ʱ������ endOfSegment=true��
 *               }
 *           ��
 *               function parseEnd() {
 *                   ��ǰ�����������
 *               }��
 *           �ű���Ԥ����ı������£�
 *               // currentSegment ��ǰ���������Java����
 *               //       ����ʹ�õķ��������� SegmentParser �ӿ��С�
 *               // parentSegment  �ϼ����������Java����
 *               //       ����ʹ�õķ��������� SegmentParser �ӿ��С�
 *               // me             ���� parseLine ��������ֵ��ֵΪ���ַ���""������ǰ�����ű�
 *               // result         ���������{"table_name": [{"field_name": "field_value" ... } ... ] ... };
 *               //endOfSegment    ��ǰ�������������ǣ�[true, false]��
 *     4.1.2 text_file_nameΪҪ�������ı��ļ���
 *     4.1.3 ���ؽ��ΪMap���������ṹ�����ڽ����ű���
 *           ���磺{main=[{table_name=[{field_name1=field_value1, field_name2=field_value2}]}]}
 *     
 * 
 * TODO List
 * 
 * @author  LIBOFENG
 * @version 2013.1.9		��������ʵ��
 * @version 2013.11.13		������֧��
 *
 */
public class SQLRunner implements d.SQLRunner
{
	private d.SQLRunner runner = d.E.V().newSQLRunner("");
	private Parser parser = new Parser();

	public SQLRunner()
	{
		try
		{
			parserInitialize();
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void parserInitialize() throws Exception
	{
		parser.setSQLRunner(this);
		runner.setVariable("parser", this.parser);
		runner.evalJScript("function parse(scriptname, textfilename)"
			+ " { parser.parse(scriptname, textfilename); return parser.getResultMap(); }");
	}

	public void cancel()
	{
		//parser.cancel();
		runner.cancel();
	}

	public void setDefaultDBName(String dbname)
	{
		runner.setDefaultDBName(dbname);
	}

	public void setLogger(Log log)
	{
		runner.setLogger(log);
	}

	public void useDB(DBTool dbt)
	{
		runner.useDB(dbt);
	}

	public DBTool curDB()
	{
		return runner.curDB();
	}

	public int execSQLFile(String sqlfile) throws Exception
	{
		return runner.execSQLFile(sqlfile);
	}

	public int execSQLText(String sqltext) throws Exception
	{
		return runner.execSQLText(sqltext);
	}

	public int execSQLText(String[] sqltexts) throws Exception
	{
		return runner.execSQLText(sqltexts);
	}

	public Map[] getData(String key)
	{
		return runner.getData(key);
	}

	public void putData(String key, Map[] md) throws Exception
	{
		runner.putData(key, md);
	}

	public Object getVariable(String name) throws Exception
	{
		return runner.getVariable(name);
	}

	public void setVariable(String name, Object value) throws Exception
	{
		runner.setVariable(name, value);
	}

	public Object evalJScript(String js) throws Exception
	{
		return runner.evalJScript(js);
	}
}
