// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 8:17:48
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package b;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;

// Referenced classes of package com.gif4j:
// GifFrame, ImageUtils, MorphingFilter, m,
// Watermark
public class gi
{
	public gi()
	{
		this(0, 0, 2);
	}

	gi(boolean flag)
	{
		this(0, 0, 2);
		if (flag)
			o = -1;
	}

	public gi(int i1)
	{
		this(0, 0, i1);
	}

	public gi(int i1, int j1)
	{
		this(i1, j1, 1);
	}

	public gi(int i1, int j1, int k1)
	{
		width = 0;
		height = 0;
		c = false;
		j = false;
		k = -1;
		m = 2;
		n = 200;
		o = 0;
		p = 0;
		r = null;
		s = "89a";
		if (i1 <= 0 && j1 > 0 || i1 > 0 && j1 <= 0)
		{
			throw new IllegalArgumentException("image width and height should be both equal to 0 or greater than 0.");
		}
		else
		{
			a(k1);
			width = i1;
			height = j1;
			t = new Vector();
			return;
		}
	}

	gi a(BufferedImage bufferedimage)
	{
		addGifFrame(new gf(bufferedimage, 4));
		return this;
	}

	public gi addGifFrame(gf gifframe)
	{
		return addGifFrame(gifframe, null);
	}

	public gi addGifFrame(gf gifframe, fm morphingfilter)
	{
		if (gifframe == null)
			throw new NullPointerException("GifFrame is null!");
		if (morphingfilter != null)
			gifframe.w = morphingfilter;
		if (gifframe.b < 0)
			gifframe.b = 0;
		if (gifframe.c < 0)
			gifframe.c = 0;
		if (width == 0 && height == 0)
		{
			width = gifframe.b + gifframe.width;
			height = gifframe.c + gifframe.height;
			t.add(gifframe);
			gifframe.buildFrame(o);
			o++;
		}
		else if (m == 2)
		{
			if (gifframe.b + gifframe.width > width)
				width = gifframe.b + gifframe.width;
			if (gifframe.c + gifframe.height > height)
				height = gifframe.c + gifframe.height;
			t.add(gifframe);
			gifframe.buildFrame(o);
			o++;
		}
		else if (!gifframe.x)
		{
			gifframe.a(a(gifframe.t, gifframe.b, gifframe.c));
			if (gifframe.t != null)
			{
				t.add(gifframe);
				gifframe.buildFrame(o);
				o++;
			}
		}
		else
		{
			t.add(gifframe);
			o++;
		}
		return this;
	}

	BufferedImage a(BufferedImage bufferedimage, int i1, int j1)
	{
		if (bufferedimage == null)
			return null;
		int k1 = bufferedimage.getWidth();
		int l1 = bufferedimage.getHeight();
		boolean flag = false;
		if (i1 + k1 > width)
		{
			k1 = width - i1;
			flag = true;
		}
		if (j1 + l1 > height)
		{
			l1 = height - j1;
			flag = true;
		}
		if (k1 > 0 && l1 > 0)
		{
			if (flag)
				if (m == 0)
					bufferedimage = bufferedimage.getSubimage(0, 0, k1, l1);
				else
					bufferedimage = iu.scale(bufferedimage, k1, l1, true);
			return bufferedimage;
		}
		else
		{
			return null;
		}
	}

	public void setLoopNumber(int i1)
	{
		if (i1 < 0)
			throw new IllegalArgumentException("number should be greater than or equal to 0.");
		if (r == null)
			r = new m(i1);
		else
			r.a(i1);
		if (i1 == 1)
			r = null;
	}

	public int getLoopNumber()
	{
		if (r != null)
			return r.a;
		else
			return -1;
	}

	public void setDefaultDelay(int i1)
	{
		if (i1 <= 0)
		{
			throw new IllegalArgumentException("delay should be greater than 0.");
		}
		else
		{
			n = i1;
			return;
		}
	}

	public int getDefaultDelay()
	{
		return n;
	}

	public int getCurrentLogicWidth()
	{
		return width;
	}

	public int getCurrentLogicHeight()
	{
		return height;
	}

	public int getScreenWidth()
	{
		return width;
	}

	public int getScreenHeight()
	{
		return height;
	}

	public String getVersion()
	{
		return s;
	}

	public void addComment(String s1)
	{
		if (s1 == null)
			throw new NullPointerException("Comment is null!");
		if (s1.length() > 0)
		{
			if (u == null)
				u = new Vector();
			u.addElement(s1);
		}
	}

	public String getComment(int i1)
	{
		if (u == null || i1 < 0 || i1 >= u.size())
			throw new IllegalArgumentException("Invalid comment's index: " + i1);
		else
			return (String)u.elementAt(i1);
	}

	public void removeComment(int i1)
	{
		if (u == null || i1 < 0 || i1 >= u.size())
		{
			throw new IllegalArgumentException("Invalid comment's index: " + i1);
		}
		else
		{
			u.removeElementAt(i1);
			return;
		}
	}

