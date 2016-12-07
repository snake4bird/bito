package d._;

import java.io.Serializable;
import java.util.Comparator;

public class CommonComparator implements Comparator, Serializable
{
	public static Comparator comparator = new CommonComparator();

	public int compare(Object object1, Object object2)
	{
		if (object1 == object2)
		{
			return 0;
		}
		if (object1 == null)
		{
			return -1;
		}
		if (object2 == null)
		{
			return 1;
		}
		if (object1 instanceof Comparable && object2 instanceof Comparable)
		{
			try
			{
				return ((Comparable)object1).compareTo(object2);
			}
			catch(Throwable t)
			{
			}
		}
		else
		{
			if (object1 instanceof Comparable)
			{
				return 1;
			}
			if (object2 instanceof Comparable)
			{
				return -1;
			}
		}
		if (object1.toString() == null)
		{
			return -1;
		}
		if (object2.toString() == null)
		{
			return 1;
		}
		return object1.toString().compareTo(object2.toString());
	}
}
