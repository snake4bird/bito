package d._.asql;

import java.sql.SQLException;

public class ASSException extends SQLException
{
	final String sourcename;
	final String sourcecode;
	final int start_line_no;
	final int error_line_no;

	public ASSException(String msg, String sourcename, String sourcecode, int start_line_no, int error_line_no)
	{
		super(msg);
		this.sourcename = sourcename;
		this.sourcecode = sourcecode;
		this.start_line_no = start_line_no;
		this.error_line_no = error_line_no;
	}

	public ASSException(String msg, String sourcename, String sourcecode, int start_line_no, int error_line_no,
		Throwable e)
	{
		this(msg, sourcename, sourcecode, start_line_no, error_line_no);
		super.initCause(e);
	}
}
