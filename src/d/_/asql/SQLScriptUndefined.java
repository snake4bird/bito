package d._.asql;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public class SQLScriptUndefined extends SQLScriptObject
{
	static final String undefined = "undefined";

	public SQLScriptUndefined(SQLScriptScope scope, SQLScriptable parent, String key)
	{
		super(scope, parent, key);
	}

	public String getClassName()
	{
		return undefined;
	}

	public Object getDefaultValue(Class typeHint)
	{
		if (typeHint == ScriptRuntime.StringClass)
		{
			return undefined;
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
		return (value == Undefined.instance || value instanceof SQLScriptUndefined)?Boolean.TRUE:Boolean.FALSE;
	}

	protected Object getDefaultValue(String name, Scriptable start)
	{
		throw Context.reportRuntimeError("field '" + name + "' undefined in object '" + getFullName() + "'.");
	}

	protected Object getDefaultValue(int index, Scriptable start)
	{
		throw Context.reportRuntimeError("index '" + index + "' undefined in object '" + getFullName() + "'.");
	}
}
