package bito.util.cfg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommonConfigure extends ConfigFileParser
{
	public CommonConfigure(String filename)
	{
		super(filename);
		setPrimaryKey(new String[]{});
	}

	public Map getConfigMap()
	{
		Map ret = d.E.V().newMapSortedByAddTime();
		Map[] cm = getConfigure();
		if (cm != null && cm.length > 0)
		{
			for(Map m : cm)
			{
				ret.putAll(m);
			}
		}
		return ret;
	}
}
