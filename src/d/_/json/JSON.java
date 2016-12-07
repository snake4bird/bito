package d._.json;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class JSON
{
	public static Object parse(String json)
	{
		try
		{
			return new JSONTokener(json).nextValue();
		}
		catch(ParseException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String toString(Object v)
	{
		return toString(v, new HashMap());
	}

	private static String toString(Object v, HashMap loop_ref_cache)
	{
		if (v == null)
		{
			return JSONObject.NULL.toString();
		}
		if (v instanceof Map)
		{
			return transMap2JO(loop_ref_cache, (Map)v).toString(2).replace("\n", "\r\n");
		}
		if (v instanceof Collection)
		{
			return transCollection2JO(loop_ref_cache, (Collection)v).toString(2).replace("\n", "\r\n");
		}
		if (v instanceof Object[])
		{
			return transCollection2JO(loop_ref_cache, Arrays.asList((Object[])v)).toString(2).replace("\n", "\r\n");
		}
		return v.toString();
	}

	private static final Object LOOP_REFFERENCE = new Object()
	{
		public String toString()
		{
			return "[loop refference]";
		}
	};

	private static Object transO2JO(HashMap loop_ref_cache, Object v)
	{
		if (loop_ref_cache.containsKey(v))
		{
			return loop_ref_cache.get(v);
		}
		loop_ref_cache.put(v, LOOP_REFFERENCE);
		Object ret;
		if (v == null)
		{
			ret = JSONObject.NULL;
		}
		else if (v instanceof Map)
		{
			ret = transMap2JO(loop_ref_cache, (Map)v);
		}
		else if (v instanceof Collection)
		{
			ret = transCollection2JO(loop_ref_cache, (Collection)v);
		}
		else if (v instanceof Object[])
		{
			ret = transCollection2JO(loop_ref_cache, Arrays.asList((Object[])v));
		}
		else if (v instanceof Number)
		{
			ret = v;
		}
		else
		{
			ret = v.toString();
		}
		loop_ref_cache.put(v, ret);
		return ret;
	}

	private static JSONObject transMap2JO(HashMap loop_ref_cache, Map map)
	{
		JSONObject ret = new JSONObject();
		for(Object e : map.entrySet())
		{
			Entry me = ((Entry)e);
			Object k = (me.getKey());
			Object v = (me.getValue());
			ret.put(k == null?null:k.toString(), transO2JO(loop_ref_cache, v));
		}
		return ret;
	}

	private static JSONArray transCollection2JO(HashMap loop_ref_cache, Collection col)
	{
		JSONArray ret = new JSONArray();
		Iterator ci = col.iterator();
		while(ci.hasNext())
		{
			ret.put(transO2JO(loop_ref_cache, ci.next()));
		}
		return ret;
	}
}
