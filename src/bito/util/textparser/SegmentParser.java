package bito.util.textparser;

import java.util.Map;

import bito.util.dba.SQLRunner;
import bito.util.logger.Log;

public interface SegmentParser
{
	/**
	 * ���õ�ǰ��������ű�
	 * ͨ��ϵͳ������Ϣ��ȡ�ű����ƶ�Ӧ�Ľű��ļ�������ָ��Ϊname
	 * parser.script.name=script.file.path
	 * ͨ��ϵͳ������Ϣ��ȡ�ű����ƶ�Ӧ�Ľű����ݣ�����ָ��Ϊname����Ӧ������Ϣ��"{"��ʼ,"}"����
	 * parser.script.name={script.file.path}
	 * ֱ�����ýű����ݣ�����ָ���ַ�����"{"��ʼ,"}"����
	 */
	public void setParserScript(String parserScriptName) throws Exception;

	/**
	 * ���������־
	 */
	public void setLogger(Log log);

	/**
	 * ���� SQLRunner
	 */
	public void setSQLRunner(SQLRunner sqlrunner);

	/**
	 * ��ȡ��ǰ�����������ı�ʶ
	 * ��setParserScript��ָ����parserScriptName
	 */
	public String getParserName() throws Exception;

	/**
	 * ���õ�ǰ��������ű��еı���
	 */
	public void setVariable(String key, Object value) throws Exception;

	/**
	 * ���õ�ǰ����
	 */
	public void setParentSegment(SegmentParser parentSegment) throws Exception;

	/**
	 * ��ȡ���������������
	 */
	public SegmentParser getParentSegment() throws Exception;

	/**
	 * ����һ�У����ؽ������еĶ����������
	 * 
	 * ��ǰ���Ƿ��������ǰ���Ƿ����ڵ�ǰ��
	 *       No                 Yes          => ���ڽ��������ص�ǰ��
	 *       Yes                Yes          => ������ɣ����ص�ǰ��
	 *       Yes                No           => δ�����У��轻�������������䴦������null
	 *       No                 No           => ����Ƕ�ף������µĽ�������
	 */
	public SegmentParser parseLine(String line, int lineno) throws Exception;

	/**
	 * ��ǰ�����Ƿ����
	 */
	public boolean isEndOfSegment() throws Exception;

	/**
	 * ��ȡ��ǰ����������
	 */
	public Map getResult() throws Exception;

	/**
	 * ��������
	 */
	public void parseEnd() throws Exception;
}
