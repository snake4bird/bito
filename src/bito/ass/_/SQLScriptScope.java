package bito.ass._;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class SQLScriptScope extends SQLScriptObject
{
	public static final String scope = "";

	public SQLScriptScope(Context context)
	{
		super(null, null, scope);
		context.initStandardObjects(this);
		ScriptableObject.putProperty(this, SQLScriptUnkown.unkown, new SQLScriptUnkown(this, null, null));
	}

	protected Object getDefaultValue(String name, Scriptable start)
	{
		return null;
	}
}
