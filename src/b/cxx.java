// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 16:44:20
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package b;

import java.awt.image.*;

// Referenced classes of package com.gif4j.quantizer:
// d, f, g, j,
// k, m, n, o,
// p, t, u, w
class cxx
{
	static class t
	{
		static final int a()
		{
			return 1;
		}

		t()
		{
		}
	}

	static class j
	{
		static final int a()
		{
			t.a();
			return 1;
		}

		j()
		{
		}
	}

	static class m
	{
		static final int a(g g1, g g2, u au[])
		{
			int ai[] = new int[1];
			int ai1[] = new int[1];
			int ai2[] = new int[1];
			long l = cxx.a(g1, au);
			long l1 = cxx.b(g1, au);
			long l2 = cxx.c(g1, au);
			long l3 = cxx.d(g1, au);
			float f1 = cxx.a(g1, 2, g1.a + 1, g1.b, ai, l, l1, l2, l3, au);
			float f2 = cxx.a(g1, 1, g1.c + 1, g1.d, ai1, l, l1, l2, l3, au);
			float f3 = cxx.a(g1, 0, g1.e + 1, g1.f, ai2, l, l1, l2, l3, au);
			byte byte0;
			if (f1 >= f2 && f1 >= f2)
			{
				byte0 = 2;
				if (ai[0] < 0)
					return 0;
			}
			else if (f2 >= f1 && f2 >= f3)
				byte0 = 1;
			else
				byte0 = 0;
			g2.b = g1.b;
			g2.d = g1.d;
			g2.f = g1.f;
			switch(byte0)
			{
			case 2: // '\002'
				g2.a = g1.b = ai[0];
				g2.c = g1.c;
				g2.e = g1.e;
				break;
			case 1: // '\001'
				g2.a = g1.a;
				g2.c = g1.d = ai1[0];
				g2.e = g1.e;
				break;
			case 0: // '\0'
				g2.a = g1.a;
				g2.c = g1.c;
				g2.e = g1.f = ai2[0];
				break;
			}
			g1.g = (g1.b - g1.a) * (g1.d - g1.c) * (g1.f - g1.e);
			g2.g = (g2.b - g2.a) * (g2.d - g2.c) * (g2.f - g2.e);
			j.a();
			return 1;
		}

		m()
		{
		}
	}

	static class n
	{
		static final int a(g g1, g g2, u au[])
		{
			int ai[] = new int[1];
			int ai1[] = new int[1];
			int ai2[] = new int[1];
			long l = cxx.a(g1, au);
			long l1 = cxx.b(g1, au);
			long l2 = cxx.c(g1, au);
			long l3 = cxx.d(g1, au);
			float f1 = cxx.a(g1, 2, g1.a + 1, g1.b, ai, l, l1, l2, l3, au);
			float f2 = cxx.a(g1, 1, g1.c + 1, g1.d, ai1, l, l1, l2, l3, au);
			float f3 = cxx.a(g1, 0, g1.e + 1, g1.f, ai2, l, l1, l2, l3, au);
			byte byte0;
			if (f1 >= f2 && f1 >= f2)
			{
				byte0 = 2;
				if (ai[0] < 0)
					return 0;
			}
			else if (f2 >= f1 && f2 >= f3)
				byte0 = 1;
			else
				byte0 = 0;
			g2.b = g1.b;
			g2.d = g1.d;
			g2.f = g1.f;
			switch(byte0)
			{
			case 2: // '\002'
				g2.a = g1.b = ai[0];
				g2.c = g1.c;
				g2.e = g1.e;
				break;
			case 1: // '\001'
				g2.a = g1.a;
				g2.c = g1.d = ai1[0];
				g2.e = g1.e;
				break;
			case 0: // '\0'
				g2.a = g1.a;
				g2.c = g1.c;
				g2.e = g1.f = ai2[0];
				break;
			}
			g1.g = (g1.b - g1.a) * (g1.d - g1.c) * (g1.f - g1.e);
			g2.g = (g2.b - g2.a) * (g2.d - g2.c) * (g2.f - g2.e);
			m.a(null, null, null);
			return 1;
		}

		n()
		{
		}
	}

	static class f
	{
		static final int a(g g1, g g2, u au[])
		{
			int ai[] = new int[1];
			int ai1[] = new int[1];
			int ai2[] = new int[1];
			long l = cxx.a(g1, au);
			long l1 = cxx.b(g1, au);
			long l2 = cxx.c(g1, au);
			long l3 = cxx.d(g1, au);
			float f1 = cxx.a(g1, 2, g1.a + 1, g1.b, ai, l, l1, l2, l3, au);
			float f2 = cxx.a(g1, 1, g1.c + 1, g1.d, ai1, l, l1, l2, l3, au);
			float f3 = cxx.a(g1, 0, g1.e + 1, g1.f, ai2, l, l1, l2, l3, au);
			byte byte0;
			if (f1 >= f2 && f1 >= f2)
			{
				byte0 = 2;
				if (ai[0] < 0)
					return 0;
			}
			else if (f2 >= f1 && f2 >= f3)
				byte0 = 1;
			else
				byte0 = 0;
			g2.b = g1.b;
			g2.d = g1.d;
			g2.f = g1.f;
			switch(byte0)
			{
			case 2: // '\002'
				g2.a = g1.b = ai[0];
				g2.c = g1.c;
				g2.e = g1.e;
				break;
			case 1: // '\001'
				g2.a = g1.a;
				g2.c = g1.d = ai1[0];
				g2.e = g1.e;
				break;
			case 0: // '\0'
				g2.a = g1.a;
				g2.c = g1.c;
				g2.e = g1.f = ai2[0];
				break;
			}
			g1.g = (g1.b - g1.a) * (g1.d - g1.c) * (g1.f - g1.e);
			g2.g = (g2.b - g2.a) * (g2.d - g2.c) * (g2.f - g2.e);
			n.a(null, null, null);
			return 1;
		}

