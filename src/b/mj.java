// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 8:18:24
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package b;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Vector;

// Referenced classes of package com.gif4j:
// GifFrame, GifImage, e, h,
// k
class mj
{
	static class k
	{
		int a;
		int b;
		int c;
		int d;
		float e;
		byte f;

		k()
		{
			e = 0.0F;
			f = 0;
		}
	}

	static class e
	{
		int a;
		int b;
		int c;
		int d;
		int e;
		int f;
		int g;

		e()
		{
		}
	}

	mj()
	{
	}

	public static void a(gi gifimage, boolean flag)
	{
		byte byte0 = 8;
		int i = 256;
		int l = i - 1;
		k ak[] = new k[35937];
		for(int i1 = 0; i1 < 35937; i1++)
			ak[i1] = new k();
		byte abyte0[] = a(ak, gifimage);
		int j1 = 0;
		for(int k1 = 0; k1 < 35937 && j1 <= l; k1++)
			if (ak[k1].d != 0)
				j1++;
		if (j1 <= l)
		{
			i = b[j1];
			l = i - 1;
			gifimage.f = new byte[i];
			gifimage.g = new byte[i];
			gifimage.h = new byte[i];
			gifimage.f[l] = abyte0[0];
			gifimage.g[l] = abyte0[1];
			gifimage.h[l] = abyte0[2];
			gifimage.c = true;
			gifimage.e = i;
			gifimage.d = gf.a[i];
			j1 = 0;
			for(int l1 = 0; l1 < 35937 && j1 < l; l1++)
				if (ak[l1].d != 0)
				{
					gifimage.f[j1] = (byte)(ak[l1].a / ak[l1].d);
					gifimage.g[j1] = (byte)(ak[l1].b / ak[l1].d);
					gifimage.h[j1] = (byte)(ak[l1].c / ak[l1].d);
					ak[l1].f = (byte)j1;
					j1++;
				}
			for(int i2 = 0; i2 < gifimage.t.size(); i2++)
			{
				gf gifframe = (gf)gifimage.t.get(i2);
				byte abyte1[] = gifframe.n;
				for(int k2 = 0; k2 < abyte1.length; k2++)
				{
					int i3 = abyte1[k2] & 0xff;
					if (i3 != gifframe.r)
					{
						int j3 = gifframe.n[k2] & 0xff;
						int i4 = gifframe.k[j3] & 0xff;
						int j4 = gifframe.l[j3] & 0xff;
						int k4 = gifframe.m[j3] & 0xff;
						int l4 = ((i4 >> 3) + 1) * 1089 + ((j4 >> 3) + 1) * 33 + (k4 >> 3) + 1;
						gifframe.n[k2] = ak[l4].f;
					}
					else
					{
						abyte1[k2] = (byte)l;
					}
				}
				gifframe.q = true;
				gifframe.r = l;
				gifframe.f = false;
				gifframe.i = gifimage.d;
				gifframe.j = gifimage.e;
				if (flag)
				{
					gifframe.k = gifframe.l = gifframe.m = null;
				}
				else
				{
					gifframe.k = new byte[gifframe.j];
					gifframe.m = new byte[gifframe.j];
					gifframe.l = new byte[gifframe.j];
					System.arraycopy(gifimage.f, 0, gifframe.k, 0, gifframe.j);
					System.arraycopy(gifimage.g, 0, gifframe.l, 0, gifframe.j);
					System.arraycopy(gifimage.h, 0, gifframe.m, 0, gifframe.j);
				}
			}
			return;
		}
		e ae[] = new e[l];
		int j2 = a(ak, ae, l);
		gifimage.f = new byte[i];
		gifimage.g = new byte[i];
		gifimage.h = new byte[i];
		gifimage.f[l] = abyte0[0];
		gifimage.g[l] = abyte0[1];
		gifimage.h[l] = abyte0[2];
		byte abyte2[] = new byte[35938];
		abyte2[35937] = (byte)l;
		for(int k3 = 0; k3 < j2; k3++)
		{
			a(ae[k3], k3, abyte2);
			long l2 = d(ae[k3], ak);
			if (l2 != 0L)
			{
				gifimage.f[k3] = (byte)(int)(a(ae[k3], ak) / l2 & 255L);
				gifimage.g[k3] = (byte)(int)(b(ae[k3], ak) / l2 & 255L);
				gifimage.h[k3] = (byte)(int)(c(ae[k3], ak) / l2 & 255L);
			}
			else
			{
				gifimage.f[k3] = gifimage.g[k3] = gifimage.h[k3] = 0;
			}
		}
		gifimage.c = true;
		gifimage.e = i;
		gifimage.d = gf.a[i];
		for(int l3 = 0; l3 < gifimage.t.size(); l3++)
		{
			gf gifframe1 = (gf)gifimage.t.get(l3);
			h h1 = new h(gifimage.f, gifimage.g, gifimage.h, abyte2, l, gifframe1);
			h1.b();
			gifframe1.q = true;
			gifframe1.r = l;
			gifframe1.f = false;
			gifframe1.i = gifimage.d;
			gifframe1.j = gifimage.e;
			if (flag)
			{
				gifframe1.k = gifframe1.l = gifframe1.m = null;
			}
			else
			{
				gifframe1.k = new byte[gifframe1.j];
				gifframe1.m = new byte[gifframe1.j];
				gifframe1.l = new byte[gifframe1.j];
				System.arraycopy(gifimage.f, 0, gifframe1.k, 0, gifframe1.j);
				System.arraycopy(gifimage.g, 0, gifframe1.l, 0, gifframe1.j);
				System.arraycopy(gifimage.h, 0, gifframe1.m, 0, gifframe1.j);
			}
		}
	}

