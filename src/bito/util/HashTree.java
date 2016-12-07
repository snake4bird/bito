package bito.util;

import java.lang.reflect.Array;
import java.util.*;

public class HashTree<T> extends HashTreeBase<T>
{
	// HashTree 的树根
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
			//删除对应数据
			km.remove(ValueKey);
			// 子树中已没有任何信息,才可以删除
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
		public final HashMap subtree_km; // 找到的子树
		public final int finding_keyi; // 匹配键值组中的位置
		public final int last_wildcard_keyi; // 查询键是否为模糊匹配, -1代表模糊匹配,
		public final int last_wildcard_indexi; // 索引键是否为模糊匹配, -1代表模糊匹配,

		public FoundSubTree(HashMap km, int fkeyi, int lwki, int lwii)
		{
			this.subtree_km = km;
			this.finding_keyi = fkeyi;
			this.last_wildcard_keyi = lwki;
			this.last_wildcard_indexi = lwii;
		}
	}

	/**
	 * 模糊查找,
	 * keys中的通配符与索引中的任意key匹配,
	 * 索引中的通配符与keys中任意key匹配
	 */
	public ArrayList<T> findLikes(Object[] keys, int maxrets)
	{
		TreeMap<String, T> ret = new TreeMap();
		Stack stack = new Stack();
		HashMap tn = root;
		stack.push(new FoundSubTree(root, 0, -1, -1));
		while(stack.size() > 0 && (maxrets <= 0 || ret.size() < maxrets))
		{
			FoundSubTree o = (FoundSubTree)stack.pop();//深度优先
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
					// keys通配
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
								// 索引中的通配符与keys中任意key匹配
								stack.push(new FoundSubTree(tn, o.finding_keyi, o.last_wildcard_keyi, o.finding_keyi));
							}
							else
							{
								if (o.finding_keyi < keys.length && WildcardKey.equals(keys[o.finding_keyi]))
								{
									// 新keys通配
									stack.push(new FoundSubTree(tn,
										o.finding_keyi + 1,
										o.finding_keyi,
										o.last_wildcard_indexi));
								}
								else if (o.finding_keyi < keys.length && key.equals(keys[o.finding_keyi]))
								{
									// 精确匹配
									stack.push(new FoundSubTree(tn,
										o.finding_keyi + 1,
										o.last_wildcard_keyi,
										o.last_wildcard_indexi));
								}
								else
								{
									// 前keys通配
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
						// 索引中的通配符与keys中任意key匹配
						stack.push(new FoundSubTree(tn, o.finding_keyi, o.last_wildcard_keyi, o.finding_keyi));
					}
					else if (o.finding_keyi < keys.length && o.last_wildcard_indexi != -1)
					{
						// 前索引通配
						stack.push(new FoundSubTree(o.subtree_km,
							o.last_wildcard_indexi + 1,
							o.last_wildcard_keyi,
							o.last_wildcard_indexi + 1));
					}
					if (o.finding_keyi < keys.length
						&& !WildcardKey.equals(keys[o.finding_keyi])
							&& (tn = (HashMap)o.subtree_km.get(keys[o.finding_keyi])) != null)
					{
						// 精确匹配
						stack.push(new FoundSubTree(tn, o.finding_keyi + 1, -1, -1));
					}
					else if (o.finding_keyi < keys.length && WildcardKey.equals(keys[o.finding_keyi]))
					{
						// keys中的通配符与索引中的任意key匹配
						stack.push(new FoundSubTree(o.subtree_km,
							o.finding_keyi + 1,
							o.finding_keyi,
							o.last_wildcard_indexi));
					}
				}
				// else 无动作
			}
		}
		return new ArrayList(ret.values());
	}

	/**
	 * 匹配查找,keys中的通配符与索引中的任意key匹配,索引中的通配符仅与keys中通配符匹配
	 */
	public ArrayList<T> findMatches(Object[] keys, int maxrets)
	{
		TreeMap<String, T> ret = new TreeMap();
		Stack stack = new Stack();
		HashMap tn = root;
		stack.push(new FoundSubTree(root, 0, -1, -1));
		while(stack.size() > 0 && (maxrets <= 0 || ret.size() < maxrets))
		{
			FoundSubTree o = (FoundSubTree)stack.pop();//深度优先
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
								// 新keys通配
								stack.push(new FoundSubTree(tn,
									o.finding_keyi + 1,
									o.finding_keyi,
									o.last_wildcard_indexi));
							}
							else if (o.finding_keyi < keys.length && key.equals(keys[o.finding_keyi]))
							{
								// 精确匹配
								stack.push(new FoundSubTree(tn,
									o.finding_keyi + 1,
									o.last_wildcard_keyi,
									o.last_wildcard_indexi));
							}
							else
							{
								// 前keys通配
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
					// keys中的通配符与索引中的任意key匹配
					stack.push(new FoundSubTree(o.subtree_km,
						o.finding_keyi + 1,
						o.finding_keyi,
						o.last_wildcard_indexi));
				}
				else if (o.finding_keyi < keys.length && (tn = (HashMap)o.subtree_km.get(keys[o.finding_keyi])) != null)
				{
					// 精确匹配
					stack.push(new FoundSubTree(tn, o.finding_keyi + 1, o.last_wildcard_keyi, o.last_wildcard_indexi));
				}
			}
		}
		return new ArrayList(ret.values());
	}

	/**
	 * 一般查找,keys中的通配符与索引中的通配符匹配,索引中的通配符与keys中任意key匹配
	 */
	public ArrayList<T> find(Object[] keys, int maxrets)
	{
		TreeMap<String, T> ret = new TreeMap();
		Stack stack = new Stack();
		HashMap tn = root;
		stack.push(new FoundSubTree(root, 0, -1, -1));
		while(stack.size() > 0 && (maxrets <= 0 || ret.size() < maxrets))
		{
			FoundSubTree o = (FoundSubTree)stack.pop();//深度优先
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
					// 索引中的通配符与keys中任意key匹配
					stack.push(new FoundSubTree(tn, o.finding_keyi, o.last_wildcard_keyi, o.finding_keyi));
				}
				else if (o.finding_keyi < keys.length && o.last_wildcard_indexi != -1)
				{
					// 前索引通配
					stack.push(new FoundSubTree(o.subtree_km,
						o.last_wildcard_indexi + 1,
						o.last_wildcard_keyi,
						o.last_wildcard_indexi + 1));
				}
				if (o.finding_keyi < keys.length
					&& !WildcardKey.equals(keys[o.finding_keyi])
						&& (tn = (HashMap)o.subtree_km.get(keys[o.finding_keyi])) != null)
				{
					// 精确匹配
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
	 * 精确查找,keys中的通配符仅与索引中的通配符匹配,索引中的通配符仅与keys中的通配符匹配
	 * 只会返回一个值
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