		f()
		{
		}
	}

	static class p
	{
		static final int a(g g1, g g2, u au[])
		{
			int ai[] = new int[1];
			int ai1[] = new int[1];
			int ai2[] = new int[1];
			long l = cxx.a(g1, au);
			long l1 = cxx.b(g1, au);
			long l2 = cxx.c(g1, au);
			long l3 = cxx.d(g1, au);
			float f1 = cxx.a(g1, 2, g1.a + 1, g1.b, ai, l, l1, l2, l3, au);
			float f2 = cxx.a(g1, 1, g1.c + 1, g1.d, ai1, l, l1, l2, l3, au);
			float f3 = cxx.a(g1, 0, g1.e + 1, g1.f, ai2, l, l1, l2, l3, au);
			byte byte0;
			if (f1 >= f2 && f1 >= f2)
			{
				byte0 = 2;
				if (ai[0] < 0)
					return 0;
			}
			else if (f2 >= f1 && f2 >= f3)
				byte0 = 1;
			else
				byte0 = 0;
			g2.b = g1.b;
			g2.d = g1.d;
			g2.f = g1.f;
			switch(byte0)
			{
			case 2: // '\002'
				g2.a = g1.b = ai[0];
				g2.c = g1.c;
				g2.e = g1.e;
				break;
			case 1: // '\001'
				g2.a = g1.a;
				g2.c = g1.d = ai1[0];
				g2.e = g1.e;
				break;
			case 0: // '\0'
				g2.a = g1.a;
				g2.c = g1.c;
				g2.e = g1.f = ai2[0];
				break;
			}
			g1.g = (g1.b - g1.a) * (g1.d - g1.c) * (g1.f - g1.e);
			g2.g = (g2.b - g2.a) * (g2.d - g2.c) * (g2.f - g2.e);
			f.a(null, null, null);
			return 1;
		}

		p()
		{
		}
	}

	static class u
	{
		int a;
		int b;
		int c;
		int d;
		float e;
		byte f;

		u()
		{
			e = 0.0F;
			f = 0;
		}
	}

	static class g
	{
		int a;
		int b;
		int c;
		int d;
		int e;
		int f;
		int g;

		g()
		{
		}
	}

	cxx()
	{
	}

	public static BufferedImage a(BufferedImage bufferedimage, int l, boolean flag, boolean flag1, boolean flag2)
	{
		char c1 = flag2?'\u0100':'\b';
		if (l <= c1)
			return a(bufferedimage, l, flag2);
		else
			return b(bufferedimage, l, flag2);
	}

	public static BufferedImage a(BufferedImage bufferedimage, int l, boolean flag, boolean flag1)
	{
		return a(bufferedimage, l, flag, flag1, false);
	}

	private static final BufferedImage a(int ai[], int l, int i1, int j1, byte abyte0[], int k1)
	{
		int l1 = 1 << j1;
		kxx k2 = new kxx(l1);
		int i2 = 0;
		for(int j2 = 0; j2 < ai.length && i2 <= l1; j2++)
		{
			int l2 = ai[j2] >> 24 & 0xff;
			if (l2 > k1 && k2.c(ai[j2]))
				i2++;
		}
		if (i2 >= l1)
			return null;
		i2 = b[i2];
		byte abyte1[] = new byte[i2];
		byte abyte2[] = new byte[i2];
		byte abyte3[] = new byte[i2];
		abyte1[i2 - 1] = abyte0[0];
		abyte2[i2 - 1] = abyte0[1];
		abyte3[i2 - 1] = abyte0[2];
		int ai1[] = (int[])k2.a();
		dxx d1 = new dxx(i2);
		int i3 = 0;
		boolean aflag[] = k2.g;
		for(int j3 = 0; j3 < ai1.length; j3++)
			if (aflag[j3])
			{
				int k3 = ai1[j3];
				abyte1[i3] = (byte)(k3 >> 16 & 0xff);
				abyte2[i3] = (byte)(k3 >> 8 & 0xff);
				abyte3[i3] = (byte)(k3 & 0xff);
				d1.b(k3, i3);
				i3++;
			}
		IndexColorModel indexcolormodel = new IndexColorModel(j1, i2, abyte1, abyte2, abyte3, i2 - 1);
		DataBufferByte databufferbyte = new DataBufferByte(ai.length);
		byte abyte4[] = databufferbyte.getData();
		for(int l3 = 0; l3 < ai.length; l3++)
		{
			int i4 = ai[l3] >> 24 & 0xff;
			if (i4 > k1)
				abyte4[l3] = (byte)d1.c(ai[l3]);
			else
				abyte4[l3] = (byte)(i2 - 1);
		}
		WritableRaster writableraster = Raster.createInterleavedRaster(databufferbyte, l, i1, l, 1, new int[]{0}, null);
		return new BufferedImage(indexcolormodel, writableraster, false, null);
	}

