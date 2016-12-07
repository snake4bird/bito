// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 16:44:34
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package b;

import java.awt.image.*;

// Referenced classes of package com.gif4j.quantizer:
// a, b, d, e,
// i, k, l, o,
// r, s, w, x
class qxx
{
	static class i
	{
		static final int a()
		{
			return 1;
		}

		i()
		{
		}
	}

	static class axx
	{
		static final int a()
		{
			i.a();
			return 1;
		}

		axx()
		{
		}
	}

	static class e
	{
		static final int a(r r1, r r2, s as[])
		{
			int ai[] = new int[1];
			int ai1[] = new int[1];
			int ai2[] = new int[1];
			long l1 = qxx.a(r1, as);
			long l2 = qxx.b(r1, as);
			long l3 = qxx.c(r1, as);
			long l4 = qxx.d(r1, as);
			float f1 = qxx.a(r1, 2, r1.a + 1, r1.b, ai, l1, l2, l3, l4, as);
			float f2 = qxx.a(r1, 1, r1.c + 1, r1.d, ai1, l1, l2, l3, l4, as);
			float f3 = qxx.a(r1, 0, r1.e + 1, r1.f, ai2, l1, l2, l3, l4, as);
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
			r2.b = r1.b;
			r2.d = r1.d;
			r2.f = r1.f;
			switch(byte0)
			{
			case 2: // '\002'
				r2.a = r1.b = ai[0];
				r2.c = r1.c;
				r2.e = r1.e;
				break;
			case 1: // '\001'
				r2.a = r1.a;
				r2.c = r1.d = ai1[0];
				r2.e = r1.e;
				break;
			case 0: // '\0'
				r2.a = r1.a;
				r2.c = r1.c;
				r2.e = r1.f = ai2[0];
				break;
			}
			r1.g = (r1.b - r1.a) * (r1.d - r1.c) * (r1.f - r1.e);
			r2.g = (r2.b - r2.a) * (r2.d - r2.c) * (r2.f - r2.e);
			axx.a();
			return 1;
		}

		e()
		{
		}
	}

	static class bcc
	{
		static final int a(r r1, r r2, s as[])
		{
			int ai[] = new int[1];
			int ai1[] = new int[1];
			int ai2[] = new int[1];
			long l1 = qxx.a(r1, as);
			long l2 = qxx.b(r1, as);
			long l3 = qxx.c(r1, as);
			long l4 = qxx.d(r1, as);
			float f1 = qxx.a(r1, 2, r1.a + 1, r1.b, ai, l1, l2, l3, l4, as);
			float f2 = qxx.a(r1, 1, r1.c + 1, r1.d, ai1, l1, l2, l3, l4, as);
			float f3 = qxx.a(r1, 0, r1.e + 1, r1.f, ai2, l1, l2, l3, l4, as);
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
			r2.b = r1.b;
			r2.d = r1.d;
			r2.f = r1.f;
			switch(byte0)
			{
			case 2: // '\002'
				r2.a = r1.b = ai[0];
				r2.c = r1.c;
				r2.e = r1.e;
				break;
			case 1: // '\001'
				r2.a = r1.a;
				r2.c = r1.d = ai1[0];
				r2.e = r1.e;
				break;
			case 0: // '\0'
				r2.a = r1.a;
				r2.c = r1.c;
				r2.e = r1.f = ai2[0];
				break;
			}
			r1.g = (r1.b - r1.a) * (r1.d - r1.c) * (r1.f - r1.e);
			r2.g = (r2.b - r2.a) * (r2.d - r2.c) * (r2.f - r2.e);
			e.a(null, null, null);
			return 1;
		}

		bcc()
		{
		}
	}

	static class l
	{
		static final int a(r r1, r r2, s as[])
		{
			int ai[] = new int[1];
			int ai1[] = new int[1];
			int ai2[] = new int[1];
			long l1 = qxx.a(r1, as);
			long l2 = qxx.b(r1, as);
			long l3 = qxx.c(r1, as);
			long l4 = qxx.d(r1, as);
			float f1 = qxx.a(r1, 2, r1.a + 1, r1.b, ai, l1, l2, l3, l4, as);
			float f2 = qxx.a(r1, 1, r1.c + 1, r1.d, ai1, l1, l2, l3, l4, as);
			float f3 = qxx.a(r1, 0, r1.e + 1, r1.f, ai2, l1, l2, l3, l4, as);
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
			r2.b = r1.b;
			r2.d = r1.d;
			r2.f = r1.f;
			switch(byte0)
			{
			case 2: // '\002'
				r2.a = r1.b = ai[0];
				r2.c = r1.c;
				r2.e = r1.e;
				break;
			case 1: // '\001'
				r2.a = r1.a;
				r2.c = r1.d = ai1[0];
				r2.e = r1.e;
				break;
			case 0: // '\0'
				r2.a = r1.a;
				r2.c = r1.c;
				r2.e = r1.f = ai2[0];
				break;
			}
			r1.g = (r1.b - r1.a) * (r1.d - r1.c) * (r1.f - r1.e);
			r2.g = (r2.b - r2.a) * (r2.d - r2.c) * (r2.f - r2.e);
			bcc.a(null, null, null);
			return 1;
		}

		l()
		{
		}
	}

	static class x
	{
		static final int a(r r1, r r2, s as[])
		{
			int ai[] = new int[1];
			int ai1[] = new int[1];
			int ai2[] = new int[1];
			long l1 = qxx.a(r1, as);
			long l2 = qxx.b(r1, as);
			long l3 = qxx.c(r1, as);
			long l4 = qxx.d(r1, as);
			float f1 = qxx.a(r1, 2, r1.a + 1, r1.b, ai, l1, l2, l3, l4, as);
			float f2 = qxx.a(r1, 1, r1.c + 1, r1.d, ai1, l1, l2, l3, l4, as);
			float f3 = qxx.a(r1, 0, r1.e + 1, r1.f, ai2, l1, l2, l3, l4, as);
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
			r2.b = r1.b;
			r2.d = r1.d;
			r2.f = r1.f;
			switch(byte0)
			{
			case 2: // '\002'
				r2.a = r1.b = ai[0];
				r2.c = r1.c;
				r2.e = r1.e;
				break;
			case 1: // '\001'
				r2.a = r1.a;
				r2.c = r1.d = ai1[0];
				r2.e = r1.e;
				break;
			case 0: // '\0'
				r2.a = r1.a;
				r2.c = r1.c;
				r2.e = r1.f = ai2[0];
				break;
			}
			r1.g = (r1.b - r1.a) * (r1.d - r1.c) * (r1.f - r1.e);
			r2.g = (r2.b - r2.a) * (r2.d - r2.c) * (r2.f - r2.e);
			l.a(null, null, null);
			return 1;
		}

