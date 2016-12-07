package d._.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AntPathMatcher
{
	/* member class not found */
	class AntPatternComparator
	{
	}

	public AntPathMatcher()
	{
		pathSeparator = "/";
	}

	public void setPathSeparator(String pathSeparator)
	{
		this.pathSeparator = pathSeparator == null?"/":pathSeparator;
	}

	public boolean isPattern(String path)
	{
		return path.indexOf('*') != -1 || path.indexOf('?') != -1;
	}

	public boolean match(String pattern, String path)
	{
		return doMatch(pattern, path, true, null);
	}

	public boolean matchStart(String pattern, String path)
	{
		return doMatch(pattern, path, false, null);
	}

	protected boolean doMatch(String pattern, String path, boolean fullMatch, Map uriTemplateVariables)
	{
		if (path.startsWith(pathSeparator) != pattern.startsWith(pathSeparator))
			return false;
		String pattDirs[] = StringUtils.tokenizeToStringArray(pattern, pathSeparator);
		String pathDirs[] = StringUtils.tokenizeToStringArray(path, pathSeparator);
		int pattIdxStart = 0;
		int pattIdxEnd = pattDirs.length - 1;
		int pathIdxStart = 0;
		int pathIdxEnd;
		for(pathIdxEnd = pathDirs.length - 1; pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd; pathIdxStart++)
		{
			String patDir = pattDirs[pattIdxStart];
			if ("**".equals(patDir))
				break;
			if (!matchStrings(patDir, pathDirs[pathIdxStart], uriTemplateVariables))
				return false;
			pattIdxStart++;
		}
		if (pathIdxStart > pathIdxEnd)
		{
			if (pattIdxStart > pattIdxEnd)
				return pattern.endsWith(pathSeparator)?path.endsWith(pathSeparator):!path.endsWith(pathSeparator);
			if (!fullMatch)
				return true;
			if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") && path.endsWith(pathSeparator))
				return true;
			for(int i = pattIdxStart; i <= pattIdxEnd; i++)
				if (!pattDirs[i].equals("**"))
					return false;
			return true;
		}
		if (pattIdxStart > pattIdxEnd)
			return false;
		if (!fullMatch && "**".equals(pattDirs[pattIdxStart]))
			return true;
		for(; pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd; pathIdxEnd--)
		{
			String patDir = pattDirs[pattIdxEnd];
			if (patDir.equals("**"))
				break;
			if (!matchStrings(patDir, pathDirs[pathIdxEnd], uriTemplateVariables))
				return false;
			pattIdxEnd--;
		}
		if (pathIdxStart > pathIdxEnd)
		{
			for(int i = pattIdxStart; i <= pattIdxEnd; i++)
				if (!pattDirs[i].equals("**"))
					return false;
			return true;
		}
		while(pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd)
		{
			int patIdxTmp = -1;
			for(int i = pattIdxStart + 1; i <= pattIdxEnd; i++)
			{
				if (!pattDirs[i].equals("**"))
					continue;
				patIdxTmp = i;
				break;
			}
			if (patIdxTmp == pattIdxStart + 1)
			{
				pattIdxStart++;
			}
			else
			{
				int patLength = patIdxTmp - pattIdxStart - 1;
				int strLength = (pathIdxEnd - pathIdxStart) + 1;
				int foundIdx = -1;
				label0: for(int i = 0; i <= strLength - patLength; i++)
				{
					for(int j = 0; j < patLength; j++)
					{
						String subPat = pattDirs[pattIdxStart + j + 1];
						String subStr = pathDirs[pathIdxStart + i + j];
						if (!matchStrings(subPat, subStr, uriTemplateVariables))
							continue label0;
					}
					foundIdx = pathIdxStart + i;
					break;
				}
				if (foundIdx == -1)
					return false;
				pattIdxStart = patIdxTmp;
				pathIdxStart = foundIdx + patLength;
			}
		}
		for(int i = pattIdxStart; i <= pattIdxEnd; i++)
			if (!pattDirs[i].equals("**"))
				return false;
		return true;
	}

	private boolean matchStrings(String pattern, String str, Map uriTemplateVariables)
	{
		AntPathStringMatcher matcher = new AntPathStringMatcher(pattern, str, uriTemplateVariables);
		return matcher.matchStrings();
	}

	public String extractPathWithinPattern(String pattern, String path)
	{
		String patternParts[] = StringUtils.tokenizeToStringArray(pattern, pathSeparator);
		String pathParts[] = StringUtils.tokenizeToStringArray(path, pathSeparator);
		StringBuilder builder = new StringBuilder();
		int puts = 0;
		for(int i = 0; i < patternParts.length; i++)
		{
			String patternPart = patternParts[i];
			if ((patternPart.indexOf('*') > -1 || patternPart.indexOf('?') > -1) && pathParts.length >= i + 1)
			{
				if (puts > 0 || i == 0 && !pattern.startsWith(pathSeparator))
					builder.append(pathSeparator);
				builder.append(pathParts[i]);
				puts++;
			}
		}
		for(int i = patternParts.length; i < pathParts.length; i++)
		{
			if (puts > 0 || i > 0)
				builder.append(pathSeparator);
			builder.append(pathParts[i]);
		}
		return builder.toString();
	}

	public Map extractUriTemplateVariables(String pattern, String path)
	{
		Map variables = new LinkedHashMap();
		boolean result = doMatch(pattern, path, true, variables);
		return variables;
	}

	public String combine(String pattern1, String pattern2)
	{
		if (!StringUtils.hasText(pattern1) && !StringUtils.hasText(pattern2))
			return "";
		if (!StringUtils.hasText(pattern1))
			return pattern2;
		if (!StringUtils.hasText(pattern2))
			return pattern1;
		if (match(pattern1, pattern2))
			return pattern2;
		if (pattern1.endsWith("/*"))
			if (pattern2.startsWith("/"))
				return (new StringBuilder(String.valueOf(pattern1.substring(0, pattern1.length() - 1))))
					.append(pattern2.substring(1)).toString();
			else
				return (new StringBuilder(String.valueOf(pattern1.substring(0, pattern1.length() - 1))))
					.append(pattern2).toString();
		if (pattern1.endsWith("/**"))
			if (pattern2.startsWith("/"))
				return (new StringBuilder(String.valueOf(pattern1))).append(pattern2).toString();
			else
				return (new StringBuilder(String.valueOf(pattern1))).append("/").append(pattern2).toString();
		int dotPos1 = pattern1.indexOf('.');
		if (dotPos1 == -1)
			if (pattern1.endsWith("/") || pattern2.startsWith("/"))
				return (new StringBuilder(String.valueOf(pattern1))).append(pattern2).toString();
			else
				return (new StringBuilder(String.valueOf(pattern1))).append("/").append(pattern2).toString();
		String fileName1 = pattern1.substring(0, dotPos1);
		String extension1 = pattern1.substring(dotPos1);
		int dotPos2 = pattern2.indexOf('.');
		String fileName2;
		String extension2;
		if (dotPos2 != -1)
		{
			fileName2 = pattern2.substring(0, dotPos2);
			extension2 = pattern2.substring(dotPos2);
		}
		else
		{
			fileName2 = pattern2;
			extension2 = "";
		}
		String fileName = fileName1.endsWith("*")?fileName2:fileName1;
		String extension = extension1.startsWith("*")?extension2:extension1;
		return (new StringBuilder(String.valueOf(fileName))).append(extension).toString();
	}

	public static final String DEFAULT_PATH_SEPARATOR = "/";
	private String pathSeparator;
}
