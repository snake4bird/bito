// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 8:17:40
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package b;

import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;

// Referenced classes of package com.gif4j:
// ImageUtils, Watermark, MorphingFilter
public class gf
{
	gf()
	{
		b = 0;
		c = 0;
		f = true;
		g = false;
		h = false;
		p = false;
		q = false;
		r = -1;
		s = -1;
		t = null;
		u = -1;
		v = 0;
		w = null;
		x = false;
	}

	public gf(BufferedImage bufferedimage)
	{
		this(bufferedimage, new Point(0, 0), 1, -1);
	}

	public gf(BufferedImage bufferedimage, int i1)
	{
		this(bufferedimage, i1, 1, -1);
	}

	public gf(BufferedImage bufferedimage, int i1, int j1)
	{
		this(bufferedimage, i1, j1, -1);
	}

	public gf(BufferedImage bufferedimage, int i1, int j1, int k1)
	{
		this(bufferedimage, new Point(0, 0), j1, k1);
		setLayoutConstraint(i1);
	}

	public gf(BufferedImage bufferedimage, Point point)
	{
		this(bufferedimage, point, 1, -1);
	}

	public gf(BufferedImage bufferedimage, Point point, int i1)
	{
		this(bufferedimage, point, i1, -1);
	}

	public gf(BufferedImage bufferedimage, Point point, int i1, int j1)
	{
		b = 0;
		c = 0;
		f = true;
		g = false;
		h = false;
		p = false;
		q = false;
		r = -1;
		s = -1;
		t = null;
		u = -1;
		v = 0;
		w = null;
		x = false;
		if (bufferedimage == null)
			throw new NullPointerException("image is null!");
		b = point.x;
		c = point.y;
		if (b < 0)
			b = 0;
		if (c < 0)
			c = 0;
		if (j1 <= 0 && j1 != -1)
		{
			throw new IllegalArgumentException("delay time (in 1/100 sec) should be greater than 0.");
		}
		else
		{
			a(bufferedimage);
			setDisposalMethod(i1);
			setDelay(j1);
			return;
		}
	}

	public gf(gf gf)
	{
		this(gf.getAsBufferedImage(), new Point(gf.b, gf.c), gf.o, gf.c);
	}

	public void setDisposalMethod(int i1)
	{
		if (i1 < 0 || i1 > 3)
		{
			throw new IllegalArgumentException("unknown disposal method.");
		}
		else
		{
			o = i1;
			return;
		}
	}

	public int getDisposalMethod()
	{
		return o;
	}

	public int getX()
	{
		return b;
	}

