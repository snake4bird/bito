package bito.util.dba.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SQL
{
	public static final char LBracket = '(';
	public static final char RBracket = ')';
	public static final char LSquare = '[';
	public static final char RSquare = ']';
	public static final char SQuotes = '\'';
	public static final char DQuotes = '\"';
	public static final char Comma = ',';
	public static final char Dot = '.';
	public static final char Blank = ' ';
	public static final String SELECT = "SELECT ";
	public static final String FROM = " FROM ";
	public static final String WHERE = " WHERE ";
	public static final String AND = " AND ";
	public static final String OR = " OR ";
	public static final String ORDER_BY = " ORDER BY ";
	public static final String INSERT_INTO = "INSERT INTO ";
	public static final String VALUES = "VALUES";
	public static final String UPDATE = "UPDATE ";
	public static final String SET = " SET ";
	public static final String DELETE_FROM = "DELETE FROM ";
	public static final String NULL = "NULL";
	public static final String ISNULL = " IS NULL ";
	public static final String[] OPERATORS = {	"=",
												">",
												">=",
												"<",
												"<=",
												"<>",
												"!=",
												"like",
												"not like"};
	public static final String[] LOGIC_OPERATORS = {"and", "or", "not"};
	public static final Set OPERATORSET = new HashSet(Arrays.asList(OPERATORS));
	public static final Set LOGIC_OPERATORSET = new HashSet(Arrays.asList(LOGIC_OPERATORS));

	/**
	 * str will be replaced "'" to "''"
	 * @param str if str is null, it will be replaced to " IS NULL" 
	 * @return
	 */
	public static final String EqualString(String str)
	{
		return str == null?ISNULL:"=\'" + str.replaceAll("'", "''") + '\'';
	}

	/**
	 * @param likename
	 * @return
	 */
	public static String LikeString(String likestr)
	{
		return " LIKE '"
			+ (likestr == null?"%":likestr.replaceAll("'", "''"))
				+ "'";
	}
}
