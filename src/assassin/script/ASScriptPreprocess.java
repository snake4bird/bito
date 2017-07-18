package assassin.script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ASScriptPreprocess
{
	private static ArrayList<Pair> pair_define = new ArrayList();

	private static class Pair implements Serializable
	{
		ASScriptPreprocess asp;
		String begin;
		String end;
		boolean recursive_enabled;
		int i_begin;
		int i_end;
		StringBuffer sb_last_section_content = new StringBuffer();

		Pair(String begin, String end, boolean recursive_enabled)
		{
			this.asp = null;
			this.begin = begin;
			this.end = end;
			this.recursive_enabled = recursive_enabled;
			this.i_begin = 0;
			this.i_end = 0;
		}

		Pair apply(ASScriptPreprocess asp)
		{
			try
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(this);
				oos.close();
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				ObjectInputStream ois = new ObjectInputStream(bais);
				Pair dp = (Pair)ois.readObject();
				ois.close();
				dp.asp = asp;
				return dp;
			}
			catch(IOException e)
			{
			}
			catch(ClassNotFoundException e)
			{
			}
			return null;
		}

		Object get(String name)
		{
			try
			{
				return this.getClass().getField(name).get(this);
			}
			catch(Exception e)
			{
				return null;
			}
		}

		boolean match_begin(ASScriptPreprocess asp)
		{
			for(int i = 0; i < this.begin.length(); i++)
			{
				if (asp.cs[asp.i_pos + i] != this.begin.charAt(i))
				{
					return false;
				}
			}
			return true;
		}

		boolean match_end()
		{
			for(int i = 0; i < this.end.length(); i++)
			{
				if (asp.cs[asp.i_pos + i] != this.end.charAt(i))
				{
					return false;
				}
			}
			return true;
		}

		void begin_proc()
		{
			i_begin = asp.i_pos;
			for(int i = 0; i < this.begin.length(); i++)
			{
				sb_last_section_content.append(asp.cs[asp.i_pos++]);
			}
			i_end = asp.i_pos;
		}

		void in_proc()
		{
			sb_last_section_content.append(asp.cs[asp.i_pos++]);
			i_end = asp.i_pos;
		}

		void pause_proc(Pair npair)
		{
			asp.sbout.append(this.sb_last_section_content);
			i_end = asp.i_pos;
			this.sb_last_section_content = new StringBuffer();
		}

		void continue_proc(Pair npair)
		{
		}

		void end_proc()
		{
			for(int i = 0; i < this.end.length(); i++)
			{
				sb_last_section_content.append(asp.cs[asp.i_pos++]);
			}
			asp.sbout.append(this.sb_last_section_content);
			i_end = asp.i_pos;
		}
	}

	static
	{
		pair_define.addAll(
			Arrays.asList(new Pair[]{new Pair("//", "\n", false), new Pair("/*", "*/", false), new Pair("`", "`", false)
			{
				void begin_proc()
				{
					this.i_begin = asp.i_pos;
					this.sb_last_section_content.append("\"");
					asp.i_pos = this.i_begin + this.begin.length();
					this.i_end = asp.i_pos;
				}

				void in_proc()
				{
					char c = asp.cs[i_end];
					if (c == '\r' && (i_end + 1) < asp.cs.length && asp.cs[i_end + 1] == '\n')
					{
						sb_last_section_content.append("\\r\\n\"+").append("\r\n");
						c = '\"';
						asp.i_pos++;
					}
					else if (c == '\r')
					{
						sb_last_section_content.append("\\");
						c = 'r';
						i_end--;
					}
					else if (c == '\n')
					{
						sb_last_section_content.append("\\n\"+").append("\n");
						c = '\"';
					}
					else if (c == '\\')
					{
						sb_last_section_content.append("\\");
					}
					else if (c == '\'')
					{
						sb_last_section_content.append("\\");
					}
					else if (c == '\"')
					{
						sb_last_section_content.append("\\");
					}
					sb_last_section_content.append(c);
					asp.i_pos++;
					this.i_end = asp.i_pos;
				}

				void end_proc()
				{
					this.sb_last_section_content.append("\"");
					this.i_end += this.end.length();
					asp.sbout.append(this.sb_last_section_content);
					asp.i_pos = this.i_end;
				}
			}, new Pair("\"", "\"", false), new Pair("\'", "\'", false), new Pair("{", "}", true)
			{
				StringBuffer sbls = new StringBuffer();
				public String last_statement = null;
				boolean isobject = false;

				void begin_proc()
				{
					super.begin_proc();
				}

				void in_proc()
				{
					char c = asp.cs[asp.i_pos];
					sbls.append(c);
					if (c == ':' || c == '?' || c == '=')
					{
						isobject = true;
					}
					else if (c == ';')
					{
						isobject = false;
						save_lao();
					}
					else if (!isobject && Character.isWhitespace(c))
					{
						save_lao();
					}
					super.in_proc();
				}

				void pause_proc(Pair npair)
				{
					super.pause_proc(npair);
				}

				void continue_proc(Pair nnpair)
				{
					if (nnpair.begin.equals("{"))
					{
						String ls = (String)nnpair.get("last_statement");
						if (ls != null)
						{
							if (ls.length() == nnpair.i_end - nnpair.i_begin - 2)
							{
								sbls.append(nnpair.begin + ls + nnpair.end);
							}
							else
							{
								sbls = new StringBuffer(ls);
							}
						}
					}
					else if (nnpair.begin.equals("//") || nnpair.begin.equals("/*"))
					{
						// skip comments
					}
					else
					{
						sbls.append(nnpair.sb_last_section_content);
					}
				}

				void save_lao()
				{
					String s = sbls.toString();
					if (s.trim().length() > 0)
					{
						last_statement = s;
						sbls = new StringBuffer();
					}
				}

				void end_proc()
				{
					save_lao();
					super.end_proc();
				}
			}, new Pair("$.include(", ")", true)
			{
				boolean match_begin()
				{
					if (asp.cs[asp.i_pos] == '$')
					{
						if (asp.i_pos == 0 || new String(asp.cs, asp.i_pos - 1, 1).matches("(?s)\\b"))
						{
							String ts = new String(asp.cs, asp.i_pos + 7, asp.cs.length - (asp.i_pos + 7));
							Pattern p = Pattern.compile("(?s)(\\s*.\\s*include\\s*\\().*");
							Matcher m = p.matcher(ts);
							if (m.find())
							{
								begin = "$" + m.group(1);
								return true;
							}
						}
					}
					return false;
				}

				void end_proc()
				{
					sb_last_section_content.append(", \"" + asp.filename + "\"");
					super.end_proc();
				}
			}, new Pair("--@$$", "--@$$", true)
			{
				String s_var = "";
				int n = 0;
				boolean iv = false;

				void begin_proc()
				{
					this.i_begin = asp.i_pos;
					this.sb_last_section_content.append("/*" + this.begin + "*/");
					asp.i_pos = this.i_begin + this.begin.length();
					this.i_end = asp.i_pos;
				}

				void in_proc()
				{
					char c = asp.cs[asp.i_pos];
					if (!iv && !Character.isWhitespace(c) && c != ';')
					{
						if (n == 0)
						{
							int i_var = asp.i_vars++;
							s_var = "_var_" + i_var + "_";
							sb_last_section_content.append(s_var + "=[];");
						}
						sb_last_section_content.append(s_var + ".push(\"");
						n++;
						iv = true;
					}
					if (iv)
					{
						if (c == '\r' && (asp.i_pos + 1) < asp.cs.length && asp.cs[asp.i_pos + 1] == '\n')
						{
							sb_last_section_content.append("\\r\\n\");").append("\r");
							c = '\n';
							asp.i_pos++;
							iv = false;
						}
						else if (c == '\r')
						{
							sb_last_section_content.append("\\");
							c = 'r';
							asp.i_pos--;
						}
						else if (c == '\n')
						{
							sb_last_section_content.append("\\n\");");
							iv = false;
						}
						else if (c == ';')
						{
							sb_last_section_content.append("\");");
							iv = false;
						}
						else if (c == '\'')
						{
							sb_last_section_content.append("\\");
						}
						else if (c == '\"')
						{
							sb_last_section_content.append("\\");
						}
					}
					if (c == ';')
					{
						if (n > 0)
						{
							sb_last_section_content.append("runsql(" + s_var + ")");
							n = 0;
						}
					}
					sb_last_section_content.append(c);
					asp.i_pos++;
					this.i_end = asp.i_pos;
				}

				void pause_proc(Pair npair)
				{
					if (iv)
					{
						sb_last_section_content.append("\");");
						iv = false;
					}
					//
					if (npair.begin.equals("{"))
					{
						if (n == 0)
						{
							int i_var = asp.i_vars++;
							s_var = "_var_" + i_var + "_";
							sb_last_section_content.append(s_var + "=[];");
						}
						sb_last_section_content.append(s_var + ".push(function(){");
						n++;
					}
					else if (npair.begin.equals("\'") || npair.begin.equals("\"") || npair.begin.equals("`"))
					{
						if (n == 0)
						{
							int i_var = asp.i_vars++;
							s_var = "_var_" + i_var + "_";
							sb_last_section_content.append(s_var + "=[];");
						}
						sb_last_section_content.append(s_var + ".push(\"");
						n++;
					}
					else
					{
						// ignore comments
					}
					super.pause_proc(npair);
				}

				void continue_proc(Pair npair)
				{
					if (npair.begin.equals("{"))
					{
						int n = 0;
						String s = (String)npair.get("last_statement");
						if (s != null
							&& (n = asp.sbout.lastIndexOf(s.trim())) >= 0
								&& !s.matches(".*[\\{\\s]return\\s.*"))
						{
							asp.sbout.insert(n, " return ");
						}
						sb_last_section_content.append("});");
					}
					else if (npair.begin.equals("\'") || npair.begin.equals("\"") || npair.begin.equals("`"))
					{
						sb_last_section_content.append("\");");
					}
				}

				void end_proc()
				{
					if (n > 0)
					{
						sb_last_section_content.append("runsql(" + s_var + ")");
						sb_last_section_content.append(";");
					}
					sb_last_section_content.append("/*" + this.end + "*/");
					asp.i_pos += this.end.length();
					asp.sbout.append(sb_last_section_content);
					this.i_end = asp.i_pos;
				}
			}}));
	}

	public ASScriptPreprocess()
	{
	}

	private String filename;
	private char[] cs;
	private StringBuffer sbout;
	private int i_pos;
	private Stack<Pair> inpairof;
	private Pair ppair;
	private Pair apair;
	private int i_vars;

	public String proc(String filename, char[] code, List<String> fjscode)
	{
		this.filename = filename;
		cs = code;
		i_pos = 0;
		i_vars = 1;
		inpairof = new Stack();
		sbout = new StringBuffer();
		ppair = null;
		apair = null;
		scan();
		if (fjscode != null)
		{
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < sbout.length(); i++)
			{
				char c = sbout.charAt(i);
				sb.append(c);
				if (c == '\n')
				{
					fjscode.add(sb.toString());
					sb = new StringBuffer();
				}
			}
			fjscode.add(sb.toString());
		}
		return sbout.toString();
	}

	private void scan()
	{
		while(i_pos < cs.length)
		{
			next();
		}
	}

	private Pair findPairBegin()
	{
		for(Pair pair : pair_define)
		{
			if (pair.match_begin(this))
			{
				return pair.apply(this);
			}
		}
		return null;
	}

	private void next()
	{
		if (apair != null)
		{
			Pair npair;
			if (apair.match_end())
			{
				apair.end_proc();
				if (inpairof.size() > 0)
				{
					npair = apair;
					apair = inpairof.pop();
					ppair = (inpairof.size() > 0)?inpairof.peek():null;
					apair.continue_proc(npair);
				}
				else
				{
					apair = null;
				}
			}
			else if (apair.recursive_enabled && (npair = findPairBegin()) != null)
			{
				apair.pause_proc(npair);
				ppair = apair;
				inpairof.push(apair);
				apair = npair;
				apair.begin_proc();
			}
			else
			{
				apair.in_proc();
			}
		}
		else if ((apair = findPairBegin()) != null)
		{
			apair.begin_proc();
		}
		else
		{
			sbout.append(cs[i_pos++]);
		}
	}

	private void procPair()
	{
		if ("{".equals(apair.begin))
		{
		}
		else if ("--@$$".equals(apair.begin))
		{
			//scan sql
			List<String> nvars = new ArrayList();
			while(!apair.match_end())
			{
				sbout.append("function _nestjs_1_(){" + "" + "};");
				nvars.add("_nestjs_1_()");
				sbout.append("var _nsqls_1_ = " + "" + ";");
				nvars.add("_nsqls_1_");
			}
			sbout.append("runsql(");
			for(String var : nvars)
			{
				sbout.append(var);
				sbout.append("+");
			}
			sbout.append(");");
		}
	}
}