	private static final BufferedImage a(BufferedImage bufferedimage, int l, boolean flag)
	{
		byte byte0 = ((byte)(bufferedimage.getColorModel().hasAlpha()?126:-1));
		int i1 = bufferedimage.getWidth();
		int j1 = bufferedimage.getHeight();
		int k1 = l;
		if (!flag)
			k1 = 1 << l;
		else
			l = oxx.a(k1);
		int l1 = k1 - 1;
		int ai[] = oxx.a(bufferedimage);
		u au[] = new u[4913];
		for(int i2 = 0; i2 < 4913; i2++)
			au[i2] = new u();
		byte abyte0[] = a(au, ai, byte0);
		int j2 = 0;
		for(int k2 = 0; k2 < 4913 && j2 <= l1; k2++)
			if (au[k2].d != 0)
				j2++;
		if (j2 <= l1)
		{
			if (j2 == 1 && abyte0[0] == 0 && abyte0[1] == 0 && abyte0[2] == 0)
				abyte0[0] = abyte0[1] = abyte0[2] = -1;
			BufferedImage bufferedimage1 = a(ai, i1, j1, l, abyte0, byte0);
			if (bufferedimage1 != null)
				return bufferedimage1;
			k1 = b[j2];
			l1 = k1 - 1;
			byte abyte2[] = new byte[k1];
			byte abyte4[] = new byte[k1];
			byte abyte6[] = new byte[k1];
			abyte2[l1] = abyte0[0];
			abyte4[l1] = abyte0[1];
			abyte6[l1] = abyte0[2];
			j2 = 0;
			for(int l2 = 0; l2 < 4913 && j2 < l1; l2++)
				if (au[l2].d != 0)
				{
					abyte2[j2] = (byte)(au[l2].a / au[l2].d);
					abyte4[j2] = (byte)(au[l2].b / au[l2].d);
					abyte6[j2] = (byte)(au[l2].c / au[l2].d);
					au[l2].f = (byte)j2;
					j2++;
				}
			IndexColorModel indexcolormodel = new IndexColorModel(l, k1, abyte2, abyte4, abyte6, l1);
			DataBufferByte databufferbyte = new DataBufferByte(ai.length);
			byte abyte7[] = databufferbyte.getData();
			for(int j3 = 0; j3 < ai.length; j3++)
			{
				int i4 = ai[j3] >> 24 & 0xff;
				if (i4 > byte0)
				{
					int j4 = ai[j3] >> 16 & 0xff;
					int k4 = ai[j3] >> 8 & 0xff;
					int l4 = ai[j3] & 0xff;
					int j5 = ((j4 >> 4) + 1) * 289 + ((k4 >> 4) + 1) * 17 + (l4 >> 4) + 1;
					abyte7[j3] = au[j5].f;
				}
				else
				{
					abyte7[j3] = (byte)l1;
				}
			}
			WritableRaster writableraster = Raster.createInterleavedRaster(databufferbyte,
				i1,
				j1,
				i1,
				1,
				new int[]{0},
				null);
			return new BufferedImage(indexcolormodel, writableraster, false, null);
		}
		byte abyte1[] = new byte[k1];
		byte abyte3[] = new byte[k1];
		byte abyte5[] = new byte[k1];
		abyte1[l1] = abyte0[0];
		abyte3[l1] = abyte0[1];
		abyte5[l1] = abyte0[2];
		g ag[] = new g[l1];
		int i3 = a(au, ag, l1);
		for(int k3 = 0; k3 < i3; k3++)
		{
			long l3 = h(ag[k3], au);
			if (l3 != 0L)
			{
				abyte1[k3] = (byte)(int)(e(ag[k3], au) / l3 & 255L);
				abyte3[k3] = (byte)(int)(f(ag[k3], au) / l3 & 255L);
				abyte5[k3] = (byte)(int)(g(ag[k3], au) / l3 & 255L);
			}
			else
			{
				abyte1[k3] = abyte3[k3] = abyte5[k3] = 0;
			}
		}
		IndexColorModel indexcolormodel1 = new IndexColorModel(l, k1, abyte1, abyte3, abyte5, l1);
		DataBufferByte databufferbyte1 = new DataBufferByte(ai.length);
		byte abyte8[] = databufferbyte1.getData();
		wxx w1 = new wxx(abyte1, abyte3, abyte5, l1);
		for(int i5 = 0; i5 < ai.length; i5++)
			if ((ai[i5] >> 24 & 0xff) > byte0)
				abyte8[i5] = w1.a(ai[i5]);
			else
				abyte8[i5] = (byte)l1;
		WritableRaster writableraster1 = Raster.createInterleavedRaster(databufferbyte1,
			i1,
			j1,
			i1,
			1,
			new int[]{0},
			null);
		return new BufferedImage(indexcolormodel1, writableraster1, false, null);
	}

