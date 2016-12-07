package bito.util;

import java.lang.reflect.Array;
import java.util.*;

public class HashTree<T> extends HashTreeBase<T>
{
	// HashTree ������
	private HashMap root = new HashMap();
	private Class TClass = Object.class;

	public HashTree(Class componentType)
	{
		root.put(HTKSKey, "");
		TClass = componentType;
	}

	public String toString()
	{
		return root.toString();
	}

	@Override
	public void put(String key, T value)
	{
		put(toHashTreeKeys(key), value);
	}

	/**
	 * put value of item referred to specific keys
	 * 
	 * @param keys
	 * @param value
	 */
	@Override
	public void put(Object keys[], T value)
	{
		if (TClass == null && value != null)
		{
			TClass = value.getClass();
		}
		HashMap km = root;
		for(int i = 0; i < keys.length; i++)
		{
			HashMap tm = km;
			km = (HashMap)tm.get(keys[i]);
			if (km == null)
			{
				km = new HashMap();
				String htks = (String)tm.get(HTKSKey);
				htks += keys[i] + "|";
				km.put(HTKSKey, htks);
				tm.put(keys[i], km);
			}
		}
		km.put(ValueKey, value);
	}

	@Override
	public T get(String key)
	{
		return get(toHashTreeKeys(key));
	}

	/**
	 * get value of item refer to specific keys
	 * 
	 * @param keys
	 */
	@Override
	public T get(Object keys[])
	{
		return findEquals(keys);
	}

	public void remove(String key)
	{
		remove(toHashTreeKeys(key));
	}

	/**
	 * remove sub tree refer to specific keys
	 * 
	 * @param keys
	 */
	public void remove(Object keys[])
	{
		HashMap km = root;
		Stack stack = new Stack();
		for(int i = 0; km != null && i < keys.length; i++)
		{
			stack.push(km);
			km = (HashMap)km.get(keys[i]);
		}
		if (km != null)
		{
			//ɾ����Ӧ����
			km.remove(ValueKey);
			// ��������û���κ���Ϣ,�ſ���ɾ��
			for(int i = keys.length - 1; km.size() == 0 && i >= 0; i--)
			{
				km = (HashMap)stack.pop();
				km.remove(keys[i]);
			}
		}
	}

	public boolean containsKeys(String key)
	{
		return containsKeys(toHashTreeKeys(key));
	}

	public boolean containsKeys(Object[] keys)
	{
		HashMap km = root;
		for(int i = 0; km != null && i < keys.length; i++)
		{
			km = (HashMap)km.get(keys[i]);
		}
		return (km != null);
	}

	private class FoundSubTree
	{
		public final HashMap subtree_km; // �ҵ�������
		public final int finding_keyi; // ƥ���ֵ���е�λ��
		public final int last_wildcard_keyi; // ��ѯ���Ƿ�Ϊģ��ƥ��, -1����ģ��ƥ��,
		public final int last_wildcard_indexi; // �������Ƿ�Ϊģ��ƥ��, -1����ģ��ƥ��,

		public FoundSubTree(HashMap km, int fkeyi, int lwki, int lwii)
		{
			this.subtree_km = km;
			this.finding_keyi = fkeyi;
			this.last_wildcard_keyi = lwki;
			this.last_wildcard_indexi = lwii;
		}
	}

