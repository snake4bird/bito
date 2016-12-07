package d._.asql;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;

class SQLScriptArray extends NativeArray implements SQLScriptable
{
	private final SQLScriptScope scope;
	private SQLScriptable parent;
	private String key;

	private static Object[] transJSO2ASSO(SQLScriptObject scope, SQLScriptable parent, String key, Object[] array)
	{
		Object[] ret = new Object[array.length];
		for(int i = 0; i < ret.length; i++)
		{
			ret[i] = scope.transJSO2ASSO(parent, "[" + i + "]", array[i]);
		}
		return ret;
	}

	public SQLScriptArray(SQLScriptScope scope, SQLScriptable parent, String key, Object[] array)
	{
		super(transJSO2ASSO(scope, parent, key, array));
		this.scope = scope;
		ScriptRuntime.setBuiltinProtoAndParent(this, scope, TopLevel.Builtins.Array);
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
		super.put(name, start, scope.transJSO2ASSO(parent, "." + name, value));
	}

	public void put(int index, Scriptable start, Object value)
	{
		super.put(index, start, scope.transJSO2ASSO(parent, "[" + index + "]", value));
	}

	public Object get(int index, Scriptable start)
	{
		Object v = super.get(index, start);
		if (v == NOT_FOUND)
		{
			return new SQLScriptUnkown(scope, this, "[" + index + "]");
		}
		return v;
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
		if (default_value != null && default_value != Scriptable.NOT_FOUND)
		{
			return default_value;
		}
		return new DataSetValues(getFullName(), name, start, toArray());
	}
}
