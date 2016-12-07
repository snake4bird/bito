package bito.util.dba;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DBServerType
{
	public static final int UNKNOWN = 0;
	public static final int SQLSERVER = 1;
	public static final int DB2 = 2;
	public static final int ORACLE = 3;
	public static final int INFORMIX = 4;
	public static final int ACCESS = 5;
	public static final int MYSQL = 6;
	public static final int SYBASE = 7;
	public static final int H2 = 8;
	public static final HashMap DBServerNamesMap = new HashMap();
	static
	{
		DBServerNamesMap.put(new Integer(UNKNOWN), "Unknown DB Server");
		DBServerNamesMap.put(new Integer(SQLSERVER), "SQL Server");
		DBServerNamesMap.put(new Integer(DB2), "DB2");
		DBServerNamesMap.put(new Integer(ORACLE), "Oracle");
		DBServerNamesMap.put(new Integer(INFORMIX), "Informix");
		DBServerNamesMap.put(new Integer(ACCESS), "Access");
		DBServerNamesMap.put(new Integer(MYSQL), "MYSQL");
		DBServerNamesMap.put(new Integer(SYBASE), "Sybase");
		DBServerNamesMap.put(new Integer(H2), "H2");
	};

	public static String getDBProductName(Connection connection)
	{
		try
		{
			return connection.getMetaData().getDatabaseProductName();
		}
		catch(Exception e)
		{
		}
		return (String)DBServerNamesMap.get(new Integer(UNKNOWN));
	}

	public static int getDBType(Connection connection)
	{
		String DBProductName = getDBProductName(connection);
		Iterator snmesi = DBServerNamesMap.entrySet().iterator();
		while(snmesi.hasNext())
		{
			Map.Entry me = (Map.Entry)snmesi.next();
			Integer it = (Integer)me.getKey();
			String sn = (String)me.getValue();
			if (DBProductName.toLowerCase().indexOf(sn.toLowerCase()) >= 0)
			{
				return it.intValue();
			}
		}
		return UNKNOWN;
	}
}