	/**
	 * ģ������,
	 * keys�е�ͨ����������е�����keyƥ��,
	 * �����е�ͨ�����keys������keyƥ��
	 */
	public ArrayList<T> findLikes(Object[] keys, int maxrets)
	{
		TreeMap<String, T> ret = new TreeMap();
		Stack stack = new Stack();
		HashMap tn = root;
		stack.push(new FoundSubTree(root, 0, -1, -1));
		while(stack.size() > 0 && (maxrets <= 0 || ret.size() < maxrets))
		{
			FoundSubTree o = (FoundSubTree)stack.pop();//�������
			if (o.finding_keyi >= keys.length)
			{
				//found one
				T v = (T)o.subtree_km.get(ValueKey);
				//System.out.println(" ==> " + v);
				if (v != null)
				{
					ret.put((String)o.subtree_km.get(HTKSKey), v);
				}
			}
			{
				if (o.last_wildcard_keyi != -1)
				{
					// keysͨ��
					Iterator ksi = o.subtree_km.entrySet().iterator();
					while(ksi.hasNext())
					{
						Map.Entry me = (Map.Entry)ksi.next();
						Object key = me.getKey();
						if (!ValueKey.equals(key) && !HTKSKey.equals(key))
						{
							tn = (HashMap)me.getValue();
							if (WildcardKey.equals(key))
							{
								// �����е�ͨ�����keys������keyƥ��
								stack.push(new FoundSubTree(tn, o.finding_keyi, o.last_wildcard_keyi, o.finding_keyi));
							}
							else
							{
								if (o.finding_keyi < keys.length && WildcardKey.equals(keys[o.finding_keyi]))
								{
									// ��keysͨ��
									stack.push(new FoundSubTree(tn,
										o.finding_keyi + 1,
										o.finding_keyi,
										o.last_wildcard_indexi));
								}
								else if (o.finding_keyi < keys.length && key.equals(keys[o.finding_keyi]))
								{
									// ��ȷƥ��
									stack.push(new FoundSubTree(tn,
										o.finding_keyi + 1,
										o.last_wildcard_keyi,
										o.last_wildcard_indexi));
								}
								else
								{
									// ǰkeysͨ��
									stack.push(new FoundSubTree(tn,
										o.last_wildcard_keyi + 1,
										o.last_wildcard_keyi,
										o.last_wildcard_indexi));
								}
							}
						}
					}
				}
				else
				{
					if ((tn = (HashMap)o.subtree_km.get(WildcardKey)) != null)
					{
						// �����е�ͨ�����keys������keyƥ��
						stack.push(new FoundSubTree(tn, o.finding_keyi, o.last_wildcard_keyi, o.finding_keyi));
					}
					else if (o.finding_keyi < keys.length && o.last_wildcard_indexi != -1)
					{
						// ǰ����ͨ��
						stack.push(new FoundSubTree(o.subtree_km,
							o.last_wildcard_indexi + 1,
							o.last_wildcard_keyi,
							o.last_wildcard_indexi + 1));
					}
					if (o.finding_keyi < keys.length
						&& !WildcardKey.equals(keys[o.finding_keyi])
							&& (tn = (HashMap)o.subtree_km.get(keys[o.finding_keyi])) != null)
					{
						// ��ȷƥ��
						stack.push(new FoundSubTree(tn, o.finding_keyi + 1, -1, -1));
					}
					else if (o.finding_keyi < keys.length && WildcardKey.equals(keys[o.finding_keyi]))
					{
						// keys�е�ͨ����������е�����keyƥ��
						stack.push(new FoundSubTree(o.subtree_km,
							o.finding_keyi + 1,
							o.finding_keyi,
							o.last_wildcard_indexi));
					}
				}
				// else �޶���
			}
		}
		return new ArrayList(ret.values());
	}

	/**
	 * ƥ�����,keys�е�ͨ����������е�����keyƥ��,�����е�ͨ�������keys��ͨ���ƥ��
	 */
	public ArrayList<T> findMatches(Object[] keys, int maxrets)
	{
		TreeMap<String, T> ret = new TreeMap();
		Stack stack = new Stack();
		HashMap tn = root;
		stack.push(new FoundSubTree(root, 0, -1, -1));
		while(stack.size() > 0 && (maxrets <= 0 || ret.size() < maxrets))
		{
			FoundSubTree o = (FoundSubTree)stack.pop();//�������
			if (o.finding_keyi >= keys.length)
			{
				//found one
				T v = (T)o.subtree_km.get(ValueKey);
				//System.out.println(" ==> " + v);
				if (v != null)
				{
					ret.put((String)o.subtree_km.get(HTKSKey), v);
				}
			}
			{
				if (o.last_wildcard_keyi != -1)
				{
					Iterator ksi = o.subtree_km.entrySet().iterator();
					while(ksi.hasNext())
					{
						Map.Entry me = (Map.Entry)ksi.next();
						Object key = me.getKey();
						if (!ValueKey.equals(key) && !HTKSKey.equals(key))
						{
							tn = (HashMap)me.getValue();
							if (o.finding_keyi < keys.length && WildcardKey.equals(keys[o.finding_keyi]))
							{
								// ��keysͨ��
								stack.push(new FoundSubTree(tn,
									o.finding_keyi + 1,
									o.finding_keyi,
									o.last_wildcard_indexi));
							}
							else if (o.finding_keyi < keys.length && key.equals(keys[o.finding_keyi]))
							{
								// ��ȷƥ��
								stack.push(new FoundSubTree(tn,
									o.finding_keyi + 1,
									o.last_wildcard_keyi,
									o.last_wildcard_indexi));
							}
							else
							{
								// ǰkeysͨ��
								stack.push(new FoundSubTree(tn,
									o.last_wildcard_keyi + 1,
									o.last_wildcard_keyi,
									o.last_wildcard_indexi));
							}
						}
					}
				}
				else if (o.finding_keyi < keys.length && WildcardKey.equals(keys[o.finding_keyi]))
				{
					// keys�е�ͨ����������е�����keyƥ��
					stack.push(new FoundSubTree(o.subtree_km,
						o.finding_keyi + 1,
						o.finding_keyi,
						o.last_wildcard_indexi));
				}
				else if (o.finding_keyi < keys.length && (tn = (HashMap)o.subtree_km.get(keys[o.finding_keyi])) != null)
				{
					// ��ȷƥ��
					stack.push(new FoundSubTree(tn, o.finding_keyi + 1, o.last_wildcard_keyi, o.last_wildcard_indexi));
				}
			}
		}
		return new ArrayList(ret.values());
	}

