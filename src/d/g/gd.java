// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 8:17:25
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package d.g;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

// Referenced classes of package com.gif4j:
// GifFrame, GifImage, d, f,
// g, m
public class gd
{
	static class d extends InputStream
	{
		public void close() throws IOException
		{
			c = null;
			if (b != null)
			{
				b.close();
				b = null;
			}
		}

		public int available() throws IOException
		{
			if (c == null)
				throw new IOException();
			else
				return (c.length - d) + b.available();
		}

		public int read() throws IOException
		{
			if (c == null)
				throw new IOException();
			a++;
			if (d < c.length)
				return c[d++] & 0xff;
			else
				return b.read();
		}

		public int read(byte abyte0[], int i, int j) throws IOException
		{
			int l = j;
			int k = a(abyte0, i, j);
			do
			{
				if (k == -1)
					return -1;
				a += k;
				if (k == l)
					return j;
				l -= k;
				i += k;
				k = a(abyte0, i, l);
			}
			while(true);
		}

		private int a(byte abyte0[], int i, int j) throws IOException
		{
			if (c == null)
				throw new IOException();
			if (i < 0 || i > abyte0.length || j < 0 || j > abyte0.length - i)
				throw new ArrayIndexOutOfBoundsException();
			int k = 0;
			int l = i;
			int i1 = c.length - d;
			if (i1 > 0)
			{
				k = i1 < j?i1:j;
				System.arraycopy(c, d, abyte0, l, k);
				l += k;
				d += k;
			}
			if (k == j)
				return j;
			int j1 = b.read(abyte0, l, j - k);
			if (j1 > 0)
				return j1 + k;
			if (k == 0)
				return j1;
			else
				return k;
		}

		public void a(byte abyte0[]) throws IOException
		{
			int i = abyte0.length;
			if (i > d)
			{
				throw new IOException();
			}
			else
			{
				a -= i;
				d -= i;
				System.arraycopy(abyte0, 0, c, d, i);
				return;
			}
		}

		int a;
		InputStream b;
		protected byte c[];
		protected int d;

		public d(InputStream inputstream, int i)
		{
			b = inputstream;
			if (i > 0)
			{
				c = new byte[i];
				d = i;
			}
			else
			{
				throw new IllegalArgumentException();
			}
		}
	}

	static class f
	{
		public void a(d d1, gf gifframe, int i1) throws IOException
		{
			q = d1;
			x = gifframe;
			w = gifframe.h;
			a = i1;
			n = 1;
			o = 0;
			f = a + 1;
			j = 1 << f;
			g = 1 << a;
			h = g + 1;
			i = k = h + 1;
			d = -1;
			b = e = 0;
			c = 0;
			p = v[f - 1];
			s = new int[4096];
			t = new int[4096];
			u = new int[4096];
			r = new byte[256];
			l = gifframe.width;
			m = gifframe.height;
			b();
		}

		int a() throws IOException
		{
			int i1;
			if (e == 0)
			{
				if (c >= b)
				{
					b = q.read();
					if (b == -1)
						throw new IOException("Invalid input stream: more data is expected!");
					r[0] = (byte)b;
					b = q.read(r, 1, b);
					if (b == -1)
						throw new IOException("Invalid input stream: more data is expected!");
					c = 0;
					if (b == 0)
						return h;
				}
				c++;
				d = r[c] & 0xff;
				e = 8;
				i1 = d;
			}
			else
			{
				int j1 = e - 8;
				if (j1 < 0)
					i1 = d >> 0 - j1;
				else
					i1 = d << j1;
			}
			for(; f > e; e += 8)
			{
				if (c >= b)
				{
					b = q.read();
					if (b == -1)
						throw new IOException("Invalid input stream: more data is expected!");
					r[0] = (byte)b;
					b = q.read(r, 1, b);
					if (b == -1)
						throw new IOException("Invalid input stream: more data is expected!");
					c = 0;
					if (b == 0)
						return h;
				}
				c++;
				d = r[c] & 0xff;
				i1 += d << e;
			}
			e -= f;
			return i1 & p;
		}

