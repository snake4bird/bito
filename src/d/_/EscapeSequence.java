package d._;

public class EscapeSequence implements d.EscapeSequence
{
	private static String InvalidEscapeSequence = "Invalid escape sequence '\\', "
		+ "valid ones are \\b \\t \\n \\f \\r \\\\ \\\" \\\' \\uhhhh \\xhh \\0xhh \\0ooo";

	public EscapeSequence()
	{
	}

	public String encode(String s)
	{
		if (s == null || s.length() == 0)
		{
			return null;
		}
		char[] cs = s.toCharArray();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < cs.length; i++)
		{
			switch(cs[i])
			{
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\"':
				sb.append("\\\"");
				break;
			case '\'':
				sb.append("\\\'");
				break;
			default:
				if (cs[i] < ' ' || cs[i] > '~')
				{
					String ihs = Integer.toHexString((int)cs[i]);
					while(ihs.length() < 4)
					{
						ihs = "0" + ihs;
					}
					sb.append("\\u" + ihs);
				}
				else
				{
					sb.append(cs[i]);
				}
				break;
			}
		}
		return sb.toString();
	}

	public String decode(String s, boolean allowerror)
	{
		if (s == null || s.length() == 0)
		{
			return s;
		}
		char[] cs = s.toCharArray();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < cs.length; i++)
		{
			if (cs[i] == '\\' && (i + 1) < cs.length)
			{
				i++;
				switch(cs[i])
				{
				case 'b':
					sb.append('\b');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'r':
					sb.append('\r');
					break;
				case '\\':
					sb.append('\\');
					break;
				case '\"':
					sb.append('\"');
					break;
				case '\'':
					sb.append('\'');
					break;
				case 'u':
				case 'U':
					i = appendU(cs, i, sb, allowerror);
					break;
				case 'x':
				case 'X':
					i = appendX(cs, i, sb, allowerror);
					break;
				case '0':
					i = append0(cs, i, sb, allowerror);
					break;
				default:
					if (allowerror)
					{
						if (('a' <= cs[i] && cs[i] <= 'z')
							|| ('A' <= cs[i] && cs[i] <= 'Z')
								|| ('0' <= cs[i] && cs[i] <= '9'))
						{
							sb.append('\\');
						}
						sb.append(cs[i]);
					}
					else
					{
						throw new RuntimeException(InvalidEscapeSequence);
					}
					break;
				}
			}
			else
			{
				if (!allowerror && cs[i] == '\\')
				{
					throw new RuntimeException(InvalidEscapeSequence);
				}
				sb.append(cs[i]);
			}
		}
		return sb.toString();
	}

	private static int append0(char[] cs, int i, StringBuffer sb,
		boolean allowerror)
	{
		try
		{
			if (cs[i + 1] == 'x' || cs[i + 1] == 'X')
			{
				i = appendX(cs, i + 1, sb, allowerror);
			}
			else
			{
				i = appendO(cs, i, sb, allowerror);
			}
		}
		catch(Exception e)
		{
			if (allowerror)
			{
				// skip '\0' 
			}
			else
			{
				throw new RuntimeException(e);
			}
		}
		return i;
	}

	private static int appendU(char[] cs, int i, StringBuffer sb,
		boolean allowerror)
	{
		try
		{
			StringBuffer xsb = new StringBuffer();
			xsb.append(cs[i + 1]);
			xsb.append(cs[i + 2]);
			xsb.append(cs[i + 3]);
			xsb.append(cs[i + 4]);
			char c = (char)Integer.parseInt(xsb.toString(), 16);
			sb.append(c);
			i += 4;
		}
		catch(Exception e)
		{
			if (allowerror)
			{
				sb.append('\\');
				sb.append(cs[i]);
			}
			else
			{
				throw new RuntimeException(e);
			}
		}
		return i;
	}

	private static int appendO(char[] cs, int i, StringBuffer sb,
		boolean allowerror)
	{
		int x = i + 1;
		StringBuffer xsb = new StringBuffer();
		while(x <= i + 3 && '0' <= cs[x] && cs[x] <= '7')
		{
			xsb.append(cs[x]);
			x++;
		}
		char c = (char)Integer.parseInt(xsb.toString(), 8);
		sb.append(c);
		i = x - 1;
		return i;
	}

	private static int appendX(char[] cs, int i, StringBuffer sb,
		boolean allowerror)
	{
		try
		{
			StringBuffer xsb = new StringBuffer();
			xsb.append(cs[i + 1]);
			xsb.append(cs[i + 2]);
			char c = (char)Integer.parseInt(xsb.toString(), 16);
			sb.append(c);
			i += 2;
		}
		catch(Exception e)
		{
			if (allowerror)
			{
				sb.append('\\');
				sb.append(cs[i]);
			}
			else
			{
				throw new RuntimeException(e);
			}
		}
		return i;
	}
}
