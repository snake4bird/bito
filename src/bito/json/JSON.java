package bito.json;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class JSON
{
	public static class Format
	{
		public static final int default_v0_indent = 2;
		public static final boolean default_v0_wchar_encode = true;
		public static final String default_v0_object_head_separator = "{\n";
		public static final String default_v0_object_tail_separator = "}";
		public static final String default_v0_array_head_separator = "[\n";
		public static final String default_v0_array_tail_separator = "]";
		public static final String default_v0_kv_separator = ": ";
		public static final String default_v0_field_separator = ",\n";
		public static final String default_v0_indent_separator = " ";
		public static final String default_v0_before_first_field_separator = "";
		public static final String default_v0_after_last_field_separator = "";
		//
		public static final int default_indent = 2;
		public static final boolean default_wchar_encode = false;
		public static final String default_object_head_separator = "{";
		public static final String default_object_tail_separator = "}";
		public static final String default_array_head_separator = "[";
		public static final String default_array_tail_separator = "]";
		public static final String default_kv_separator = ": ";
		public static final String default_field_separator = ",\r\n";
		public static final String default_indent_separator = " ";
		public static final String default_before_first_field_separator = "\r\n";
		public static final String default_after_last_field_separator = "";
		//
		public int indent = default_indent;
		public boolean wchar_encode = default_wchar_encode;
		public String object_head_separator = default_object_head_separator;
		public String object_tail_separator = default_object_tail_separator;
		public String array_head_separator = default_array_head_separator;
		public String array_tail_separator = default_array_tail_separator;
		public String kv_separator = default_kv_separator;
		public String field_separator = default_field_separator;
		public String indent_separator = default_indent_separator;
		public String before_first_field_separator = default_before_first_field_separator;
		public String after_last_field_separator = default_after_last_field_separator;
	}

	public static Object parse(String json)
	{
		try
		{
			return new JSONTokener(json).nextValue();
		}
		catch(ParseException e)
		{
			return e.getMessage() + "\r\n" + json;
		}
	}

	public static String stringify(Object v)
	{
		Format f = new Format();
		f.indent = 0;
		f.wchar_encode = true;
		f.field_separator = ",";
		f.kv_separator = ":";
		f.before_first_field_separator = "";
		f.after_last_field_separator = "";
		return toString(v, f);
	}

	public static String toString(Object v)
	{
		Format f = new Format();
		return toString(v, f);
	}

	public static String toString(Object v, Format f)
	{
		return toString(v, new HashMap(), f);
	}

	private static String toString(Object v, HashMap loop_ref_cache, Format f)
	{
		if (v == null)
		{
			return JSONObject.NULL.toString();
		}
		if (v instanceof Map)
		{
			return transMap2JO(loop_ref_cache, (Map)v).toString(f);
		}
		if (v instanceof Collection)
		{
			return transCollection2JO(loop_ref_cache, (Collection)v).toString(f);
		}
		if (v instanceof Object[])
		{
			return transCollection2JO(loop_ref_cache, Arrays.asList((Object[])v)).toString(f);
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
