package d._.asql;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SQLStringSetX
{
	private SortedMap<String, Integer> keysUsedInScript = d.E.V().newMapSortedByAddTime();
	private String[] sblabels = null;
	private StringBuffer[] sbsqls = new StringBuffer[]{new StringBuffer()};
	private static Pattern plabel = Pattern.compile("(?is)\\s*(\\w*)\\s*:\\s*(.*)");
	private String scriptInString = "";

	void append(char[] chars, int start, int length)
	{
		for(int n = 0; n < sbsqls.length; n++)
		{
			sbsqls[n].append(chars, start, length);
		}
	}

	void append(String sql)
	{
		for(int n = 0; n < sbsqls.length; n++)
		{
			sbsqls[n].append(sql);
		}
	}

	public void scriptInString(String scriptInString)
	{
		this.scriptInString = scriptInString;
	}

	private void appendRepeatValue(DataSetValues rv, int rvRepeatCount)
	{
		for(int n = 0; n < sbsqls.length;)
		{
			for(int v = 0; v < rv.values.length; v++)
			{
				for(int rvli = 0; rvli < rvRepeatCount; rvli++)
				{
					sbsqls[n].append(replaceSingleQuote(rv.values[v]));
					n++;
				}
			}
		}
	}

	private String replaceSingleQuote(String value)
	{
		return value == null?null:("\'".equals(scriptInString)?value.replaceAll("\\'", "''"):("\""
			.equals(scriptInString)?value.replaceAll("\\\"", "\"\""):value));
	}

	public void appendDataSetValues(DataSetValues rv)
	{
		if (rv != null)
		{
			if (!keysUsedInScript.containsKey(rv.label)
				|| rv.values.length == 0
					|| sbsqls.length != ((sbsqls.length / rv.values.length) * rv.values.length))
			{
				if (rv.values.length == 0 && sblabels == null)
				{
					ArrayList<String> lbs = new ArrayList();
					for(int n = 0; n < sbsqls.length; n++)
					{
						String sql = sbsqls[n].toString();
						Matcher m = plabel.matcher(sql);
						if (m.matches())
						{
							String lb = m.group(1);
							if (lb != null && lb.length() > 0)
							{
								lbs.add(lb + ":");
							}
						}
					}
					sblabels = lbs.toArray(new String[0]);
				}
				StringBuffer[] newretsql = new StringBuffer[sbsqls.length * rv.values.length];
				int nvi = 0;
				for(int v = 0; v < rv.values.length; v++)
				{
					for(int n = 0; n < sbsqls.length; n++)
					{
						newretsql[nvi] = new StringBuffer();
						newretsql[nvi].append(sbsqls[n]);
						newretsql[nvi].append(replaceSingleQuote(rv.values[v]));
						nvi++;
					}
				}
				int nextLabelRepeatCount = sbsqls.length;
				sbsqls = newretsql;
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

	public int size()
	{
		return sbsqls.length == 0?(sblabels == null?0:sblabels.length):sbsqls.length;
	}

	public String get(int line)
	{
		return sbsqls.length == 0?(sblabels == null?null:sblabels[line]):sbsqls[line].toString();
	}

	public boolean matchLabel(String label)
	{
		if (label == null || label.length() == 0)
		{
			return true;
		}
		if (size() == 0)
		{
			return false;
		}
		for(int i = 0; i < size(); i++)
		{
			String match[];
			if ((match = SStrMatcher.match(get(i), "(?is)\\s*(\\w*)\\s*:\\s*(.*)")).length > 0)
			{
				if (label.equals(match[0]))
				{
					return true;
				}
			}
		}
		return false;
	}
}