package bito.util.dba.impl;

import java.sql.*;
import java.util.*;

import bito.util.dba.DBAccess;

/**
 * Provides various database metadata information.
 */
public class BLMetadata
{
	private static HashMap cache = new HashMap();

	public static BLMetadata getInstance(DBAccess dba)
	{
		BLMetadata m = (BLMetadata)cache.get(dba.getID());
		if (m == null)
		{
			m = new BLMetadata(dba);
			cache.put(dba.getID(), m);
		}
		return m;
	}

	private Hashtable sAutoIncrementFieldsTable = new Hashtable();
	private Hashtable sFieldClassesTable = new Hashtable();
	private Hashtable sFieldClassNamesTable = new Hashtable();
	private Hashtable sFieldsTable = new Hashtable();
	private Hashtable sKeyFieldsTable = new Hashtable();
	private Hashtable sUniqueFieldsTable = new Hashtable();
	private Hashtable sNullableFieldsTable = new Hashtable();
	private DBAccess dba;

	private BLMetadata(DBAccess dba)
	{
		this.dba = dba;
	}

	private List uniqueFieldsList(String table) throws Exception
	{
		if (!sUniqueFieldsTable.containsKey(table))
		{
			Connection conn = dba.getConnection();
			DatabaseMetaData meta = null;
			try
			{
				meta = conn.getMetaData();
				if (meta == null)
				{
					return new ArrayList(0);
				}
				List retlist = new ArrayList();
				Collection keys = null;
				ResultSet result = meta.getIndexInfo(null,
					null,
					table,
					true,
					false);
				String lastindexname = null;
				String indexname = null;
				while(result.next())
				{
					if (result.getShort("TYPE") == DatabaseMetaData.tableIndexStatistic)
					{
					}
					else
					{
						indexname = result.getString("INDEX_NAME");
						if (!indexname.equals(lastindexname))
						{
							keys = new ArrayList();
							retlist.add(keys);
							lastindexname = indexname;
						}
						keys.add(result.getString("COLUMN_NAME"));
					}
				}
				result.close();
				sUniqueFieldsTable.put(table, retlist);
			}
			finally
			{
				conn.close();
			}
		}
		return ((List)sUniqueFieldsTable.get(table));
	}

	/**
	 * Provide the key field names of a table
	 * @param table the table name
	 * @return Iterator a string iterator of key field names
	 * @throws Exception 
	 */
	private Set keyFieldsSet(String table) throws Exception
	{
		if (!sKeyFieldsTable.containsKey(table))
		{
			Connection conn = dba.getConnection();
			DatabaseMetaData meta = null;
			try
			{
				meta = conn.getMetaData();
				if (meta == null)
				{
					return new HashSet(0);
				}
				Collection keys = null;
				ResultSet result = meta.getPrimaryKeys(null, null, table);
				keys = new HashSet(2);
				while(result.next())
				{
					keys.add(result.getString("COLUMN_NAME"));
				}
				result.close();
				if (keys.size() == 0)
				{
					result = meta.getPrimaryKeys(null, null, table);
					keys = new HashSet(2);
					while(result.next())
					{
						keys.add(result.getString("COLUMN_NAME"));
					}
					result.close();
				}
				sKeyFieldsTable.put(table, keys);
			}
			finally
			{
				conn.close();
			}
		}
		return ((Set)sKeyFieldsTable.get(table));
	}

	public Iterator keyFields(String table) throws Exception
	{
		return keyFieldsSet(table).iterator();
	}

	public String[] getFieldsNames(String table) throws Exception
	{
		List ll = getFieldsList(table);
		if (ll == null)
		{
			return new String[0];
		}
		String[] sa = new String[ll.size()];
		ll.toArray(sa);
		return sa;
	}

	public String[] getKeyFieldNames(String table) throws Exception
	{
		Set st = keyFieldsSet(table);
		String[] sa = new String[st.size()];
		st.toArray(sa);
		return sa;
	}

	public String[][] getUniqueFieldNames(String table) throws Exception
	{
		List ll = uniqueFieldsList(table);
		String[][] sa = new String[ll.size()][];
		for(int i = 0; i < ll.size(); i++)
		{
			List l = (List)ll.get(i);
			sa[i] = new String[l.size()];
			l.toArray(sa[i]);
		}
		return sa;
	}

	/**
	 * Checks if a field is a key field of some table.
	 * @param table the table name
	 * @param field the field name
	 * @return boolean true if the field is a key field of the table
	 * @throws Exception 
	 */
	public boolean isKeyField(String table, String field) throws Exception
	{
		if (!sKeyFieldsTable.containsKey(table))
		{
			keyFields(table);
		}
		return ((Collection)sKeyFieldsTable.get(table)).contains(field);
	}

	/**
	     * Manually sets the key fields for a table.  Used at the time when the key field
	 * metadata is not available.
	 * @param table the table name
	 * @param keyFields the assigned key fields
	 * @return Collection the original key field set if any.
	 */
	public Collection establishKeyFields(String table, Collection keyFields)
	{
		return (Collection)sKeyFieldsTable.put(table, keyFields);
	}

