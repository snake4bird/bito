package d._.asql;

public interface SQLScriptable
{
	public SQLScriptScope getSQLScriptScope();

	public String getKey();

	public SQLScriptable getParentObject();

	public String getFullName();
}
