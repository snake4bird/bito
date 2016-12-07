package bito.util.textparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

import bito.util.dba.SQLRunner;
import bito.util.logger.Log;

public class Parser
{
	private Log log = new Log("");
	private SQLRunner sqlrunner = null;
	// 当前数据段对象
	private SegmentParser cxo = null;
	private boolean onErrorContinue = false;
	private int count = 0;
	private long clock = 0;
	private Map result = null;

	public Parser()
	{
	}

	public void setLogger(Log log)
	{
		this.log = log;
	}

	public void setSQLRunner(SQLRunner sqlrunner)
	{
		this.sqlrunner = sqlrunner;
	}

	public void setResultMap(Map result)
	{
		this.result = result;
	}

	public Map getResultMap()
	{
		return this.result;
	}

	public void parse(String scriptname, String textFilename) throws Exception
	{
		onErrorContinue = "continue".equalsIgnoreCase(System.getProperty("parser.script.on.error"));
		cxo = new SegmentParserScript();
		cxo.setLogger(log);
		cxo.setSQLRunner(sqlrunner);
		cxo.setParserScript(scriptname);
		{
			FileReader fr = null;
			BufferedReader br = null;
			try
			{
				if (log != null)
				{
					log.info("开始解析，" + textFilename);
				}
				fr = new FileReader(textFilename);
				br = new BufferedReader(fr);
				String oneLine;
				//开始处理
				count = 0;
				clock = System.currentTimeMillis();
				long tc = 0;
				boolean brpol = true;
				beginProcess();
				// 读一行
				while(brpol && (oneLine = br.readLine()) != null)
				{
					count++;
					// 如果有下一行，就处理一行。
					brpol = processOneline(oneLine);
					long t = (System.currentTimeMillis() - clock) / 10000;
					if (t > tc)
					{
						tc = t;
						if (log != null)
						{
							log.debug("" + count + "行解析已完成");
						}
					}
				}
				if (!brpol)
				{
					if (log != null)
					{
						log.debug("未完成全部文件的解析");
					}
				}
				// 如果没有下一行，做结束处理。
				endProcess();
			}
			catch(FileNotFoundException e)
			{
				throw new Exception("未找到要解析的文件：" + textFilename);
			}
			finally
			{
				if (br != null)
				{
					br.close();
				}
				else if (fr != null)
				{
					fr.close();
				}
			}
		}
	}

	private void beginProcess()
	{
		if (log != null)
		{
			log.info("解析行处理开始");
		}
	}

	private boolean processOneline(String oneLine) throws Exception
	{
		// 判断当前对象是否为空
		try
		{
			if (cxo == null)
			{
				return false;
			}
			SegmentParser spo = cxo.parseLine(oneLine, count);
			/**
			 * 解析一行，可能的结果
			 * 当前段是否结束，当前行是否属于当前段
			 *       No                 Yes          => 正在解析，返回当前段
			 *       Yes                Yes          => 解析完成，返回当前段
			 *       Yes                No           => 未处理行，需交给父级解析段落处理，返回null
			 *       No                 No           => 段落嵌套，返回新的解析段落
			 */
			if (cxo.equals(spo))
			{
				if (cxo.isEndOfSegment())
				{
					spo = cxo.getParentSegment();
					endParse(cxo);
					cxo = spo;
				}
				else
				{
					//continue
				}
			}
			else
			{
				if (spo != null)
				{
					spo.setParentSegment(cxo);
				}
				else
				{
					spo = cxo.getParentSegment();
					endParse(cxo);
				}
				cxo = spo;
				return processOneline(oneLine);
			}
		}
		catch(Exception e)
		{
			String s = "解析异常：" + e.getMessage() + "\r\nline[" + count + "]:" + oneLine;
			if (onErrorContinue)
			{
				if (log != null)
				{
					log.error(s, e);
				}
			}
			else
			{
				throw new Exception(s, e);
			}
		}
		return true;
	}

	// 结束处理
	private void endProcess() throws Exception
	{
		if (log != null)
		{
			log.info("解析" + count + "行，结束处理");
		}
		while(cxo != null)
		{
			endParse(cxo);
			cxo = cxo.getParentSegment();
		}
		long t = (System.currentTimeMillis() - clock + 500) / 1000;
		if (log != null)
		{
			log.info("解析完成，用时" + (t / 60) + "分" + (t % 60) + "秒");
		}
	}

	private void endParse(SegmentParser spo) throws Exception
	{
		spo.parseEnd();
		String key = spo.getParserName();
		result = spo.getResult();
		if (log != null)
		{
			log.debug("解析结果[" + key + "]:" + result.toString());
		}
	}
}
