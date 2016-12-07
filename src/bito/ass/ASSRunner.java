package bito.ass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import bito.util.cfg.SystemConfig;
import bito.util.logger.Log;

/**
 * 动态SQL脚本执行器
 * 
 * 1. 支持基本的SQL语句
 *   1.1 SQL语句以分号";"结尾
 *   1.2 分号";"至本行结束为注释
 *   1.3 USE DB 〈数据库配置名dbname〉 可用于切换数据库连接
 *   1.4 数据库配置信息定义在 config.properties 文件中，可以在启动时通过参数 -Dconfig.file=〈配置文件路径〉 指定
 *   1.5 数据库配置信息包括：
 *     1.5.1 dbname.driver= JDBC driver
 *     1.5.2 dbname.url= 数据库JDBC连接URL
 *     1.5.3 dbname.username=
 *     1.5.4 dbname.password=
 *     1.5.5 dbname.maxconnect=0 最大连接数，0为无限制
 *     1.5.6 dbname.maxidleseconds=600 最大闲置时间，缺省为600秒，即该连接10分钟不用就自动断开
 *   1.6 ON ERROR CONTINUE 当后续SQL发生异常时，继续执行下一句，异常信息记入日志
 *   1.7 ON ERROR BREAK 当后续SQL发生异常时，中断所有脚本运行，抛出异常信息，缺省处理方式为BREAK
 *   1.8 TRANSACTION BEGIN 开始事务处理
 *   1.9 TRANSACTION END 结束事务处理，如果需要回滚，只需要再次执行 TRANSACTION BEGIN
 *   1.10 任何一句 SQL 语句之前都可以增加标签
 *     1.8.1 形式 label_name : SQL语句 ;
 * 
 * 2. 支持内嵌的JavaScript
 *   2.1 以“{”开始，“}”结束
 *   2.2 内嵌脚本不能放在SQL语句的字符串表达式中，只能内嵌在两个SQL语句的字符串表达式之间，脚本解析后自动将结果及前后字符串连接为一个完整字符串
 *     2.2.1 例如：''{"abc"}'*'{123}'' == 'abc*123'
 *   2.3 内嵌脚本的语法规则请参考JavaScript相关文档
 *   2.4 内嵌脚本中可以直接使用Java类、对象
 *   2.5 内嵌脚本的最终返回值可以是基本数据类型 String、Number 或数组类型 String[]、Number[]，其它类型将被强制转换为字符串 String 类型
 *   2.6 内嵌脚本中的 runner 对象，提供的主要方法有：
 *       2.6.1 获取当前使用的数据库，curDB() : DBTool
 *       2.6.2 指定要使用的数据库，useDB(DBTool)
 *       2.6.3 设置 label 对应的数据，putData(String label, Map[] datas)
 *       2.6.4 获取 label 对应的数据，getData(String label) : Map[]
 *       2.6.5 执行动态 SQL 脚本，execSQLText(String sqltext)
 *       2.6.6 读文件，loadFile(filename)
 *       2.6.7 写文件，saveFile(filename, content)
 *   2.7 内嵌脚本中的 log 对象可用于同步日志输出
 *       log 对象提供的主要方法有：debug(msg), info(msg), warn(msg), error(msg)
 *   2.8 内嵌脚本中的 exit(return_code) 函数可用于退出当前 SQLRunner 执行过程
 *   2.9 内嵌脚本中的 goto(label) 函数可用于跳转到 label 指定 SQL 文本行
 *   2.10 内嵌脚本中的 sleep(ms) 函数可用于释放CPU时间片 ms 毫秒
 *   2.11 内嵌脚本中的 config(key) 函数可用于获取配置信息
 *   2.12 内嵌脚本中的 include(sqlfile) 函数可用于执行asql文件
 * 
 * 3. 支持SQL语句中动态数据替换
 *   3.1 在SELECT语句前增加标签，可将查询结果集保存在变量名为 label_name，变量类型为 Map 数组（Object Array） 的全局变量中
 *     3.1.1 形式 label_name : SELECT ID,NAME FROM TABLE1
 *   3.2 在后续SQL中使用该标签关联的结果集
 *     3.2.1 形式 INSERT INTO TABLE2 (ID,NAME) VALUES ({lable_name.ID}, ''{lable_name.NAME}'')
 *       3.2.1.1 label_name.ID 以数组形式返回结果集中的ID字段内容
 *       3.2.1.2 该语句会自动循环n次，n为结果集中的记录数，n为0时，该语句不会被执行
 *       3.2.1.3 结果集引用实际上是内嵌脚本，遵循内嵌JavaScript规则
 *     3.2.2 形式 DELETE FROM TABLE1 WHERE ID={lable_name[0].ID}
 *       3.2.2.1 label_name[0].ID 返回结果集中第一行数据中的ID字段，如果数据不存在，该语句不会被执行
 *       3.2.2.1 label_name[0].ID 返回值可能为 null
 *     3.2.3 形式 DELETE FROM TABLE1 WHERE ID={lable_name.ID[0]}
 *       3.2.3.1 label_name.ID[0] 返回结果集中第一行数据中的ID字段，如果数据不存在，返回空字符串
 *       3.2.3.2 label_name.ID[0] 返回值不会为 null
 *   3.3 标签是一个内嵌脚本中的变量，变量类型为 Map 数组（Object Array）
 *   3.4 最后一次SELECT语句，查询结果集总会覆盖到""所代表的标签中，
 *       在内嵌脚本中可以通过 this[""] 的形式访问，如 {this[""].ID[0]}
 *   3.5 一句SQL中可以{label_name.ID}形式引用多个标签的结果集，该语句循环次数为多个结果集记录数的乘积
 * 
 * 
 * 4. 支持文本解析功能
 *   4.1 在内嵌脚本中调用文本解析功能
 *       { text_parser_result = parse(parser_script_name, text_file_name); };
 *     4.1.1 parser_script_name为文本段落解析脚本名称
 *     
 *           * 系统可以通过配置信息获取脚本名称对应的脚本文件
 *           * parser.script.parser_script_name=脚本文件名
 *           * 系统可以通过系统配置信息获取脚本名称对应的脚本内容，对应配置信息以"{"开始,"}"结束
 *           * parser.script.parser_script_name={ 脚本内容 }
 *           * parser_script_name也可以是直接的脚本内容，以"{"开始,"}"结束
 *
 *           在脚本中定义具体的解析方法，
 *               function parseLine(line, number) {
 *                   line   为当前行内容
 *                   number 为当前行号
 *                   返回值：当前行可以被识别并解析时，返回 me，即空字符串""。
 *                           当前行属于嵌套内容，可以被其它脚本解析时，返回该脚本名称
 *                           当前行不可识别时，返回空，null。
 *                   已知当前行为段落结束行时，设置 endOfSegment=true。
 *               }
 *           和
 *               function parseEnd() {
 *                   当前段落解析结束
 *               }。
 *           脚本中预定义的变量如下：
 *               // currentSegment 当前解析段落的Java对象
 *               //       可以使用的方法定义在 SegmentParser 接口中。
 *               // parentSegment  上级解析段落的Java对象
 *               //       可以使用的方法定义在 SegmentParser 接口中。
 *               // me             内置 parseLine 函数返回值，值为空字符串""，代表当前解析脚本
 *               // result         解析结果：{"table_name": [{"field_name": "field_value" ... } ... ] ... };
 *               //endOfSegment    当前解析段落结束标记，[true, false]。
 *     4.1.2 text_file_name为要解析的文本文件名
 *     4.1.3 返回结果为Map对象，其具体结构定义在解析脚本中
 *           例如：{main=[{table_name=[{field_name1=field_value1, field_name2=field_value2}]}]}
 *     
 * 
 * TODO List
 * 
 * @author  LIBOFENG
 * @version 2013.1.9		基本功能实现
 * @version 2013.11.13		事务处理支持
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
	 * 按配置信息运行
	 * @return
	 */
	public int run()
	{
		return o.run();
	}

	/**
	 * 执行指定脚本
	 * @return
	 * @throws Exception 
	 */
	public Object execute(String procname, String sqlfiles, Map parameters) throws Exception
	{
		return o.execute(procname, sqlfiles, parameters);
	}

	/**
	 * 清除系统资源
	 */
	public void cleanupAll()
	{
		o.cleanupAll();
	}

	/**
	 * 启动新的处理线程
	 * @param procname 处理线程名称
	 * @param sqlfiles 处理过程文件名，相对系统当前路径
	 * @param schedule 系统运行时间安排，cron表达式，可以为null
	 * @param start_delay  首次启动延迟秒，0立即启动，-1只按cron表达式运行
	 * @param min_interval 处理过程两次执行的最小时间间隔秒
	 * @param parameters 指定处理过程参数
	 */
	public void start(String procname, String sqlfiles, String schedule, long start_delay, long min_interval,
		Map parameters)
	{
		o.start(procname, sqlfiles, schedule, start_delay, min_interval, parameters);
	}

	/**
	 * 返回处理过程运行状态 
	 * 		"N" (none，线程不存在) 
	 * 		"W" (waiting，线程已经启动，但未执行处理过程) 
	 * 		"R" (running，处理过程正在执行) 
	 * 		"E" (error，处理过程异常结束) 
	 * 		"C" (completed，处理线程正常结束) 
	 * 处理线程结束后，状态保留一天，超时后状态归为"N" (none)
	 */
	public String status(String procname)
	{
		return o.status(procname);
	}

	/**
	 * 当前处理过程结束后，终止处理线程，
	 * 即，停止处理线程，但不会中断正在执行的处理过程
	 */
	public void stop(String procname)
	{
		o.stop(procname);
	}

	/**
	 * 正在运行的处理过程
	 */
	public String[] runningProcs()
	{
		return o.runningProcs();
	}

	/**
	 * 正在运行的处理过程数量
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
		//系统停止的标记文件
		File stopfile = new File(SystemConfig.get("stop.file", "stop"));
		try
		{
			//系统启动，清除停止标记文件
			if (stopfile.exists())
			{
				stopfile.delete();
			}
			//系统初始化
			long tnorunning = 0;
			int runningcount = 0;
			log.info("begin.");
			Runtime.getRuntime().addShutdownHook(hook);
			//系统运行
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
			//系统正常终止
			cleanupAll();
			Runtime.getRuntime().removeShutdownHook(hook);
			log.info("end.");
		}
		catch(Throwable e)
		{
			log.error(e);
			//系统异常终止
		}
		finally
		{
			if (stopfile.exists())
			{
				stopfile.delete();
			}
			//系统终止前处理
		}
	}
}