	/**
	 * Check if a field is automatically incremented so no value should be set
	 * within the program.
	 * @param table the table name
	 * @param field the field name
	 * @return boolean true if the field is auto incremented in the table
	 * @throws Exception 
	 */
	public boolean isFieldAutoIncremented(String table, String field)
		throws Exception
	{
		if (!sAutoIncrementFieldsTable.containsKey(table))
		{
			constructResultSetMetaData(table);
		}
		return ((Collection)sAutoIncrementFieldsTable.get(table))
			.contains(field);
	}

	/**
	 * Check if a field is nullable.
	 * @param table the table name
	 * @param field the field name
	 * @return boolean true if the field is nullable in the table
	 * @throws Exception 
	 */
	public boolean isFieldNullable(String table, String field) throws Exception
	{
		if (!sNullableFieldsTable.containsKey(table))
		{
			constructResultSetMetaData(table);
		}
		return ((Collection)sNullableFieldsTable.get(table)).contains(field);
	}

	/**
	 * Build the metadata by sending a vacuous query.
	 * @param table the table name whose metadata is to be built
	 * @exception Exception
	 */
	protected void constructResultSetMetaData(String table) throws Exception
	{
		Connection conn = dba.getConnection();
		try
		{
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery("select * from "
				+ table
					+ " where 1=2");
			ResultSetMetaData meta = result.getMetaData();
			int cc = meta.getColumnCount();
			HashSet auto = new HashSet(2);
			HashSet readonly = new HashSet(2);
			HashSet nulls = new HashSet(2);
			List fields = new ArrayList(cc);
			HashMap fieldClasses = new HashMap(cc);
			HashMap fieldClassNames = new HashMap(cc);
			for(int i = 1; i <= cc; i++)
			{
				String field = meta.getColumnName(i);
				fields.add(field);
				String name = meta.getColumnClassName(i);
				if (name.equals("java.math.BigDecimal")
					&& meta.getScale(i) == 0)
				{
					//Convert Double.0 to Integer
					name = "java.lang.Integer";
				}
				if (name.equals("java.io.InputStream"))
				{
					name = "[B";
				}
				else if (name.equals("oracle.sql.BLOB"))
				{
					name = "[B";
				}
				else if (name.equals("byte[]"))
				{
					name = "[B";
				}
				fieldClasses.put(field, Class.forName(name));
				fieldClassNames.put(field, name);
				if (meta.isAutoIncrement(i))
				{
					auto.add(field);
				}
				if (meta.isReadOnly(i))
				{
					readonly.add(field);
				}
				if (meta.isNullable(i) != ResultSetMetaData.columnNoNulls)
				{
					nulls.add(field);
				}
			}
			sAutoIncrementFieldsTable.put(table, auto);
			sFieldClassesTable.put(table, fieldClasses);
			sNullableFieldsTable.put(table, nulls);
			sFieldClassNamesTable.put(table, fieldClassNames);
			sFieldsTable.put(table, fields);
		}
		finally
		{
			conn.close();
		}
	}

	/**
	 * Get all the fields of a table.
	 * @param table the table name
	 * @return Iterator a string iterator of all field names for the table.
	 * @throws Exception 
	 */
	public List getFieldsList(String table) throws Exception
	{
		if (!sFieldsTable.containsKey(table))
		{
			constructResultSetMetaData(table);
		}
		return (List)sFieldsTable.get(table);
	}

	public Set getFieldsSet(String table) throws Exception
	{
		if (getFieldsList(table) != null)
		{
			Set st = new HashSet();
			st.addAll(getFieldsList(table));
			return st;
		}
		return null;
	}

	public Iterator getFields(String table) throws Exception
	{
		if (getFieldsList(table) != null)
		{
			return getFieldsList(table).iterator();
		}
		return null;
	}

	/**
	 * Get the class object that properly maps to the field of a table.
	 * @param table the table name
	 * @param field the field name
	 * @return Class the class object mapped
	 * @throws Exception 
	 */
	public Class getFieldClass(String table, String field) throws Exception
	{
		if (!sFieldClassesTable.containsKey(table))
		{
			constructResultSetMetaData(table);
		}
		return (Class)((HashMap)sFieldClassesTable.get(table)).get(field);
	}

	/**
	 * Get the name of the class that properly maps to the field of a table.
	 * @param table the table name
	 * @param field the field name
	 * @return String the name of the class mapped
	 * @throws Exception 
	 */
	public String getFieldClassName(String table, String field)
		throws Exception
	{
		if (!sFieldClassNamesTable.containsKey(table))
		{
			constructResultSetMetaData(table);
		}
		return (String)((HashMap)sFieldClassNamesTable.get(table)).get(field);
	}

	/**
	 * Info log the metadata for a table.
	 * @param table the table name
	 * @return 
	 * @throws Exception 
	 */
	public String toString(String table) throws Exception
	{
		StringBuffer sb = new StringBuffer();
		sb.append("***metadata information " + table + "***\r\n");
		Iterator iter = getFields(table);
		while(iter.hasNext())
		{
			String field = (String)iter.next();
			sb.append("Field "
				+ field
					+ " "
					+ (isKeyField(table, field)?" key":"")
					+ (isFieldNullable(table, field)?" nullable":"")
					+ (isFieldAutoIncremented(table, field)?" autoinc":"")
					+ " "
					+ getFieldClassName(table, field)
					+ " "
					+ getFieldClass(table, field)
					+ "\r\n");
		}
		sb.append("****************************************");
		return sb.toString();
	}
}