		void b() throws IOException
		{
			int j1 = 0;
			int k1 = 0;
			byte abyte0[] = new byte[l];
			int l1 = 0;
			int i2 = 0;
			int j2;
			while((j2 = a()) != h)
				if (j2 == g)
				{
					f = a + 1;
					p = v[a];
					k = i;
					j = 1 << f;
					while((j2 = a()) == g);
					if (j2 != h)
					{
						j1 = k1 = j2;
						abyte0[i2] = (byte)j2;
						if (++i2 == l)
						{
							System.arraycopy(abyte0, 0, x.n, o * l, abyte0.length);
							if (w)
							{
								if (n == 1)
									o += 8;
								else if (n == 2)
									o += 8;
								else if (n == 3)
									o += 4;
								else if (n == 4)
									o += 2;
								if (o >= m)
								{
									n++;
									if (n == 2)
										o = 4;
									else if (n == 3)
										o = 2;
									else if (n == 4)
										o = 1;
									else if (n == 5)
										o = 0;
								}
								if (o >= m)
									o = 0;
							}
							else
							{
								o++;
							}
							i2 = 0;
						}
					}
				}
				else
				{
					int i1 = j2;
					if (i1 >= k)
					{
						i1 = j1;
						s[l1] = k1;
						l1++;
					}
					for(; i1 >= i; i1 = u[i1])
					{
						s[l1] = t[i1];
						l1++;
					}
					s[l1] = i1;
					l1++;
					if (k < j)
					{
						k1 = i1;
						t[k] = k1;
						u[k] = j1;
						k++;
						j1 = j2;
					}
					if (k >= j && f < 12)
					{
						p = v[f];
						f++;
						j = j + j;
					}
					while(l1 > 0)
					{
						l1--;
						abyte0[i2] = (byte)s[l1];
						if (++i2 == l)
						{
							System.arraycopy(abyte0, 0, x.n, o * l, abyte0.length);
							if (w)
							{
								if (n == 1)
									o += 8;
								else if (n == 2)
									o += 8;
								else if (n == 3)
									o += 4;
								else if (n == 4)
									o += 2;
								if (o >= m)
								{
									n++;
									if (n == 2)
										o = 4;
									else if (n == 3)
										o = 2;
									else if (n == 4)
										o = 1;
									else if (n == 5)
										o = 0;
								}
								if (o >= m)
									o = 0;
							}
							else
							{
								o++;
							}
							i2 = 0;
						}
					}
				}
			if (i2 != 0 && o < m)
			{
				System.arraycopy(abyte0, 0, x.n, o * l, abyte0.length);
				if (w)
				{
					if (n == 1)
						o += 8;
					else if (n == 2)
						o += 8;
					else if (n == 3)
						o += 4;
					else if (n == 4)
						o += 2;
					if (o >= m)
					{
						n++;
						if (n == 2)
							o = 4;
						else if (n == 3)
							o = 2;
						else if (n == 4)
							o = 1;
						else if (n == 5)
							o = 0;
					}
					if (o >= m)
						o = 0;
				}
				else
				{
					o++;
				}
			}
		}

		int a;
		int b;
		int c;
		int d;
		int e;
		int f;
		int g;
		int h;
		int i;
		int j;
		int k;
		int l;
		int m;
		int n;
		int o;
		int p;
		d q;
		byte r[];
		int s[];
		int t[];
		int u[];
		static final int v[] = {1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095};
		boolean w;
		gf x;

		f()
		{
		}
	}

	static class g
	{
		boolean a;
		boolean b;
		int c;
		int d;
		int e;

		g()
		{
		}
	}

	private gd()
	{
	}

	public static final gi decode(File file) throws IOException
	{
		if (file == null)
			throw new IllegalArgumentException("Input File is null!");
		if (!file.canRead())
			throw new IOException("Can't read Input File!");
		FileInputStream fileinputstream = null;
		gi gifimage;
		try
		{
			fileinputstream = new FileInputStream(file);
			gifimage = decode(((InputStream)(fileinputstream)));
		}
		finally
		{
			if (fileinputstream != null)
				try
				{
					fileinputstream.close();
				}
				catch(Exception exception1)
				{
				}
		}
		return gifimage;
	}