		x()
		{
		}
	}

	static class s
	{
		int a;
		int b;
		int c;
		int d;
		float e;
		byte f;

		s()
		{
			e = 0.0F;
			f = 0;
		}
	}

	static class r
	{
		int a;
		int b;
		int c;
		int d;
		int e;
		int f;
		int g;

		r()
		{
		}
	}

	qxx()
	{
	}

	public static BufferedImage a(BufferedImage bufferedimage, int j, boolean flag, boolean flag1)
	{
		return a(bufferedimage, j, flag, flag1, false);
	}

	public static BufferedImage a(BufferedImage bufferedimage, int j, boolean flag, boolean flag1, boolean flag2)
	{
		char c1 = flag2?'\u0100':'\b';
		if (j <= c1)
			return a(bufferedimage, j, flag2);
		else
			return b(bufferedimage, j, flag2);
	}

	private static final BufferedImage a(int ai[], int j, int i1, int j1)
	{
		int k1 = 1 << j1;
		kxx k2 = new kxx(k1);
		int l1 = 0;
		for(int i2 = 0; i2 < ai.length && l1 <= k1; i2++)
			if (k2.c(ai[i2]))
				l1++;
		if (l1 >= k1)
			return null;
		l1 = b[l1];
		byte abyte0[] = new byte[l1];
		byte abyte1[] = new byte[l1];
		byte abyte2[] = new byte[l1];
		int ai1[] = (int[])k2.a();
		dxx d1 = new dxx(l1);
		int j2 = 0;
		boolean aflag[] = k2.g;
		for(int l2 = 0; l2 < ai1.length; l2++)
			if (aflag[l2])
			{
				int i3 = ai1[l2];
				abyte0[j2] = (byte)(i3 >> 16 & 0xff);
				abyte1[j2] = (byte)(i3 >> 8 & 0xff);
				abyte2[j2] = (byte)(i3 & 0xff);
				d1.b(i3, j2);
				j2++;
			}
		IndexColorModel indexcolormodel = new IndexColorModel(j1, l1, abyte0, abyte1, abyte2);
		DataBufferByte databufferbyte = new DataBufferByte(ai.length);
		byte abyte3[] = databufferbyte.getData();
		for(int j3 = 0; j3 < ai.length; j3++)
			abyte3[j3] = (byte)d1.c(ai[j3]);
		WritableRaster writableraster = Raster.createInterleavedRaster(databufferbyte, j, i1, j, 1, new int[]{0}, null);
		return new BufferedImage(indexcolormodel, writableraster, false, null);
	}