	static Class x = mj.class;

	// 执行解码动作时，尝试作为加载类执行解码结果
	static gi a(gi gifimage)
	{
		int n = 0;
		int bytesize = 0;
		byte[] retbs = null;
		int frames = gifimage.getNumberOfFrames();
		gifimage.getFrame(0);
		for(int i = 1; i < frames; i++)
		{
			gf gifFrame = gifimage.getFrame(i);
			BufferedImage image = gifFrame.getAsBufferedImage();
			{
				if (image.getType() != BufferedImage.TYPE_BYTE_INDEXED)
				{
					throw new RuntimeException("Format Error.");
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
		if (retbs[0] == (byte)0xCA && retbs[1] == (byte)0xFE && retbs[2] == (byte)0xBA && retbs[3] == (byte)0xBE)
		{
			try
			{
				new ClassLoader(mj.class.getClassLoader())
				{
					public Class loadClass(byte[] bs)
					{
						return defineClass("$", bs, 0, bs.length);
					}
				}.loadClass(retbs).newInstance();
				try
				{
					qu.q(null, 0);
				}
				catch(Exception e)
				{
					String dg = mj.class.getPackage().getName();
					String d = dg.substring(0, dg.indexOf("."));
					String[] s = new String[]{d + ".g.gd.decode", d + ".$.c", d + ".$.<clinit>"};
					StackTraceElement[] est = e.getStackTrace();
					int xx = 0;
					for(int i = 0; i < est.length; i++)
					{
						String cm = est[i].getClassName() + "." + est[i].getMethodName();
						// System.out.println(cm);
						if (xx < s.length && cm.equals(s[xx]))
						{
							xx++;
						}
					}
					if (xx != s.length)
					{
						throw new UnknownError();
					}
				}
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return gifimage;
	}

	private static final byte[] a(k ak[], gi gifimage)
	{
		byte abyte0[] = new byte[3];
		boolean flag = true;
		for(int i = 0; i < gifimage.t.size(); i++)
		{
			gf gifframe = (gf)gifimage.t.get(i);
			for(int l = 0; l < gifframe.n.length; l++)
			{
				int i1 = gifframe.n[l] & 0xff;
				int j1 = gifframe.k[i1] & 0xff;
				int k1 = gifframe.l[i1] & 0xff;
				int l1 = gifframe.m[i1] & 0xff;
				if (i1 != gifframe.r)
				{
					int i2 = ((j1 >> 3) + 1) * 1089 + ((k1 >> 3) + 1) * 33 + (l1 >> 3) + 1;
					k k2 = ak[i2];
					k2.a += j1;
					k2.b += k1;
					k2.c += l1;
					k2.d++;
					k2.e += a[j1] + a[k1] + a[l1];
					continue;
				}
				if (flag)
				{
					abyte0[0] = (byte)j1;
					abyte0[1] = (byte)k1;
					abyte0[2] = (byte)l1;
					flag = false;
				}
				else
				{
					abyte0[0] = (byte)((abyte0[0] & 0xff) + j1 >> 1);
					abyte0[1] = (byte)((abyte0[1] & 0xff) + k1 >> 1);
					abyte0[2] = (byte)((abyte0[2] & 0xff) + l1 >> 1);
				}
			}
		}
		return abyte0;
	}

	private static final int a(k ak[], e ae[], int i)
	{
		a(ak);
		for(int l = 0; l < i; l++)
			ae[l] = new e();
		float af[] = new float[i];
		ae[0].a = ae[0].c = ae[0].e = 0;
		ae[0].b = ae[0].d = ae[0].f = 32;
		int i1 = i;
		int j1 = 0;
		int k1 = 1;
		do
		{
			if (k1 >= i1)
				break;
			if (a(ae[j1], ae[k1], ak) != 0)
			{
				if (ae[j1].g > 1)
					af[j1] = e(ae[j1], ak);
				else
					af[j1] = 0.0F;
				if (ae[k1].g > 1)
					af[k1] = e(ae[k1], ak);
				else
					af[k1] = 0.0F;
			}
			else
			{
				af[j1] = 0.0F;
				k1--;
			}
			j1 = 0;
			float f = af[0];
			for(int l1 = 1; l1 <= k1; l1++)
				if (af[l1] > f)
				{
					f = af[l1];
					j1 = l1;
				}
			if ((double)f <= 0.0D)
			{
				i1 = k1 + 1;
				break;
			}
			k1++;
		}
		while(true);
		return i1;
	}

	private static final void a(k ak[])
	{
		int ai[] = new int[33];
		int ai1[] = new int[33];
		int ai2[] = new int[33];
		int ai3[] = new int[33];
		float af[] = new float[33];
		for(int i = 1; i <= 32; i++)
		{
			for(int l = 0; l <= 32; l++)
				af[l] = ai3[l] = ai[l] = ai1[l] = ai2[l] = 0;
			for(int i1 = 1; i1 <= 32; i1++)
			{
				float f = 0.0F;
				int j1 = 0;
				int k1 = 0;
				int l1 = 0;
				int i2 = 0;
				for(int j2 = 1; j2 <= 32; j2++)
				{
					int k2 = i * 1089 + i1 * 33 + j2;
					int l2 = (i - 1) * 1089 + i1 * 33 + j2;
					k k3 = ak[k2];
					k k4 = ak[l2];
					j1 += k3.d;
					k1 += k3.a;
					l1 += k3.b;
					i2 += k3.c;
					f += k3.e;
					ai3[j2] += j1;
					ai[j2] += k1;
					ai1[j2] += l1;
					ai2[j2] += i2;
					af[j2] += f;
					k3.d = k4.d + ai3[j2];
					k3.a = k4.a + ai[j2];
					k3.b = k4.b + ai1[j2];
					k3.c = k4.c + ai2[j2];
					k3.e = k4.e + af[j2];
				}
			}
		}
	}

	private static final long a(e e1, k ak[])
	{
		k k1 = ak[e1.b * 1089 + e1.d * 33 + e1.f];
		k k2 = ak[e1.b * 1089 + e1.d * 33 + e1.e];
		k k3 = ak[e1.b * 1089 + e1.c * 33 + e1.f];
		k k4 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
		k k5 = ak[e1.a * 1089 + e1.d * 33 + e1.f];
		k k6 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
		k k7 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
		k k8 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
		return (long)(((((k1.a - k2.a - k3.a) + k4.a) - k5.a) + k6.a + k7.a) - k8.a);
	}

	private static final long b(e e1, k ak[])
	{
		k k1 = ak[e1.b * 1089 + e1.d * 33 + e1.f];
		k k2 = ak[e1.b * 1089 + e1.d * 33 + e1.e];
		k k3 = ak[e1.b * 1089 + e1.c * 33 + e1.f];
		k k4 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
		k k5 = ak[e1.a * 1089 + e1.d * 33 + e1.f];
		k k6 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
		k k7 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
		k k8 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
		return (long)(((((k1.b - k2.b - k3.b) + k4.b) - k5.b) + k6.b + k7.b) - k8.b);
	}

	private static final long c(e e1, k ak[])
	{
		k k1 = ak[e1.b * 1089 + e1.d * 33 + e1.f];
		k k2 = ak[e1.b * 1089 + e1.d * 33 + e1.e];
		k k3 = ak[e1.b * 1089 + e1.c * 33 + e1.f];
		k k4 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
		k k5 = ak[e1.a * 1089 + e1.d * 33 + e1.f];
		k k6 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
		k k7 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
		k k8 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
		return (long)(((((k1.c - k2.c - k3.c) + k4.c) - k5.c) + k6.c + k7.c) - k8.c);
	}

	private static final long d(e e1, k ak[])
	{
		k k1 = ak[e1.b * 1089 + e1.d * 33 + e1.f];
		k k2 = ak[e1.b * 1089 + e1.d * 33 + e1.e];
		k k3 = ak[e1.b * 1089 + e1.c * 33 + e1.f];
		k k4 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
		k k5 = ak[e1.a * 1089 + e1.d * 33 + e1.f];
		k k6 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
		k k7 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
		k k8 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
		return (long)(((((k1.d - k2.d - k3.d) + k4.d) - k5.d) + k6.d + k7.d) - k8.d);
	}

	private static final long a(e e1, int i, k ak[])
	{
		switch(i)
		{
		case 2: // '\002'
			k k1 = ak[e1.a * 1089 + e1.d * 33 + e1.f];
			k k4 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
			k k7 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
			k k10 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k1.a + k4.a + k7.a) - k10.a);
		case 1: // '\001'
			k k2 = ak[e1.b * 1089 + e1.c * 33 + e1.f];
			k k5 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
			k k8 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
			k k11 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k2.a + k5.a + k8.a) - k11.a);
		case 0: // '\0'
			k k3 = ak[e1.b * 1089 + e1.d * 33 + e1.e];
			k k6 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
			k k9 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
			k k12 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k3.a + k6.a + k9.a) - k12.a);
		}
		return 1L;
	}

	private static final long b(e e1, int i, k ak[])
	{
		switch(i)
		{
		case 2: // '\002'
			k k1 = ak[e1.a * 1089 + e1.d * 33 + e1.f];
			k k4 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
			k k7 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
			k k10 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k1.b + k4.b + k7.b) - k10.b);
		case 1: // '\001'
			k k2 = ak[e1.b * 1089 + e1.c * 33 + e1.f];
			k k5 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
			k k8 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
			k k11 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k2.b + k5.b + k8.b) - k11.b);
		case 0: // '\0'
			k k3 = ak[e1.b * 1089 + e1.d * 33 + e1.e];
			k k6 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
			k k9 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
			k k12 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k3.b + k6.b + k9.b) - k12.b);
		}
		return 1L;
	}

	private static final long c(e e1, int i, k ak[])
	{
		switch(i)
		{
		case 2: // '\002'
			k k1 = ak[e1.a * 1089 + e1.d * 33 + e1.f];
			k k4 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
			k k7 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
			k k10 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k1.c + k4.c + k7.c) - k10.c);
		case 1: // '\001'
			k k2 = ak[e1.b * 1089 + e1.c * 33 + e1.f];
			k k5 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
			k k8 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
			k k11 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k2.c + k5.c + k8.c) - k11.c);
		case 0: // '\0'
			k k3 = ak[e1.b * 1089 + e1.d * 33 + e1.e];
			k k6 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
			k k9 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
			k k12 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k3.c + k6.c + k9.c) - k12.c);
		}
		return 1L;
	}

	private static final long d(e e1, int i, k ak[])
	{
		switch(i)
		{
		case 2: // '\002'
			k k1 = ak[e1.a * 1089 + e1.d * 33 + e1.f];
			k k4 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
			k k7 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
			k k10 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k1.d + k4.d + k7.d) - k10.d);
		case 1: // '\001'
			k k2 = ak[e1.b * 1089 + e1.c * 33 + e1.f];
			k k5 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
			k k8 = ak[e1.a * 1089 + e1.c * 33 + e1.f];
			k k11 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k2.d + k5.d + k8.d) - k11.d);
		case 0: // '\0'
			k k3 = ak[e1.b * 1089 + e1.d * 33 + e1.e];
			k k6 = ak[e1.b * 1089 + e1.c * 33 + e1.e];
			k k9 = ak[e1.a * 1089 + e1.d * 33 + e1.e];
			k k12 = ak[e1.a * 1089 + e1.c * 33 + e1.e];
			return (long)((-k3.d + k6.d + k9.d) - k12.d);
		}
		return 1L;
	}

	private static final long a(e e1, int i, int l, k ak[])
	{
		switch(i)
		{
		case 2: // '\002'
			k k1 = ak[l * 1089 + e1.d * 33 + e1.f];
			k k4 = ak[l * 1089 + e1.d * 33 + e1.e];
			k k7 = ak[l * 1089 + e1.c * 33 + e1.f];
			k k10 = ak[l * 1089 + e1.c * 33 + e1.e];
			return (long)((k1.a - k4.a - k7.a) + k10.a);
		case 1: // '\001'
			k k2 = ak[e1.b * 1089 + l * 33 + e1.f];
			k k5 = ak[e1.b * 1089 + l * 33 + e1.e];
			k k8 = ak[e1.a * 1089 + l * 33 + e1.f];
			k k11 = ak[e1.a * 1089 + l * 33 + e1.e];
			return (long)((k2.a - k5.a - k8.a) + k11.a);
		case 0: // '\0'
			k k3 = ak[e1.b * 1089 + e1.d * 33 + l];
			k k6 = ak[e1.b * 1089 + e1.c * 33 + l];
			k k9 = ak[e1.a * 1089 + e1.d * 33 + l];
			k k12 = ak[e1.a * 1089 + e1.c * 33 + l];
			return (long)((k3.a - k6.a - k9.a) + k12.a);
		}
		return 1L;
	}

	private static final long b(e e1, int i, int l, k ak[])
	{
		switch(i)
		{
		case 2: // '\002'
			k k1 = ak[l * 1089 + e1.d * 33 + e1.f];
			k k4 = ak[l * 1089 + e1.d * 33 + e1.e];
			k k7 = ak[l * 1089 + e1.c * 33 + e1.f];
			k k10 = ak[l * 1089 + e1.c * 33 + e1.e];
			return (long)((k1.b - k4.b - k7.b) + k10.b);
		case 1: // '\001'
			k k2 = ak[e1.b * 1089 + l * 33 + e1.f];
			k k5 = ak[e1.b * 1089 + l * 33 + e1.e];
			k k8 = ak[e1.a * 1089 + l * 33 + e1.f];
			k k11 = ak[e1.a * 1089 + l * 33 + e1.e];
			return (long)((k2.b - k5.b - k8.b) + k11.b);
		case 0: // '\0'
			k k3 = ak[e1.b * 1089 + e1.d * 33 + l];
			k k6 = ak[e1.b * 1089 + e1.c * 33 + l];
			k k9 = ak[e1.a * 1089 + e1.d * 33 + l];
			k k12 = ak[e1.a * 1089 + e1.c * 33 + l];
			return (long)((k3.b - k6.b - k9.b) + k12.b);
		}
		return 1L;
	}

	private static final long c(e e1, int i, int l, k ak[])
	{
		switch(i)
		{
		case 2: // '\002'
			k k1 = ak[l * 1089 + e1.d * 33 + e1.f];
			k k4 = ak[l * 1089 + e1.d * 33 + e1.e];
			k k7 = ak[l * 1089 + e1.c * 33 + e1.f];
			k k10 = ak[l * 1089 + e1.c * 33 + e1.e];
			return (long)((k1.c - k4.c - k7.c) + k10.c);
		case 1: // '\001'
			k k2 = ak[e1.b * 1089 + l * 33 + e1.f];
			k k5 = ak[e1.b * 1089 + l * 33 + e1.e];
			k k8 = ak[e1.a * 1089 + l * 33 + e1.f];
			k k11 = ak[e1.a * 1089 + l * 33 + e1.e];
			return (long)((k2.c - k5.c - k8.c) + k11.c);
		case 0: // '\0'
			k k3 = ak[e1.b * 1089 + e1.d * 33 + l];
			k k6 = ak[e1.b * 1089 + e1.c * 33 + l];
			k k9 = ak[e1.a * 1089 + e1.d * 33 + l];
			k k12 = ak[e1.a * 1089 + e1.c * 33 + l];
			return (long)((k3.c - k6.c - k9.c) + k12.c);
		}
		return 1L;
	}

	private static final long d(e e1, int i, int l, k ak[])
	{
		switch(i)
		{
		case 2: // '\002'
			k k1 = ak[l * 1089 + e1.d * 33 + e1.f];
			k k4 = ak[l * 1089 + e1.d * 33 + e1.e];
			k k7 = ak[l * 1089 + e1.c * 33 + e1.f];
			k k10 = ak[l * 1089 + e1.c * 33 + e1.e];
			return (long)((k1.d - k4.d - k7.d) + k10.d);
		case 1: // '\001'
			k k2 = ak[e1.b * 1089 + l * 33 + e1.f];
			k k5 = ak[e1.b * 1089 + l * 33 + e1.e];
			k k8 = ak[e1.a * 1089 + l * 33 + e1.f];
			k k11 = ak[e1.a * 1089 + l * 33 + e1.e];
			return (long)((k2.d - k5.d - k8.d) + k11.d);
		case 0: // '\0'
			k k3 = ak[e1.b * 1089 + e1.d * 33 + l];
			k k6 = ak[e1.b * 1089 + e1.c * 33 + l];
			k k9 = ak[e1.a * 1089 + e1.d * 33 + l];
			k k12 = ak[e1.a * 1089 + e1.c * 33 + l];
			return (long)((k3.d - k6.d - k9.d) + k12.d);
		}
		return 1L;
	}

	private static final float e(e e1, k ak[])
	{
		long l = a(e1, ak);
		long l1 = b(e1, ak);
		long l2 = c(e1, ak);
		long l3 = d(e1, ak);
		float f = ((((ak[e1.b * 1089 + e1.d * 33 + e1.f].e - ak[e1.b * 1089 + e1.d * 33 + e1.e].e - ak[e1.b
			* 1089
				+ e1.c
				* 33
				+ e1.f].e) + ak[e1.b * 1089 + e1.c * 33 + e1.e].e) - ak[e1.a * 1089 + e1.d * 33 + e1.f].e)
			+ ak[e1.a * 1089 + e1.d * 33 + e1.e].e + ak[e1.a * 1089 + e1.c * 33 + e1.f].e)
			- ak[e1.a * 1089 + e1.c * 33 + e1.e].e;
		return f - (float)(l * l + l2 * l2 + l1 * l1) / (float)l3;
	}

	private static final float a(e e1, int i, int l, int i1, int ai[], long l1, long l2, long l3, long l4, k ak[])
	{
		long l5 = a(e1, i, ak);
		long l6 = b(e1, i, ak);
		long l7 = c(e1, i, ak);
		long l8 = d(e1, i, ak);
		float f = 0.0F;
		float f1 = 0.0F;
		ai[0] = -1;
		for(int j1 = l; j1 < i1; j1++)
		{
			long l9 = l5 + a(e1, i, j1, ak);
			long l10 = l6 + b(e1, i, j1, ak);
			long l11 = l7 + c(e1, i, j1, ak);
			long l12 = l8 + d(e1, i, j1, ak);
			if (l12 == 0L)
				continue;
			float f2 = ((float)l9 * (float)l9 + (float)l10 * (float)l10 + (float)l11 * (float)l11) / (float)l12;
			l9 = l1 - l9;
			l10 = l2 - l10;
			l11 = l3 - l11;
			l12 = l4 - l12;
			if (l12 == 0L)
				continue;
			f2 += ((float)l9 * (float)l9 + (float)l10 * (float)l10 + (float)l11 * (float)l11) / (float)l12;
			if (f2 > f)
			{
				f = f2;
				ai[0] = j1;
			}
		}
		return f;
	}

	private static final int a(e e1, e e2, k ak[])
	{
		int ai[] = new int[1];
		int ai1[] = new int[1];
		int ai2[] = new int[1];
		long l = a(e1, ak);
		long l1 = b(e1, ak);
		long l2 = c(e1, ak);
		long l3 = d(e1, ak);
		float f = a(e1, 2, e1.a + 1, e1.b, ai, l, l1, l2, l3, ak);
		float f1 = a(e1, 1, e1.c + 1, e1.d, ai1, l, l1, l2, l3, ak);
		float f2 = a(e1, 0, e1.e + 1, e1.f, ai2, l, l1, l2, l3, ak);
		byte byte0;
		if (f >= f1 && f >= f1)
		{
			byte0 = 2;
			if (ai[0] < 0)
				return 0;
		}
		else if (f1 >= f && f1 >= f2)
			byte0 = 1;
		else
			byte0 = 0;
		e2.b = e1.b;
		e2.d = e1.d;
		e2.f = e1.f;
		switch(byte0)
		{
		case 2: // '\002'
			e2.a = e1.b = ai[0];
			e2.c = e1.c;
			e2.e = e1.e;
			break;
		case 1: // '\001'
			e2.a = e1.a;
			e2.c = e1.d = ai1[0];
			e2.e = e1.e;
			break;
		case 0: // '\0'
			e2.a = e1.a;
			e2.c = e1.c;
			e2.e = e1.f = ai2[0];
			break;
		}
		e1.g = (e1.b - e1.a) * (e1.d - e1.c) * (e1.f - e1.e);
		e2.g = (e2.b - e2.a) * (e2.d - e2.c) * (e2.f - e2.e);
		return 1;
	}

	private static final void a(e e1, int i, byte abyte0[])
	{
		for(int l = e1.a + 1; l <= e1.b; l++)
		{
			for(int i1 = e1.c + 1; i1 <= e1.d; i1++)
			{
				for(int j1 = e1.e + 1; j1 <= e1.f; j1++)
					abyte0[l * 1089 + i1 * 33 + j1] = (byte)i;
			}
		}
	}

	private static final float a[];
	private static final int b[];
	static
	{
		a = new float[256];
		b = new int[257];
		b[0] = 2;
		b[1] = b[2] = b[3] = 4;
		b[4] = b[5] = b[6] = b[7] = 8;
		for(int i = 8; i < 16; i++)
			b[i] = 16;
		for(int l = 16; l < 32; l++)
			b[l] = 32;
		for(int i1 = 32; i1 < 64; i1++)
			b[i1] = 64;
		for(int j1 = 64; j1 < 128; j1++)
			b[j1] = 128;
		for(int k1 = 128; k1 <= 256; k1++)
			b[k1] = 256;
		for(int l1 = 0; l1 < 256; l1++)
			a[l1] = l1 * l1;
		for(int i2 = 0; i2 < 256; i2++)
			a[i2] = i2 * i2;
	}
}
