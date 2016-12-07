package d._;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

public class FileAppender implements d.FileAppender
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

	public FileAppender(String filename, long maxfilesize, int maxbackindex)
	{
		this(filename, maxfilesize, maxbackindex, null);
	}

	public FileAppender(String filename, long maxfilesize, int maxbackindex, String output_encoding)
	{
		this.originalFilename = filename;
		this.filenameContainsVariable = originalFilename.indexOf("[") >= 0 && originalFilename.indexOf("]") > 0;
		this.maxfilesize = maxfilesize;
		this.maxbackindex = maxbackindex;
		this.output_encoding = output_encoding;
	}

	protected void finalize() throws Throwable
	{
		closeAppender();
		super.finalize();
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
			renameExistFile(fnn, fname, idx + 1, fnext);
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
