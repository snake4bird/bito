package d;

import java.io.IOException;

public interface FileAppender
{
	public void appendfile(String msg, Throwable e) throws IOException;
}
