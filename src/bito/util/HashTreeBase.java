package bito.util;

import java.util.*;

public abstract class HashTreeBase<T>
{
	public static final Object NullKey = new Object()
	{
		public String toString()
		{
			return "<0>";
		}
	};
	public static final Object ValueKey = new Object()
	{
		public String toString()
		{
			return "<V>";
		}
	};
	public static final Object WildcardKey = new Object()
	{
		public String toString()
		{
			return "<*>";
		}
	};
	public static final Object HTKSKey = new Object()
	{
		public String toString()
		{
			return "<K>";
		}
	};

	//
	public static Object[] toHashTreeKeys(String key)
	{
		return toHashTreeKeys(key, '*');
	}

	public static Object[] toHashTreeKeys(String key, int wildchar)
	{
		boolean wildcard_flag = false;
		ArrayList al = new ArrayList();
		if (key == null)
		{
			return new Object[]{NullKey};
		}
		char[] cs = key.toCharArray();
		for(int i = 0; i < cs.length; i++)
		{
			if (cs[i] == wildchar)
			{
				if (!wildcard_flag)
				{
					al.add(WildcardKey);
					wildcard_flag = true;
				}
			}
			else
			{
				al.add(new Character(cs[i]));
				wildcard_flag = false;
			}
		}
		return al.toArray();
	}

	//
	protected HashTreeBase()
	{
	}

	/**
	 * 精确匹配,keys中的通配符仅与索引中的通配符匹配,索引中的通配符仅与keys中的通配符匹配,
	 * 字符串 key 中的*会被映射为 WildcardKey
	 */
	public abstract void put(String key, T value);

	/**
	 * 精确匹配,keys中的通配符仅与索引中的通配符匹配,索引中的通配符仅与keys中的通配符匹配
	 */
	public abstract void put(Object keys[], T value);

	/**
	 * 精确查找,keys中的通配符仅与索引中的通配符匹配,索引中的通配符仅与keys中的通配符匹配
	 * 字符串 key 中的*会被映射为 WildcardKey
	 */
	public abstract T get(String key);

	/**
	 * 精确查找,keys中的通配符仅与索引中的通配符匹配,索引中的通配符仅与keys中的通配符匹配
	 */
	public abstract T get(Object keys[]);

	/**
	 * 精确匹配,keys中的通配符仅与索引中的通配符匹配,索引中的通配符仅与keys中的通配符匹配
	 * 字符串 key 中的*会被映射为 WildcardKey
	 */
	public abstract void remove(String key);

	/**
	 * 精确匹配,keys中的通配符仅与索引中的通配符匹配,索引中的通配符仅与keys中的通配符匹配
	 */
	public abstract void remove(Object keys[]);

	/**
	 * 精确匹配,keys中的通配符仅与索引中的通配符匹配,索引中的通配符仅与keys中的通配符匹配
	 * 字符串 key 中的*会被映射为 WildcardKey
	 */
	public abstract boolean containsKeys(String key);

	/**
	 * 精确匹配,keys中的通配符仅与索引中的通配符匹配,索引中的通配符仅与keys中的通配符匹配
	 */
	public abstract boolean containsKeys(Object[] keys);

	/**
	 * 模糊查找,
	 * keys中的通配符与索引中的任意key匹配,
	 * 索引中的通配符与keys中任意key匹配
	 */
	public abstract Collection<T> findLikesList(Object[] keys);

	/**
	 * 匹配查找,keys中的通配符与索引中的任意key匹配,索引中的通配符仅与keys中通配符匹配
	 */
	public abstract Collection<T> findMatchesList(Object[] keys);

	/**
	 * 一般查找,keys中的通配符与索引中的通配符匹配,索引中的通配符与keys中任意key匹配
	 */
	public abstract Collection<T> findList(Object[] keys);

	/**
	 * 模糊查找,keys中的通配符与索引中的任意key匹配,索引中的通配符与keys中任意key匹配
	 */
	public abstract T[] findLikes(Object[] keys);

	/**
	 * 匹配查找,keys中的通配符与索引中的任意key匹配,索引中的通配符仅与keys中通配符匹配
	 */
	public abstract T[] findMatches(Object[] keys);

	/**
	 * 一般查找,keys中的通配符仅与索引中的通配符匹配,索引中的通配符与keys中任意key匹配
	 */
	public abstract T[] find(Object[] keys);

	/**
	 * 精确查找,keys中的通配符仅与索引中的通配符匹配,索引中的通配符仅与keys中的通配符匹配
	 * 只会返回一个值
	 */
	public abstract T findEquals(Object[] keys);

	/**
	 * 模糊查找,
	 * keys中的通配符与索引中的任意key匹配,
	 * 索引中的通配符与keys中任意key匹配
	 */
	public abstract T findFirstLikes(Object[] keys);

	/**
	 * 匹配查找,keys中的通配符与索引中的任意key匹配,索引中的通配符仅与keys中通配符匹配
	 */
	public abstract T findFirstMatches(Object[] keys);

	/**
	 * 一般查找,keys中的通配符与索引中的通配符匹配,索引中的通配符与keys中任意key匹配
	 */
	public abstract T findFirst(Object[] keys);
}