	private static final BufferedImage b(BufferedImage bufferedimage, int l, boolean flag)
	{
		int i1 = bufferedimage.getWidth();
		int j1 = bufferedimage.getHeight();
		int k1 = l;
		if (!flag)
			k1 = 1 << l;
		int ai[] = oxx.a(bufferedimage);
		u au[] = new u[4913];
		for(int l1 = 0; l1 < 4913; l1++)
			au[l1] = new u();
		a(au, ai);
		g ag[] = new g[k1];
		int i2 = a(au, ag, k1);
		byte abyte0[] = new byte[k1];
		byte abyte1[] = new byte[k1];
		byte abyte2[] = new byte[k1];
		if (i1 < -2)
			p.a(ag[0], ag[1], au);
		int ai1[] = new int[4913];
		for(int j2 = 0; j2 < i2; j2++)
		{
			a(ag[j2], j2, ai1);
			long l2 = h(ag[j2], au);
			if (l2 != 0L)
			{
				abyte0[j2] = (byte)(int)(e(ag[j2], au) / l2 & 255L);
				abyte1[j2] = (byte)(int)(f(ag[j2], au) / l2 & 255L);
				abyte2[j2] = (byte)(int)(g(ag[j2], au) / l2 & 255L);
			}
			else
			{
				abyte0[j2] = abyte1[j2] = abyte2[j2] = 0;
			}
		}
		byte byte0 = 2;
		BufferedImage bufferedimage1 = new BufferedImage(i1, j1, byte0);
		int ai2[] = ((DataBufferInt)bufferedimage1.getRaster().getDataBuffer()).getData();
		for(int k2 = 0; k2 < ai.length; k2++)
		{
			int i3 = ai[k2] >> 16 & 0xff;
			int j3 = ai[k2] >> 8 & 0xff;
			int k3 = ai[k2] & 0xff;
			int l3 = ai1[((i3 >> 4) + 1) * 289 + ((j3 >> 4) + 1) * 17 + (k3 >> 4) + 1];
			ai2[k2] = ai[k2] & 0xff000000 | (abyte0[l3] & 0xff) << 16 | (abyte1[l3] & 0xff) << 8 | abyte2[l3] & 0xff;
		}
		return bufferedimage1;
	}

	private static final int a(u au[], g ag[], int l)
	{
		a(au);
		for(int i1 = 0; i1 < l; i1++)
			ag[i1] = new g();
		float af[] = new float[l];
		ag[0].a = ag[0].c = ag[0].e = 0;
		ag[0].b = ag[0].d = ag[0].f = 16;
		int j1 = l;
		int k1 = 0;
		int l1 = 1;
		do
		{
			if (l1 >= j1)
				break;
			if (a(ag[k1], ag[l1], au) != 0)
			{
				if (ag[k1].g > 1)
					af[k1] = i(ag[k1], au);
				else
					af[k1] = 0.0F;
				if (ag[l1].g > 1)
					af[l1] = i(ag[l1], au);
				else
					af[l1] = 0.0F;
			}
			else
			{
				af[k1] = 0.0F;
				l1--;
			}
			k1 = 0;
			float f1 = af[0];
			for(int i2 = 1; i2 <= l1; i2++)
				if (af[i2] > f1)
				{
					f1 = af[i2];
					k1 = i2;
				}
			if ((double)f1 <= 0.0D)
			{
				j1 = l1 + 1;
				break;
			}
			l1++;
		}
		while(true);
		return j1;
	}

	private static final byte[] a(u au[], int ai[], int l)
	{
		byte abyte0[] = new byte[3];
		boolean flag = true;
		for(int i1 = 0; i1 < ai.length; i1++)
		{
			int j1 = ai[i1] >> 24 & 0xff;
			int k1 = ai[i1] >> 16 & 0xff;
			int l1 = ai[i1] >> 8 & 0xff;
			int i2 = ai[i1] & 0xff;
			if (j1 > l)
			{
				int j2 = ((k1 >> 4) + 1) * 289 + ((l1 >> 4) + 1) * 17 + (i2 >> 4) + 1;
				u u1 = au[j2];
				u1.a += k1;
				u1.b += l1;
				u1.c += i2;
				u1.d++;
				u1.e += a[k1] + a[l1] + a[i2];
				continue;
			}
			if (flag)
			{
				abyte0[0] = (byte)k1;
				abyte0[1] = (byte)l1;
				abyte0[2] = (byte)i2;
				flag = false;
			}
			else
			{
				abyte0[0] = (byte)((abyte0[0] & 0xff) + k1 >> 1);
				abyte0[1] = (byte)((abyte0[1] & 0xff) + l1 >> 1);
				abyte0[2] = (byte)((abyte0[2] & 0xff) + i2 >> 1);
			}
		}
		return abyte0;
	}

