package bito.util.cache;

public class Cache extends LRUMap
{
	public Cache(int maxsize)
	{
		super(maxsize);
	}

	protected boolean removeLRU(LinkEntry entry)
	{
		Object o = entry.getValue();
		if (o instanceof CachableObject)
		{
			CachableObject p = (CachableObject)o;
			p.destroy();
		}
		return super.removeLRU(entry);
	}
}
