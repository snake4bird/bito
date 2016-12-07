package bito.util.dba.impl;

import java.util.Properties;

import bito.util.cfg.SystemConfig;

public class DBConfig
{
	public final String dbname;
	public final String dbdriver;
	public final String dburl;
	public final String dbuser;
	public final String dbpswd;
	public final int dbmxcon;
	public final long keepidletime;
	public final int rowslimit;

	public DBConfig(String dbname)
	{
		Properties pp = SystemConfig.getProperties();
		String dbdriver = pp.getProperty(dbname + ".driver", "");
		String dburl = pp.getProperty(dbname + ".url", "");
		String dbuser = pp.getProperty(dbname + ".username", "");
		String dbpswd = pp.getProperty(dbname + ".password", "");
		int dbmxcon;
		try
		{
			dbmxcon = Integer.parseInt(pp.getProperty(dbname + ".maxconnect", "0"));
		}
		catch(NumberFormatException nfe)
		{
			dbmxcon = 0;
		}
		long keepidletime;
		try
		{
			keepidletime = Long.parseLong(pp.getProperty(dbname + ".maxidleseconds", "600")) * 1000;
		}
		catch(NumberFormatException nfe)
		{
			keepidletime = 600 * 1000;
		}
		int rowslimit;
		try
		{
			rowslimit = Integer.parseInt(pp.getProperty(dbname + ".rows.limit", "10000"));
		}
		catch(NumberFormatException nfe)
		{
			rowslimit = 0;
		}
		this.dbname = dbname;
		this.dbdriver = dbdriver;
		this.dburl = dburl;
		this.dbuser = dbuser;
		this.dbpswd = dbpswd;
		this.dbmxcon = dbmxcon;
		this.keepidletime = keepidletime;
		this.rowslimit = rowslimit;
	}

	public DBConfig(String dbname, String dbdriver, String dburl, String dbuser, String dbpswd, int dbmxcon,
		long keepidletime, int rowslimit)
	{
		this.dbname = dbname;
		this.dbdriver = dbdriver;
		this.dburl = dburl;
		this.dbuser = dbuser;
		this.dbpswd = dbpswd;
		this.dbmxcon = dbmxcon;
		this.keepidletime = keepidletime;
		this.rowslimit = rowslimit;
	}

	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof DBConfig))
		{
			return false;
		}
		return toString().equals(((DBConfig)o).toString());
	}

	public int hashCode()
	{
		return toString().hashCode();
	}

	public String toString()
	{
		return "db["
			+ (dbname == null?super.toString():dbname)
				+ "]"
				+ "url:"
				+ dburl
				+ "#user:"
				+ dbuser
				+ "#pswd:"
				+ dbpswd
				+ "#mxcc:"
				+ dbmxcon
				+ "#kpit:"
				+ keepidletime
				+ "#rows:"
				+ rowslimit;
	}
}
