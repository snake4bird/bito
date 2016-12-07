// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 16:44:05
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package b;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

// Referenced classes of package com.gif4j.quantizer:
// c, q
public final class qu
{
	public qu()
	{
	}

	public static BufferedImage q(BufferedImage bufferedimage, int i)
	{
		return q(a, bufferedimage, i, false);
	}

	public static BufferedImage q(BufferedImage bufferedimage, int i, boolean flag)
	{
		return q(a, bufferedimage, i, flag);
	}

	public static BufferedImage q(int i, BufferedImage bufferedimage, int j)
	{
		return q(i, bufferedimage, j, false);
	}

	public static BufferedImage q(int i, BufferedImage bufferedimage, int j, boolean flag)
	{
		if (bufferedimage == null)
			throw new NullPointerException("source image is null");
		if (j < 2 || j > 16)
			throw new IllegalArgumentException("color bit depth must be between 2 and 16.");
		if (bufferedimage.getColorModel().hasAlpha() || flag)
			return cxx.a(bufferedimage, j, false, false);
		else
			return qxx.a(bufferedimage, j, false, false);
	}

	public static void setDefaultMode(int i)
	{
		if (i >= 0 && i <= 7)
			a = i;
	}

	private static int a = 4;
	public static final int MEMORY_LOW_FAST = 0;
	public static final int MEMORY_NORMAL_FAST = 4;
	public static final int MEMORY_LOW_OPTIMIZED = 2;
	public static final int MEMORY_NORMAL_OPTIMIZED = 6;
	public static final int MEMORY_LOW_OPTIMIZED_DITHER = 3;
	public static final int MEMORY_NORMAL_OPTIMIZED_DITHER = 7;
	public static final int MEMORY_LOW_FAST_DITHER = 1;
	public static final int MEMORY_NORMAL_FAST_DITHER = 5;
	public static final int MEMORY_NORMAL_OPTIMIZED_DITHER_SOFT = 8;
}