	public int getNumberOfComments()
	{
		if (u == null)
			return 0;
		else
			return u.size();
	}

	public Iterator comments()
	{
		if (u != null)
			return u.iterator();
		else
			return null;
	}

	public Iterator frames()
	{
		if (t != null)
			return t.iterator();
		else
			return null;
	}

	public int getNumberOfFrames()
	{
		if (t == null)
			return 0;
		else
			return t.size();
	}

	public gf getFrame(int i1)
	{
		if (t == null || i1 < 0 || i1 >= t.size())
			throw new IllegalArgumentException("Invalid frame's index: " + i1);
		else
			return (gf)t.elementAt(i1);
	}

	public gf getLastFrame()
	{
		if (t == null || t.size() == 0)
			return null;
		else
			return (gf)t.elementAt(t.size() - 1);
	}

	public Color getTransparentColor()
	{
		return q;
	}

	public void setTransparentColor(Color color)
	{
		q = color;
	}

	public int getResizeStrategy()
	{
		return m;
	}

	void a(int i1)
	{
		if (m >= 0 && m <= 2)
			m = i1;
	}

	Vector a(boolean flag)
	{
		if (t.size() == 0)
			return null;
		Vector vector = new Vector();
		for(int i1 = 0; i1 < t.size(); i1++)
		{
			gf gifframe = (gf)t.get(i1);
			gifframe.a(width, height);
			if (gifframe.s == -1)
				gifframe.s = n;
			if (o == 0 && !c)
			{
				c = true;
				d = gifframe.i;
				e = gifframe.j;
				f = gifframe.k;
				g = gifframe.l;
				h = gifframe.m;
				gifframe.f = false;
			}
			if (gifframe.w != null)
			{
				gf agifframe[] = gifframe.w.a(this, gifframe);
				vector.addAll(Arrays.asList(agifframe));
			}
			else
			{
				vector.add(gifframe);
			}
		}
		if (q != null)
			if (c)
			{
				if (k != -1)
					try
					{
						f[k] = (byte)q.getRed();
						g[k] = (byte)q.getGreen();
						h[k] = (byte)q.getBlue();
					}
					catch(Exception exception)
					{
					}
			}
			else
			{
				Iterator iterator = vector.iterator();
				do
				{
					if (!iterator.hasNext())
						break;
					gf gifframe1 = (gf)iterator.next();
					if (gifframe1.r != -1 && gifframe1.q)
						try
						{
							gifframe1.k[gifframe1.r] = (byte)q.getRed();
							gifframe1.l[gifframe1.r] = (byte)q.getGreen();
							gifframe1.m[gifframe1.r] = (byte)q.getBlue();
						}
						catch(Exception exception1)
						{
						}
				}
				while(true);
			}
		if (r == null)
			r = new m();
		return vector;
	}

	byte[] a()
	{
		byte abyte0[] = new byte[e * 3];
		int i1 = 0;
		int j1 = 0;
		for(; i1 < f.length; i1++)
		{
			abyte0[j1] = f[i1];
			j1++;
			abyte0[j1] = g[i1];
			j1++;
			abyte0[j1] = h[i1];
			j1++;
		}
		return abyte0;
	}

	void b()
	{
		width = 0;
		height = 0;
		c = false;
		d = 0;
		e = 0;
		f = g = h = i = null;
		j = false;
		k = 0;
		l = 0;
		m = 2;
		n = 200;
		o = 0;
		q = null;
		if (t != null)
			t.removeAllElements();
	}

	void a(gi gifimage)
	{
		gifimage.k = k;
		gifimage.p = p;
		gifimage.n = n;
		gifimage.m = m;
		gifimage.l = l;
		gifimage.c = c;
		gifimage.c = c;
		gifimage.d = d;
		gifimage.e = e;
		if (f != null)
		{
			gifimage.f = new byte[f.length];
			System.arraycopy(f, 0, gifimage.f, 0, f.length);
		}
		if (g != null)
		{
			gifimage.g = new byte[g.length];
			System.arraycopy(g, 0, gifimage.g, 0, g.length);
		}
		if (h != null)
		{
			gifimage.h = new byte[h.length];
			System.arraycopy(h, 0, gifimage.h, 0, h.length);
		}
		if (u != null)
			gifimage.u = (Vector)u.clone();
		if (r != null)
			gifimage.r = new m(r.a);
		gifimage.q = q;
	}

	public static final int RESIZE_STRATEGY_CROP_TO_FIT_IMAGE_SIZE = 0;
	public static final int RESIZE_STRATEGY_SCALE_TO_FIT_IMAGE_SIZE = 1;
	public static final int RESIZE_STRATEGY_EXTEND_TO_CURRENT = 2;
	int width;
	int height;
	boolean c;
	int d;
	int e;
	byte f[];
	byte g[];
	byte h[];
	byte i[];
	boolean j;
	int k;
	byte l;
	int m;
	int n;
	int o;
	int p;
	Color q;
	m r;
	String s;
	Vector t;
	Vector u;
}
