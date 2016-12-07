// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 8:18:17
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package b;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sun.awt.image.BytePackedRaster;

// Referenced classes of package com.gif4j:
// Watermark, l, GifImage
public class iu
{
	public iu()
	{
	}

	static final void a(Image image) throws InterruptedException
	{
		int i;
		synchronized(a)
		{
			i = c++;
		}
		b.addImage(image, i);
		try
		{
			b.waitForID(i, 0L);
			if (b.isErrorID(i))
				throw new InterruptedException("Can't load image");
		}
		catch(InterruptedException interruptedexception)
		{
			throw interruptedexception;
		}
		finally
		{
			b.removeImage(image, i);
		}
	}

	static final boolean b(Image image)
	{
		if (image instanceof BufferedImage)
		{
			BufferedImage bufferedimage = (BufferedImage)image;
			return bufferedimage.getColorModel().hasAlpha();
		}
		PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, 1, 1, false);
		try
		{
			pixelgrabber.grabPixels();
		}
		catch(InterruptedException interruptedexception)
		{
		}
		ColorModel colormodel = pixelgrabber.getColorModel();
		if (colormodel instanceof IndexColorModel)
			return ((IndexColorModel)colormodel).getTransparentPixel() != -1;
		if (colormodel instanceof DirectColorModel)
			return ((DirectColorModel)colormodel).getAlphaMask() != 0;
		else
			return false;
	}

	static final BufferedImage a(BufferedImage bufferedimage, boolean flag)
	{
		if (bufferedimage.getType() != 10)
			return null;
		byte abyte0[] = new byte[256];
		for(int i = 0; i < 256; i++)
			abyte0[i] = (byte)i;
		if (flag)
		{
			IndexColorModel indexcolormodel = new IndexColorModel(8, 256, abyte0, abyte0, abyte0, 256);
			WritableRaster writableraster = indexcolormodel.createCompatibleWritableRaster(bufferedimage.getWidth(),
				bufferedimage.getHeight());
			byte abyte1[] = ((DataBufferByte)writableraster.getDataBuffer()).getData();
			byte abyte3[] = ((DataBufferByte)bufferedimage.getRaster().getDataBuffer()).getData();
			for(int j = 0; j < abyte3.length; j++)
				if (abyte3[j] == -1)
					abyte3[j] = -2;
			System.arraycopy(abyte3, 0, abyte1, 0, abyte3.length);
			return new BufferedImage(indexcolormodel, writableraster, false, null);
		}
		else
		{
			IndexColorModel indexcolormodel1 = new IndexColorModel(8, 256, abyte0, abyte0, abyte0);
			WritableRaster writableraster1 = indexcolormodel1.createCompatibleWritableRaster(bufferedimage.getWidth(),
				bufferedimage.getHeight());
			byte abyte2[] = ((DataBufferByte)writableraster1.getDataBuffer()).getData();
			byte abyte4[] = ((DataBufferByte)bufferedimage.getRaster().getDataBuffer()).getData();
			System.arraycopy(abyte4, 0, abyte2, 0, abyte4.length);
			return new BufferedImage(indexcolormodel1, writableraster1, false, null);
		}
	}

	static final BufferedImage b(BufferedImage bufferedimage, boolean flag)
	{
		if (bufferedimage.getType() != 12)
			return null;
		int i = bufferedimage.getWidth();
		int j = bufferedimage.getHeight();
		IndexColorModel indexcolormodel = (IndexColorModel)bufferedimage.getColorModel();
		int k = indexcolormodel.getMapSize();
		IndexColorModel indexcolormodel1 = null;
		if (flag)
		{
			byte byte0 = 0;
			if (k <= 2)
				byte0 = 2;
			else if (k <= 4)
				byte0 = 3;
			else if (k <= 16)
				byte0 = 5;
			byte abyte0[];
			indexcolormodel.getReds(abyte0 = new byte[k * 2]);
			byte abyte2[];
			indexcolormodel.getGreens(abyte2 = new byte[k * 2]);
			byte abyte4[];
			indexcolormodel.getBlues(abyte4 = new byte[k * 2]);
			indexcolormodel1 = new IndexColorModel(byte0, k * 2, abyte0, abyte2, abyte4, k * 2);
		}
		else
		{
			byte byte1 = 0;
			if (k <= 2)
				byte1 = 1;
			else if (k <= 4)
				byte1 = 2;
			else if (k <= 16)
				byte1 = 4;
			byte abyte1[];
			indexcolormodel.getReds(abyte1 = new byte[k]);
			byte abyte3[];
			indexcolormodel.getGreens(abyte3 = new byte[k]);
			byte abyte5[];
			indexcolormodel.getBlues(abyte5 = new byte[k]);
			indexcolormodel1 = new IndexColorModel(byte1, k, abyte1, abyte3, abyte5);
		}
		byte abyte6[] = ((BytePackedRaster)bufferedimage.getRaster()).getByteData(0,
			0,
			bufferedimage.getWidth(),
			bufferedimage.getHeight(),
			null);
		DataBufferByte databufferbyte = new DataBufferByte(abyte6, abyte6.length);
		return new BufferedImage(indexcolormodel1, Raster.createInterleavedRaster(databufferbyte,
			i,
			j,
			i,
			1,
			new int[]{0},
			new Point(0, 0)), false, null);
	}

	public static final BufferedImage toBufferedImage(Image image)
	{
		BufferedImage bufferedimage;
		Graphics2D graphics2d;
		if (image == null)
			throw new NullPointerException("image is null!");
		if (image instanceof BufferedImage)
			return (BufferedImage)image;
		try
		{
			a(image);
		}
		catch(InterruptedException interruptedexception)
		{
			return null;
		}
		byte byte0 = 1;
		if (b(image))
			byte0 = 2;
		try
		{
			a(image);
		}
		catch(InterruptedException interruptedexception1)
		{
			return null;
		}
		bufferedimage = new BufferedImage(image.getWidth(null), image.getHeight(null), byte0);
		graphics2d = null;
		graphics2d = bufferedimage.createGraphics();
		try
		{
			graphics2d.drawImage(image, 0, 0, null);
		}
		finally
		{
			if (graphics2d != null)
				graphics2d.dispose();
		}
		return bufferedimage;
	}

	public static final BufferedImage scale(BufferedImage bufferedimage, int i, int j, boolean flag)
	{
		int k;
		int i1;
		BufferedImage bufferedimage1;
		Graphics2D graphics2d;
		if (bufferedimage == null)
			throw new NullPointerException("source image == null!");
		k = bufferedimage.getWidth();
		i1 = bufferedimage.getHeight();
		if (k <= i && i1 <= j)
			return bufferedimage;
		if (i <= 0 && j <= 0)
			return bufferedimage;
		double d = (double)i / (double)k;
		double d1 = (double)j / (double)i1;
		if (i == 0)
			d = d1;
		if (j == 0)
			d1 = d;
		AffineTransform affinetransform = null;
		if (flag)
		{
			if (d < d1)
			{
				k = (int)Math.rint((double)k * d - 0.01D);
				i1 = (int)Math.rint((double)i1 * d - 0.01D);
				affinetransform = AffineTransform.getScaleInstance(d, d);
			}
			else
			{
				k = (int)Math.rint((double)k * d1 - 0.01D);
				i1 = (int)Math.rint((double)i1 * d1 - 0.01D);
				affinetransform = AffineTransform.getScaleInstance(d1, d1);
			}
		}
		else
		{
			k = (int)Math.rint((double)k * d - 0.01D);
			i1 = (int)Math.rint((double)i1 * d1 - 0.01D);
			affinetransform = AffineTransform.getScaleInstance(d, d1);
		}
		AffineTransformOp affinetransformop = new AffineTransformOp(affinetransform, 2);
		bufferedimage1 = null;
		boolean flag1 = false;
		try
		{
			bufferedimage1 = new BufferedImage(k, i1, bufferedimage.getType());
			affinetransformop.filter(bufferedimage, bufferedimage1);
			flag1 = true;
		}
		catch(ImagingOpException imagingopexception)
		{
		}
		catch(IllegalArgumentException illegalargumentexception)
		{
		}
		if (!flag1)
		{
			byte byte0 = 1;
			if (bufferedimage.getColorModel().hasAlpha())
				byte0 = 2;
			bufferedimage1 = new BufferedImage(k, i1, byte0);
		}
		graphics2d = null;
		graphics2d = bufferedimage1.createGraphics();
		try
		{
			graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			graphics2d.drawImage(bufferedimage, 0, 0, k, i1, null);
		}
		finally
		{
			if (graphics2d != null)
				graphics2d.dispose();
		}
		return bufferedimage1;
	}

	public static BufferedImage borderWithPaint(BufferedImage bufferedimage, int i, int j, Paint paint)
	{
		BufferedImage bufferedimage1;
		Graphics2D graphics2d;
		if (bufferedimage == null)
			throw new NullPointerException("image is null!");
		if (bufferedimage.getWidth() >= i && bufferedimage.getHeight() >= j)
			return bufferedimage;
		if (bufferedimage.getWidth() > i)
			i = bufferedimage.getWidth();
		if (bufferedimage.getHeight() > j)
			j = bufferedimage.getHeight();
		byte byte0 = 1;
		if (bufferedimage.getColorModel().hasAlpha())
			byte0 = 2;
		bufferedimage1 = new BufferedImage(i, j, byte0);
		graphics2d = null;
		graphics2d = bufferedimage1.createGraphics();
		try
		{
			graphics2d.setPaint(paint);
			graphics2d.fillRect(0, 0, i, j);
			graphics2d.drawImage(bufferedimage,
				null,
				(i - bufferedimage.getWidth()) / 2,
				(j - bufferedimage.getHeight()) / 2);
		}
		finally
		{
			if (graphics2d != null)
				graphics2d.dispose();
		}
		return bufferedimage1;
	}

	public static final BufferedImage addInsets(BufferedImage bufferedimage, Insets insets, Paint paint)
	{
		BufferedImage bufferedimage1;
		Graphics2D graphics2d;
		if (bufferedimage == null)
			throw new NullPointerException("image is null!");
		byte byte0 = 1;
		if (bufferedimage.getColorModel().hasAlpha())
			byte0 = 2;
		bufferedimage1 = new BufferedImage(bufferedimage.getWidth() + insets.left + insets.right,
			bufferedimage.getHeight() + insets.top + insets.bottom,
			byte0);
		graphics2d = null;
		graphics2d = bufferedimage1.createGraphics();
		try
		{
			graphics2d.setPaint(paint);
			graphics2d.fillRect(0, 0, bufferedimage1.getWidth(), bufferedimage1.getHeight());
			graphics2d.drawImage(bufferedimage, null, insets.left, insets.top);
		}
		finally
		{
			if (graphics2d != null)
				graphics2d.dispose();
		}
		return bufferedimage1;
	}

	private static final Component a;
	private static final MediaTracker b;
	private static int c = 0;
	static
	{
		a = new l();
		b = new MediaTracker(a);
	}

	// Unreferenced inner class com/gif4j/l
	static class l extends Component
	{
		l()
		{
		}
	}
}
