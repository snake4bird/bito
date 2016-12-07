package d._;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import d.g.gd;
import d.g.ge;
import d.g.gf;
import d.g.gi;

public class GIFEncrypt
{
	public static byte[] decode(byte[] bs) throws IOException
	{
		return gd.bs(bs);
	}

	public static byte[] encode(String comment, byte[] bs, int chaos_first_frame) throws IOException
	{
		int bytesize = bs.length;
		int points = (bytesize + 4);
		int width = (int)(Math.sqrt(points * 16 / 9) + 0.5);
		int maxwidth = 800;
		int maxheight = 450;
		if (width <= 0)
		{
			width = 1;
		}
		else if (width > maxwidth)
		{
			width = maxwidth;
		}
		int height = (points + width - 1) / width;
		int frames = 1;
		if (height > maxheight)
		{
			frames = (height + maxheight - 1) / maxheight;
			height = maxheight;
		}
		BufferedImage[] images = new BufferedImage[frames + 1];
		images[0] = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
		int n = 0;
		for(int i = 1; i <= frames; i++)
		{
			int c = 0;
			byte[] colorIndexArray = new byte[width * height];
			if (i == 1)
			{
				colorIndexArray[0] = (byte)((bytesize >> 24) & 0xFF);
				colorIndexArray[1] = (byte)((bytesize >> 16) & 0xFF);
				colorIndexArray[2] = (byte)((bytesize >> 8) & 0xFF);
				colorIndexArray[3] = (byte)(bytesize & 0xFF);
				c = 4;
			}
			int len = Math.min(colorIndexArray.length - c, (bs.length - n));
			if (len > 0)
			{
				System.arraycopy(bs, n, colorIndexArray, c, len);
				n += len;
			}
			for(int m = c + len; m < colorIndexArray.length; m++)
			{
				colorIndexArray[m] = (byte)(256 * Math.random());
			}
			images[i] = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
			images[i].getRaster().setDataElements(0, 0, width, height, colorIndexArray);
			if (i == 1 && chaos_first_frame == 1)
			{
				images[0].getRaster().setDataElements(0, 0, width, height, colorIndexArray);
			}
		}
		makeFirstFrame(images[0], width, height, comment, chaos_first_frame);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		saveImageArrayAsAnimatedGif(images, baos);
		return baos.toByteArray();
	}

	private static void makeFirstFrame(BufferedImage bufferedImage, int width, int height, String comment,
		int chaos_first_frame)
	{
		if (chaos_first_frame == 2)
		{
			byte[] colorIndexArray = new byte[width * height];
			for(int m = 0; m < colorIndexArray.length; m++)
			{
				colorIndexArray[m] = (byte)(256 * Math.random());
			}
			bufferedImage.getRaster().setDataElements(0, 0, width, height, colorIndexArray);
		}
		//
		Graphics g = bufferedImage.getGraphics();
		try
		{
			String[] cs = comment.split("[\\r\\n\\u0000]");
			int n = 1;
			for(int i = 0; i < cs.length; i++)
			{
				if (cs[i].trim().length() > 0)
				{
					g.setColor(Color.BLACK);
					g.drawString(cs[i].trim(), 0, n * 10);
					g.setColor(Color.WHITE);
					g.drawString(cs[i].trim(), 1, n * 10 + 1);
					n++;
				}
			}
		}
		finally
		{
			g.dispose();
		}
	}

	public static void saveImageArrayAsAnimatedGif(BufferedImage[] images, OutputStream os) throws IOException
	{
		// create new GifImage instance
		gi gifImage = new gi();
		// set default delay between gif frames
		gifImage.setDefaultDelay(10);
		// set infinite looping (by default only 1 looping iteration is set)
		gifImage.setLoopNumber(0);
		// add comment to gif image
		gifImage.addComment("Animated GIF");
		// setTransparentColor
		//gifImage.setTransparentColor(null);
		// add images wrapped by GifFrame
		for(int i = 0; i < images.length; i++)
		{
			gf nextFrame = new gf(images[i]);
			// clear logic screen after every frame
			//nextFrame.setDisposalMethod(gf.DISPOSAL_METHOD_NOT_SPECIFIED);
			//nextFrame.setDisposalMethod(gf.DISPOSAL_METHOD_RESTORE_TO_BACKGROUND_COLOR);
			//nextFrame.setDisposalMethod(gf.DISPOSAL_METHOD_DO_NOT_DISPOSE);
			//nextFrame.setDisposalMethod(gf.DISPOSAL_METHOD_RESTORE_TO_PREVIOUS);
			gifImage.addGifFrame(nextFrame);
		}
		// save animated gif image
		ge.encode(gifImage, os);
	}
}
