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
	 * ��ȷƥ��,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�������keys�е�ͨ���ƥ��,
	 * �ַ��� key �е�*�ᱻӳ��Ϊ WildcardKey
	 */
	public abstract void put(String key, T value);

	/**
	 * ��ȷƥ��,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�������keys�е�ͨ���ƥ��
	 */
	public abstract void put(Object keys[], T value);

	/**
	 * ��ȷ����,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�������keys�е�ͨ���ƥ��
	 * �ַ��� key �е�*�ᱻӳ��Ϊ WildcardKey
	 */
	public abstract T get(String key);

	/**
	 * ��ȷ����,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�������keys�е�ͨ���ƥ��
	 */
	public abstract T get(Object keys[]);

	/**
	 * ��ȷƥ��,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�������keys�е�ͨ���ƥ��
	 * �ַ��� key �е�*�ᱻӳ��Ϊ WildcardKey
	 */
	public abstract void remove(String key);

	/**
	 * ��ȷƥ��,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�������keys�е�ͨ���ƥ��
	 */
	public abstract void remove(Object keys[]);

	/**
	 * ��ȷƥ��,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�������keys�е�ͨ���ƥ��
	 * �ַ��� key �е�*�ᱻӳ��Ϊ WildcardKey
	 */
	public abstract boolean containsKeys(String key);

	/**
	 * ��ȷƥ��,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�������keys�е�ͨ���ƥ��
	 */
	public abstract boolean containsKeys(Object[] keys);

	/**
	 * ģ������,
	 * keys�е�ͨ����������е�����keyƥ��,
	 * �����е�ͨ�����keys������keyƥ��
	 */
	public abstract Collection<T> findLikesList(Object[] keys);

	/**
	 * ƥ�����,keys�е�ͨ����������е�����keyƥ��,�����е�ͨ�������keys��ͨ���ƥ��
	 */
	public abstract Collection<T> findMatchesList(Object[] keys);

	/**
	 * һ�����,keys�е�ͨ����������е�ͨ���ƥ��,�����е�ͨ�����keys������keyƥ��
	 */
	public abstract Collection<T> findList(Object[] keys);

	/**
	 * ģ������,keys�е�ͨ����������е�����keyƥ��,�����е�ͨ�����keys������keyƥ��
	 */
	public abstract T[] findLikes(Object[] keys);

	/**
	 * ƥ�����,keys�е�ͨ����������е�����keyƥ��,�����е�ͨ�������keys��ͨ���ƥ��
	 */
	public abstract T[] findMatches(Object[] keys);

	/**
	 * һ�����,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�����keys������keyƥ��
	 */
	public abstract T[] find(Object[] keys);

	/**
	 * ��ȷ����,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�������keys�е�ͨ���ƥ��
	 * ֻ�᷵��һ��ֵ
	 */
	public abstract T findEquals(Object[] keys);

	/**
	 * ģ������,
	 * keys�е�ͨ����������е�����keyƥ��,
	 * �����е�ͨ�����keys������keyƥ��
	 */
	public abstract T findFirstLikes(Object[] keys);

	/**
	 * ƥ�����,keys�е�ͨ����������е�����keyƥ��,�����е�ͨ�������keys��ͨ���ƥ��
	 */
	public abstract T findFirstMatches(Object[] keys);

	/**
	 * һ�����,keys�е�ͨ����������е�ͨ���ƥ��,�����е�ͨ�����keys������keyƥ��
	 */
	public abstract T findFirst(Object[] keys);
}
