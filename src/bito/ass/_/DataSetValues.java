package bito.ass._;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import bito.json.JSON;

class DataSetValues implements Scriptable
{
	private Scriptable prototype, parent;
	public String label;
	public String[] values;

	/**
	 * The zero-parameter constructor.
	*/
	public DataSetValues()
	{
	}

	public DataSetValues(String objectfullname, String fieldname, Scriptable start, Object[] values)
	{
		this.label = objectfullname;
		this.values = new String[values.length];
		for(int i = 0; i < values.length; i++)
		{
			Object a = values[i];
			if (a == null)
			{
				this.values[i] = "";
			}
			else if (a == Scriptable.NOT_FOUND
				|| a == Undefined.instance
					|| a instanceof SQLScriptUnkown
					|| a instanceof SQLScriptUndefined)
			{
				throw Context.reportRuntimeError((fieldname != null && fieldname.length() > 0)?("field '"
					+ fieldname
						+ "' is "
						+ a
						+ " in object '"
						+ objectfullname + "'."):("object '+" + objectfullname + "+[" + i + "]' is " + a + "."));
			}
			else if (a instanceof Scriptable)
			{
				if (start != null && fieldname != null)
				{
					Scriptable sa = ((Scriptable)a);
					Object v = sa.get(fieldname, start);
					if (v == null)
					{
						this.values[i] = "";
					}
					else if (v == Scriptable.NOT_FOUND
						|| v == Undefined.instance
							|| v instanceof SQLScriptUnkown
							|| v instanceof SQLScriptUndefined)
					{
						throw Context.reportRuntimeError("object '"
							+ objectfullname
								+ "' field '"
								+ fieldname
								+ "' not found.");
					}
					else if (v instanceof Number)
					{
						this.values[i] = "" + df.format(v);
					}
					else
					{
						this.values[i] = v.toString();
					}
				}
				else
				{
					this.values[i] = JSON.toString(a);
				}
			}
			else if (a instanceof Number)
			{
				this.values[i] = "" + df.format(a);
			}
			else if (fieldname != null)
			{
				throw Context
					.reportRuntimeError("object '" + objectfullname + "' field '" + fieldname + "' not found.");
			}
			else
			{
				this.values[i] = a.toString();
			}
		}
	}

	private static DecimalFormat df = new DecimalFormat("#.#");

	public DataSetValues(String label, String[] values)
	{
		this.label = label;
		this.values = values;
	}

	/**
	 * Returns the name of this JavaScript class.
	 */
	public String getClassName()
	{
		return "DataSetValues";
	}

	/**
	 * @param name the name of the property
	 * @param start the object where lookup began
	 */
	public boolean has(String name, Scriptable start)
	{
		//accept all property
		return true;
	}

	/**
	 * Defines all numeric properties by returning true.
	 *
	 * @param index the index of the property
	 * @param start the object where lookup began
	 */
	public boolean has(int index, Scriptable start)
	{
		return true;
	}

	/**
	 * Get the named property.
	 * @param name the property name
	 * @param start the object where the lookup began
	 */
	public Object get(String name, Scriptable start)
	{
		if ("length".equals(name))
		{
			return values.length;
		}
		else if ("name".equals(name))
		{
			return label;
		}
		return null;
	}

	public String toString()
	{
		return Arrays.toString(values);
	}

	/**
	 * Get the indexed property.
	 * @param index the index of the integral property
	 * @param start the object where the lookup began
	 */
	public Object get(int index, Scriptable start)
	{
		if (index < values.length)
		{
			return values[index];
		}
		else
		{
			return "";
		}
	}

	/**
	 * Set a named property.
	 *
	 * We do nothing here, so all properties are effectively read-only.
	 */
	public void put(String name, Scriptable start, Object value)
	{
	}

	/**
	 * Set an indexed property.
	 *
	 * We do nothing here, so all properties are effectively read-only.
	 */
	public void put(int index, Scriptable start, Object value)
	{
	}

	/**
	 * Remove a named property.
	 */
	public void delete(String id)
	{
	}

	/**
	 * Remove an indexed property.
	 */
	public void delete(int index)
	{
	}

	/**
	 * Get prototype.
	 */
	public Scriptable getPrototype()
	{
		return prototype;
	}

	/**
	 * Set prototype.
	 */
	public void setPrototype(Scriptable prototype)
	{
		this.prototype = prototype;
	}

	/**
	 * Get parent.
	 */
	public Scriptable getParentScope()
	{
		return parent;
	}

	/**
	 * Set parent.
	 */
	public void setParentScope(Scriptable parent)
	{
		this.parent = parent;
	}

	/**
	 * Get properties.
	 */
	public Object[] getIds()
	{
		return new Object[0];
	}

	/**
	 * Default value.
	 *
	 * Use the convenience method from Context that takes care of calling
	 * toString, etc.
	 */
	public Object getDefaultValue(Class typeHint)
	{
		return "[object DataSetValues]";
	}

	/**
	 * instanceof operator.
	 *
	 * We mimick the normal JavaScript instanceof semantics, returning
	 * true if <code>this</code> appears in <code>value</code>'s prototype
	 * chain.
	 */
	public boolean hasInstance(Scriptable value)
	{
		Scriptable proto = value.getPrototype();
		while(proto != null)
		{
			if (proto.equals(this))
				return true;
			proto = proto.getPrototype();
		}
		return false;
	}
}
