package bito.ass._;

import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public class SQLScriptUnkown extends SQLScriptObject
{
	static final String unkown = "unkown";

	public SQLScriptUnkown(SQLScriptScope scope, SQLScriptable parent, String key)
	{
		super(scope, parent, key);
	}

	public String getClassName()
	{
		return unkown;
	}

	public Object getDefaultValue(Class typeHint)
	{
		if (typeHint == ScriptRuntime.StringClass)
		{
			return unkown;
		}
		if (typeHint == ScriptRuntime.BooleanClass)
		{
			return false;
		}
		return super.getDefaultValue(typeHint);
	}

	public boolean avoidObjectDetection()
	{
		return true;
	}

	protected Object equivalentValues(Object value)
	{
		return (value == Undefined.instance || value instanceof SQLScriptUnkown)?Boolean.TRUE:Boolean.FALSE;
	}

	protected Object getDefaultValue(String name, Scriptable start)
	{
		return new SQLScriptUnkown(getSQLScriptScope(), this, name);
	}

	protected Object getDefaultValue(int index, Scriptable start)
	{
		return new SQLScriptUnkown(getSQLScriptScope(), this, "[" + index + "]");
	}
}
