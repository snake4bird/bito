package d._.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AntPathStringMatcher
{

	AntPathStringMatcher(String pattern, String str, Map uriTemplateVariables)
    {
        this.str = str;
        this.uriTemplateVariables = uriTemplateVariables;
        this.pattern = createPattern(pattern);
    }

    private Pattern createPattern(String pattern)
    {
        StringBuilder patternBuilder = new StringBuilder();
		Matcher m = GLOB_PATTERN.matcher(pattern);
        int end;
        for(end = 0; m.find(); end = m.end())
        {
            patternBuilder.append(quote(pattern, end, m.start()));
            String match = m.group();
            if("?".equals(match))
                patternBuilder.append('.');
            else
            if("*".equals(match))
                patternBuilder.append(".*");
            else
            if(match.startsWith("{") && match.endsWith("}"))
            {
                int colonIdx = match.indexOf(':');
                if(colonIdx == -1)
                {
                    patternBuilder.append("(.*)");
                    variableNames.add(m.group(1));
                } else
                {
                    String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                    patternBuilder.append('(');
                    patternBuilder.append(variablePattern);
                    patternBuilder.append(')');
                    String variableName = match.substring(1, colonIdx);
                    variableNames.add(variableName);
                }
            }
        }

        patternBuilder.append(quote(pattern, end, pattern.length()));
        return Pattern.compile(patternBuilder.toString());
    }

    private String quote(String s, int start, int end)
    {
        if(start == end)
            return "";
        else
            return Pattern.quote(s.substring(start, end));
    }

    public boolean matchStrings()
    {
        Matcher matcher = pattern.matcher(str);
        if(matcher.matches())
        {
            if(uriTemplateVariables != null)
            {
                for(int i = 1; i <= matcher.groupCount(); i++)
                {
                    String name = (String)variableNames.get(i - 1);
                    String value = matcher.group(i);
                    uriTemplateVariables.put(name, value);
                }

            }
            return true;
        } else
        {
            return false;
        }
    }

    private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{([^/]+?)\\}");
    private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";
    private final Pattern pattern;
    private String str;
	private final List variableNames = new LinkedList();
    private final Map uriTemplateVariables;

}