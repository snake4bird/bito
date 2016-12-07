package bito.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class MapSortedByAddTime implements SortedMap, Cloneable, Serializable
{
	private static final long serialVersionUID = 1L;
	private long index = 0;
	private HashMap keyIndex = new HashMap();
	private TreeMap sortedMap = new TreeMap(new KeyOrderComparator());

	private class KeyOrderComparator implements Comparator, Serializable
	{
		private static final long serialVersionUID = 1L;

		public KeyOrderComparator()
		{
		}

		public int compare(Object o1, Object o2)
		{
			if (o1 == o2 || (o1 != null && o1.equals(o2)))
			{
				return 0;
			}
			Long oi1 = (Long)keyIndex.get(o1);
			Long oi2 = (Long)keyIndex.get(o2);
			if (oi1 == null)
			{
				return 1;
			}
			if (oi2 == null)
			{
				return -1;
			}
			return oi1.longValue() > oi2.longValue()?1:-1;
		}
	};

	public MapSortedByAddTime()
	{
	}

	public void clear()
	{
		sortedMap.clear();
	}

	public boolean containsKey(Object key)
	{
		return sortedMap.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		return sortedMap.containsValue(value);
	}

	public Set entrySet()
	{
		return sortedMap.entrySet();
	}

	public Object get(Object key)
	{
		return sortedMap.get(key);
	}

	public boolean isEmpty()
	{
		return sortedMap.isEmpty();
	}

	public Set keySet()
	{
		return sortedMap.keySet();
	}

	public Object put(Object key, Object value)
	{
		Object ro = sortedMap.put(key, value);
		if (!keyIndex.containsKey(key))
		{
			index++;
			keyIndex.put(key, new Long(index));
		}
		return ro;
	}

	public void putAll(Map map)
	{
		Iterator mesi = map.entrySet().iterator();
		while(mesi.hasNext())
		{
			Entry e = (Entry)mesi.next();
			put(e.getKey(), e.getValue());
		}
	}

	public Object remove(Object key)
	{
		if (sortedMap.containsKey(key))
		{
			Object ro = sortedMap.remove(key);
			keyIndex.remove(key);
			return ro;
		}
		return null;
	}

	public int size()
	{
		return sortedMap.size();
	}

	public Collection values()
	{
		return sortedMap.values();
	}

	public Comparator comparator()
	{
		return sortedMap.comparator();
	}

	public Object firstKey()
	{
		return sortedMap.firstKey();
	}

	public SortedMap headMap(Object endKey)
	{
		return sortedMap.headMap(endKey);
	}

	public Object lastKey()
	{
		return sortedMap.lastKey();
	}

	public SortedMap subMap(Object startKey, Object endKey)
	{
		return sortedMap.subMap(startKey, endKey);
	}

	public SortedMap tailMap(Object startKey)
	{
		return sortedMap.tailMap(startKey);
	}

	public boolean equals(Object o)
	{
		return sortedMap.equals(o);
	}

	public int hashCode()
	{
		return sortedMap.hashCode();
	}

	public String toString()
	{
		return sortedMap.toString();
	}
}