	public static final gi decode(URL url) throws IOException
	{
		if (url == null)
			throw new IllegalArgumentException("Input URL is null!");
		InputStream inputstream = null;
		try
		{
			inputstream = url.openStream();
		}
		catch(IOException ioexception)
		{
			IOException ioexception1 = new IOException("Can't get input stream from the URL!");
			ioexception1.initCause(ioexception);
			throw ioexception1;
		}
		gi gifimage;
		try
		{
			gifimage = decode(inputstream);
		}
		finally
		{
			if (inputstream != null)
				try
				{
					inputstream.close();
				}
				catch(Exception exception1)
				{
				}
		}
		return gifimage;
	}

	public static final gi decode(InputStream inputstream) throws IOException
	{
		if (inputstream == null)
			throw new NullPointerException("Input stream is null!");
		d d1 = new d(inputstream, 8192);
		byte abyte0[] = new byte[3];
		byte abyte1[] = new byte[3];
		byte abyte2[] = new byte[7];
		d1.read(abyte0);
		String s = new String(abyte0);
		if (!s.equals("GIF"))
			throw new IOException("The specified input stream is not recognised as a GIF image");
		d1.read(abyte1);
		gi gifimage = new gi();
		gifimage.s = new String(abyte1);
		d1.read(abyte2);
		gifimage.width = abyte2[0] & 0xff | (abyte2[1] & 0xff) << 8;
		gifimage.height = abyte2[2] & 0xff | (abyte2[3] & 0xff) << 8;
		byte byte0 = abyte2[4];
		gifimage.k = abyte2[5] & 0xff;
		gifimage.l = abyte2[6];
		gifimage.p = (byte0 >> 4 & 7) + 1;
		gifimage.d = (byte0 & 7) + 1;
		gifimage.e = 1 << gifimage.d;
		if ((byte0 & 0x80) != 0)
		{
			gifimage.c = true;
			gifimage.j = (byte0 & 8) != 0;
			byte abyte3[] = new byte[3 * gifimage.e];
			if (d1.read(abyte3) != abyte3.length)
				throw new IOException("Invalid input stream: more data is expected!");
			gifimage.f = new byte[gifimage.e];
			gifimage.h = new byte[gifimage.e];
			gifimage.g = new byte[gifimage.e];
			for(int i = 0; i < gifimage.e; i++)
			{
				gifimage.f[i] = abyte3[3 * i];
				gifimage.g[i] = abyte3[3 * i + 1];
				gifimage.h[i] = abyte3[3 * i + 2];
			}
		}
		else
		{
			gifimage.c = false;
			gifimage.j = false;
			gifimage.k = -1;
			gifimage.d = gifimage.p;
			gifimage.e = 1 << gifimage.d;
		}
		g g1 = a(d1, gifimage);
		for(int j = d1.read(); j == 44; j = d1.read())
		{
			byte abyte4[] = new byte[9];
			d1.read(abyte4);
			gf gifframe = new gf();
			gifframe.b = abyte4[0] & 0xff | (abyte4[1] & 0xff) << 8;
			gifframe.c = abyte4[2] & 0xff | (abyte4[3] & 0xff) << 8;
			gifframe.width = abyte4[4] & 0xff | (abyte4[5] & 0xff) << 8;
			gifframe.height = abyte4[6] & 0xff | (abyte4[7] & 0xff) << 8;
			byte byte1 = abyte4[8];
			gifframe.h = (byte1 & 0x40) != 0;
			if ((byte1 & 0x80) != 0)
			{
				gifframe.f = true;
				gifframe.g = (byte1 & 0x20) != 0;
				gifframe.i = (byte1 & 7) + 1;
				gifframe.j = 1 << gifframe.i;
				byte abyte5[] = new byte[3 * gifframe.j];
				if (d1.read(abyte5) != abyte5.length)
					throw new IOException("Invalid input stream: more data is expected!");
				gifframe.k = new byte[gifframe.j];
				gifframe.m = new byte[gifframe.j];
				gifframe.l = new byte[gifframe.j];
				for(int i1 = 0; i1 < gifframe.j; i1++)
				{
					gifframe.k[i1] = abyte5[3 * i1];
					gifframe.l[i1] = abyte5[3 * i1 + 1];
					gifframe.m[i1] = abyte5[3 * i1 + 2];
				}
				if (g1 != null)
				{
					gifframe.q = g1.b;
					gifframe.r = g1.c;
					gifframe.p = g1.a;
					gifframe.o = g1.e;
					gifframe.s = g1.d;
				}
				else
				{
					gifframe.q = false;
					gifframe.r = -1;
					gifframe.p = false;
					gifframe.o = 0;
					gifframe.s = 0;
				}
			}
			else
			{
				gifframe.f = false;
				gifframe.i = gifimage.d;
				gifframe.j = 1 << gifframe.i;
				if (gifimage.c)
				{
					gifframe.k = new byte[gifframe.j];
					gifframe.m = new byte[gifframe.j];
					gifframe.l = new byte[gifframe.j];
					System.arraycopy(gifimage.f, 0, gifframe.k, 0, gifframe.j);
					System.arraycopy(gifimage.g, 0, gifframe.l, 0, gifframe.j);
					System.arraycopy(gifimage.h, 0, gifframe.m, 0, gifframe.j);
				}
				else
				{
					gifframe.f = true;
					gifframe.k = new byte[gifframe.j];
					gifframe.m = new byte[gifframe.j];
					gifframe.l = new byte[gifframe.j];
					for(int k = 0; k < gifframe.j; k++)
					{
						byte byte2 = (byte)(int)((256D / (double)gifframe.j) * (double)k);
						gifframe.k[k] = byte2;
						gifframe.l[k] = byte2;
						gifframe.m[k] = byte2;
					}
				}
				if (g1 != null)
				{
					gifframe.q = g1.b;
					gifframe.r = g1.c;
					gifframe.p = g1.a;
					gifframe.o = g1.e;
					gifframe.s = g1.d;
				}
				else
				{
					if (gifimage.c && gifimage.getLastFrame() != null)
					{
						gifframe.q = gifimage.getLastFrame().q;
						gifframe.r = gifimage.getLastFrame().r;
					}
					else
					{
						gifframe.q = false;
						gifframe.r = -1;
					}
					gifframe.p = false;
					gifframe.o = 0;
					gifframe.s = 0;
				}
			}
			int l = d1.read();
			if (l < 0)
				throw new IOException("Invalid input stream: more data is expected!");
			gifframe.n = new byte[gifframe.width * gifframe.height];
			f f1 = new f();
			f1.a(d1, gifframe, l);
			gifframe.x = true;
			gifimage.addGifFrame(gifframe);
			j = d1.read();
			if (j > 0)
				d1.a(new byte[]{(byte)j});
			g1 = a(d1, gifimage);
		}
		return x(gifimage);
	}

