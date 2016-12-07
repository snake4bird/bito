package bito;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import bito.util.FileAppender;
import bito.util.cfg.SystemConfig;
import bito.net.URLReader;
import bito.evframe.EVF;
import bito.util.DESX;
import bito.util.GIFEncrypt;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			if (args.length >= 3 && args[0].equals("encrypt"))
			{
				encryptFile(args[1], args[2], (args.length >= 4)?args[3]:null, (args.length >= 5)?args[4]:null);
			}
			else if (args.length >= 3 && args[0].equals("decrypt"))
			{
				decryptFile(args[1], args[2], (args.length >= 4)?args[3]:null);
			}
			else if (args.length >= 1 && args[0].equals("output"))
			{
				outputfile((args.length > 1)?args[1]:"stdout.[yyyy][MM][dd].txt",
					SystemConfig.getLong("maxfilesize", 2048000),
					SystemConfig.getInt("maxbackindex", -1),
					SystemConfig.getBoolean("stdout", true));
			}
			else if (args.length >= 1 && args[0].equals("tail"))
			{
				if (args.length == 1)
				{
					System.out.println("bito.Main tail <file>");
				}
				tailingfile(args[1]);
			}
			else if (args.length >= 1 && args[0].startsWith("!"))
			{
				new EVF(args).run();
			}
			else if (args.length == 0 && SystemConfig.get("run.class") != null)
			{
				Runnable r = (Runnable)Class.forName(SystemConfig.get("run.class")).newInstance();
				r.run();
			}
			else if (args.length > 1 && args[0].equals("wget"))
			{
				URLReader r = new URLReader();
				if ("debug".equals(System.getProperty("mode")))
				{
					r.setDebugOutput(System.out, System.getProperty("debug.setting"));
				}
				byte[] respbs = r.ReadURL(args[1],
					System.getProperty("method"),
					System.getProperty("username"),
					System.getProperty("password"),
					System.getProperty("proxyhost"),
					System.getProperty("proxyport"),
					System.getProperty("proxyuser"),
					System.getProperty("proxypswd"),
					null,
					args.length > 2?args[2].getBytes():null,
					null,
					-1,
					-1,
					Integer.parseInt(System.getProperty("timeout", "-1")));
				System.out.println(new String(respbs, System.getProperty("charset", Charset.defaultCharset().name())));
			}
			else if (args.length == 0)
			{
				new EVF(new String[]{"!bito.util.RegxMatch"}).run();
			}
			else
			{
				System.out.println("bito.Main ?");
				System.out.println("bito.Main !<JPanel Class Name>");
				System.out.println("bito.Main encrypt <srcfile> <desfile> <key>");
				System.out.println("bito.Main decrypt <srcfile> <desfile> <key>");
				System.out.println("bito.Main tail <tailfile>");
				System.out
					.println("[-Dusername=...] [-Dpassword=...] [-Dmode=debug] bito.Main wget <http[s]url> [postdata]");
				System.out.println("[-Dusermail=...] [-Dpassword=...] bito.Main register");
				System.out
					.println("-Dmaxfilesize=2048000 -Dmaxbackindex=-1 bito.Main output <redirect stdout to filename>");
				System.out.println();
				System.out.println(Arrays.toString(args));
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	private static void tailingfile(String filename)
	{
		long output_byte_index = -1;
		File f = new File(filename);
		while(true)
		{
			try
			{
				if (f.length() < output_byte_index)
				{
					output_byte_index = 0;
				}
				if (f.length() > output_byte_index)
				{
					FileInputStream fis = new FileInputStream(f);
					try
					{
						if (output_byte_index == -1)
						{
							if (fis.available() > 1024)
							{
								output_byte_index = fis.available() - 1024;
							}
							else
							{
								output_byte_index = 0;
							}
						}
						fis.skip(output_byte_index);
						if (fis.available() > 0)
						{
							output_byte_index += fis.available();
							pipe(fis, System.out);
						}
					}
					finally
					{
						fis.close();
					}
				}
				else
				{
					Thread.sleep(100);
				}
			}
			catch(IOException e)
			{
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e1)
				{
				}
			}
			catch(InterruptedException e)
			{
			}
		}
	}

	private static void outputfile(String filename, long maxsize, int maxback, boolean stdout) throws IOException
	{
		String input_encoding = System.getProperty("input.encoding");
		String output_encoding = System.getProperty("output.encoding");
		FileAppender fa = null;
		if (filename != null && filename.length() > 0)
		{
			fa = new FileAppender(filename, maxsize, maxback, output_encoding);
		}
		try
		{
			int c;
			while((c = System.in.read()) != -1)
			{
				byte[] bs = new byte[System.in.available() + 1];
				bs[0] = (byte)c;
				System.in.read(bs, 1, bs.length - 1);
				String s = input_encoding == null?new String(bs):new String(bs, input_encoding);
				if (fa != null)
				{
					fa.appendfile(s, null);
				}
				if (stdout)
				{
					try
					{
						System.out.print(s);
					}
					catch(Throwable e)
					{
					}
				}
			}
		}
		catch(Throwable e)
		{
			fa.appendfile("Failure:", e);
			fa.appendfile("Available charsets: " + Charset.availableCharsets().keySet().toString(), null);
		}
	}

	private static void encryptFile(String srcfile, String desfile, String key, String comments) throws Exception
	{
		if (key == null)
		{
			key = inputPassword();
			if (key.length() == 0)
			{
				key = null;
			}
		}
		byte[] bs = readBytes(srcfile);
		byte[] zbs = key == null?bs:DESX.Encrypt(bs, key.getBytes());
		String s;
		if (comments == null)
		{
			s = "";
		}
		else if (comments.length() == 0)
		{
			if (key.trim().length() > 0)
			{
				s = new String(zbs, 0, zbs.length > 1000?1000:zbs.length);
			}
			else
			{
				s = new String(bs, 0, bs.length > 1000?1000:bs.length);
			}
		}
		else
		{
			s = comments;
		}
		bs = GIFEncrypt.encode(s, zbs, key == null?1:2);
		FileOutputStream fos = new FileOutputStream(desfile);
		fos.write(bs);
		fos.close();
	}

	private static void decryptFile(String srcfile, String desfile, String key) throws Exception
	{
		if (key == null)
		{
			key = inputPassword();
			if (key.length() == 0)
			{
				key = null;
			}
		}
		byte[] bs = readBytes(srcfile);
		bs = GIFEncrypt.decode(bs);
		bs = key == null?bs:DESX.Decrypt(bs, key.getBytes());
		FileOutputStream fos = new FileOutputStream(desfile);
		fos.write(bs);
		fos.close();
	}

	public static void pipe(InputStream is, OutputStream os) throws IOException
	{
		int c = is.read();
		while(c >= 0)
		{
			os.write(c);
			while(is.available() > 0)
			{
				byte[] bs = new byte[is.available()];
				is.read(bs);
				os.write(bs);
			}
			c = is.read();
		}
	}

	public static byte[] readBytes(String filename) throws IOException
	{
		FileInputStream fis = new FileInputStream(filename);
		try
		{
			return readBytes(fis);
		}
		finally
		{
			fis.close();
		}
	}

	/**
	 * 不读完不返回，不会关闭InputStream
	 */
	public static byte[] readBytes(InputStream is) throws IOException
	{
		int c = is.read();
		if (c == -1)
		{
			return new byte[0];
		}
		byte[] rbs = new byte[]{(byte)c};
		while(c >= 0)
		{
			int a = is.available();
			if (a > 0)
			{
				byte[] bs = new byte[rbs.length + a];
				System.arraycopy(rbs, 0, bs, 0, rbs.length);
				c = is.read(bs, rbs.length, a);
				if (c == 0)
				{
					// 没有读出数据
				}
				else if (c > 0 && c < a)
				{
					// 一次读不出所有数据
					byte[] nbs = new byte[rbs.length + c];
					System.arraycopy(bs, 0, nbs, 0, nbs.length);
					rbs = nbs;
				}
				else
				{
					// 读出了所有available数据
					rbs = bs;
				}
			}
			else
			{
				c = is.read();
				if (c >= 0)
				{
					byte[] bs = new byte[rbs.length + 1];
					System.arraycopy(rbs, 0, bs, 0, rbs.length);
					bs[bs.length - 1] = (byte)c;
					rbs = bs;
				}
			}
		}
		return rbs;
	}

	private static String inputPassword() throws IOException, InterruptedException
	{
		System.out.println("Password:");
		while(System.in.available() == 0)
		{
			Thread.sleep(1);
		}
		byte[] bs = new byte[System.in.available()];
		System.in.read(bs);
		return new String(bs).replaceAll("[\r\n]", "");
	}
}
