package d._.asql;

import java.sql.SQLException;

public class SQLScriptException extends SQLException
{
	private int position = 0;

	public SQLScriptException(String msg)
	{
		super(msg);
	}

	public SQLScriptException(String msg, Throwable e)
	{
		super(msg);
		super.initCause(e);
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
