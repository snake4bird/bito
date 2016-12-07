package d._.asql;

class SQLRunnerGotoException extends SQLScriptException
{
	public SQLRunnerGotoException(String label)
	{
		super(label);
	}

	public String getLable()
	{
		return getMessage();
	}
}
