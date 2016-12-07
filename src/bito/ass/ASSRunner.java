package bito.ass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import bito.util.cfg.SystemConfig;
import bito.util.logger.Log;

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
public class ASSRunner
{
	ASSRunner o = (ASSRunner)b.i.t.o();

	public ASSRunner()
	{
	}

	public void setSystemInitializeScriptFilename(String initscript_filename)
	{
		o.setSystemInitializeScriptFilename(initscript_filename);
	}

	public void setCommonIncludeScriptFilename(String initscript_filename)
	{
		o.setCommonIncludeScriptFilename(initscript_filename);
	}

	public void setCommonIncludeScript(String initscript)
	{
		o.setCommonIncludeScript(initscript);
	}

	public void setParameter(String key, Object value)
	{
		o.setParameter(key, value);
	}

	public Object getResult()
	{
		return o.getResult();
	}

	public Throwable getLastError()
	{
		return o.getLastError();
	}

	/**
	 * ��������Ϣ����
	 * @return
	 */
	public int run()
	{
		return o.run();
	}

	/**
	 * ִ��ָ���ű�
	 * @return
	 * @throws Exception 
	 */
	public Object execute(String procname, String sqlfiles, Map parameters) throws Exception
	{
		return o.execute(procname, sqlfiles, parameters);
	}

	/**
	 * ���ϵͳ��Դ
	 */
	public void cleanupAll()
	{
		o.cleanupAll();
	}

	/**
	 * �����µĴ����߳�
	 * @param procname �����߳�����
	 * @param sqlfiles ��������ļ��������ϵͳ��ǰ·��
	 * @param schedule ϵͳ����ʱ�䰲�ţ�cron���ʽ������Ϊnull
	 * @param start_delay  �״������ӳ��룬0����������-1ֻ��cron���ʽ����
	 * @param min_interval �����������ִ�е���Сʱ������
	 * @param parameters ָ��������̲���
	 */
	public void start(String procname, String sqlfiles, String schedule, long start_delay, long min_interval,
		Map parameters)
	{
		o.start(procname, sqlfiles, schedule, start_delay, min_interval, parameters);
	}

	/**
	 * ���ش����������״̬ 
	 * 		"N" (none���̲߳�����) 
	 * 		"W" (waiting���߳��Ѿ���������δִ�д������) 
	 * 		"R" (running�������������ִ��) 
	 * 		"E" (error����������쳣����) 
	 * 		"C" (completed�������߳���������) 
	 * �����߳̽�����״̬����һ�죬��ʱ��״̬��Ϊ"N" (none)
	 */
	public String status(String procname)
	{
		return o.status(procname);
	}

	/**
	 * ��ǰ������̽�������ֹ�����̣߳�
	 * ����ֹͣ�����̣߳��������ж�����ִ�еĴ������
	 */
	public void stop(String procname)
	{
		o.stop(procname);
	}

	/**
	 * �������еĴ������
	 */
	public String[] runningProcs()
	{
		return o.runningProcs();
	}

	/**
	 * �������еĴ����������
	 */
	public int runningProcsCount()
	{
		return o.runningProcsCount();
	}