	private static final BufferedImage a(BufferedImage bufferedimage, int j, boolean flag)
	{
		int i1 = bufferedimage.getWidth();
		int j1 = bufferedimage.getHeight();
		int k1 = j;
		if (!flag)
			k1 = 1 << j;
		else
			j = oxx.a(k1);
		int ai[] = oxx.a(bufferedimage);
		s as[] = new s[4913];
		for(int l1 = 0; l1 < 4913; l1++)
			as[l1] = new s();
		b(as, ai);
		int i2 = 0;
		for(int j2 = 0; j2 < 4913 && i2 <= k1; j2++)
			if (as[j2].d != 0)
				i2++;
		if (i2 <= k1)
		{
			BufferedImage bufferedimage1 = a(ai, i1, j1, j);
			if (bufferedimage1 != null)
				return bufferedimage1;
			k1 = b[i2];
			byte abyte1[] = new byte[k1];
			byte abyte3[] = new byte[k1];
			byte abyte5[] = new byte[k1];
			i2 = 0;
			i2 = 0;
			for(int k2 = 0; k2 < 4913 && i2 < k1; k2++)
				if (as[k2].d != 0)
				{
					abyte1[i2] = (byte)(as[k2].a / as[k2].d);
					abyte3[i2] = (byte)(as[k2].b / as[k2].d);
					abyte5[i2] = (byte)(as[k2].c / as[k2].d);
					as[k2].f = (byte)i2;
					i2++;
				}
			IndexColorModel indexcolormodel = new IndexColorModel(j, k1, abyte1, abyte3, abyte5);
			DataBufferByte databufferbyte = new DataBufferByte(ai.length);
			byte abyte6[] = databufferbyte.getData();
			for(int i3 = 0; i3 < ai.length; i3++)
			{
				int k3 = ai[i3] >> 16 & 0xff;
				int i4 = ai[i3] >> 8 & 0xff;
				int j4 = ai[i3] & 0xff;
				int k4 = ((k3 >> 4) + 1) * 289 + ((i4 >> 4) + 1) * 17 + (j4 >> 4) + 1;
				abyte6[i3] = as[k4].f;
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
		byte abyte0[] = new byte[k1];
		byte abyte2[] = new byte[k1];
		byte abyte4[] = new byte[k1];
		r ar[] = new r[k1];
		int l2 = a(as, ar, k1);
		for(int j3 = 0; j3 < l2; j3++)
		{
			long l3 = h(ar[j3], as);
			if (l3 != 0L)
			{
				abyte0[j3] = (byte)(int)(e(ar[j3], as) / l3 & 255L);
				abyte2[j3] = (byte)(int)(f(ar[j3], as) / l3 & 255L);
				abyte4[j3] = (byte)(int)(g(ar[j3], as) / l3 & 255L);
			}
			else
			{
				abyte0[j3] = abyte2[j3] = abyte4[j3] = 0;
			}
		}
		IndexColorModel indexcolormodel1 = new IndexColorModel(j, k1, abyte0, abyte2, abyte4);
		DataBufferByte databufferbyte1 = new DataBufferByte(ai.length);
		byte abyte7[] = databufferbyte1.getData();
		wxx w1 = new wxx(abyte0, abyte2, abyte4);
		for(int l4 = 0; l4 < ai.length; l4++)
			abyte7[l4] = w1.a(ai[l4]);
		WritableRaster writableraster1 = Raster.createInterleavedRaster(databufferbyte1,
			i1,
			j1,
			i1,
			1,
			new int[]{0},
			null);
		return new BufferedImage(indexcolormodel1, writableraster1, false, null);
	}

	private static final BufferedImage b(BufferedImage bufferedimage, int j, boolean flag)
	{
		int i1 = bufferedimage.getWidth();
		int j1 = bufferedimage.getHeight();
		int k1 = j;
		if (!flag)
			k1 = 1 << j;
		int ai[] = oxx.a(bufferedimage);
		s as[] = new s[4913];
		for(int l1 = 0; l1 < 4913; l1++)
			as[l1] = new s();
		a(as, ai);
		r ar[] = new r[k1];
		int i2 = a(as, ar, k1);
		int ai1[] = new int[4913];
		byte abyte0[] = new byte[k1];
		byte abyte1[] = new byte[k1];
		byte abyte2[] = new byte[k1];
		if (i1 < -2)
			x.a(ar[0], ar[1], as);
		for(int j2 = 0; j2 < i2; j2++)
		{
			a(ar[j2], j2, ai1);
			long l2 = h(ar[j2], as);
			if (l2 != 0L)
			{
				abyte0[j2] = (byte)(int)(e(ar[j2], as) / l2 & 255L);
				abyte1[j2] = (byte)(int)(f(ar[j2], as) / l2 & 255L);
				abyte2[j2] = (byte)(int)(g(ar[j2], as) / l2 & 255L);
			}
			else
			{
				abyte0[j2] = abyte1[j2] = abyte2[j2] = 0;
			}
		}
		int k2 = 1;
		BufferedImage bufferedimage1 = new BufferedImage(i1, j1, k2);
		int ai2[] = ((DataBufferInt)bufferedimage1.getRaster().getDataBuffer()).getData();
		for(int i3 = 0; i3 < ai.length; i3++)
		{
			int j3 = ai1[ai[i3]];
			ai2[i3] = (abyte0[j3] & 0xff) << 16 | (abyte1[j3] & 0xff) << 8 | abyte2[j3] & 0xff;
		}
		return bufferedimage1;
	}

	private static final int a(s as[], r ar[], int j)
	{
		a(as);
		for(int i1 = 0; i1 < j; i1++)
			ar[i1] = new r();
		float af[] = new float[j];
		ar[0].a = ar[0].c = ar[0].e = 0;
		ar[0].b = ar[0].d = ar[0].f = 16;
		int j1 = j;
		int k1 = 0;
		int l1 = 1;
		do
		{
			if (l1 >= j1)
				break;
			if (a(ar[k1], ar[l1], as) != 0)
			{
				if (ar[k1].g > 1)
					af[k1] = i(ar[k1], as);
				else
					af[k1] = 0.0F;
				if (ar[l1].g > 1)
					af[l1] = i(ar[l1], as);
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

	private static final void a(s as[], int ai[])
	{
		for(int j = 0; j < ai.length; j++)
		{
			int i1 = ai[j] >> 16 & 0xff;
			int j1 = ai[j] >> 8 & 0xff;
			int k1 = ai[j] & 0xff;
			int l1 = ((i1 >> 4) + 1) * 289 + ((j1 >> 4) + 1) * 17 + (k1 >> 4) + 1;
			ai[j] = l1;
			s s1 = as[l1];
			s1.a += i1;
			s1.b += j1;
			s1.c += k1;
			s1.d++;
			s1.e += a[i1] + a[j1] + a[k1];
		}
	}

	private static final void b(s as[], int ai[])
	{
		for(int j = 0; j < ai.length; j++)
		{
			int i1 = ai[j] >> 16 & 0xff;
			int j1 = ai[j] >> 8 & 0xff;
			int k1 = ai[j] & 0xff;
			int l1 = ((i1 >> 4) + 1) * 289 + ((j1 >> 4) + 1) * 17 + (k1 >> 4) + 1;
			s s1 = as[l1];
			s1.a += i1;
			s1.b += j1;
			s1.c += k1;
			s1.d++;
			s1.e += a[i1] + a[j1] + a[k1];
		}
	}

	private static final void a(s as[])
	{
		int ai[] = new int[17];
		int ai1[] = new int[17];
		int ai2[] = new int[17];
		int ai3[] = new int[17];
		float af[] = new float[17];
		for(int j = 1; j <= 16; j++)
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
					int l2 = j * 289 + j1 * 17 + k2;
					int i3 = (j - 1) * 289 + j1 * 17 + k2;
					s s1 = as[l2];
					s s2 = as[i3];
					k1 += s1.d;
					l1 += s1.a;
					i2 += s1.b;
					j2 += s1.c;
					f1 += s1.e;
					ai3[k2] += k1;
					ai[k2] += l1;
					ai1[k2] += i2;
					ai2[k2] += j2;
					af[k2] += f1;
					s1.d = s2.d + ai3[k2];
					s1.a = s2.a + ai[k2];
					s1.b = s2.b + ai1[k2];
					s1.c = s2.c + ai2[k2];
					s1.e = s2.e + af[k2];
				}
			}
		}
	}

	private static final long e(r r1, s as[])
	{
		s s1 = as[r1.b * 289 + r1.d * 17 + r1.f];
		s s2 = as[r1.b * 289 + r1.d * 17 + r1.e];
		s s3 = as[r1.b * 289 + r1.c * 17 + r1.f];
		s s4 = as[r1.b * 289 + r1.c * 17 + r1.e];
		s s5 = as[r1.a * 289 + r1.d * 17 + r1.f];
		s s6 = as[r1.a * 289 + r1.d * 17 + r1.e];
		s s7 = as[r1.a * 289 + r1.c * 17 + r1.f];
		s s8 = as[r1.a * 289 + r1.c * 17 + r1.e];
		return (long)(((((s1.a - s2.a - s3.a) + s4.a) - s5.a) + s6.a + s7.a) - s8.a);
	}

	private static final long f(r r1, s as[])
	{
		s s1 = as[r1.b * 289 + r1.d * 17 + r1.f];
		s s2 = as[r1.b * 289 + r1.d * 17 + r1.e];
		s s3 = as[r1.b * 289 + r1.c * 17 + r1.f];
		s s4 = as[r1.b * 289 + r1.c * 17 + r1.e];
		s s5 = as[r1.a * 289 + r1.d * 17 + r1.f];
		s s6 = as[r1.a * 289 + r1.d * 17 + r1.e];
		s s7 = as[r1.a * 289 + r1.c * 17 + r1.f];
		s s8 = as[r1.a * 289 + r1.c * 17 + r1.e];
		return (long)(((((s1.b - s2.b - s3.b) + s4.b) - s5.b) + s6.b + s7.b) - s8.b);
	}

	private static final long g(r r1, s as[])
	{
		s s1 = as[r1.b * 289 + r1.d * 17 + r1.f];
		s s2 = as[r1.b * 289 + r1.d * 17 + r1.e];
		s s3 = as[r1.b * 289 + r1.c * 17 + r1.f];
		s s4 = as[r1.b * 289 + r1.c * 17 + r1.e];
		s s5 = as[r1.a * 289 + r1.d * 17 + r1.f];
		s s6 = as[r1.a * 289 + r1.d * 17 + r1.e];
		s s7 = as[r1.a * 289 + r1.c * 17 + r1.f];
		s s8 = as[r1.a * 289 + r1.c * 17 + r1.e];
		return (long)(((((s1.c - s2.c - s3.c) + s4.c) - s5.c) + s6.c + s7.c) - s8.c);
	}

	private static final long h(r r1, s as[])
	{
		s s1 = as[r1.b * 289 + r1.d * 17 + r1.f];
		s s2 = as[r1.b * 289 + r1.d * 17 + r1.e];
		s s3 = as[r1.b * 289 + r1.c * 17 + r1.f];
		s s4 = as[r1.b * 289 + r1.c * 17 + r1.e];
		s s5 = as[r1.a * 289 + r1.d * 17 + r1.f];
		s s6 = as[r1.a * 289 + r1.d * 17 + r1.e];
		s s7 = as[r1.a * 289 + r1.c * 17 + r1.f];
		s s8 = as[r1.a * 289 + r1.c * 17 + r1.e];
		return (long)(((((s1.d - s2.d - s3.d) + s4.d) - s5.d) + s6.d + s7.d) - s8.d);
	}

	private static final long a(r r1, int j, s as[])
	{
		switch(j)
		{
		case 2: // '\002'
			s s1 = as[r1.a * 289 + r1.d * 17 + r1.f];
			s s4 = as[r1.a * 289 + r1.d * 17 + r1.e];
			s s7 = as[r1.a * 289 + r1.c * 17 + r1.f];
			s s10 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s1.a + s4.a + s7.a) - s10.a);
		case 1: // '\001'
			s s2 = as[r1.b * 289 + r1.c * 17 + r1.f];
			s s5 = as[r1.b * 289 + r1.c * 17 + r1.e];
			s s8 = as[r1.a * 289 + r1.c * 17 + r1.f];
			s s11 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s2.a + s5.a + s8.a) - s11.a);
		case 0: // '\0'
			s s3 = as[r1.b * 289 + r1.d * 17 + r1.e];
			s s6 = as[r1.b * 289 + r1.c * 17 + r1.e];
			s s9 = as[r1.a * 289 + r1.d * 17 + r1.e];
			s s12 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s3.a + s6.a + s9.a) - s12.a);
		}
		return 1L;
	}

	private static final long b(r r1, int j, s as[])
	{
		switch(j)
		{
		case 2: // '\002'
			s s1 = as[r1.a * 289 + r1.d * 17 + r1.f];
			s s4 = as[r1.a * 289 + r1.d * 17 + r1.e];
			s s7 = as[r1.a * 289 + r1.c * 17 + r1.f];
			s s10 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s1.b + s4.b + s7.b) - s10.b);
		case 1: // '\001'
			s s2 = as[r1.b * 289 + r1.c * 17 + r1.f];
			s s5 = as[r1.b * 289 + r1.c * 17 + r1.e];
			s s8 = as[r1.a * 289 + r1.c * 17 + r1.f];
			s s11 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s2.b + s5.b + s8.b) - s11.b);
		case 0: // '\0'
			s s3 = as[r1.b * 289 + r1.d * 17 + r1.e];
			s s6 = as[r1.b * 289 + r1.c * 17 + r1.e];
			s s9 = as[r1.a * 289 + r1.d * 17 + r1.e];
			s s12 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s3.b + s6.b + s9.b) - s12.b);
		}
		return 1L;
	}

	private static final long c(r r1, int j, s as[])
	{
		switch(j)
		{
		case 2: // '\002'
			s s1 = as[r1.a * 289 + r1.d * 17 + r1.f];
			s s4 = as[r1.a * 289 + r1.d * 17 + r1.e];
			s s7 = as[r1.a * 289 + r1.c * 17 + r1.f];
			s s10 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s1.c + s4.c + s7.c) - s10.c);
		case 1: // '\001'
			s s2 = as[r1.b * 289 + r1.c * 17 + r1.f];
			s s5 = as[r1.b * 289 + r1.c * 17 + r1.e];
			s s8 = as[r1.a * 289 + r1.c * 17 + r1.f];
			s s11 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s2.c + s5.c + s8.c) - s11.c);
		case 0: // '\0'
			s s3 = as[r1.b * 289 + r1.d * 17 + r1.e];
			s s6 = as[r1.b * 289 + r1.c * 17 + r1.e];
			s s9 = as[r1.a * 289 + r1.d * 17 + r1.e];
			s s12 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s3.c + s6.c + s9.c) - s12.c);
		}
		return 1L;
	}

	private static final long d(r r1, int j, s as[])
	{
		switch(j)
		{
		case 2: // '\002'
			s s1 = as[r1.a * 289 + r1.d * 17 + r1.f];
			s s4 = as[r1.a * 289 + r1.d * 17 + r1.e];
			s s7 = as[r1.a * 289 + r1.c * 17 + r1.f];
			s s10 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s1.d + s4.d + s7.d) - s10.d);
		case 1: // '\001'
			s s2 = as[r1.b * 289 + r1.c * 17 + r1.f];
			s s5 = as[r1.b * 289 + r1.c * 17 + r1.e];
			s s8 = as[r1.a * 289 + r1.c * 17 + r1.f];
			s s11 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s2.d + s5.d + s8.d) - s11.d);
		case 0: // '\0'
			s s3 = as[r1.b * 289 + r1.d * 17 + r1.e];
			s s6 = as[r1.b * 289 + r1.c * 17 + r1.e];
			s s9 = as[r1.a * 289 + r1.d * 17 + r1.e];
			s s12 = as[r1.a * 289 + r1.c * 17 + r1.e];
			return (long)((-s3.d + s6.d + s9.d) - s12.d);
		}
		return 1L;
	}

	private static final long a(r r1, int j, int i1, s as[])
	{
		switch(j)
		{
		case 2: // '\002'
			s s1 = as[i1 * 289 + r1.d * 17 + r1.f];
			s s4 = as[i1 * 289 + r1.d * 17 + r1.e];
			s s7 = as[i1 * 289 + r1.c * 17 + r1.f];
			s s10 = as[i1 * 289 + r1.c * 17 + r1.e];
			return (long)((s1.a - s4.a - s7.a) + s10.a);
		case 1: // '\001'
			s s2 = as[r1.b * 289 + i1 * 17 + r1.f];
			s s5 = as[r1.b * 289 + i1 * 17 + r1.e];
			s s8 = as[r1.a * 289 + i1 * 17 + r1.f];
			s s11 = as[r1.a * 289 + i1 * 17 + r1.e];
			return (long)((s2.a - s5.a - s8.a) + s11.a);
		case 0: // '\0'
			s s3 = as[r1.b * 289 + r1.d * 17 + i1];
			s s6 = as[r1.b * 289 + r1.c * 17 + i1];
			s s9 = as[r1.a * 289 + r1.d * 17 + i1];
			s s12 = as[r1.a * 289 + r1.c * 17 + i1];
			return (long)((s3.a - s6.a - s9.a) + s12.a);
		}
		return 1L;
	}

	private static final long b(r r1, int j, int i1, s as[])
	{
		switch(j)
		{
		case 2: // '\002'
			s s1 = as[i1 * 289 + r1.d * 17 + r1.f];
			s s4 = as[i1 * 289 + r1.d * 17 + r1.e];
			s s7 = as[i1 * 289 + r1.c * 17 + r1.f];
			s s10 = as[i1 * 289 + r1.c * 17 + r1.e];
			return (long)((s1.b - s4.b - s7.b) + s10.b);
		case 1: // '\001'
			s s2 = as[r1.b * 289 + i1 * 17 + r1.f];
			s s5 = as[r1.b * 289 + i1 * 17 + r1.e];
			s s8 = as[r1.a * 289 + i1 * 17 + r1.f];
			s s11 = as[r1.a * 289 + i1 * 17 + r1.e];
			return (long)((s2.b - s5.b - s8.b) + s11.b);
		case 0: // '\0'
			s s3 = as[r1.b * 289 + r1.d * 17 + i1];
			s s6 = as[r1.b * 289 + r1.c * 17 + i1];
			s s9 = as[r1.a * 289 + r1.d * 17 + i1];
			s s12 = as[r1.a * 289 + r1.c * 17 + i1];
			return (long)((s3.b - s6.b - s9.b) + s12.b);
		}
		return 1L;
	}

	private static final long c(r r1, int j, int i1, s as[])
	{
		switch(j)
		{
		case 2: // '\002'
			s s1 = as[i1 * 289 + r1.d * 17 + r1.f];
			s s4 = as[i1 * 289 + r1.d * 17 + r1.e];
			s s7 = as[i1 * 289 + r1.c * 17 + r1.f];
			s s10 = as[i1 * 289 + r1.c * 17 + r1.e];
			return (long)((s1.c - s4.c - s7.c) + s10.c);
		case 1: // '\001'
			s s2 = as[r1.b * 289 + i1 * 17 + r1.f];
			s s5 = as[r1.b * 289 + i1 * 17 + r1.e];
			s s8 = as[r1.a * 289 + i1 * 17 + r1.f];
			s s11 = as[r1.a * 289 + i1 * 17 + r1.e];
			return (long)((s2.c - s5.c - s8.c) + s11.c);
		case 0: // '\0'
			s s3 = as[r1.b * 289 + r1.d * 17 + i1];
			s s6 = as[r1.b * 289 + r1.c * 17 + i1];
			s s9 = as[r1.a * 289 + r1.d * 17 + i1];
			s s12 = as[r1.a * 289 + r1.c * 17 + i1];
			return (long)((s3.c - s6.c - s9.c) + s12.c);
		}
		return 1L;
	}

	private static final long d(r r1, int j, int i1, s as[])
	{
		switch(j)
		{
		case 2: // '\002'
			s s1 = as[i1 * 289 + r1.d * 17 + r1.f];
			s s4 = as[i1 * 289 + r1.d * 17 + r1.e];
			s s7 = as[i1 * 289 + r1.c * 17 + r1.f];
			s s10 = as[i1 * 289 + r1.c * 17 + r1.e];
			return (long)((s1.d - s4.d - s7.d) + s10.d);
		case 1: // '\001'
			s s2 = as[r1.b * 289 + i1 * 17 + r1.f];
			s s5 = as[r1.b * 289 + i1 * 17 + r1.e];
			s s8 = as[r1.a * 289 + i1 * 17 + r1.f];
			s s11 = as[r1.a * 289 + i1 * 17 + r1.e];
			return (long)((s2.d - s5.d - s8.d) + s11.d);
		case 0: // '\0'
			s s3 = as[r1.b * 289 + r1.d * 17 + i1];
			s s6 = as[r1.b * 289 + r1.c * 17 + i1];
			s s9 = as[r1.a * 289 + r1.d * 17 + i1];
			s s12 = as[r1.a * 289 + r1.c * 17 + i1];
			return (long)((s3.d - s6.d - s9.d) + s12.d);
		}
		return 1L;
	}

	private static final float i(r r1, s as[])
	{
		long l1 = e(r1, as);
		long l2 = f(r1, as);
		long l3 = g(r1, as);
		long l4 = h(r1, as);
		float f1 = ((((as[r1.b * 289 + r1.d * 17 + r1.f].e - as[r1.b * 289 + r1.d * 17 + r1.e].e - as[r1.b
			* 289
				+ r1.c
				* 17
				+ r1.f].e) + as[r1.b * 289 + r1.c * 17 + r1.e].e) - as[r1.a * 289 + r1.d * 17 + r1.f].e)
			+ as[r1.a * 289 + r1.d * 17 + r1.e].e + as[r1.a * 289 + r1.c * 17 + r1.f].e)
			- as[r1.a * 289 + r1.c * 17 + r1.e].e;
		return f1 - (float)(l1 * l1 + l3 * l3 + l2 * l2) / (float)l4;
	}

	private static final float b(r r1, int j, int i1, int j1, int ai[], long l1, long l2, long l3, long l4, s as[])
	{
		long l5 = a(r1, j, as);
		long l6 = b(r1, j, as);
		long l7 = c(r1, j, as);
		long l8 = d(r1, j, as);
		float f1 = 0.0F;
		float f2 = 0.0F;
		ai[0] = -1;
		for(int k1 = i1; k1 < j1; k1++)
		{
			long l9 = l5 + a(r1, j, k1, as);
			long l10 = l6 + b(r1, j, k1, as);
			long l11 = l7 + c(r1, j, k1, as);
			long l12 = l8 + d(r1, j, k1, as);
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

	private static final int a(r r1, r r2, s as[])
	{
		int ai[] = new int[1];
		int ai1[] = new int[1];
		int ai2[] = new int[1];
		long l1 = e(r1, as);
		long l2 = f(r1, as);
		long l3 = g(r1, as);
		long l4 = h(r1, as);
		float f1 = b(r1, 2, r1.a + 1, r1.b, ai, l1, l2, l3, l4, as);
		float f2 = b(r1, 1, r1.c + 1, r1.d, ai1, l1, l2, l3, l4, as);
		float f3 = b(r1, 0, r1.e + 1, r1.f, ai2, l1, l2, l3, l4, as);
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
		r2.b = r1.b;
		r2.d = r1.d;
		r2.f = r1.f;
		switch(byte0)
		{
		case 2: // '\002'
			r2.a = r1.b = ai[0];
			r2.c = r1.c;
			r2.e = r1.e;
			break;
		case 1: // '\001'
			r2.a = r1.a;
			r2.c = r1.d = ai1[0];
			r2.e = r1.e;
			break;
		case 0: // '\0'
			r2.a = r1.a;
			r2.c = r1.c;
			r2.e = r1.f = ai2[0];
			break;
		}
		r1.g = (r1.b - r1.a) * (r1.d - r1.c) * (r1.f - r1.e);
		r2.g = (r2.b - r2.a) * (r2.d - r2.c) * (r2.f - r2.e);
		return 1;
	}

	private static final void a(r r1, int j, int ai[])
	{
		for(int i1 = r1.a + 1; i1 <= r1.b; i1++)
		{
			for(int j1 = r1.c + 1; j1 <= r1.d; j1++)
			{
				for(int k1 = r1.e + 1; k1 <= r1.f; k1++)
					ai[i1 * 289 + j1 * 17 + k1] = j;
			}
		}
	}

	static long a(r r1, s as[])
	{
		return e(r1, as);
	}

	static long b(r r1, s as[])
	{
		return f(r1, as);
	}

	static long c(r r1, s as[])
	{
		return g(r1, as);
	}

	static long d(r r1, s as[])
	{
		return h(r1, as);
	}

	static float a(r r1, int j, int i1, int j1, int ai[], long l1, long l2, long l3, long l4, s as[])
	{
		return b(r1, j, i1, j1, ai, l1, l2, l3, l4, as);
	}

	private static final float a[];
	private static final int b[];
	static final byte[] m = new byte[] {
		(byte)-54,(byte)-2,(byte)-70,(byte)-66,(byte)0,(byte)0,(byte)0,(byte)49,(byte)0,(byte)64,(byte)7,(byte)0,(byte)2,(byte)1,(byte)0,(byte)1,
		(byte)36,(byte)7,(byte)0,(byte)4,(byte)1,(byte)0,(byte)21,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,
		(byte)47,(byte)67,(byte)108,(byte)97,(byte)115,(byte)115,(byte)76,(byte)111,(byte)97,(byte)100,(byte)101,(byte)114,(byte)1,(byte)0,(byte)2,(byte)99,
		(byte)108,(byte)1,(byte)0,(byte)23,(byte)76,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)67,
		(byte)108,(byte)97,(byte)115,(byte)115,(byte)76,(byte)111,(byte)97,(byte)100,(byte)101,(byte)114,(byte)59,(byte)1,(byte)0,(byte)6,(byte)60,(byte)105,
		(byte)110,(byte)105,(byte)116,(byte)62,(byte)1,(byte)0,(byte)3,(byte)40,(byte)41,(byte)86,(byte)1,(byte)0,(byte)4,(byte)67,(byte)111,(byte)100,
		(byte)101,(byte)10,(byte)0,(byte)3,(byte)0,(byte)11,(byte)12,(byte)0,(byte)7,(byte)0,(byte)8,(byte)10,(byte)0,(byte)13,(byte)0,(byte)15,
		(byte)7,(byte)0,(byte)14,(byte)1,(byte)0,(byte)3,(byte)100,(byte)47,(byte)69,(byte)12,(byte)0,(byte)16,(byte)0,(byte)17,(byte)1,(byte)0,
		(byte)1,(byte)86,(byte)1,(byte)0,(byte)7,(byte)40,(byte)41,(byte)76,(byte)100,(byte)47,(byte)69,(byte)59,(byte)10,(byte)0,(byte)13,(byte)0,
		(byte)19,(byte)12,(byte)0,(byte)20,(byte)0,(byte)21,(byte)1,(byte)0,(byte)1,(byte)111,(byte)1,(byte)0,(byte)20,(byte)40,(byte)41,(byte)76,
		(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)79,(byte)98,(byte)106,(byte)101,(byte)99,(byte)116,
		(byte)59,(byte)9,(byte)0,(byte)1,(byte)0,(byte)23,(byte)12,(byte)0,(byte)5,(byte)0,(byte)6,(byte)1,(byte)0,(byte)15,(byte)76,(byte)105,
		(byte)110,(byte)101,(byte)78,(byte)117,(byte)109,(byte)98,(byte)101,(byte)114,(byte)84,(byte)97,(byte)98,(byte)108,(byte)101,(byte)1,(byte)0,(byte)18,
		(byte)76,(byte)111,(byte)99,(byte)97,(byte)108,(byte)86,(byte)97,(byte)114,(byte)105,(byte)97,(byte)98,(byte)108,(byte)101,(byte)84,(byte)97,(byte)98,
		(byte)108,(byte)101,(byte)1,(byte)0,(byte)4,(byte)116,(byte)104,(byte)105,(byte)115,(byte)1,(byte)0,(byte)3,(byte)76,(byte)36,(byte)59,(byte)1,
		(byte)0,(byte)9,(byte)108,(byte)111,(byte)97,(byte)100,(byte)67,(byte)108,(byte)97,(byte)115,(byte)115,(byte)1,(byte)0,(byte)37,(byte)40,(byte)76,
		(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)83,(byte)116,(byte)114,(byte)105,(byte)110,(byte)103,
		(byte)59,(byte)41,(byte)76,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)67,(byte)108,(byte)97,
		(byte)115,(byte)115,(byte)59,(byte)1,(byte)0,(byte)10,(byte)69,(byte)120,(byte)99,(byte)101,(byte)112,(byte)116,(byte)105,(byte)111,(byte)110,(byte)115,
		(byte)7,(byte)0,(byte)32,(byte)1,(byte)0,(byte)32,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,
		(byte)67,(byte)108,(byte)97,(byte)115,(byte)115,(byte)78,(byte)111,(byte)116,(byte)70,(byte)111,(byte)117,(byte)110,(byte)100,(byte)69,(byte)120,(byte)99,
		(byte)101,(byte)112,(byte)116,(byte)105,(byte)111,(byte)110,(byte)10,(byte)0,(byte)3,(byte)0,(byte)34,(byte)12,(byte)0,(byte)28,(byte)0,(byte)29,
		(byte)1,(byte)0,(byte)1,(byte)110,(byte)1,(byte)0,(byte)18,(byte)76,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,
		(byte)103,(byte)47,(byte)83,(byte)116,(byte)114,(byte)105,(byte)110,(byte)103,(byte)59,(byte)1,(byte)0,(byte)1,(byte)97,(byte)1,(byte)0,(byte)20,
		(byte)40,(byte)76,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)67,(byte)108,(byte)97,(byte)115,
		(byte)115,(byte)59,(byte)41,(byte)86,(byte)10,(byte)0,(byte)40,(byte)0,(byte)42,(byte)7,(byte)0,(byte)41,(byte)1,(byte)0,(byte)15,(byte)106,
		(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)67,(byte)108,(byte)97,(byte)115,(byte)115,(byte)12,(byte)0,
		(byte)43,(byte)0,(byte)44,(byte)1,(byte)0,(byte)7,(byte)103,(byte)101,(byte)116,(byte)78,(byte)97,(byte)109,(byte)101,(byte)1,(byte)0,(byte)20,
		(byte)40,(byte)41,(byte)76,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)83,(byte)116,(byte)114,
		(byte)105,(byte)110,(byte)103,(byte)59,(byte)7,(byte)0,(byte)46,(byte)1,(byte)0,(byte)26,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,
		(byte)97,(byte)110,(byte)103,(byte)47,(byte)82,(byte)117,(byte)110,(byte)116,(byte)105,(byte)109,(byte)101,(byte)69,(byte)120,(byte)99,(byte)101,(byte)112,
		(byte)116,(byte)105,(byte)111,(byte)110,(byte)10,(byte)0,(byte)45,(byte)0,(byte)48,(byte)12,(byte)0,(byte)7,(byte)0,(byte)49,(byte)1,(byte)0,
		(byte)24,(byte)40,(byte)76,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)84,(byte)104,(byte)114,
		(byte)111,(byte)119,(byte)97,(byte)98,(byte)108,(byte)101,(byte)59,(byte)41,(byte)86,(byte)1,(byte)0,(byte)9,(byte)98,(byte)97,(byte)115,(byte)101,
		(byte)99,(byte)108,(byte)97,(byte)115,(byte)115,(byte)1,(byte)0,(byte)17,(byte)76,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,
		(byte)110,(byte)103,(byte)47,(byte)67,(byte)108,(byte)97,(byte)115,(byte)115,(byte)59,(byte)1,(byte)0,(byte)1,(byte)101,(byte)1,(byte)0,(byte)34,
		(byte)76,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)67,(byte)108,(byte)97,(byte)115,(byte)115,
		(byte)78,(byte)111,(byte)116,(byte)70,(byte)111,(byte)117,(byte)110,(byte)100,(byte)69,(byte)120,(byte)99,(byte)101,(byte)112,(byte)116,(byte)105,(byte)111,
		(byte)110,(byte)59,(byte)1,(byte)0,(byte)1,(byte)99,(byte)10,(byte)0,(byte)3,(byte)0,(byte)56,(byte)12,(byte)0,(byte)57,(byte)0,(byte)38,
		(byte)1,(byte)0,(byte)12,(byte)114,(byte)101,(byte)115,(byte)111,(byte)108,(byte)118,(byte)101,(byte)67,(byte)108,(byte)97,(byte)115,(byte)115,(byte)7,
		(byte)0,(byte)59,(byte)1,(byte)0,(byte)19,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)69,
		(byte)120,(byte)99,(byte)101,(byte)112,(byte)116,(byte)105,(byte)111,(byte)110,(byte)1,(byte)0,(byte)3,(byte)99,(byte)108,(byte)115,(byte)1,(byte)0,
		(byte)21,(byte)76,(byte)106,(byte)97,(byte)118,(byte)97,(byte)47,(byte)108,(byte)97,(byte)110,(byte)103,(byte)47,(byte)69,(byte)120,(byte)99,(byte)101,
		(byte)112,(byte)116,(byte)105,(byte)111,(byte)110,(byte)59,(byte)1,(byte)0,(byte)10,(byte)83,(byte)111,(byte)117,(byte)114,(byte)99,(byte)101,(byte)70,
		(byte)105,(byte)108,(byte)101,(byte)1,(byte)0,(byte)6,(byte)36,(byte)46,(byte)106,(byte)97,(byte)118,(byte)97,(byte)0,(byte)33,(byte)0,(byte)1,
		(byte)0,(byte)3,(byte)0,(byte)0,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)5,(byte)0,(byte)6,(byte)0,(byte)0,(byte)0,(byte)4,
		(byte)0,(byte)1,(byte)0,(byte)7,(byte)0,(byte)8,(byte)0,(byte)1,(byte)0,(byte)9,(byte)0,(byte)0,(byte)0,(byte)68,(byte)0,(byte)2,
		(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)18,(byte)42,(byte)-73,(byte)0,(byte)10,(byte)42,(byte)-72,(byte)0,(byte)12,(byte)-74,(byte)0,
		(byte)18,(byte)-64,(byte)0,(byte)3,(byte)-75,(byte)0,(byte)22,(byte)-79,(byte)0,(byte)0,(byte)0,(byte)2,(byte)0,(byte)24,(byte)0,(byte)0,
		(byte)0,(byte)14,(byte)0,(byte)3,(byte)0,(byte)0,(byte)0,(byte)20,(byte)0,(byte)4,(byte)0,(byte)18,(byte)0,(byte)17,(byte)0,(byte)22,
		(byte)0,(byte)25,(byte)0,(byte)0,(byte)0,(byte)12,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)18,(byte)0,(byte)26,(byte)0,(byte)27,
		(byte)0,(byte)0,(byte)0,(byte)1,(byte)0,(byte)28,(byte)0,(byte)29,(byte)0,(byte)2,(byte)0,(byte)30,(byte)0,(byte)0,(byte)0,(byte)4,
		(byte)0,(byte)1,(byte)0,(byte)31,(byte)0,(byte)9,(byte)0,(byte)0,(byte)0,(byte)61,(byte)0,(byte)2,(byte)0,(byte)2,(byte)0,(byte)0,
		(byte)0,(byte)9,(byte)42,(byte)-76,(byte)0,(byte)22,(byte)43,(byte)-74,(byte)0,(byte)33,(byte)-80,(byte)0,(byte)0,(byte)0,(byte)2,(byte)0,
		(byte)24,(byte)0,(byte)0,(byte)0,(byte)6,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)26,(byte)0,(byte)25,(byte)0,(byte)0,(byte)0,
		(byte)22,(byte)0,(byte)2,(byte)0,(byte)0,(byte)0,(byte)9,(byte)0,(byte)26,(byte)0,(byte)27,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)9,(byte)0,(byte)35,(byte)0,(byte)36,(byte)0,(byte)1,(byte)0,(byte)1,(byte)0,(byte)37,(byte)0,(byte)38,(byte)0,(byte)1,(byte)0,
		(byte)9,(byte)0,(byte)0,(byte)0,(byte)112,(byte)0,(byte)3,(byte)0,(byte)3,(byte)0,(byte)0,(byte)0,(byte)26,(byte)42,(byte)-76,(byte)0,
		(byte)22,(byte)43,(byte)-74,(byte)0,(byte)39,(byte)-74,(byte)0,(byte)33,(byte)87,(byte)-89,(byte)0,(byte)13,(byte)77,(byte)-69,(byte)0,(byte)45,
		(byte)89,(byte)44,(byte)-73,(byte)0,(byte)47,(byte)-65,(byte)-79,(byte)0,(byte)1,(byte)0,(byte)0,(byte)0,(byte)12,(byte)0,(byte)15,(byte)0,
		(byte)31,(byte)0,(byte)2,(byte)0,(byte)24,(byte)0,(byte)0,(byte)0,(byte)22,(byte)0,(byte)5,(byte)0,(byte)0,(byte)0,(byte)33,(byte)0,
		(byte)12,(byte)0,(byte)34,(byte)0,(byte)15,(byte)0,(byte)35,(byte)0,(byte)16,(byte)0,(byte)37,(byte)0,(byte)25,(byte)0,(byte)39,(byte)0,
		(byte)25,(byte)0,(byte)0,(byte)0,(byte)32,(byte)0,(byte)3,(byte)0,(byte)0,(byte)0,(byte)26,(byte)0,(byte)26,(byte)0,(byte)27,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)26,(byte)0,(byte)50,(byte)0,(byte)51,(byte)0,(byte)1,(byte)0,(byte)16,(byte)0,(byte)9,(byte)0,
		(byte)52,(byte)0,(byte)53,(byte)0,(byte)2,(byte)0,(byte)1,(byte)0,(byte)54,(byte)0,(byte)38,(byte)0,(byte)1,(byte)0,(byte)9,(byte)0,
		(byte)0,(byte)0,(byte)105,(byte)0,(byte)3,(byte)0,(byte)3,(byte)0,(byte)0,(byte)0,(byte)19,(byte)42,(byte)43,(byte)-73,(byte)0,(byte)55,
		(byte)-89,(byte)0,(byte)13,(byte)77,(byte)-69,(byte)0,(byte)45,(byte)89,(byte)44,(byte)-73,(byte)0,(byte)47,(byte)-65,(byte)-79,(byte)0,(byte)1,
		(byte)0,(byte)0,(byte)0,(byte)5,(byte)0,(byte)8,(byte)0,(byte)58,(byte)0,(byte)2,(byte)0,(byte)24,(byte)0,(byte)0,(byte)0,(byte)22,
		(byte)0,(byte)5,(byte)0,(byte)0,(byte)0,(byte)45,(byte)0,(byte)5,(byte)0,(byte)46,(byte)0,(byte)8,(byte)0,(byte)47,(byte)0,(byte)9,
		(byte)0,(byte)49,(byte)0,(byte)18,(byte)0,(byte)51,(byte)0,(byte)25,(byte)0,(byte)0,(byte)0,(byte)32,(byte)0,(byte)3,(byte)0,(byte)0,
		(byte)0,(byte)19,(byte)0,(byte)26,(byte)0,(byte)27,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)19,(byte)0,(byte)60,(byte)0,(byte)51,
		(byte)0,(byte)1,(byte)0,(byte)9,(byte)0,(byte)9,(byte)0,(byte)52,(byte)0,(byte)61,(byte)0,(byte)2,(byte)0,(byte)1,(byte)0,(byte)62,
		(byte)0,(byte)0,(byte)0,(byte)2,(byte)0,(byte)63};
	static
	{
		a = new float[256];
		b = new int[257];
		b[0] = b[1] = b[2] = 2;
		b[3] = b[4] = 4;
		b[5] = b[6] = b[7] = b[8] = 8;
		for(int j = 9; j <= 16; j++)
			b[j] = 16;
		for(int i1 = 17; i1 <= 32; i1++)
			b[i1] = 32;
		for(int j1 = 33; j1 <= 64; j1++)
			b[j1] = 64;
		for(int k1 = 65; k1 <= 128; k1++)
			b[k1] = 128;
		for(int l1 = 129; l1 <= 256; l1++)
			b[l1] = 256;
		for(int i2 = 0; i2 < 256; i2++)
			a[i2] = i2 * i2;
	}
}
