package d._.asql;

public class SQLRunnerExitException extends SQLScriptException
{
	private int returnCode;

	public SQLRunnerExitException(int returnCode)
	{
		super("" + returnCode);
		this.returnCode = returnCode;
	}

	public int getReturnCode()
	{
		return returnCode;
	}
}
