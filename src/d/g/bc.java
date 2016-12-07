// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 8:16:28
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package d.g;

import java.awt.*;
import java.awt.image.*;

// Referenced classes of package com.gif4j:
// GifFrame, GifImage
class bc
{
	bc(gi gifimage)
	{
		e = null;
		a = 0;
		b = false;
		f = null;
		g = null;
		h = null;
		i = null;
		e = gifimage;
	}

	BufferedImage a()
	{
		if (!b)
		{
			f = new BufferedImage(e.getScreenWidth(), e.getScreenHeight(), 2);
			h = f.createGraphics();
			h.setBackground(c);
			h.clearRect(0, 0, e.getScreenWidth(), e.getScreenHeight());
			b = true;
		}
		BufferedImage bufferedimage = null;
		gf gifframe = e.getFrame(a);
		Point point = gifframe.b(e.getScreenWidth(), e.getScreenHeight());
		if (gifframe.o == 0 || gifframe.o == 1)
		{
			h.drawImage(gifframe.getAsBufferedImage(), null, point.x, point.y);
			bufferedimage = f;
		}
		else if (gifframe.o == 3)
		{
			if (g == null)
			{
				g = new BufferedImage(e.getScreenWidth(), e.getScreenHeight(), 2);
				i = g.createGraphics();
				i.setBackground(c);
				i.clearRect(0, 0, e.getScreenWidth(), e.getScreenHeight());
			}
			int ai[] = ((DataBufferInt)f.getRaster().getDataBuffer()).getData();
			int ai2[] = ((DataBufferInt)g.getRaster().getDataBuffer()).getData();
			System.arraycopy(ai, 0, ai2, 0, ai.length);
			i.drawImage(gifframe.getAsBufferedImage(), null, point.x, point.y);
			bufferedimage = g;
		}
		else if (gifframe.o == 2)
		{
			if (g == null)
			{
				g = new BufferedImage(e.getScreenWidth(), e.getScreenHeight(), 2);
				i = g.createGraphics();
			}
			int ai1[] = ((DataBufferInt)f.getRaster().getDataBuffer()).getData();
			int ai3[] = ((DataBufferInt)g.getRaster().getDataBuffer()).getData();
			System.arraycopy(ai1, 0, ai3, 0, ai1.length);
			i.drawImage(gifframe.getAsBufferedImage(), null, point.x, point.y);
			bufferedimage = g;
			h.setBackground(c);
			h.clearRect(point.x, point.y, gifframe.width, gifframe.height);
		}
		if (++a == e.getNumberOfFrames())
			b();
		return bufferedimage;
	}

	void b()
	{
		if (b)
		{
			b = false;
			if (h != null)
				h.dispose();
			if (i != null)
				i.dispose();
		}
		g = null;
		i = null;
		f = null;
		h = null;
		e = null;
	}

	private gi e;
	int a;
	boolean b;
	private BufferedImage f;
	private BufferedImage g;
	private Graphics2D h;
	private Graphics2D i;
	static final Color c;
	static final int d;
	static
	{
		c = new Color(0xffffff, true);
		d = c.getRGB();
	}
}
