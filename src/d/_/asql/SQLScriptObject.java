package d._.asql;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;

class SQLScriptObject extends NativeObject implements SQLScriptable
{
	public static final String not_found = "not found";
	//
	private final SQLScriptScope scope;
	protected final SQLScriptUndefined undefined;
	private SQLScriptable parent;
	private String key;

	public SQLScriptObject(SQLScriptScope scope, SQLScriptable parent, String key)
	{
		super();
		if (scope == null)
		{
			this.scope = (SQLScriptScope)this;
			this.undefined = new SQLScriptUndefined((SQLScriptScope)this, null, null);
		}
		else
		{
			this.scope = scope;
			this.undefined = scope.undefined;
			ScriptRuntime.setBuiltinProtoAndParent(this, this.scope, TopLevel.Builtins.Object);
		}
		setKey(parent, key);
	}

	public void setKey(SQLScriptable parent, String key)
	{
		boolean changed = false;
		if (this.parent == null && parent != null)
		{
			this.parent = parent;
			changed = true;
		}
		if ((this.key == null || this.key.length() == 0) && key != null && key.length() > 0)
		{
			this.key = key;
			changed = true;
		}
		if (changed)
		{
			Object[] ids = this.getAllIds();
			for(int i = 0; i < ids.length; i++)
			{
				if (ids[i] instanceof Integer)
				{
					scope.transJSO2ASSO(this, "[" + i + "]", this.get(((Integer)ids[i]).intValue(), this));
				}
				else if (ids[i] instanceof String)
				{
					scope.transJSO2ASSO(this, (String)ids[i], this.get(((String)ids[i]), this));
				}
				else
				{
					// nothing
				}
			}
		}
	}

	public SQLScriptScope getSQLScriptScope()
	{
		return scope;
	}

	public String getKey()
	{
		return key;
	}

	public SQLScriptable getParentObject()
	{
		return parent;
	}

	public String getFullName()
	{
		SQLScriptable s = getSQLScriptScope();
		SQLScriptable p = getParentObject();
		String k = getKey();
		while(p != null)
		{
			k = p.getKey() + k;
			s = p.getSQLScriptScope();
			p = p.getParentObject();
		}
		k = s.getKey() + k;
		return k;
	}

	public void put(String name, Scriptable start, Object value)
	{
		if (value == Undefined.instance)
		{
			value = undefined;
		}
		super.put(name, start, scope.transJSO2ASSO(parent, "." + name, value));
	}

	public void put(int index, Scriptable start, Object value)
	{
		super.put(index, start, scope.transJSO2ASSO(parent, "[" + index + "]", value));
	}

	public Object get(String name, Scriptable start)
	{
		Object default_value = super.get(name, start);
		if (default_value == Scriptable.NOT_FOUND)
		{
			Scriptable prototype = this.getPrototype();
			if (prototype != null)
			{
				default_value = ScriptableObject.getProperty(prototype, name);
			}
		}
		if (default_value == Scriptable.NOT_FOUND)
		{
			return getDefaultValue(name, start);
		}
		return default_value;
	}

	public Object get(int index, Scriptable start)
	{
		Object default_value = super.get(index, start);
		if (default_value == Scriptable.NOT_FOUND)
		{
			Scriptable prototype = this.getPrototype();
			if (prototype != null)
			{
				default_value = ScriptableObject.getProperty(prototype, index);
			}
		}
		if (default_value == Scriptable.NOT_FOUND)
		{
			return getDefaultValue(index, start);
		}
		return default_value;
	}

	protected Object getDefaultValue(String name, Scriptable start)
	{
		return new SQLScriptUndefined(scope, this, "." + name);
	}

	protected Object getDefaultValue(int index, Scriptable start)
	{
		return new SQLScriptUndefined(scope, this, "[" + index + "]");
	}

	private Object transMap2SSO(SQLScriptable parent, String key, Map map)
	{
		SQLScriptObject ret = new SQLScriptObject(scope, parent, key);
		for(Object e : map.entrySet())
		{
			Entry me = ((Entry)e);
			String k = (String)(me.getKey());
			Object v = (me.getValue());
			ret.put(k, ret, v);
		}
		return ret;
	}

	private Object transJSO2ASSO_internal(SQLScriptable parent, String key, Object obj) throws ClassCastException
	{
		if (obj instanceof Map && !(obj instanceof SortedMap))
		{
			return transMap2SSO(parent, key, (Map)obj);
		}
		if (obj instanceof List)
		{
			SQLScriptArray result = new SQLScriptArray(scope, parent, key, ((List)obj).toArray());
			return result;
		}
		if (obj instanceof Object[])
		{
			return transJSO2ASSO_internal(parent, key, Arrays.asList((Object[])obj));
		}
		throw new ClassCastException("");
	}

	public Object transJSO2ASSO(SQLScriptable parent, String key, Object obj)
	{
		if (obj == null)
		{
			return null;
		}
		if (obj == not_found)
		{
			return Scriptable.NOT_FOUND;
		}
		if (obj instanceof Function)
		{
			return obj;
		}
		if (obj instanceof SQLScriptObject)
		{
			SQLScriptObject sso = (SQLScriptObject)obj;
			sso.setKey(parent, key);
			return sso;
		}
		if (obj instanceof SQLScriptArray)
		{
			SQLScriptArray ssa = (SQLScriptArray)obj;
			ssa.setKey(parent, key);
			return ssa;
		}
		if (obj instanceof NativeJavaObject)
		{
			NativeJavaObject njo = (NativeJavaObject)obj;
			Object jo = njo.unwrap();
			try
			{
				return transJSO2ASSO_internal(parent, key, jo);
			}
			catch(ClassCastException e)
			{
				return obj;
			}
		}
		try
		{
			return transJSO2ASSO_internal(parent, key, obj);
		}
		catch(ClassCastException e)
		{
			return obj;
		}
	}
}
