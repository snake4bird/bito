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
	// ��ǰ���ݶζ���
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
					log.info("��ʼ������" + textFilename);
				}
				fr = new FileReader(textFilename);
				br = new BufferedReader(fr);
				String oneLine;
				//��ʼ����
				count = 0;
				clock = System.currentTimeMillis();
				long tc = 0;
				boolean brpol = true;
				beginProcess();
				// ��һ��
				while(brpol && (oneLine = br.readLine()) != null)
				{
					count++;
					// �������һ�У��ʹ���һ�С�
					brpol = processOneline(oneLine);
					long t = (System.currentTimeMillis() - clock) / 10000;
					if (t > tc)
					{
						tc = t;
						if (log != null)
						{
							log.debug("" + count + "�н��������");
						}
					}
				}
				if (!brpol)
				{
					if (log != null)
					{
						log.debug("δ���ȫ���ļ��Ľ���");
					}
				}
				// ���û����һ�У�����������
				endProcess();
			}
			catch(FileNotFoundException e)
			{
				throw new Exception("δ�ҵ�Ҫ�������ļ���" + textFilename);
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
			log.info("�����д���ʼ");
		}
	}

	private boolean processOneline(String oneLine) throws Exception
	{
		// �жϵ�ǰ�����Ƿ�Ϊ��
		try
		{
			if (cxo == null)
			{
				return false;
			}
			SegmentParser spo = cxo.parseLine(oneLine, count);
			/**
			 * ����һ�У����ܵĽ��
			 * ��ǰ���Ƿ��������ǰ���Ƿ����ڵ�ǰ��
			 *       No                 Yes          => ���ڽ��������ص�ǰ��
			 *       Yes                Yes          => ������ɣ����ص�ǰ��
			 *       Yes                No           => δ�����У��轻�������������䴦������null
			 *       No                 No           => ����Ƕ�ף������µĽ�������
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
			String s = "�����쳣��" + e.getMessage() + "\r\nline[" + count + "]:" + oneLine;
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

	// ��������
	private void endProcess() throws Exception
	{
		if (log != null)
		{
			log.info("����" + count + "�У���������");
		}
		while(cxo != null)
		{
			endParse(cxo);
			cxo = cxo.getParentSegment();
		}
		long t = (System.currentTimeMillis() - clock + 500) / 1000;
		if (log != null)
		{
			log.info("������ɣ���ʱ" + (t / 60) + "��" + (t % 60) + "��");
		}
	}

	private void endParse(SegmentParser spo) throws Exception
	{
		spo.parseEnd();
		String key = spo.getParserName();
		result = spo.getResult();
		if (log != null)
		{
			log.debug("�������[" + key + "]:" + result.toString());
		}
	}
}
