// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 8:17:33
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package b;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;

// Referenced classes of package com.gif4j:
// GifFrame, GifImage, ImageUtils, a,
// c, m
public class ge
{
	private ge()
	{
	}

	public static final void encode(gi gifimage, OutputStream outputstream) throws IOException
	{
		encode(gifimage, outputstream, false);
	}

	public static final void encode(gi gifimage, OutputStream outputstream, boolean flag) throws IOException
	{
		if (gifimage == null)
			throw new NullPointerException("gif image is null!");
		if (outputstream == null)
			throw new NullPointerException("output stream is null!");
		DataOutputStream dataoutputstream = null;
		try
		{
			dataoutputstream = new DataOutputStream(outputstream);
			a(gifimage, dataoutputstream, flag);
		}
		finally
		{
			if (dataoutputstream != null)
				try
				{
					dataoutputstream.flush();
				}
				catch(IOException ioexception)
				{
				}
		}
	}

	public static final void encode(gi gifimage, File file) throws IOException
	{
		encode(gifimage, file, false);
	}

	public static final void encode(gi gifimage, File file, boolean flag) throws IOException
	{
		DataOutputStream dataoutputstream;
		if (gifimage == null)
			throw new NullPointerException("gif image is null!");
		if (file == null)
			throw new NullPointerException("output is null!");
		dataoutputstream = null;
		file.delete();
		dataoutputstream = new DataOutputStream(new FileOutputStream(file));
		try
		{
			a(gifimage, dataoutputstream, flag);
		}
		finally
		{
			if (dataoutputstream != null)
				dataoutputstream.close();
		}
	}

	private static final void a(gi gifimage, DataOutput dataoutput, boolean flag) throws IOException
	{
		if (dataoutput == null)
			throw new NullPointerException("output us null!");
		Vector vector = gifimage.a(flag);
		if (vector == null)
		{
			return;
		}
		else
		{
			a(gifimage, dataoutput);
			a(vector, dataoutput);
			b(gifimage, dataoutput);
			dataoutput.write(59);
			gifimage.b();
			return;
		}
	}

	public static final void encode(BufferedImage bufferedimage, OutputStream outputstream) throws IOException
	{
		if (bufferedimage == null)
		{
			throw new NullPointerException("image == null!");
		}
		else
		{
			encode((new gi(true)).a(bufferedimage), outputstream);
			return;
		}
	}

	public static final void encode(BufferedImage bufferedimage, DataOutput dataoutput) throws IOException
	{
		if (bufferedimage == null)
		{
			throw new NullPointerException("image == null!");
		}
		else
		{
			a((new gi(true)).a(bufferedimage), dataoutput, false);
			return;
		}
	}

	public static final void encode(BufferedImage bufferedimage, File file) throws IOException
	{
		if (bufferedimage == null)
		{
			throw new NullPointerException("image == null!");
		}
		else
		{
			encode((new gi(true)).a(bufferedimage), file, false);
			return;
		}
	}

	private static final void a(gi gifimage, DataOutput dataoutput) throws IOException
	{
		dataoutput.write(a);
		a(gifimage.width, dataoutput);
		a(gifimage.height, dataoutput);
		byte byte0 = (byte)((gifimage.c?0x80:0) | 0x70 | (gifimage.c?gifimage.d - 1:0));
		dataoutput.write(byte0);
		dataoutput.write(gifimage.k);
		dataoutput.write(gifimage.l);
		if (gifimage.c)
			dataoutput.write(gifimage.a());
		if (gifimage.r != null)
			gifimage.r.a(dataoutput);
	}

	private static final void b(gi gifimage, DataOutput dataoutput) throws IOException
	{
		int i = gifimage.getNumberOfComments();
		if (i > 0)
		{
			Object obj = null;
			for(int j = 0; j < i; j++)
			{
				c c1 = new c(gifimage.getComment(j));
				dataoutput.write(c1.a());
			}
		}
	}

	private static final void a(Vector vector, DataOutput dataoutput) throws IOException
	{
		ac a1 = new ac(dataoutput);
		int i = vector.size();
		for(int j = 0; j < i; j++)
		{
			gf gifframe = (gf)vector.elementAt(j);
			dataoutput.write(gifframe.c());
			dataoutput.write(gifframe.d());
			if (gifframe.f)
				dataoutput.write(gifframe.b());
			int k = gifframe.i > 1?gifframe.i:2;
			dataoutput.write((byte)k);
			byte abyte0[] = gifframe.e();
			if (abyte0.length > 0)
				a1.a(gifframe.e(), k + 1);
			dataoutput.write(0);
			gifframe.a();
		}
		ac.a(a1);
	}

	private static final void a(int i, DataOutput dataoutput) throws IOException
	{
		dataoutput.write((byte)(i & 0xff));
		dataoutput.write((byte)(i >> 8 & 0xff));
	}

	private static final byte a[];
	static
	{
		a = new byte[6];
		a[0] = 71;
		a[1] = 73;
		a[2] = 70;
		a[3] = 56;
		a[4] = 57;
		a[5] = 97;
	}
}
