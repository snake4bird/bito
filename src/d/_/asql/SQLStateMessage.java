package d._.asql;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import bito.util.dba.DBServerType;

public class SQLStateMessage
{
	public static final Map<String, String> db2_message = d.E.V().newMapSortedByAddTime();
	static
	{
		try
		{
			InputStream is = SQLStateMessage.class.getResourceAsStream("db2_message.txt");
			String message_map_string;
			try
			{
				message_map_string = new String(d.E.V().readBytes(is));
			}
			finally
			{
				is.close();
			}
			db2_message.putAll((Map<String, String>)new SQLScriptRunner("").quick_eval(message_map_string));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String getMessage(int dbtype, String sqlState)
	{
		String s;
		switch(dbtype)
		{
		case DBServerType.DB2:
		default:
			s = db2_message.get(sqlState);
		}
		return s;
	}
}
