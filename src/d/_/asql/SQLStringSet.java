package d._.asql;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SQLStringSet
{
	private SortedMap<String, Integer> keysUsedInScript = d.E.V().newMapSortedByAddTime();
	private StringBuffer[] sbsqls = new StringBuffer[]{new StringBuffer()};
	private StringBuffer[] datastringset = new StringBuffer[]{new StringBuffer()};
	private char flagScriptInLastQuote = '\0';

	public void append(String sql)
	{
		if (sql.length() == 0)
		{
			return;
		}
		if (datastringset.length != sbsqls.length)
		{
			throw new RuntimeException("Impossible: error.");
		}
		append_inner(sql);
	}

	private void append_inner(String sql)
	{
		char flagScriptInFirstQuote = '\0';
		if (sql.length() > 0
			&& ((flagScriptInFirstQuote = sql.charAt(0)) == '\'' || flagScriptInFirstQuote == '\"')
				&& flagScriptInLastQuote == flagScriptInFirstQuote)
		{
			sql = sql.substring(1);
			for(int n = 0; n < sbsqls.length; n++)
			{
				sbsqls[n].deleteCharAt(sbsqls[n].length() - 1);
				sbsqls[n].append(replaceQuote(datastringset[n].toString(), flagScriptInFirstQuote));
				sbsqls[n].append(sql);
				datastringset[n] = new StringBuffer();
			}
		}
		else
		{
			for(int n = 0; n < sbsqls.length; n++)
			{
				sbsqls[n].append(datastringset[n].toString());
				sbsqls[n].append(sql);
				datastringset[n] = new StringBuffer();
			}
		}
		if (sql.length() == 0
			|| ((flagScriptInLastQuote = sql.charAt(sql.length() - 1)) != '\'' && flagScriptInLastQuote != '\"'))
		{
			flagScriptInLastQuote = '\0';
		}
	}

	private String replaceQuote(String value, char quoteflag)
	{
		return value == null?null //
				:('\'' == quoteflag)?value.replaceAll("\\'", "''") //
						:('\"' == quoteflag?value.replaceAll("\\\"", "\"\""):value);
	}

	private void appendRepeatValue(DataSetValues rv, int rvRepeatCount)
	{
		for(int n = 0; n < datastringset.length;)
		{
			for(int v = 0; v < rv.values.length; v++)
			{
				for(int rvli = 0; rvli < rvRepeatCount; rvli++)
				{
					datastringset[n].append(rv.values[v]);
					n++;
				}
			}
		}
	}

	public void appendDataSetValues(DataSetValues rv)
	{
		if (rv != null)
		{
			if (datastringset.length != sbsqls.length)
			{
				throw new RuntimeException("Impossible: error.");
			}
			if (!keysUsedInScript.containsKey(rv.label)
				|| rv.values.length == 0
					|| datastringset.length != ((datastringset.length / rv.values.length) * rv.values.length))
			{
				StringBuffer[] newdatastringset = new StringBuffer[datastringset.length * rv.values.length];
				StringBuffer[] newsbsqls = new StringBuffer[sbsqls.length * rv.values.length];
				int nvi = 0;
				for(int v = 0; v < rv.values.length; v++)
				{
					for(int n = 0; n < datastringset.length; n++)
					{
						newsbsqls[nvi] = new StringBuffer(sbsqls[n]);
						newdatastringset[nvi] = new StringBuffer(datastringset[n]);
						newdatastringset[nvi].append(rv.values[v]);
						nvi++;
					}
				}
				int nextLabelRepeatCount = datastringset.length;
				datastringset = newdatastringset;
				sbsqls = newsbsqls;
				keysUsedInScript.put(rv.label, nextLabelRepeatCount);
			}
			else
			{
				// retsql.length 是 rv.values.length 的整数倍 m
				// rv.values 中的每个值在 retsql 出现 m 次
				// retsql.length == ((retsql.length / rvRepeatCount / rv.values.length) * rvRepeatCount * rv.values.length)
				int rvRepeatCount = keysUsedInScript.get(rv.label);
				appendRepeatValue(rv, rvRepeatCount);
			}
		}
	}

	public void end()
	{
		append_inner("");
	}

	public int size()
	{
		return sbsqls.length;
	}

	public String get(int line)
	{
		return sbsqls.length == 0?(null):sbsqls[line].toString();
	}
}