package assassin.script;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SStrMatcher
{
	static String[] match(String input, String regex)
	{
		return bito.util.E.V().match(input, regex);
	}
}