	public int getY()
	{
		return c;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setLayoutConstraint(int i1)
	{
		if (i1 < 0 || i1 > 8)
		{
			throw new IllegalArgumentException("unknown layout constraint.");
		}
		else
		{
			u = i1;
			return;
		}
	}

	public void setDelay(int i1)
	{
		if (i1 < 0)
			i1 = -1;
		s = i1;
	}

	public int getDelay()
	{
		if (s < 0)
			return 0;
		else
			return s;
	}

	public boolean isInterlaced()
	{
		return h;
	}

	public void setInterlaced(boolean flag)
	{
		h = flag;
	}

	public IndexColorModel getColorModel()
	{
		byte abyte0[] = new byte[k.length];
		System.arraycopy(k, 0, abyte0, 0, k.length);
		byte abyte1[] = new byte[l.length];
		System.arraycopy(l, 0, abyte1, 0, l.length);
		byte abyte2[] = new byte[m.length];
		System.arraycopy(m, 0, abyte2, 0, m.length);
		return new IndexColorModel(i, Math.min(j, k.length), abyte0, abyte1, abyte2, r);
	}

	public Image getAsImage()
	{
		byte abyte0[] = new byte[n.length];
		System.arraycopy(n, 0, abyte0, 0, n.length);
		return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width,
			height,
			getColorModel(),
			abyte0,
			0,
			width));
	}

	public BufferedImage getAsBufferedImage()
	{
		DataBufferByte databufferbyte = new DataBufferByte(n.length);
		byte abyte0[] = databufferbyte.getData();
		System.arraycopy(n, 0, abyte0, 0, n.length);
		WritableRaster writableraster = Raster.createInterleavedRaster(databufferbyte,
			width,
			height,
			width,
			1,
			new int[]{0},
			null);
		IndexColorModel indexcolormodel = getColorModel();
		return new BufferedImage(indexcolormodel, writableraster, indexcolormodel.isAlphaPremultiplied(), null);
	}

	void a(BufferedImage bufferedimage)
	{
		if (bufferedimage == null)
		{
			return;
		}
		else
		{
			t = bufferedimage;
			width = bufferedimage.getWidth();
			height = bufferedimage.getHeight();
			return;
		}
	}

	void buildFrame(int i1)
	{
		try
		{
			if (a(i1))
				return;
		}
		catch(Exception e)
		{
		}
		if (t.getType() != 13)
		{
			if (t.getType() == 10)
				t = iu.a(t, i1 != -1);
			else if (t.getType() == 12)
				t = iu.b(t, i1 != -1);
			else
				t = qu.q(t, 8, i1 != -1);
		}
		else if (!(t.getRaster().getDataBuffer() instanceof DataBufferByte)
			|| w != null
				&& ((IndexColorModel)t.getColorModel()).getTransparentPixel() == -1)
			t = qu.q(t, 8, i1 != -1);
		b(t);
		x = true;
	}

	void b(BufferedImage bufferedimage)
	{
		IndexColorModel indexcolormodel = (IndexColorModel)bufferedimage.getColorModel();
		indexcolormodel.getReds(k = new byte[indexcolormodel.getMapSize()]);
		indexcolormodel.getBlues(m = new byte[indexcolormodel.getMapSize()]);
		indexcolormodel.getGreens(l = new byte[indexcolormodel.getMapSize()]);
		byte abyte0[] = ((DataBufferByte)bufferedimage.getRaster().getDataBuffer()).getData();
		n = new byte[abyte0.length];
		System.arraycopy(abyte0, 0, n, 0, abyte0.length);
		i = a[k.length];
		j = 1 << i;
		q = indexcolormodel.hasAlpha();
		r = indexcolormodel.getTransparentPixel();
	}

	void a(int i1, int j1)
	{
		if (u != -1)
		{
			switch(u)
			{
			case 0: // '\0'
				b = 0;
				c = 0;
				break;
			case 1: // '\001'
				b = (i1 - width) / 2;
				c = 0;
				break;
			case 2: // '\002'
				b = i1 - width;
				c = 0;
				break;
			case 3: // '\003'
				b = 0;
				c = (j1 - height) / 2;
				break;
			case 4: // '\004'
				b = (i1 - width) / 2;
				c = (j1 - height) / 2;
				break;
			case 5: // '\005'
				b = i1 - width;
				c = (j1 - height) / 2;
				break;
			case 6: // '\006'
				b = 0;
				c = j1 - height;
				break;
			case 7: // '\007'
				b = (i1 - width) / 2;
				c = j1 - height;
				break;
			case 8: // '\b'
				b = i1 - width;
				c = j1 - height;
				break;
			}
			u = -1;
		}
	}

	Point b(int i1, int j1)
	{
		Point point = new Point(b, c);
		if (u != -1)
			switch(u)
			{
			case 0: // '\0'
				point.x = 0;
				point.y = 0;
				break;
			case 1: // '\001'
				point.x = (i1 - width) / 2;
				point.y = 0;
				break;
			case 2: // '\002'
				point.x = i1 - width;
				point.y = 0;
				break;
			case 3: // '\003'
				point.x = 0;
				point.y = (j1 - height) / 2;
				break;
			case 4: // '\004'
				point.x = (i1 - width) / 2;
				point.y = (j1 - height) / 2;
				break;
			case 5: // '\005'
				point.x = i1 - width;
				point.y = (j1 - height) / 2;
				break;
			case 6: // '\006'
				point.x = 0;
				point.y = j1 - height;
				break;
			case 7: // '\007'
				point.x = (i1 - width) / 2;
				point.y = j1 - height;
				break;
			case 8: // '\b'
				point.x = i1 - width;
				point.y = j1 - height;
				break;
			}
		return point;
	}

	void a()
	{
		k = m = l = n = null;
		t = null;
	}

	byte[] b()
	{
		byte abyte0[] = new byte[j * 3];
		int i1 = 0;
		int j1 = 0;
		for(; i1 < k.length; i1++)
		{
			abyte0[j1] = k[i1];
			j1++;
			abyte0[j1] = l[i1];
			j1++;
			abyte0[j1] = m[i1];
			j1++;
		}
		return abyte0;
	}

	byte[] c()
	{
		byte abyte0[] = new byte[8];
		abyte0[0] = 33;
		abyte0[1] = -7;
		abyte0[2] = 4;
		abyte0[3] = (byte)(o << 2 | (p?2:0) | (q?1:0));
		abyte0[4] = (byte)(s & 0xff);
		abyte0[5] = (byte)(s >> 8 & 0xff);
		abyte0[6] = (byte)r;
		abyte0[7] = 0;
		return abyte0;
	}

	byte[] d()
	{
		byte abyte0[] = new byte[10];
		abyte0[0] = 44;
		abyte0[1] = (byte)(b & 0xff);
		abyte0[2] = (byte)(b >> 8 & 0xff);
		abyte0[3] = (byte)(c & 0xff);
		abyte0[4] = (byte)(c >> 8 & 0xff);
		abyte0[5] = (byte)(width & 0xff);
		abyte0[6] = (byte)(width >> 8 & 0xff);
		abyte0[7] = (byte)(height & 0xff);
		abyte0[8] = (byte)(height >> 8 & 0xff);
		abyte0[9] = (byte)((f?0x80:0) | (h?0x40:0) | i - 1);
		return abyte0;
	}

	byte[] e()
	{
		if (h)
		{
			byte abyte0[] = new byte[n.length];
			int i1 = 0;
			for(int j1 = 0; j1 < height;)
			{
				System.arraycopy(n, j1 * width, abyte0, i1 * width, width);
				j1 += 8;
				i1++;
			}
			for(int k1 = 4; k1 < height;)
			{
				System.arraycopy(n, k1 * width, abyte0, i1 * width, width);
				k1 += 8;
				i1++;
			}
			for(int l1 = 2; l1 < height;)
			{
				System.arraycopy(n, l1 * width, abyte0, i1 * width, width);
				l1 += 4;
				i1++;
			}
			for(int i2 = 1; i2 < height;)
			{
				System.arraycopy(n, i2 * width, abyte0, i1 * width, width);
				i2 += 2;
				i1++;
			}
			return abyte0;
		}
		else
		{
			return n;
		}
	}

	gf f()
	{
		return a(false);
	}

	gf a(boolean flag)
	{
		gf gifframe = new gf();
		gifframe.n = new byte[n.length];
		if (flag)
		{
			for(int i1 = 0; i1 < n.length; i1++)
				gifframe.n[i1] = (byte)r;
		}
		else
		{
			System.arraycopy(n, 0, gifframe.n, 0, n.length);
		}
		if (f)
		{
			gifframe.f = true;
			gifframe.k = new byte[k.length];
			gifframe.l = new byte[l.length];
			gifframe.m = new byte[m.length];
			System.arraycopy(k, 0, gifframe.k, 0, k.length);
			System.arraycopy(l, 0, gifframe.l, 0, l.length);
			System.arraycopy(m, 0, gifframe.m, 0, m.length);
		}
		else
		{
			gifframe.f = false;
			if (k != null)
			{
				gifframe.k = new byte[k.length];
				System.arraycopy(k, 0, gifframe.k, 0, k.length);
			}
			if (l != null)
			{
				gifframe.l = new byte[l.length];
				System.arraycopy(l, 0, gifframe.l, 0, l.length);
			}
			if (m != null)
			{
				gifframe.m = new byte[m.length];
				System.arraycopy(m, 0, gifframe.m, 0, m.length);
			}
		}
		gifframe.h = h;
		gifframe.s = s;
		gifframe.o = o;
		gifframe.u = u;
		gifframe.i = i;
		gifframe.j = j;
		gifframe.g = g;
		gifframe.p = p;
		gifframe.q = q;
		gifframe.r = r;
		gifframe.b = b;
		gifframe.c = c;
		gifframe.width = width;
		gifframe.height = height;
		gifframe.w = w;
		gifframe.v = v;
		gifframe.x = true;
		return gifframe;
	}

	gf g()
	{
		gf gifframe = new gf();
		gifframe.h = h;
		gifframe.s = s;
		gifframe.o = o;
		gifframe.u = u;
		gifframe.p = p;
		gifframe.b = b;
		gifframe.c = c;
		gifframe.width = width;
		gifframe.height = height;
		gifframe.x = true;
		gifframe.w = w;
		gifframe.v = v;
		return gifframe;
	}

	// 第一次执行解码动作时，检查调用者信息
	boolean a(int n) throws Exception
	{
		if (x && y)
		{
			try
			{
				qu.q(null, 0);
			}
			catch(Exception e)
			{
				String dg = this.getClass().getPackage().getName();
				String d = dg.substring(0, dg.indexOf("."));
				String[] s = new String[]{d + ".g.gd.decode", d + ".$.c", d + ".$.<clinit>"};
				StackTraceElement[] est = e.getStackTrace();
				int xx = 0;
				for(int i = 0; i < est.length; i++)
				{
					String cm = est[i].getClassName() + "." + est[i].getMethodName();
					if (xx < s.length && cm.equals(s[xx]))
					{
						xx++;
					}
				}
				if (mj.x == mj.class && xx != s.length)
				{
					x = false;
				}
			}
			y = !y;
		}
		v = n;
		return x;
	}

	gf a(int i1, int j1, int k1, int l1, boolean flag)
	{
		gf gifframe = new gf();
		gifframe.n = new byte[k1 * l1];
		if (flag)
		{
			for(int i2 = 0; i2 < gifframe.n.length; i2++)
				gifframe.n[i2] = (byte)r;
		}
		else
		{
			for(int j2 = 0; j2 < l1; j2++)
				System.arraycopy(n, (j1 + j2) * width + i1, gifframe.n, j2 * k1, k1);
		}
		if (f)
		{
			gifframe.k = new byte[k.length];
			gifframe.l = new byte[l.length];
			gifframe.m = new byte[m.length];
			System.arraycopy(k, 0, gifframe.k, 0, k.length);
			System.arraycopy(l, 0, gifframe.l, 0, l.length);
			System.arraycopy(m, 0, gifframe.m, 0, m.length);
		}
		else
		{
			gifframe.f = false;
		}
		gifframe.h = h;
		gifframe.s = s;
		gifframe.o = o;
		gifframe.u = u;
		gifframe.i = i;
		gifframe.j = j;
		gifframe.g = g;
		gifframe.p = p;
		gifframe.q = q;
		gifframe.r = r;
		gifframe.b = 0;
		gifframe.c = 0;
		gifframe.width = k1;
		gifframe.height = l1;
		gifframe.x = true;
		return gifframe;
	}

	public static final int DISPOSAL_METHOD_NOT_SPECIFIED = 0;
	public static final int DISPOSAL_METHOD_DO_NOT_DISPOSE = 1;
	public static final int DISPOSAL_METHOD_RESTORE_TO_BACKGROUND_COLOR = 2;
	public static final int DISPOSAL_METHOD_RESTORE_TO_PREVIOUS = 3;
	public static final int LAYOUT_TOP_LEFT = 0;
	public static final int LAYOUT_TOP_CENTER = 1;
	public static final int LAYOUT_TOP_RIGHT = 2;
	public static final int LAYOUT_MIDDLE_LEFT = 3;
	public static final int LAYOUT_MIDDLE_CENTER = 4;
	public static final int LAYOUT_MIDDLE_RIGHT = 5;
	public static final int LAYOUT_BOTTOM_LEFT = 6;
	public static final int LAYOUT_BOTTOM_CENTER = 7;
	public static final int LAYOUT_BOTTOM_RIGHT = 8;
	static final byte a[];
	static boolean y = true;
	int b;
	int c;
	int width;
	int height;
	boolean f;
	boolean g;
	boolean h;
	int i;
	int j;
	byte k[];
	byte l[];
	byte m[];
	byte n[];
	int o;
	boolean p;
	boolean q;
	int r;
	int s;
	BufferedImage t;
	int u;
	int v;
	fm w;
	boolean x;
	static
	{
		a = new byte[257];
		a[1] = a[2] = 1;
		a[3] = a[4] = 2;
		a[5] = a[6] = a[7] = a[8] = 3;
		for(int i1 = 9; i1 <= 16; i1++)
			a[i1] = 4;
		for(int j1 = 17; j1 <= 32; j1++)
			a[j1] = 5;
		for(int k1 = 33; k1 <= 64; k1++)
			a[k1] = 6;
		for(int l1 = 65; l1 <= 128; l1++)
			a[l1] = 7;
		for(int i2 = 129; i2 <= 256; i2++)
			a[i2] = 8;
	}
}