	/**
	 * һ�����,keys�е�ͨ����������е�ͨ���ƥ��,�����е�ͨ�����keys������keyƥ��
	 */
	public ArrayList<T> find(Object[] keys, int maxrets)
	{
		TreeMap<String, T> ret = new TreeMap();
		Stack stack = new Stack();
		HashMap tn = root;
		stack.push(new FoundSubTree(root, 0, -1, -1));
		while(stack.size() > 0 && (maxrets <= 0 || ret.size() < maxrets))
		{
			FoundSubTree o = (FoundSubTree)stack.pop();//�������
			if (o.finding_keyi >= keys.length)
			{
				//found one
				T v = (T)o.subtree_km.get(ValueKey);
				//System.out.println(" ==> " + v);
				if (v != null)
				{
					ret.put((String)o.subtree_km.get(HTKSKey), v);
				}
			}
			{
				if ((tn = (HashMap)o.subtree_km.get(WildcardKey)) != null)
				{
					// �����е�ͨ�����keys������keyƥ��
					stack.push(new FoundSubTree(tn, o.finding_keyi, o.last_wildcard_keyi, o.finding_keyi));
				}
				else if (o.finding_keyi < keys.length && o.last_wildcard_indexi != -1)
				{
					// ǰ����ͨ��
					stack.push(new FoundSubTree(o.subtree_km,
						o.last_wildcard_indexi + 1,
						o.last_wildcard_keyi,
						o.last_wildcard_indexi + 1));
				}
				if (o.finding_keyi < keys.length
					&& !WildcardKey.equals(keys[o.finding_keyi])
						&& (tn = (HashMap)o.subtree_km.get(keys[o.finding_keyi])) != null)
				{
					// ��ȷƥ��
					stack.push(new FoundSubTree(tn, o.finding_keyi + 1, o.last_wildcard_keyi, -1));
				}
			}
		}
		return new ArrayList(ret.values());
	}

	private T[] toArray(Collection<T> al)
	{
		T[] contents = (T[])Array.newInstance(TClass, 0);
		return al.toArray(contents);
	}

	@Override
	public Collection<T> findLikesList(Object[] keys)
	{
		return findLikes(keys, -1);
	}

	@Override
	public Collection<T> findMatchesList(Object[] keys)
	{
		return findMatches(keys, -1);
	}

	@Override
	public Collection<T> findList(Object[] keys)
	{
		return find(keys, -1);
	}

	@Override
	public T[] findLikes(Object[] keys)
	{
		return toArray(findLikes(keys, -1));
	}

	@Override
	public T[] findMatches(Object[] keys)
	{
		return toArray(findMatches(keys, -1));
	}

	@Override
	public T[] find(Object[] keys)
	{
		return toArray(find(keys, -1));
	}

	/**
	 * ��ȷ����,keys�е�ͨ������������е�ͨ���ƥ��,�����е�ͨ�������keys�е�ͨ���ƥ��
	 * ֻ�᷵��һ��ֵ
	 */
	@Override
	public T findEquals(Object[] keys)
	{
		HashMap km = root;
		for(int i = 0; km != null && i < keys.length; i++)
		{
			km = (HashMap)km.get(keys[i]);
		}
		if (km != null)
		{
			return (T)km.get(ValueKey);
		}
		return null;
	}

	@Override
	public T findFirstLikes(Object[] keys)
	{
		ArrayList<T> ret = findLikes(keys, 1);
		if (ret != null && ret.size() > 0)
		{
			return ret.get(0);
		}
		return null;
	}

	@Override
	public T findFirstMatches(Object[] keys)
	{
		ArrayList<T> ret = findMatches(keys, 1);
		if (ret != null && ret.size() > 0)
		{
			return ret.get(0);
		}
		return null;
	}

	@Override
	public T findFirst(Object[] keys)
	{
		ArrayList<T> ret = find(keys, 1);
		if (ret != null && ret.size() > 0)
		{
			return ret.get(0);
		}
		return null;
	}
}