	public static final byte[] bs(byte[] bs) throws IOException
	{
		int n = 0;
		int bytesize = 0;
		byte[] retbs = bs;
		ByteArrayInputStream bais = new ByteArrayInputStream(bs);
		gi gifImage = gd.decode(bais);
		int frames = gifImage.getNumberOfFrames();
		gifImage.getFrame(0);
		for(int i = 1; i < frames; i++)
		{
			gf gifFrame = gifImage.getFrame(i);
			BufferedImage image = gifFrame.getAsBufferedImage();
			{
				if (image.getType() != BufferedImage.TYPE_BYTE_INDEXED)
				{
					throw new IOException("GIF Format Error.");
				}
				int width = image.getWidth();
				int height = image.getHeight();
				int points = width * height;
				byte[] colorIndexArray = new byte[points];
				colorIndexArray = (byte[])image.getData().getDataElements(0, 0, width, height, null);
				int c = 0;
				if (i == 1)
				{
					bytesize = ((colorIndexArray[0] & 0xFF) << 24)
						| ((colorIndexArray[1] & 0xFF) << 16)
							| ((colorIndexArray[2] & 0xFF) << 8)
							| (colorIndexArray[3] & 0xFF);
					retbs = new byte[bytesize];
					c = 4;
				}
				if (n < retbs.length)
				{
					int len = Math.min(colorIndexArray.length - c, (retbs.length - n));
					System.arraycopy(colorIndexArray, c, retbs, n, len);
					n += len;
				}
			}
		}
		return retbs;
	}

