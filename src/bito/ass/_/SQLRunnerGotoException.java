package bito.ass._;

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
