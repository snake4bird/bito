package bito.util.script;

public class ScriptExitException extends ScriptException
{
	private int returnCode;

	public ScriptExitException(int returnCode)
	{
		super("" + returnCode);
		this.returnCode = returnCode;
	}

	public int getReturnCode()
	{
		return returnCode;
	}
}
