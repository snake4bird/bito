package bito.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

public class FileAppender
{
	private String originalFilename;
	private boolean filenameContainsVariable;
	private long lastUpdateFile = 0;
	private String lastfilename = null;
	private File file = null;
	private FileOutputStream fos = null;
	private PrintStream ps = null;
	private String output_encoding = null;
	private long maxfilesize;
	private int maxbackindex;
	private int maxkeepdays;

	public FileAppender(String filename, long maxfilesize, int maxbackindex)
	{
		this(filename, maxfilesize, maxbackindex, -1, null);
	}

	public FileAppender(String filename, long maxfilesize, int maxbackindex, int maxkeepdays, String output_encoding)
	{
		this.originalFilename = filename;
		this.filenameContainsVariable = originalFilename.indexOf("[") >= 0 && originalFilename.indexOf("]") > 0;
		this.maxfilesize = maxfilesize;
		this.maxbackindex = maxbackindex;
		this.maxkeepdays = maxkeepdays;
		this.output_encoding = output_encoding;
		if (maxkeepdays > 0)
		{
			new Thread("clear old files")
			{
				public void start()
				{
					this.setDaemon(true);
					super.start();
				}

				public void run()
				{
					String fn = originalFilename.replaceAll(".*[\\/\\\\]", "").replaceAll("\\.", "\\\\.")
						.replaceAll("\\[[^\\]]*\\]", "\\\\d+");
					{
						int lastdot = fn.lastIndexOf("\\.");
						if (lastdot < 0)
						{
							lastdot = fn.length();
						}
						String fname = fn.substring(0, lastdot);
						String fnext = fn.substring(lastdot);
						fn = fname + "(?:\\.\\d+)?" + fnext;
					}
					while(true)
					{
						try
						{
							clearOldFiles(fn);
							Thread.sleep(60000);
						}
						catch(Throwable e)
						{
						}
					}
				}
			}.start();
		}
	}

	protected void finalize() throws Throwable
	{
		closeAppender();
		super.finalize();
	}

	private void clearOldFiles(String fn)
	{
		File dir = new File(originalFilename).getParentFile();
		if (dir == null)
		{
			dir = new File(".");
		}
		File[] fs = dir.listFiles();
		long t = System.currentTimeMillis() - this.maxkeepdays * 24 * 3600 * 1000;
		if (fs != null)
		{
			for(File f : fs)
			{
				if (f.lastModified() < t && f.getName().matches(fn))
				{
					f.delete();
				}
			}
		}
	}

	private void updateFile()
	{
		if (System.currentTimeMillis() > lastUpdateFile + 5000)
		{
			String fn = originalFilename;
			if (filenameContainsVariable)
			{
				Calendar cal = Calendar.getInstance();
				int year = (cal.get(Calendar.YEAR));
				int month = (cal.get(Calendar.MONTH) + 1);
				int date = (cal.get(Calendar.DAY_OF_MONTH));
				int hour = (cal.get(Calendar.HOUR_OF_DAY));
				int min = (cal.get(Calendar.MINUTE));
				fn = fn.replaceAll("\\[yyyy\\]", "" + year);
				fn = fn.replaceAll("\\[MM\\]", "" + (month < 10?"0":"") + month);
				fn = fn.replaceAll("\\[dd\\]", "" + (date < 10?"0":"") + date);
				fn = fn.replaceAll("\\[HH\\]", "" + (hour < 10?"0":"") + hour);
				fn = fn.replaceAll("\\[mm\\]", "" + (min < 10?"0":"") + min);
			}
			if (!fn.equals(lastfilename))
			{
				closeAppender();
				this.file = new File(fn);
				File pd = file.getParentFile();
				if (pd != null && !pd.exists())
				{
					pd.mkdirs();
				}
				lastfilename = fn;
			}
			lastUpdateFile = System.currentTimeMillis();
		}
	}

	public synchronized void appendfile(String msg, Throwable ex) throws IOException
	{
		updateFile();
		if (!file.exists())
		{
			closeAppender();
		}
		if (maxfilesize > 0 && file.length() > maxfilesize)
		{
			closeAppender();
			int lastdot = lastfilename.lastIndexOf(".");
			if (lastdot < 0)
			{
				lastdot = lastfilename.length();
			}
			String fname = lastfilename.substring(0, lastdot);
			String fnext = lastfilename.substring(lastdot);
			renameExistFile(file, fname, 1, fnext);
		}
		if (ps == null)
		{
			fos = new FileOutputStream(file, true);
			ps = output_encoding == null?new PrintStream(fos, true):new PrintStream(fos, true, output_encoding);
		}
		ps.print(msg);
		if (ex != null)
		{
			ex.printStackTrace(ps);
		}
	}

	private void closeAppender()
	{
		if (ps != null)
		{
			ps.close();
			ps = null;
		}
	}

	private void renameExistFile(File currentfile, String fname, int idx, String fnext)
	{
		if (maxbackindex < 0 || idx <= maxbackindex)
		{
			File fnn = new File(fname + "." + idx + fnext);
			if (fnn.exists())
			{
				renameExistFile(fnn, fname, idx + 1, fnext);
			}
			currentfile.renameTo(fnn);
		}
		else
		{
			if (currentfile.exists())
			{
				currentfile.delete();
			}
		}
	}
}
