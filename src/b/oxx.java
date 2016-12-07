// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 16:57:11
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package b;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.*;

class oxx
{
	oxx()
	{
	}

	static final int[] a(BufferedImage bufferedimage)
	{
		int ai[];
		BufferedImage bufferedimage1;
		Graphics2D graphics2d;
		boolean flag;
		int i = bufferedimage.getType();
		if (bufferedimage.getRaster().getParent() == null && (i == 1 || i == 2 || i == 3))
		{
			int ai1[] = ((DataBufferInt)bufferedimage.getRaster().getDataBuffer()).getData();
			if (bufferedimage.getProperty("GIF4J") != null
				&& bufferedimage.getProperty("GIF4J") != Image.UndefinedProperty)
			{
				ai = ai1;
			}
			else
			{
				ai = new int[ai1.length];
				System.arraycopy(ai1, 0, ai, 0, ai1.length);
			}
		}
		else
		{
			byte byte0 = 1;
			if (bufferedimage.getColorModel().hasAlpha())
				byte0 = 2;
			bufferedimage1 = new BufferedImage(bufferedimage.getWidth(), bufferedimage.getHeight(), byte0);
			graphics2d = null;
			flag = false;
			try
			{
				graphics2d = bufferedimage1.createGraphics();
				graphics2d.drawImage(bufferedimage, null, 0, 0);
				flag = true;
			}
			finally
			{
				if (graphics2d != null)
					graphics2d.dispose();
			}
			if (flag)
				ai = ((DataBufferInt)bufferedimage1.getRaster().getDataBuffer()).getData();
			else
				ai = bufferedimage.getRGB(0,
					0,
					bufferedimage.getWidth(),
					bufferedimage.getHeight(),
					null,
					0,
					bufferedimage.getWidth());
		}
		return ai;
	}

	static final int a(int i)
	{
		int j = 0;
		int k = i;
		while((i >>>= 1) > 0)
			j++;
		if (1 << j < k)
			j++;
		return j;
	}
}