	// main
	public static void main(String[] args)
	{
		if (args.length > 0 && "?".equals(args[0]))
		{
			System.out.println("Usage:");
			System.out.println("  java [options] -Dstop.file=\"stop.file\" runner");
			System.out.println("  java [options] -Dsql.runner.schedule=\"* * * * * ?\" runner -start \"ass.files;\"");
			System.out.println("  java [options] runner \"act.sql.script.files;\"");
			System.out.println("Options:");
			System.out.println("  -Dconfig.file=\"config.txt\"");
			System.out.println("  -Dcommon.include.script=\"proc.init.js\"");
			System.out.println("  -Dsystem.init.ass=\"system.init.txt\"");
			System.out.println("  -DDB.driver=...");
			System.out.println("  -DDB.url=...");
			System.out.println("  -DDB.username=...");
			System.out.println("  -DDB.password=...");
			System.out.println("  -DDB.maxconnect=...");
			System.out.println("  -DDB.maxidleseconds=...");
			System.out.println("  -DDB.rows.limit=...");
			System.out.println("  -Dlog.file=logs/log.[yyyy][MM][dd].txt");
			System.out.println("  -Dlog.file.maxsizekb=10240");
			System.out.println("  -Dlog.file.maxbakidx=10");
			System.out.println("  -Dlog.level=debug");
			System.out.println("  -Dlog.stdout=info");
			System.out.println("  -Dconfig.refresh.interval.seconds=1");
			System.out.println("  -Ddata.proc.system.exit.wait.seconds=60 #wait for none proc");
			System.out.println("  -Ddata.proc.running.timeout.seconds=60 #timeout warning");
			System.out.println("  -Ddata.proc.safe.stop.wait.seconds=60 #wait for proc stop");
			System.out.println("  -Ddata.proc.error.retry.interval.seconds=10");
			System.out.println("  -D[data.proc.name.]sql.file=...");
			System.out.println("  -D[data.proc.name.]schedule.cron=...");
			System.out.println("  -D[data.proc.name.]loop.interval.min.seconds=...");
			System.out.println("  -D[data.proc.name.]start.delay.seconds=...");
			System.out.println("  -D[data.proc.name.]log.file=...");
			System.out.println("  -D[data.proc.name.]log.file.maxsizekb=...");
			System.out.println("  -D[data.proc.name.]log.file.maxbakidx=...");
			System.out.println("  -D[data.proc.name.]log.level=...");
			System.out.println("  -D[data.proc.name.]log.stdout=...");
			return;
		}
		ASSRunner ar = new ASSRunner();
		ar.go(args);
	}

	protected void go(String[] args)
	{
		Log log = new Log("");
		setCommonIncludeScriptFilename(SystemConfig.get("common.include.script", null));
		setSystemInitializeScriptFilename(SystemConfig.get("system.init.ass", null));
		if (args != null && args.length > 0)
		{
			setParameter("parameter", args);
			if (args.length > 1 && args[0].equals("-start"))
			{
				start("ass.runner", args[1], SystemConfig.get("sql.runner.schedule"), 0, 0, null);
				while(runningProcsCount() > 0)
				{
					try
					{
						Thread.sleep(100);
					}
					catch(InterruptedException e)
					{
					}
				}
				Object r = getResult();
				log.debug("return: " + r);
				Throwable e = getLastError();
				if (e != null)
				{
					log.error(e);
				}
			}
			else
			{
				try
				{
					Object r = execute("ass.runner", args[0], null);
					log.debug("return: " + r);
				}
				catch(Exception ee)
				{
					log.error(ee);
				}
			}
		}
		else
		{
			go(log);
		}
	}

	private void go(final Log log)
	{
		Thread hook = new Thread()
		{
			public void run()
			{
				log.info("break end.");
			}
		};
		//ϵͳֹͣ�ı���ļ�
		File stopfile = new File(SystemConfig.get("stop.file", "stop"));
		try
		{
			//ϵͳ���������ֹͣ����ļ�
			if (stopfile.exists())
			{
				stopfile.delete();
			}
			//ϵͳ��ʼ��
			long tnorunning = 0;
			int runningcount = 0;
			log.info("begin.");
			Runtime.getRuntime().addShutdownHook(hook);
			//ϵͳ����
			while(!stopfile.exists()
				&& (runningcount > 0
					|| tnorunning == 0
						|| System.currentTimeMillis() < tnorunning
							+ (1000L * SystemConfig.getLong("data.proc.system.exit.wait.seconds", 30))))
			{
				runningcount = run();
				if (runningcount > 0)
				{
					tnorunning = 0;
				}
				else
				{
					if (tnorunning == 0)
					{
						tnorunning = System.currentTimeMillis();
					}
				}
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e)
				{
				}
				stopfile = new File(SystemConfig.get("stop.file", "stop"));
			}
			if (runningcount == 0)
			{
				log.info("all data proc thread is end.");
			}
			//ϵͳ������ֹ
			cleanupAll();
			Runtime.getRuntime().removeShutdownHook(hook);
			log.info("end.");
		}
		catch(Throwable e)
		{
			log.error(e);
			//ϵͳ�쳣��ֹ
		}
		finally
		{
			if (stopfile.exists())
			{
				stopfile.delete();
			}
			//ϵͳ��ֹǰ����
		}
	}
}