	private static final void a(u au[], int ai[])
	{
		for(int l = 0; l < ai.length; l++)
		{
			int i1 = ai[l] >> 16 & 0xff;
			int j1 = ai[l] >> 8 & 0xff;
			int k1 = ai[l] & 0xff;
			int l1 = ((i1 >> 4) + 1) * 289 + ((j1 >> 4) + 1) * 17 + (k1 >> 4) + 1;
			u u1 = au[l1];
			u1.a += i1;
			u1.b += j1;
			u1.c += k1;
			u1.d++;
			u1.e += a[i1] + a[j1] + a[k1];
		}
	}

	private static final void a(u au[])
	{
		int ai[] = new int[17];
		int ai1[] = new int[17];
		int ai2[] = new int[17];
		int ai3[] = new int[17];
		float af[] = new float[17];
		for(int l = 1; l <= 16; l++)
		{
			for(int i1 = 0; i1 <= 16; i1++)
				af[i1] = ai3[i1] = ai[i1] = ai1[i1] = ai2[i1] = 0;
			for(int j1 = 1; j1 <= 16; j1++)
			{
				float f1 = 0.0F;
				int k1 = 0;
				int l1 = 0;
				int i2 = 0;
				int j2 = 0;
				for(int k2 = 1; k2 <= 16; k2++)
				{
					int l2 = l * 289 + j1 * 17 + k2;
					int i3 = (l - 1) * 289 + j1 * 17 + k2;
					u u1 = au[l2];
					u u2 = au[i3];
					k1 += u1.d;
					l1 += u1.a;
					i2 += u1.b;
					j2 += u1.c;
					f1 += u1.e;
					ai3[k2] += k1;
					ai[k2] += l1;
					ai1[k2] += i2;
					ai2[k2] += j2;
					af[k2] += f1;
					u1.d = u2.d + ai3[k2];
					u1.a = u2.a + ai[k2];
					u1.b = u2.b + ai1[k2];
					u1.c = u2.c + ai2[k2];
					u1.e = u2.e + af[k2];
				}
			}
		}
	}

	private static final long e(g g1, u au[])
	{
		u u1 = au[g1.b * 289 + g1.d * 17 + g1.f];
		u u2 = au[g1.b * 289 + g1.d * 17 + g1.e];
		u u3 = au[g1.b * 289 + g1.c * 17 + g1.f];
		u u4 = au[g1.b * 289 + g1.c * 17 + g1.e];
		u u5 = au[g1.a * 289 + g1.d * 17 + g1.f];
		u u6 = au[g1.a * 289 + g1.d * 17 + g1.e];
		u u7 = au[g1.a * 289 + g1.c * 17 + g1.f];
		u u8 = au[g1.a * 289 + g1.c * 17 + g1.e];
		return (long)(((((u1.a - u2.a - u3.a) + u4.a) - u5.a) + u6.a + u7.a) - u8.a);
	}

	private static final long f(g g1, u au[])
	{
		u u1 = au[g1.b * 289 + g1.d * 17 + g1.f];
		u u2 = au[g1.b * 289 + g1.d * 17 + g1.e];
		u u3 = au[g1.b * 289 + g1.c * 17 + g1.f];
		u u4 = au[g1.b * 289 + g1.c * 17 + g1.e];
		u u5 = au[g1.a * 289 + g1.d * 17 + g1.f];
		u u6 = au[g1.a * 289 + g1.d * 17 + g1.e];
		u u7 = au[g1.a * 289 + g1.c * 17 + g1.f];
		u u8 = au[g1.a * 289 + g1.c * 17 + g1.e];
		return (long)(((((u1.b - u2.b - u3.b) + u4.b) - u5.b) + u6.b + u7.b) - u8.b);
	}

	private static final long g(g g1, u au[])
	{
		u u1 = au[g1.b * 289 + g1.d * 17 + g1.f];
		u u2 = au[g1.b * 289 + g1.d * 17 + g1.e];
		u u3 = au[g1.b * 289 + g1.c * 17 + g1.f];
		u u4 = au[g1.b * 289 + g1.c * 17 + g1.e];
		u u5 = au[g1.a * 289 + g1.d * 17 + g1.f];
		u u6 = au[g1.a * 289 + g1.d * 17 + g1.e];
		u u7 = au[g1.a * 289 + g1.c * 17 + g1.f];
		u u8 = au[g1.a * 289 + g1.c * 17 + g1.e];
		return (long)(((((u1.c - u2.c - u3.c) + u4.c) - u5.c) + u6.c + u7.c) - u8.c);
	}

	private static final long h(g g1, u au[])
	{
		u u1 = au[g1.b * 289 + g1.d * 17 + g1.f];
		u u2 = au[g1.b * 289 + g1.d * 17 + g1.e];
		u u3 = au[g1.b * 289 + g1.c * 17 + g1.f];
		u u4 = au[g1.b * 289 + g1.c * 17 + g1.e];
		u u5 = au[g1.a * 289 + g1.d * 17 + g1.f];
		u u6 = au[g1.a * 289 + g1.d * 17 + g1.e];
		u u7 = au[g1.a * 289 + g1.c * 17 + g1.f];
		u u8 = au[g1.a * 289 + g1.c * 17 + g1.e];
		return (long)(((((u1.d - u2.d - u3.d) + u4.d) - u5.d) + u6.d + u7.d) - u8.d);
	}

