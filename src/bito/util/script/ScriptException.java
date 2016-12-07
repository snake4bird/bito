package bito.util.script;

public class ScriptException extends Exception
{
	private int position = 0;

	public ScriptException(String msg)
	{
		super(msg);
	}

	public void setPosition(int position)
	{
		this.position = position;
	}

	public int getPosition()
	{
		return this.position;
	}
}
