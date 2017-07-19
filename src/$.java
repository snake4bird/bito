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

public class $ extends ClassLoader
{
	ClassLoader cl;

	public $()
	{
		try
		{
			cl = (ClassLoader)Class.forName("$.$").newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
