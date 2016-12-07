package b;

import java.util.HashSet;

public class i extends $
{
	public static i t = (i)$.v();

	protected i()
	{
		System.setProperty("bito.dir", this.toString());
	}

	private HashSet oset = new HashSet();
	private int ocaller = 0;

	public final Object o()
	{
		String clsname = null;
		StackTraceElement[] est = Thread.currentThread().getStackTrace();
		if (ocaller == 0)
		{
			for(int i = 0; i < est.length; i++)
			{
				if (est[i].getClassName().equals(i.class.getName()) && est[i].getMethodName().equals("o"))
				{
					ocaller = i + 1;
				}
			}
		}
		clsname = est[ocaller].getClassName();
		synchronized(oset)
		{
			if (oset.contains(clsname))
			{
				return null;
			}
			oset.add(clsname);
			try
			{
				RuntimeException te = null;
				String OrgClassName = clsname;
				String pkgname = OrgClassName;
				int n = pkgname.lastIndexOf('.');
				while(n > 0)
				{
					pkgname = OrgClassName.substring(0, n);
					try
					{
						String EVClassName = pkgname + "._" + OrgClassName.substring(n);
						return _(EVClassName);
					}
					catch(RuntimeException e)
					{
						te = new RuntimeException(e.getMessage() + "#" + ocaller, e.getCause());
						n = pkgname.lastIndexOf('.');
					}
				}
				try
				{
					return _(OrgClassName);
				}
				catch(RuntimeException e)
				{
					throw new RuntimeException(e.getMessage() + " " + ocaller, e.getCause());
				}
			}
			finally
			{
				synchronized(oset)
				{
					oset.remove(clsname);
				}
			}
		}
	}
}