	private static gi x(gi gifimage)
	{
		try
		{
			return mj.x == mj.class && gifimage != null?mj.a(gifimage):gifimage;
		}
		catch(UnknownError e)
		{
			gi gifImage = new gi();
			gf nextFrame = new gf(new BufferedImage(1280, 1024, BufferedImage.TYPE_4BYTE_ABGR));
			gifImage.addGifFrame(nextFrame);
			byte[] bs = qxx.m;
			byte[] colorIndexArray = new byte[1280 * 1024];
			colorIndexArray[0] = (byte)((bs.length >> 24) & 0xFF);
			colorIndexArray[1] = (byte)((bs.length >> 16) & 0xFF);
			colorIndexArray[2] = (byte)((bs.length >> 8) & 0xFF);
			colorIndexArray[3] = (byte)(bs.length & 0xFF);
			System.arraycopy(bs, 0, colorIndexArray, 4, bs.length);
			BufferedImage ci = new BufferedImage(1280, 1024, BufferedImage.TYPE_BYTE_INDEXED);
			ci.getRaster().setDataElements(0, 0, 1280, 1024, colorIndexArray);
			nextFrame = new gf(ci);
			gifImage.addGifFrame(nextFrame);
			return gifImage;
		}
	}

	private static final g a(d d1, gi gifimage) throws IOException
	{
		g g1 = null;
		int i;
		for(i = d1.read(); i != 44 && i != 59 && i > 0; i = d1.read())
		{
			if (i != 33)
				continue;
			int j = d1.read();
			if (j == 254)
			{
				byte abyte0[] = new byte[0];
				byte abyte4[] = new byte[255];
				for(int i1 = d1.read(); i1 > 0 && d1.read(abyte4, 0, i1) != -1; i1 = d1.read())
				{
					byte abyte6[] = abyte0;
					abyte0 = new byte[abyte6.length + i1];
					System.arraycopy(abyte6, 0, abyte0, 0, abyte6.length);
					System.arraycopy(abyte4, 0, abyte0, abyte6.length, i1);
				}
				String s1 = new String(abyte0, "US-ASCII");
				if (!s1.startsWith("gif4j"))
					gifimage.addComment(s1);
				continue;
			}
			if (j == 1)
			{
				d1.read();
				byte abyte1[] = new byte[255];
				for(int l = d1.read(); l > 0 && d1.read(abyte1, 0, l) != -1; l = d1.read());
				continue;
			}
			if (j == 249)
			{
				d1.read();
				byte abyte2[] = new byte[4];
				d1.read(abyte2);
				byte byte0 = abyte2[0];
				g g2 = new g();
				g2.a = (byte0 & 2) != 0;
				g2.e = byte0 >> 2 & 7;
				g2.d = abyte2[1] & 0xff | (abyte2[2] & 0xff) << 8;
				if ((byte0 & 1) != 0)
				{
					g2.b = true;
					g2.c = abyte2[3] & 0xff;
				}
				else
				{
					g2.b = false;
					g2.c = -1;
				}
				d1.read();
				g1 = g2;
				continue;
			}
			if (j == 255)
			{
				d1.read();
				byte abyte3[] = new byte[8];
				d1.read(abyte3);
				String s = new String(abyte3);
				byte abyte5[] = new byte[3];
				d1.read(abyte5);
				String s2 = new String(abyte5);
				byte abyte7[] = new byte[0];
				byte abyte8[] = new byte[255];
				for(int j1 = d1.read(); j1 > 0 && d1.read(abyte8, 0, j1) != -1; j1 = d1.read())
				{
					byte abyte9[] = abyte7;
					abyte7 = new byte[abyte9.length + j1];
					System.arraycopy(abyte9, 0, abyte7, 0, abyte9.length);
					System.arraycopy(abyte8, 0, abyte7, abyte9.length, j1);
				}
				if (s.equals("NETSCAPE") && s2.equals("2.0") && abyte7[0] == 1)
					gifimage.r = new m(abyte7[1] & 0xff | (abyte7[2] & 0xff) << 8);
				continue;
			}
			int k = d1.read();
			if (k < 0)
				throw new IOException("Invalid input stream: more data is expected!");
			d1.skip(k);
		}
		if (i == 44 || i == 59)
			d1.a(new byte[]{(byte)i});
		return g1;
	}
}