	private static final long a(g g1, int l, u au[])
	{
		switch(l)
		{
		case 2: // '\002'
			u u1 = au[g1.a * 289 + g1.d * 17 + g1.f];
			u u4 = au[g1.a * 289 + g1.d * 17 + g1.e];
			u u7 = au[g1.a * 289 + g1.c * 17 + g1.f];
			u u10 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u1.a + u4.a + u7.a) - u10.a);
		case 1: // '\001'
			u u2 = au[g1.b * 289 + g1.c * 17 + g1.f];
			u u5 = au[g1.b * 289 + g1.c * 17 + g1.e];
			u u8 = au[g1.a * 289 + g1.c * 17 + g1.f];
			u u11 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u2.a + u5.a + u8.a) - u11.a);
		case 0: // '\0'
			u u3 = au[g1.b * 289 + g1.d * 17 + g1.e];
			u u6 = au[g1.b * 289 + g1.c * 17 + g1.e];
			u u9 = au[g1.a * 289 + g1.d * 17 + g1.e];
			u u12 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u3.a + u6.a + u9.a) - u12.a);
		}
		return 1L;
	}

	private static final long b(g g1, int l, u au[])
	{
		switch(l)
		{
		case 2: // '\002'
			u u1 = au[g1.a * 289 + g1.d * 17 + g1.f];
			u u4 = au[g1.a * 289 + g1.d * 17 + g1.e];
			u u7 = au[g1.a * 289 + g1.c * 17 + g1.f];
			u u10 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u1.b + u4.b + u7.b) - u10.b);
		case 1: // '\001'
			u u2 = au[g1.b * 289 + g1.c * 17 + g1.f];
			u u5 = au[g1.b * 289 + g1.c * 17 + g1.e];
			u u8 = au[g1.a * 289 + g1.c * 17 + g1.f];
			u u11 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u2.b + u5.b + u8.b) - u11.b);
		case 0: // '\0'
			u u3 = au[g1.b * 289 + g1.d * 17 + g1.e];
			u u6 = au[g1.b * 289 + g1.c * 17 + g1.e];
			u u9 = au[g1.a * 289 + g1.d * 17 + g1.e];
			u u12 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u3.b + u6.b + u9.b) - u12.b);
		}
		return 1L;
	}

	private static final long c(g g1, int l, u au[])
	{
		switch(l)
		{
		case 2: // '\002'
			u u1 = au[g1.a * 289 + g1.d * 17 + g1.f];
			u u4 = au[g1.a * 289 + g1.d * 17 + g1.e];
			u u7 = au[g1.a * 289 + g1.c * 17 + g1.f];
			u u10 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u1.c + u4.c + u7.c) - u10.c);
		case 1: // '\001'
			u u2 = au[g1.b * 289 + g1.c * 17 + g1.f];
			u u5 = au[g1.b * 289 + g1.c * 17 + g1.e];
			u u8 = au[g1.a * 289 + g1.c * 17 + g1.f];
			u u11 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u2.c + u5.c + u8.c) - u11.c);
		case 0: // '\0'
			u u3 = au[g1.b * 289 + g1.d * 17 + g1.e];
			u u6 = au[g1.b * 289 + g1.c * 17 + g1.e];
			u u9 = au[g1.a * 289 + g1.d * 17 + g1.e];
			u u12 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u3.c + u6.c + u9.c) - u12.c);
		}
		return 1L;
	}

	private static final long d(g g1, int l, u au[])
	{
		switch(l)
		{
		case 2: // '\002'
			u u1 = au[g1.a * 289 + g1.d * 17 + g1.f];
			u u4 = au[g1.a * 289 + g1.d * 17 + g1.e];
			u u7 = au[g1.a * 289 + g1.c * 17 + g1.f];
			u u10 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u1.d + u4.d + u7.d) - u10.d);
		case 1: // '\001'
			u u2 = au[g1.b * 289 + g1.c * 17 + g1.f];
			u u5 = au[g1.b * 289 + g1.c * 17 + g1.e];
			u u8 = au[g1.a * 289 + g1.c * 17 + g1.f];
			u u11 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u2.d + u5.d + u8.d) - u11.d);
		case 0: // '\0'
			u u3 = au[g1.b * 289 + g1.d * 17 + g1.e];
			u u6 = au[g1.b * 289 + g1.c * 17 + g1.e];
			u u9 = au[g1.a * 289 + g1.d * 17 + g1.e];
			u u12 = au[g1.a * 289 + g1.c * 17 + g1.e];
			return (long)((-u3.d + u6.d + u9.d) - u12.d);
		}
		return 1L;
	}

	private static final long a(g g1, int l, int i1, u au[])
	{
		switch(l)
		{
		case 2: // '\002'
			u u1 = au[i1 * 289 + g1.d * 17 + g1.f];
			u u4 = au[i1 * 289 + g1.d * 17 + g1.e];
			u u7 = au[i1 * 289 + g1.c * 17 + g1.f];
			u u10 = au[i1 * 289 + g1.c * 17 + g1.e];
			return (long)((u1.a - u4.a - u7.a) + u10.a);
		case 1: // '\001'
			u u2 = au[g1.b * 289 + i1 * 17 + g1.f];
			u u5 = au[g1.b * 289 + i1 * 17 + g1.e];
			u u8 = au[g1.a * 289 + i1 * 17 + g1.f];
			u u11 = au[g1.a * 289 + i1 * 17 + g1.e];
			return (long)((u2.a - u5.a - u8.a) + u11.a);
		case 0: // '\0'
			u u3 = au[g1.b * 289 + g1.d * 17 + i1];
			u u6 = au[g1.b * 289 + g1.c * 17 + i1];
			u u9 = au[g1.a * 289 + g1.d * 17 + i1];
			u u12 = au[g1.a * 289 + g1.c * 17 + i1];
			return (long)((u3.a - u6.a - u9.a) + u12.a);
		}
		return 1L;
	}

	private static final long b(g g1, int l, int i1, u au[])
	{
		switch(l)
		{
		case 2: // '\002'
			u u1 = au[i1 * 289 + g1.d * 17 + g1.f];
			u u4 = au[i1 * 289 + g1.d * 17 + g1.e];
			u u7 = au[i1 * 289 + g1.c * 17 + g1.f];
			u u10 = au[i1 * 289 + g1.c * 17 + g1.e];
			return (long)((u1.b - u4.b - u7.b) + u10.b);
		case 1: // '\001'
			u u2 = au[g1.b * 289 + i1 * 17 + g1.f];
			u u5 = au[g1.b * 289 + i1 * 17 + g1.e];
			u u8 = au[g1.a * 289 + i1 * 17 + g1.f];
			u u11 = au[g1.a * 289 + i1 * 17 + g1.e];
			return (long)((u2.b - u5.b - u8.b) + u11.b);
		case 0: // '\0'
			u u3 = au[g1.b * 289 + g1.d * 17 + i1];
			u u6 = au[g1.b * 289 + g1.c * 17 + i1];
			u u9 = au[g1.a * 289 + g1.d * 17 + i1];
			u u12 = au[g1.a * 289 + g1.c * 17 + i1];
			return (long)((u3.b - u6.b - u9.b) + u12.b);
		}
		return 1L;
	}

	private static final long c(g g1, int l, int i1, u au[])
	{
		switch(l)
		{
		case 2: // '\002'
			u u1 = au[i1 * 289 + g1.d * 17 + g1.f];
			u u4 = au[i1 * 289 + g1.d * 17 + g1.e];
			u u7 = au[i1 * 289 + g1.c * 17 + g1.f];
			u u10 = au[i1 * 289 + g1.c * 17 + g1.e];
			return (long)((u1.c - u4.c - u7.c) + u10.c);
		case 1: // '\001'
			u u2 = au[g1.b * 289 + i1 * 17 + g1.f];
			u u5 = au[g1.b * 289 + i1 * 17 + g1.e];
			u u8 = au[g1.a * 289 + i1 * 17 + g1.f];
			u u11 = au[g1.a * 289 + i1 * 17 + g1.e];
			return (long)((u2.c - u5.c - u8.c) + u11.c);
		case 0: // '\0'
			u u3 = au[g1.b * 289 + g1.d * 17 + i1];
			u u6 = au[g1.b * 289 + g1.c * 17 + i1];
			u u9 = au[g1.a * 289 + g1.d * 17 + i1];
			u u12 = au[g1.a * 289 + g1.c * 17 + i1];
			return (long)((u3.c - u6.c - u9.c) + u12.c);
		}
		return 1L;
	}

	private static final long d(g g1, int l, int i1, u au[])
	{
		switch(l)
		{
		case 2: // '\002'
			u u1 = au[i1 * 289 + g1.d * 17 + g1.f];
			u u4 = au[i1 * 289 + g1.d * 17 + g1.e];
			u u7 = au[i1 * 289 + g1.c * 17 + g1.f];
			u u10 = au[i1 * 289 + g1.c * 17 + g1.e];
			return (long)((u1.d - u4.d - u7.d) + u10.d);
		case 1: // '\001'
			u u2 = au[g1.b * 289 + i1 * 17 + g1.f];
			u u5 = au[g1.b * 289 + i1 * 17 + g1.e];
			u u8 = au[g1.a * 289 + i1 * 17 + g1.f];
			u u11 = au[g1.a * 289 + i1 * 17 + g1.e];
			return (long)((u2.d - u5.d - u8.d) + u11.d);
		case 0: // '\0'
			u u3 = au[g1.b * 289 + g1.d * 17 + i1];
			u u6 = au[g1.b * 289 + g1.c * 17 + i1];
			u u9 = au[g1.a * 289 + g1.d * 17 + i1];
			u u12 = au[g1.a * 289 + g1.c * 17 + i1];
			return (long)((u3.d - u6.d - u9.d) + u12.d);
		}
		return 1L;
	}

	private static final float i(g g1, u au[])
	{
		long l = e(g1, au);
		long l1 = f(g1, au);
		long l2 = g(g1, au);
		long l3 = h(g1, au);
		float f1 = ((((au[g1.b * 289 + g1.d * 17 + g1.f].e - au[g1.b * 289 + g1.d * 17 + g1.e].e - au[g1.b
			* 289
				+ g1.c
				* 17
				+ g1.f].e) + au[g1.b * 289 + g1.c * 17 + g1.e].e) - au[g1.a * 289 + g1.d * 17 + g1.f].e)
			+ au[g1.a * 289 + g1.d * 17 + g1.e].e + au[g1.a * 289 + g1.c * 17 + g1.f].e)
			- au[g1.a * 289 + g1.c * 17 + g1.e].e;
		return f1 - (float)(l * l + l2 * l2 + l1 * l1) / (float)l3;
	}

	private static final float b(g g1, int l, int i1, int j1, int ai[], long l1, long l2, long l3, long l4, u au[])
	{
		long l5 = a(g1, l, au);
		long l6 = b(g1, l, au);
		long l7 = c(g1, l, au);
		long l8 = d(g1, l, au);
		float f1 = 0.0F;
		float f2 = 0.0F;
		ai[0] = -1;
		for(int k1 = i1; k1 < j1; k1++)
		{
			long l9 = l5 + a(g1, l, k1, au);
			long l10 = l6 + b(g1, l, k1, au);
			long l11 = l7 + c(g1, l, k1, au);
			long l12 = l8 + d(g1, l, k1, au);
			if (l12 == 0L)
				continue;
			float f3 = ((float)l9 * (float)l9 + (float)l10 * (float)l10 + (float)l11 * (float)l11) / (float)l12;
			l9 = l1 - l9;
			l10 = l2 - l10;
			l11 = l3 - l11;
			l12 = l4 - l12;
			if (l12 == 0L)
				continue;
			f3 += ((float)l9 * (float)l9 + (float)l10 * (float)l10 + (float)l11 * (float)l11) / (float)l12;
			if (f3 > f1)
			{
				f1 = f3;
				ai[0] = k1;
			}
		}
		return f1;
	}

	private static final int a(g g1, g g2, u au[])
	{
		int ai[] = new int[1];
		int ai1[] = new int[1];
		int ai2[] = new int[1];
		long l = e(g1, au);
		long l1 = f(g1, au);
		long l2 = g(g1, au);
		long l3 = h(g1, au);
		float f1 = b(g1, 2, g1.a + 1, g1.b, ai, l, l1, l2, l3, au);
		float f2 = b(g1, 1, g1.c + 1, g1.d, ai1, l, l1, l2, l3, au);
		float f3 = b(g1, 0, g1.e + 1, g1.f, ai2, l, l1, l2, l3, au);
		byte byte0;
		if (f1 >= f2 && f1 >= f2)
		{
			byte0 = 2;
			if (ai[0] < 0)
				return 0;
		}
		else if (f2 >= f1 && f2 >= f3)
			byte0 = 1;
		else
			byte0 = 0;
		g2.b = g1.b;
		g2.d = g1.d;
		g2.f = g1.f;
		switch(byte0)
		{
		case 2: // '\002'
			g2.a = g1.b = ai[0];
			g2.c = g1.c;
			g2.e = g1.e;
			break;
		case 1: // '\001'
			g2.a = g1.a;
			g2.c = g1.d = ai1[0];
			g2.e = g1.e;
			break;
		case 0: // '\0'
			g2.a = g1.a;
			g2.c = g1.c;
			g2.e = g1.f = ai2[0];
			break;
		}
		g1.g = (g1.b - g1.a) * (g1.d - g1.c) * (g1.f - g1.e);
		g2.g = (g2.b - g2.a) * (g2.d - g2.c) * (g2.f - g2.e);
		return 1;
	}

	private static final void a(g g1, int l, int ai[])
	{
		for(int i1 = g1.a + 1; i1 <= g1.b; i1++)
		{
			for(int j1 = g1.c + 1; j1 <= g1.d; j1++)
			{
				for(int k1 = g1.e + 1; k1 <= g1.f; k1++)
					ai[i1 * 289 + j1 * 17 + k1] = l;
			}
		}
	}

	static long a(g g1, u au[])
	{
		return e(g1, au);
	}

	static long b(g g1, u au[])
	{
		return f(g1, au);
	}

	static long c(g g1, u au[])
	{
		return g(g1, au);
	}

	static long d(g g1, u au[])
	{
		return h(g1, au);
	}

	static float a(g g1, int l, int i1, int j1, int ai[], long l1, long l2, long l3, long l4, u au[])
	{
		return b(g1, l, i1, j1, ai, l1, l2, l3, l4, au);
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
		for(int l = 8; l < 16; l++)
			b[l] = 16;
		for(int i1 = 16; i1 < 32; i1++)
			b[i1] = 32;
		for(int j1 = 32; j1 < 64; j1++)
			b[j1] = 64;
		for(int k1 = 64; k1 < 128; k1++)
			b[k1] = 128;
		for(int l1 = 128; l1 <= 256; l1++)
			b[l1] = 256;
		for(int i2 = 0; i2 < 256; i2++)
			a[i2] = i2 * i2;
	}
}
