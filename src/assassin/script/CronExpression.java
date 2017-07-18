package assassin.script;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class CronExpression implements Serializable, Cloneable
{
	public CronExpression(String cronExpression) throws ParseException
	{
		this.cronExpression = null;
		timeZone = null;
		lastdayOfWeek = false;
		nthdayOfWeek = 0;
		lastdayOfMonth = false;
		nearestWeekday = false;
		expressionParsed = false;
		if (cronExpression == null)
		{
			throw new IllegalArgumentException("cronExpression cannot be null");
		}
		else
		{
			this.cronExpression = cronExpression.toUpperCase(Locale.US);
			buildExpression(this.cronExpression);
			return;
		}
	}

	public boolean isSatisfiedBy(Date date)
	{
		Calendar testDateCal = Calendar.getInstance(getTimeZone());
		testDateCal.setTime(date);
		testDateCal.set(14, 0);
		Date originalDate = testDateCal.getTime();
		testDateCal.add(13, -1);
		Date timeAfter = getTimeAfter(testDateCal.getTime());
		return timeAfter != null && timeAfter.equals(originalDate);
	}

	public Date getNextValidTimeAfter(Date date)
	{
		return getTimeAfter(date);
	}

	public Date getNextInvalidTimeAfter(Date date)
	{
		long difference = 1000L;
		Calendar adjustCal = Calendar.getInstance(getTimeZone());
		adjustCal.setTime(date);
		adjustCal.set(14, 0);
		Date lastDate = adjustCal.getTime();
		Date newDate = null;
		do
		{
			if (difference != 1000L)
				break;
			newDate = getTimeAfter(lastDate);
			difference = newDate.getTime() - lastDate.getTime();
			if (difference == 1000L)
				lastDate = newDate;
		}
		while(true);
		return new Date(lastDate.getTime() + 1000L);
	}

	public TimeZone getTimeZone()
	{
		if (timeZone == null)
			timeZone = TimeZone.getDefault();
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone)
	{
		this.timeZone = timeZone;
	}

	public String toString()
	{
		return cronExpression;
	}

	public static boolean isValidExpression(String cronExpression)
	{
		try
		{
			new CronExpression(cronExpression);
		}
		catch(ParseException pe)
		{
			return false;
		}
		return true;
	}

	protected void buildExpression(String expression) throws ParseException
	{
		expressionParsed = true;
		try
		{
			if (seconds == null)
				seconds = new TreeSet();
			if (minutes == null)
				minutes = new TreeSet();
			if (hours == null)
				hours = new TreeSet();
			if (daysOfMonth == null)
				daysOfMonth = new TreeSet();
			if (months == null)
				months = new TreeSet();
			if (daysOfWeek == null)
				daysOfWeek = new TreeSet();
			if (years == null)
				years = new TreeSet();
			int exprOn = 0;
			for(StringTokenizer exprsTok = new StringTokenizer(expression, " \t", false); exprsTok.hasMoreTokens()
				&& exprOn <= YEAR; exprOn++)
			{
				String expr = exprsTok.nextToken().trim();
				if (exprOn == 3 && expr.indexOf('L') != -1 && expr.length() > 1 && expr.indexOf(",") >= 0)
					throw new ParseException("Support for specifying 'L' and 'LW' with other days of the month is not implemented",
						-1);
				if (exprOn == 5 && expr.indexOf('L') != -1 && expr.length() > 1 && expr.indexOf(",") >= 0)
					throw new ParseException("Support for specifying 'L' with other days of the week is not implemented",
						-1);
				String v;
				for(StringTokenizer vTok = new StringTokenizer(expr, ","); vTok.hasMoreTokens(); storeExpressionVals(0,
					v,
					exprOn))
					v = vTok.nextToken();
			}
			if (exprOn <= 5)
				throw new ParseException("Unexpected end of expression.", expression.length());
			if (exprOn <= YEAR)
				storeExpressionVals(0, "*", YEAR);
			TreeSet dow = getSet(5);
			TreeSet dom = getSet(3);
			boolean dayOfMSpec = !dom.contains(NO_SPEC);
			boolean dayOfWSpec = !dow.contains(NO_SPEC);
			if ((!dayOfMSpec || dayOfWSpec) && (!dayOfWSpec || dayOfMSpec))
				throw new ParseException("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.",
					0);
		}
		catch(ParseException pe)
		{
			throw pe;
		}
		catch(Exception e)
		{
			throw new ParseException((new StringBuilder()).append("Illegal cron expression format (")
				.append(e.toString()).append(")").toString(), 0);
		}
	}

	protected int storeExpressionVals(int pos, String s, int type) throws ParseException
	{
		int incr = 0;
		int i = skipWhiteSpace(pos, s);
		if (i >= s.length())
			return i;
		char c = s.charAt(i);
		if (c >= 'A' && c <= 'Z' && !s.equals("L") && !s.equals("LW"))
		{
			String sub = s.substring(i, i + 3);
			int sval = -1;
			int eval = -1;
			if (type == 4)
			{
				sval = getMonthNumber(sub) + 1;
				if (sval <= 0)
					throw new ParseException((new StringBuilder()).append("Invalid Month value: '").append(sub)
						.append("'").toString(), i);
				if (s.length() > i + 3)
				{
					c = s.charAt(i + 3);
					if (c == '-')
					{
						i += 4;
						sub = s.substring(i, i + 3);
						eval = getMonthNumber(sub) + 1;
						if (eval <= 0)
							throw new ParseException((new StringBuilder()).append("Invalid Month value: '").append(sub)
								.append("'").toString(), i);
					}
				}
			}
			else if (type == 5)
			{
				sval = getDayOfWeekNumber(sub);
				if (sval < 0)
					throw new ParseException((new StringBuilder()).append("Invalid Day-of-Week value: '").append(sub)
						.append("'").toString(), i);
				if (s.length() > i + 3)
				{
					c = s.charAt(i + 3);
					if (c == '-')
					{
						i += 4;
						sub = s.substring(i, i + 3);
						eval = getDayOfWeekNumber(sub);
						if (eval < 0)
							throw new ParseException((new StringBuilder()).append("Invalid Day-of-Week value: '")
								.append(sub).append("'").toString(), i);
					}
					else if (c == '#')
						try
						{
							i += 4;
							nthdayOfWeek = Integer.parseInt(s.substring(i));
							if (nthdayOfWeek < 1 || nthdayOfWeek > 5)
								throw new Exception();
						}
						catch(Exception e)
						{
							throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", i);
						}
					else if (c == 'L')
					{
						lastdayOfWeek = true;
						i++;
					}
				}
			}
			else
			{
				throw new ParseException((new StringBuilder()).append("Illegal characters for this position: '")
					.append(sub).append("'").toString(), i);
			}
			if (eval != -1)
				incr = 1;
			addToSet(sval, eval, incr, type);
			return i + 3;
		}
		if (c == '?')
		{
			if (++i + 1 < s.length() && s.charAt(i) != ' ' && s.charAt(i + 1) != '\t')
				throw new ParseException((new StringBuilder()).append("Illegal character after '?': ")
					.append(s.charAt(i)).toString(), i);
			if (type != 5 && type != 3)
				throw new ParseException("'?' can only be specfied for Day-of-Month or Day-of-Week.", i);
			if (type == 5 && !lastdayOfMonth)
			{
				int val = ((Integer)daysOfMonth.last()).intValue();
				if (val == NO_SPEC_INT)
					throw new ParseException("'?' can only be specfied for Day-of-Month -OR- Day-of-Week.", i);
			}
			addToSet(NO_SPEC_INT, -1, 0, type);
			return i;
		}
		if (c == '*' || c == '/')
		{
			if (c == '*' && i + 1 >= s.length())
			{
				addToSet(ALL_SPEC_INT, -1, incr, type);
				return i + 1;
			}
			if (c == '/' && (i + 1 >= s.length() || s.charAt(i + 1) == ' ' || s.charAt(i + 1) == '\t'))
				throw new ParseException("'/' must be followed by an integer.", i);
			if (c == '*')
				i++;
			c = s.charAt(i);
			if (c == '/')
			{
				if (++i >= s.length())
					throw new ParseException("Unexpected end of string.", i);
				incr = getNumericValue(s, i);
				i++;
				if (incr > 10)
					i++;
				if (incr > 59 && (type == 0 || type == 1))
					throw new ParseException((new StringBuilder()).append("Increment > 60 : ").append(incr).toString(),
						i);
				if (incr > 23 && type == 2)
					throw new ParseException((new StringBuilder()).append("Increment > 24 : ").append(incr).toString(),
						i);
				if (incr > 31 && type == 3)
					throw new ParseException((new StringBuilder()).append("Increment > 31 : ").append(incr).toString(),
						i);
				if (incr > 7 && type == 5)
					throw new ParseException((new StringBuilder()).append("Increment > 7 : ").append(incr).toString(),
						i);
				if (incr > 12 && type == 4)
					throw new ParseException((new StringBuilder()).append("Increment > 12 : ").append(incr).toString(),
						i);
			}
			else
			{
				incr = 1;
			}
			addToSet(ALL_SPEC_INT, -1, incr, type);
			return i;
		}
		if (c == 'L')
		{
			i++;
			if (type == 3)
				lastdayOfMonth = true;
			if (type == 5)
				addToSet(7, 7, 0, type);
			if (type == 3 && s.length() > i)
			{
				c = s.charAt(i);
				if (c == 'W')
				{
					nearestWeekday = true;
					i++;
				}
			}
			return i;
		}
		if (c >= '0' && c <= '9')
		{
			int val = Integer.parseInt(String.valueOf(c));
			if (++i >= s.length())
			{
				addToSet(val, -1, -1, type);
			}
			else
			{
				c = s.charAt(i);
				if (c >= '0' && c <= '9')
				{
					ValueSet vs = getValue(val, s, i);
					val = vs.value;
					i = vs.pos;
				}
				i = checkNext(i, s, val, type);
				return i;
			}
		}
		else
		{
			throw new ParseException((new StringBuilder()).append("Unexpected character: ").append(c).toString(), i);
		}
		return i;
	}

	protected int checkNext(int pos, String s, int val, int type) throws ParseException
	{
		int end = -1;
		int i = pos;
		if (i >= s.length())
		{
			addToSet(val, end, -1, type);
			return i;
		}
		char c = s.charAt(pos);
		if (c == 'L')
		{
			if (type == 5)
				lastdayOfWeek = true;
			else
				throw new ParseException((new StringBuilder()).append("'L' option is not valid here. (pos=").append(i)
					.append(")").toString(), i);
			TreeSet set = getSet(type);
			set.add(new Integer(val));
			return ++i;
		}
		if (c == 'W')
		{
			if (type == 3)
				nearestWeekday = true;
			else
				throw new ParseException((new StringBuilder()).append("'W' option is not valid here. (pos=").append(i)
					.append(")").toString(), i);
			TreeSet set = getSet(type);
			set.add(new Integer(val));
			return ++i;
		}
		if (c == '#')
		{
			if (type != 5)
				throw new ParseException((new StringBuilder()).append("'#' option is not valid here. (pos=").append(i)
					.append(")").toString(), i);
			i++;
			try
			{
				nthdayOfWeek = Integer.parseInt(s.substring(i));
				if (nthdayOfWeek < 1 || nthdayOfWeek > 5)
					throw new Exception();
			}
			catch(Exception e)
			{
				throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", i);
			}
			TreeSet set = getSet(type);
			set.add(new Integer(val));
			return ++i;
		}
		if (c == '-')
		{
			i++;
			c = s.charAt(i);
			int v = Integer.parseInt(String.valueOf(c));
			end = v;
			if (++i >= s.length())
			{
				addToSet(val, end, 1, type);
				return i;
			}
			c = s.charAt(i);
			if (c >= '0' && c <= '9')
			{
				ValueSet vs = getValue(v, s, i);
				int v1 = vs.value;
				end = v1;
				i = vs.pos;
			}
			if (i < s.length() && (c = s.charAt(i)) == '/')
			{
				i++;
				c = s.charAt(i);
				int v2 = Integer.parseInt(String.valueOf(c));
				if (++i >= s.length())
				{
					addToSet(val, end, v2, type);
					return i;
				}
				c = s.charAt(i);
				if (c >= '0' && c <= '9')
				{
					ValueSet vs = getValue(v2, s, i);
					int v3 = vs.value;
					addToSet(val, end, v3, type);
					i = vs.pos;
					return i;
				}
				else
				{
					addToSet(val, end, v2, type);
					return i;
				}
			}
			else
			{
				addToSet(val, end, 1, type);
				return i;
			}
		}
		if (c == '/')
		{
			i++;
			c = s.charAt(i);
			int v2 = Integer.parseInt(String.valueOf(c));
			if (++i >= s.length())
			{
				addToSet(val, end, v2, type);
				return i;
			}
			c = s.charAt(i);
			if (c >= '0' && c <= '9')
			{
				ValueSet vs = getValue(v2, s, i);
				int v3 = vs.value;
				addToSet(val, end, v3, type);
				i = vs.pos;
				return i;
			}
			else
			{
				throw new ParseException((new StringBuilder()).append("Unexpected character '").append(c)
					.append("' after '/'").toString(), i);
			}
		}
		else
		{
			addToSet(val, end, 0, type);
			return ++i;
		}
	}

	public String getCronExpression()
	{
		return cronExpression;
	}

	public String getExpressionSummary()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("seconds: ");
		buf.append(getExpressionSetSummary(seconds));
		buf.append("\n");
		buf.append("minutes: ");
		buf.append(getExpressionSetSummary(minutes));
		buf.append("\n");
		buf.append("hours: ");
		buf.append(getExpressionSetSummary(hours));
		buf.append("\n");
		buf.append("daysOfMonth: ");
		buf.append(getExpressionSetSummary(daysOfMonth));
		buf.append("\n");
		buf.append("months: ");
		buf.append(getExpressionSetSummary(months));
		buf.append("\n");
		buf.append("daysOfWeek: ");
		buf.append(getExpressionSetSummary(daysOfWeek));
		buf.append("\n");
		buf.append("lastdayOfWeek: ");
		buf.append(lastdayOfWeek);
		buf.append("\n");
		buf.append("nearestWeekday: ");
		buf.append(nearestWeekday);
		buf.append("\n");
		buf.append("NthDayOfWeek: ");
		buf.append(nthdayOfWeek);
		buf.append("\n");
		buf.append("lastdayOfMonth: ");
		buf.append(lastdayOfMonth);
		buf.append("\n");
		buf.append("years: ");
		buf.append(getExpressionSetSummary(years));
		buf.append("\n");
		return buf.toString();
	}

	protected String getExpressionSetSummary(Set set)
	{
		if (set.contains(NO_SPEC))
			return "?";
		if (set.contains(ALL_SPEC))
			return "*";
		StringBuffer buf = new StringBuffer();
		Iterator itr = set.iterator();
		for(boolean first = true; itr.hasNext(); first = false)
		{
			Integer iVal = (Integer)itr.next();
			String val = iVal.toString();
			if (!first)
				buf.append(",");
			buf.append(val);
		}
		return buf.toString();
	}

	protected String getExpressionSetSummary(ArrayList list)
	{
		if (list.contains(NO_SPEC))
			return "?";
		if (list.contains(ALL_SPEC))
			return "*";
		StringBuffer buf = new StringBuffer();
		Iterator itr = list.iterator();
		for(boolean first = true; itr.hasNext(); first = false)
		{
			Integer iVal = (Integer)itr.next();
			String val = iVal.toString();
			if (!first)
				buf.append(",");
			buf.append(val);
		}
		return buf.toString();
	}

	protected int skipWhiteSpace(int i, String s)
	{
		for(; i < s.length() && (s.charAt(i) == ' ' || s.charAt(i) == '\t'); i++);
		return i;
	}

	protected int findNextWhiteSpace(int i, String s)
	{
		for(; i < s.length() && (s.charAt(i) != ' ' || s.charAt(i) != '\t'); i++);
		return i;
	}

	protected void addToSet(int val, int end, int incr, int type) throws ParseException
	{
		TreeSet set = getSet(type);
		if (type == 0 || type == 1)
		{
			if ((val < 0 || val > 59 || end > 59) && val != ALL_SPEC_INT)
				throw new ParseException("Minute and Second values must be between 0 and 59", -1);
		}
		else if (type == 2)
		{
			if ((val < 0 || val > 23 || end > 23) && val != ALL_SPEC_INT)
				throw new ParseException("Hour values must be between 0 and 23", -1);
		}
		else if (type == 3)
		{
			if ((val < 1 || val > 31 || end > 31) && val != ALL_SPEC_INT && val != NO_SPEC_INT)
				throw new ParseException("Day of month values must be between 1 and 31", -1);
		}
		else if (type == 4)
		{
			if ((val < 1 || val > 12 || end > 12) && val != ALL_SPEC_INT)
				throw new ParseException("Month values must be between 1 and 12", -1);
		}
		else if (type == 5 && (val == 0 || val > 7 || end > 7) && val != ALL_SPEC_INT && val != NO_SPEC_INT)
			throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
		if ((incr == 0 || incr == -1) && val != ALL_SPEC_INT)
		{
			if (val != -1)
				set.add(new Integer(val));
			else
				set.add(NO_SPEC);
			return;
		}
		int startAt = val;
		int stopAt = end;
		if (val == ALL_SPEC_INT && incr <= 0)
		{
			incr = 1;
			set.add(ALL_SPEC);
		}
		if (type == 0 || type == 1)
		{
			if (stopAt == -1)
				stopAt = 59;
			if (startAt == -1 || startAt == ALL_SPEC_INT)
				startAt = 0;
		}
		else if (type == 2)
		{
			if (stopAt == -1)
				stopAt = 23;
			if (startAt == -1 || startAt == ALL_SPEC_INT)
				startAt = 0;
		}
		else if (type == 3)
		{
			if (stopAt == -1)
				stopAt = 31;
			if (startAt == -1 || startAt == ALL_SPEC_INT)
				startAt = 1;
		}
		else if (type == 4)
		{
			if (stopAt == -1)
				stopAt = 12;
			if (startAt == -1 || startAt == ALL_SPEC_INT)
				startAt = 1;
		}
		else if (type == 5)
		{
			if (stopAt == -1)
				stopAt = 7;
			if (startAt == -1 || startAt == ALL_SPEC_INT)
				startAt = 1;
		}
		else if (type == 6)
		{
			if (stopAt == -1)
				stopAt = 2299;
			if (startAt == -1 || startAt == ALL_SPEC_INT)
				startAt = 1970;
		}
		int max = -1;
		if (stopAt < startAt)
		{
			switch(type)
			{
			case 0: // '\0'
				max = 60;
				break;
			case 1: // '\001'
				max = 60;
				break;
			case 2: // '\002'
				max = 24;
				break;
			case 4: // '\004'
				max = 12;
				break;
			case 5: // '\005'
				max = 7;
				break;
			case 3: // '\003'
				max = 31;
				break;
			case 6: // '\006'
				throw new IllegalArgumentException("Start year must be less than stop year");
			default:
				throw new IllegalArgumentException("Unexpected type encountered");
			}
			stopAt += max;
		}
		for(int i = startAt; i <= stopAt; i += incr)
		{
			if (max == -1)
			{
				set.add(new Integer(i));
				continue;
			}
			int i2 = i % max;
			if (i2 == 0 && (type == 4 || type == 5 || type == 3))
				i2 = max;
			set.add(new Integer(i2));
		}
	}

	protected TreeSet getSet(int type)
	{
		switch(type)
		{
		case 0: // '\0'
			return seconds;
		case 1: // '\001'
			return minutes;
		case 2: // '\002'
			return hours;
		case 3: // '\003'
			return daysOfMonth;
		case 4: // '\004'
			return months;
		case 5: // '\005'
			return daysOfWeek;
		case 6: // '\006'
			return years;
		}
		return null;
	}

	protected ValueSet getValue(int v, String s, int i)
	{
		char c = s.charAt(i);
		String s1 = String.valueOf(v);
		do
		{
			if (c < '0' || c > '9')
				break;
			s1 = (new StringBuilder()).append(s1).append(c).toString();
			if (++i >= s.length())
				break;
			c = s.charAt(i);
		}
		while(true);
		ValueSet val = new ValueSet();
		val.pos = i >= s.length()?i + 1:i;
		val.value = Integer.parseInt(s1);
		return val;
	}

	protected int getNumericValue(String s, int i)
	{
		int endOfVal = findNextWhiteSpace(i, s);
		String val = s.substring(i, endOfVal);
		return Integer.parseInt(val);
	}

	protected int getMonthNumber(String s)
	{
		Integer integer = (Integer)monthMap.get(s);
		if (integer == null)
			return -1;
		else
			return integer.intValue();
	}

	protected int getDayOfWeekNumber(String s)
	{
		Integer integer = (Integer)dayMap.get(s);
		if (integer == null)
			return -1;
		else
			return integer.intValue();
	}

	protected Date getTimeAfter(Date afterTime)
	{
		Calendar cl = new GregorianCalendar(getTimeZone());
		afterTime = new Date(afterTime.getTime() + 1000L);
		cl.setTime(afterTime);
		cl.set(Calendar.MILLISECOND, 0);
		boolean gotOne = false;
		do
		{
			if (gotOne)
				break;
			if (cl.get(1) > 2999)
				return null;
			SortedSet st = null;
			int t = 0;
			int sec = cl.get(13);
			int min = cl.get(12);
			st = seconds.tailSet(new Integer(sec));
			if (st != null && st.size() != 0)
			{
				sec = ((Integer)st.first()).intValue();
			}
			else
			{
				sec = ((Integer)seconds.first()).intValue();
				min++;
				cl.set(12, min);
			}
			cl.set(13, sec);
			min = cl.get(12);
			int hr = cl.get(11);
			t = -1;
			st = minutes.tailSet(new Integer(min));
			if (st != null && st.size() != 0)
			{
				t = min;
				min = ((Integer)st.first()).intValue();
			}
			else
			{
				min = ((Integer)minutes.first()).intValue();
				hr++;
			}
			if (min != t)
			{
				cl.set(13, 0);
				cl.set(12, min);
				setCalendarHour(cl, hr);
				continue;
			}
			cl.set(12, min);
			hr = cl.get(11);
			int day = cl.get(5);
			t = -1;
			st = hours.tailSet(new Integer(hr));
			if (st != null && st.size() != 0)
			{
				t = hr;
				hr = ((Integer)st.first()).intValue();
			}
			else
			{
				hr = ((Integer)hours.first()).intValue();
				day++;
			}
			if (hr != t)
			{
				cl.set(13, 0);
				cl.set(12, 0);
				cl.set(5, day);
				setCalendarHour(cl, hr);
				continue;
			}
			cl.set(11, hr);
			day = cl.get(5);
			int mon = cl.get(2) + 1;
			t = -1;
			int tmon = mon;
			boolean dayOfMSpec = !daysOfMonth.contains(NO_SPEC);
			boolean dayOfWSpec = !daysOfWeek.contains(NO_SPEC);
			if (dayOfMSpec && !dayOfWSpec)
			{
				st = daysOfMonth.tailSet(new Integer(day));
				if (lastdayOfMonth)
				{
					if (!nearestWeekday)
					{
						t = day;
						day = getLastDayOfMonth(mon, cl.get(1));
					}
					else
					{
						t = day;
						day = getLastDayOfMonth(mon, cl.get(1));
						Calendar tcal = Calendar.getInstance(getTimeZone());
						tcal.set(13, 0);
						tcal.set(12, 0);
						tcal.set(11, 0);
						tcal.set(5, day);
						tcal.set(2, mon - 1);
						tcal.set(1, cl.get(1));
						int ldom = getLastDayOfMonth(mon, cl.get(1));
						int dow = tcal.get(7);
						if (dow == 7 && day == 1)
							day += 2;
						else if (dow == 7)
							day--;
						else if (dow == 1 && day == ldom)
							day -= 2;
						else if (dow == 1)
							day++;
						tcal.set(13, sec);
						tcal.set(12, min);
						tcal.set(11, hr);
						tcal.set(5, day);
						tcal.set(2, mon - 1);
						Date nTime = tcal.getTime();
						if (nTime.before(afterTime))
						{
							day = 1;
							mon++;
						}
					}
				}
				else if (nearestWeekday)
				{
					t = day;
					day = ((Integer)daysOfMonth.first()).intValue();
					Calendar tcal = Calendar.getInstance(getTimeZone());
					tcal.set(13, 0);
					tcal.set(12, 0);
					tcal.set(11, 0);
					tcal.set(5, day);
					tcal.set(2, mon - 1);
					tcal.set(1, cl.get(1));
					int ldom = getLastDayOfMonth(mon, cl.get(1));
					int dow = tcal.get(7);
					if (dow == 7 && day == 1)
						day += 2;
					else if (dow == 7)
						day--;
					else if (dow == 1 && day == ldom)
						day -= 2;
					else if (dow == 1)
						day++;
					tcal.set(13, sec);
					tcal.set(12, min);
					tcal.set(11, hr);
					tcal.set(5, day);
					tcal.set(2, mon - 1);
					Date nTime = tcal.getTime();
					if (nTime.before(afterTime))
					{
						day = ((Integer)daysOfMonth.first()).intValue();
						mon++;
					}
				}
				else if (st != null && st.size() != 0)
				{
					t = day;
					day = ((Integer)st.first()).intValue();
					int lastDay = getLastDayOfMonth(mon, cl.get(1));
					if (day > lastDay)
					{
						day = ((Integer)daysOfMonth.first()).intValue();
						mon++;
					}
				}
				else
				{
					day = ((Integer)daysOfMonth.first()).intValue();
					mon++;
				}
				if (day != t || mon != tmon)
				{
					cl.set(13, 0);
					cl.set(12, 0);
					cl.set(11, 0);
					cl.set(5, day);
					cl.set(2, mon - 1);
					continue;
				}
			}
			else if (dayOfWSpec && !dayOfMSpec)
			{
				if (lastdayOfWeek)
				{
					int dow = ((Integer)daysOfWeek.first()).intValue();
					int cDow = cl.get(7);
					int daysToAdd = 0;
					if (cDow < dow)
						daysToAdd = dow - cDow;
					if (cDow > dow)
						daysToAdd = dow + (7 - cDow);
					int lDay = getLastDayOfMonth(mon, cl.get(1));
					if (day + daysToAdd > lDay)
					{
						cl.set(13, 0);
						cl.set(12, 0);
						cl.set(11, 0);
						cl.set(5, 1);
						cl.set(2, mon);
						continue;
					}
					for(; day + daysToAdd + 7 <= lDay; daysToAdd += 7);
					day += daysToAdd;
					if (daysToAdd > 0)
					{
						cl.set(13, 0);
						cl.set(12, 0);
						cl.set(11, 0);
						cl.set(5, day);
						cl.set(2, mon - 1);
						continue;
					}
				}
				else if (nthdayOfWeek != 0)
				{
					int dow = ((Integer)daysOfWeek.first()).intValue();
					int cDow = cl.get(7);
					int daysToAdd = 0;
					if (cDow < dow)
						daysToAdd = dow - cDow;
					else if (cDow > dow)
						daysToAdd = dow + (7 - cDow);
					boolean dayShifted = false;
					if (daysToAdd > 0)
						dayShifted = true;
					day += daysToAdd;
					int weekOfMonth = day / 7;
					if (day % 7 > 0)
						weekOfMonth++;
					daysToAdd = (nthdayOfWeek - weekOfMonth) * 7;
					day += daysToAdd;
					if (daysToAdd < 0 || day > getLastDayOfMonth(mon, cl.get(1)))
					{
						cl.set(13, 0);
						cl.set(12, 0);
						cl.set(11, 0);
						cl.set(5, 1);
						cl.set(2, mon);
						continue;
					}
					if (daysToAdd > 0 || dayShifted)
					{
						cl.set(13, 0);
						cl.set(12, 0);
						cl.set(11, 0);
						cl.set(5, day);
						cl.set(2, mon - 1);
						continue;
					}
				}
				else
				{
					int cDow = cl.get(7);
					int dow = ((Integer)daysOfWeek.first()).intValue();
					st = daysOfWeek.tailSet(new Integer(cDow));
					if (st != null && st.size() > 0)
						dow = ((Integer)st.first()).intValue();
					int daysToAdd = 0;
					if (cDow < dow)
						daysToAdd = dow - cDow;
					if (cDow > dow)
						daysToAdd = dow + (7 - cDow);
					int lDay = getLastDayOfMonth(mon, cl.get(1));
					if (day + daysToAdd > lDay)
					{
						cl.set(13, 0);
						cl.set(12, 0);
						cl.set(11, 0);
						cl.set(5, 1);
						cl.set(2, mon);
						continue;
					}
					if (daysToAdd > 0)
					{
						cl.set(13, 0);
						cl.set(12, 0);
						cl.set(11, 0);
						cl.set(5, day + daysToAdd);
						cl.set(2, mon - 1);
						continue;
					}
				}
			}
			else
			{
				throw new UnsupportedOperationException("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
			}
			cl.set(5, day);
			mon = cl.get(2) + 1;
			int year = cl.get(1);
			t = -1;
			if (year > 2299)
				return null;
			st = months.tailSet(new Integer(mon));
			if (st != null && st.size() != 0)
			{
				t = mon;
				mon = ((Integer)st.first()).intValue();
			}
			else
			{
				mon = ((Integer)months.first()).intValue();
				year++;
			}
			if (mon != t)
			{
				cl.set(13, 0);
				cl.set(12, 0);
				cl.set(11, 0);
				cl.set(5, 1);
				cl.set(2, mon - 1);
				cl.set(1, year);
			}
			else
			{
				cl.set(2, mon - 1);
				year = cl.get(1);
				t = -1;
				st = years.tailSet(new Integer(year));
				if (st != null && st.size() != 0)
				{
					t = year;
					year = ((Integer)st.first()).intValue();
				}
				else
				{
					return null;
				}
				if (year != t)
				{
					cl.set(13, 0);
					cl.set(12, 0);
					cl.set(11, 0);
					cl.set(5, 1);
					cl.set(2, 0);
					cl.set(1, year);
				}
				else
				{
					cl.set(1, year);
					gotOne = true;
				}
			}
		}
		while(true);
		return cl.getTime();
	}

	protected void setCalendarHour(Calendar cal, int hour)
	{
		cal.set(11, hour);
		if (cal.get(11) != hour && hour != 24)
			cal.set(11, hour + 1);
	}

	protected Date getTimeBefore(Date endTime)
	{
		return null;
	}

	public Date getFinalFireTime()
	{
		return null;
	}

	protected boolean isLeapYear(int year)
	{
		return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
	}

	protected int getLastDayOfMonth(int monthNum, int year)
	{
		switch(monthNum)
		{
		case 1: // '\001'
			return 31;
		case 2: // '\002'
			return isLeapYear(year)?29:28;
		case 3: // '\003'
			return 31;
		case 4: // '\004'
			return 30;
		case 5: // '\005'
			return 31;
		case 6: // '\006'
			return 30;
		case 7: // '\007'
			return 31;
		case 8: // '\b'
			return 31;
		case 9: // '\t'
			return 30;
		case 10: // '\n'
			return 31;
		case 11: // '\013'
			return 30;
		case 12: // '\f'
			return 31;
		}
		throw new IllegalArgumentException((new StringBuilder()).append("Illegal month number: ").append(monthNum)
			.toString());
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		try
		{
			buildExpression(cronExpression);
		}
		catch(Exception ignore)
		{
		}
	}

	public Object clone()
	{
		CronExpression copy = null;
		try
		{
			copy = new CronExpression(getCronExpression());
			if (getTimeZone() != null)
				copy.setTimeZone((TimeZone)getTimeZone().clone());
		}
		catch(ParseException ex)
		{
			throw new IncompatibleClassChangeError("Not Cloneable.");
		}
		return copy;
	}

	private static final long serialVersionUID = 0x2e47e2f0fL;
	protected static final int SECOND = 0;
	protected static final int MINUTE = 1;
	protected static final int HOUR = 2;
	protected static final int DAY_OF_MONTH = 3;
	protected static final int MONTH = 4;
	protected static final int DAY_OF_WEEK = 5;
	protected static final int YEAR = 6;
	protected static final int ALL_SPEC_INT = 99;
	protected static final int NO_SPEC_INT = 98;
	protected static final Integer ALL_SPEC = new Integer(ALL_SPEC_INT);
	protected static final Integer NO_SPEC = new Integer(NO_SPEC_INT);
	protected static final Map monthMap;
	protected static final Map dayMap;
	private String cronExpression;
	private TimeZone timeZone;
	protected transient TreeSet seconds;
	protected transient TreeSet minutes;
	protected transient TreeSet hours;
	protected transient TreeSet daysOfMonth;
	protected transient TreeSet months;
	protected transient TreeSet daysOfWeek;
	protected transient TreeSet years;
	protected transient boolean lastdayOfWeek;
	protected transient int nthdayOfWeek;
	protected transient boolean lastdayOfMonth;
	protected transient boolean nearestWeekday;
	protected transient boolean expressionParsed;
	static
	{
		monthMap = new HashMap(20);
		dayMap = new HashMap(60);
		monthMap.put("JAN", new Integer(0));
		monthMap.put("FEB", new Integer(1));
		monthMap.put("MAR", new Integer(2));
		monthMap.put("APR", new Integer(3));
		monthMap.put("MAY", new Integer(4));
		monthMap.put("JUN", new Integer(5));
		monthMap.put("JUL", new Integer(6));
		monthMap.put("AUG", new Integer(7));
		monthMap.put("SEP", new Integer(8));
		monthMap.put("OCT", new Integer(9));
		monthMap.put("NOV", new Integer(10));
		monthMap.put("DEC", new Integer(11));
		dayMap.put("SUN", new Integer(1));
		dayMap.put("MON", new Integer(2));
		dayMap.put("TUE", new Integer(3));
		dayMap.put("WED", new Integer(4));
		dayMap.put("THU", new Integer(5));
		dayMap.put("FRI", new Integer(6));
		dayMap.put("SAT", new Integer(7));
	}

	class ValueSet
	{
		public int value;
		public int pos;
	}
}
