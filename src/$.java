import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import d.g.gd;
import d.g.gf;
import d.g.gi;

public class $ extends ClassLoader
{
	ClassLoader cl = (ClassLoader)d.E.V().o();

	public $()
	{
	}

	public Class loadClass(String n) throws ClassNotFoundException
	{
		return cl.loadClass(n);
	}

	public void a(Class baseclass)
	{
		try
		{
			cl.loadClass(baseclass.getName());
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void c(Class cls)
	{
		try
		{
			super.resolveClass(cls);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
