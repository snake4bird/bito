package d._;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

class ProxyClassGenerator
{
	private static class ConstantPool
	{
		/**
		 * Entry
		 */
		private static abstract class Entry
		{
			public abstract void write(DataOutputStream dataoutputstream) throws IOException;

			private Entry()
			{
			}
		}

		private static class IndirectEntry extends Entry
		{
			private int tag;
			private short index0;
			private short index1;

			public IndirectEntry(int i, short word0)
			{
				tag = i;
				index0 = word0;
				index1 = 0;
			}

			public IndirectEntry(int i, short word0, short word1)
			{
				tag = i;
				index0 = word0;
				index1 = word1;
			}

			public void write(DataOutputStream dataoutputstream) throws IOException
			{
				dataoutputstream.writeByte(tag);
				dataoutputstream.writeShort(index0);
				if (tag == CONSTANT_FIELD
					|| tag == CONSTANT_METHOD
						|| tag == CONSTANT_INTERFACEMETHOD
						|| tag == CONSTANT_NAMEANDTYPE)
					dataoutputstream.writeShort(index1);
			}

			public int hashCode()
			{
				return tag + index0 + index1;
			}

			public boolean equals(Object obj)
			{
				if (obj instanceof IndirectEntry)
				{
					IndirectEntry indirectentry = (IndirectEntry)obj;
					if (tag == indirectentry.tag && index0 == indirectentry.index0 && index1 == indirectentry.index1)
						return true;
				}
				return false;
			}
		}

		private static class ValueEntry extends Entry
		{
			private Object value;

			public ValueEntry(Object obj)
			{
				value = obj;
			}

			public void write(DataOutputStream dataoutputstream) throws IOException
			{
				if (value instanceof String)
				{
					dataoutputstream.writeByte(CONSTANT_UTF8);
					dataoutputstream.writeUTF((String)value);
				}
				else if (value instanceof Integer)
				{
					dataoutputstream.writeByte(CONSTANT_INTEGER);
					dataoutputstream.writeInt(((Integer)value).intValue());
				}
				else if (value instanceof Float)
				{
					dataoutputstream.writeByte(CONSTANT_FLOAT);
					dataoutputstream.writeFloat(((Float)value).floatValue());
				}
				else if (value instanceof Long)
				{
					dataoutputstream.writeByte(CONSTANT_LONG);
					dataoutputstream.writeLong(((Long)value).longValue());
				}
				else if (value instanceof Double)
				{
					//!! write double, not byte
					dataoutputstream.writeDouble(CONSTANT_DOUBLE);
					dataoutputstream.writeDouble(((Double)value).doubleValue());
				}
				else
				{
					throw new InternalError("bogus value entry: " + value);
				}
			}
		}

		private List pool;
		private Map map;
		private boolean readOnly;

		private ConstantPool()
		{
			pool = new ArrayList(32);
			map = new HashMap(16);
			readOnly = false;
		}

		public short getUtf8(String s)
		{
			if (s == null)
				throw new NullPointerException();
			else
				return getValue(s);
		}

		public short getInteger(int i)
		{
			return getValue(new Integer(i));
		}

		public short getFloat(float f)
		{
			return getValue(new Float(f));
		}

		public short getClass(String s)
		{
			short word0 = getUtf8(s);
			return getIndirect(new IndirectEntry(CONSTANT_CLASS, word0));
		}

		public short getString(String s)
		{
			short word0 = getUtf8(s);
			return getIndirect(new IndirectEntry(CONSTANT_STRING, word0));
		}

		public short getFieldRef(String classname, String fieldname, String typename)
		{
			short word0 = getClass(classname);
			short word1 = getNameAndType(fieldname, typename);
			return getIndirect(new IndirectEntry(CONSTANT_FIELD, word0, word1));
		}

		public short getMethodRef(String s, String s1, String s2)
		{
			short word0 = getClass(s);
			short word1 = getNameAndType(s1, s2);
			return getIndirect(new IndirectEntry(CONSTANT_METHOD, word0, word1));
		}

		public short getInterfaceMethodRef(String s, String s1, String s2)
		{
			short word0 = getClass(s);
			short word1 = getNameAndType(s1, s2);
			return getIndirect(new IndirectEntry(CONSTANT_INTERFACEMETHOD, word0, word1));
		}

		public short getNameAndType(String s, String s1)
		{
			short word0 = getUtf8(s);
			short word1 = getUtf8(s1);
			return getIndirect(new IndirectEntry(CONSTANT_NAMEANDTYPE, word0, word1));
		}

		public void setReadOnly()
		{
			readOnly = true;
		}

		public void write(OutputStream outputstream) throws IOException
		{
			DataOutputStream dataoutputstream = new DataOutputStream(outputstream);
			dataoutputstream.writeShort(pool.size() + 1);
			Entry entry;
			for(Iterator iterator = pool.iterator(); iterator.hasNext(); entry.write(dataoutputstream))
				entry = (Entry)iterator.next();
		}

		private short addEntry(Entry entry)
		{
			pool.add(entry);
			if (pool.size() >= 65535)
				throw new IllegalArgumentException("constant pool size limit exceeded");
			else
				return (short)pool.size();
		}

		private short getValue(Object obj)
		{
			Short short1 = (Short)map.get(obj);
			if (short1 != null)
				return short1.shortValue();
			if (readOnly)
			{
				throw new InternalError("late constant pool addition");
			}
			else
			{
				short word0 = addEntry(new ValueEntry(obj));
				map.put(obj, new Short(word0));
				return word0;
			}
		}

		private short getIndirect(IndirectEntry indirectentry)
		{
			Short short1 = (Short)map.get(indirectentry);
			if (short1 != null)
				return short1.shortValue();
			if (readOnly)
			{
				throw new InternalError("late constant pool addition");
			}
			else
			{
				short word0 = addEntry(indirectentry);
				map.put(indirectentry, new Short(word0));
				return word0;
			}
		}
	}

	private static class ExceptionTableEntry
	{
		private short startPcx;
		private short endPcx;
		private short handlerPcx;
		private short catchTypex;

		public ExceptionTableEntry(short startPc, short endPc, short handlerPc, short catchType)
		{
			this.startPcx = startPc;
			this.endPcx = endPc;
			this.handlerPcx = handlerPc;
			this.catchTypex = catchType;
		}

		public void write(DataOutputStream dataoutputstream) throws IOException
		{
			dataoutputstream.writeShort(this.startPcx);
			dataoutputstream.writeShort(this.endPcx);
			dataoutputstream.writeShort(this.handlerPcx);
			dataoutputstream.writeShort(this.catchTypex);
		}
	}

	private class FieldInfo
	{
		public int accessFlags;
		public String name;
		public String descriptor;

		public FieldInfo(String name, String descriptor, int accessFlags)
		{
			super();
			this.name = name;
			this.descriptor = descriptor;
			this.accessFlags = accessFlags;
			cp.getUtf8(name);
			cp.getUtf8(descriptor);
		}

		public void write(DataOutputStream dataoutputstream) throws IOException
		{
			dataoutputstream.writeShort(accessFlags);
			dataoutputstream.writeShort(cp.getUtf8(name));
			dataoutputstream.writeShort(cp.getUtf8(descriptor));
			dataoutputstream.writeShort(0);
		}
	}

	private static class PrimitiveTypeInfo
	{
		private static void add(Class class1, Class class2)
		{
			table.put(class1, new PrimitiveTypeInfo(class1, class2));
		}

		public static PrimitiveTypeInfo get(Class class1)
		{
			return (PrimitiveTypeInfo)table.get(class1);
		}

		public final String baseTypeString;
		public final String wrapperClassName;
		public final String wrapperValueOfDesc;
		public final String unwrapMethodName;
		public final String unwrapMethodDesc;
		private static Map table = new HashMap();
		static
		{
			add(Byte.TYPE, Byte.class);
			add(Character.TYPE, Character.class);
			add(Double.TYPE, Double.class);
			add(Float.TYPE, Float.class);
			add(Integer.TYPE, Integer.class);
			add(Long.TYPE, Long.class);
			add(Short.TYPE, Short.class);
			add(Boolean.TYPE, Boolean.class);
		}

		private PrimitiveTypeInfo(Class class1, Class class2)
		{
			baseTypeString = Array.newInstance(class1, 0).getClass().getName().substring(1);
			wrapperClassName = ProxyClassGenerator.dotToSlash(class2.getName());
			wrapperValueOfDesc = ("(" + baseTypeString + ")L" + wrapperClassName + ";");
			unwrapMethodName = (class1.getName() + "Value");
			unwrapMethodDesc = ("()" + baseTypeString);
		}
	}

	private class MethodInfo
	{
		private int accessFlagsx;
		private String namex;
		private String descriptorx;
		private short maxStackx;
		private short maxLocalsx;
		private ByteArrayOutputStream code;
		private List exceptionTable;
		private short declaredExceptions[];

		public MethodInfo(String name, String descriptor, int accessFlags)
		{
			super();
			code = new ByteArrayOutputStream();
			exceptionTable = new ArrayList();
			this.namex = name;
			this.descriptorx = descriptor;
			this.accessFlagsx = accessFlags;
			cp.getUtf8(name);
			cp.getUtf8(descriptor);
			cp.getUtf8("Code");
			cp.getUtf8("Exceptions");
		}

		public void write(DataOutputStream dataoutputstream) throws IOException
		{
			dataoutputstream.writeShort(accessFlagsx);
			dataoutputstream.writeShort(cp.getUtf8(namex));
			dataoutputstream.writeShort(cp.getUtf8(descriptorx));
			dataoutputstream.writeShort(2);
			dataoutputstream.writeShort(cp.getUtf8("Code"));
			dataoutputstream.writeInt(12 + code.size() + 8 * exceptionTable.size());
			dataoutputstream.writeShort(maxStackx);
			dataoutputstream.writeShort(maxLocalsx);
			dataoutputstream.writeInt(code.size());
			code.writeTo(dataoutputstream);
			dataoutputstream.writeShort(exceptionTable.size());
			ExceptionTableEntry exceptiontableentry;
			for(Iterator iterator = exceptionTable.iterator(); iterator.hasNext();)
			{
				exceptiontableentry = (ExceptionTableEntry)iterator.next();
				exceptiontableentry.write(dataoutputstream);
			}
			dataoutputstream.writeShort(0);
			dataoutputstream.writeShort(cp.getUtf8("Exceptions"));
			dataoutputstream.writeInt(2 + 2 * declaredExceptions.length);
			dataoutputstream.writeShort(declaredExceptions.length);
			for(int i = 0; i < declaredExceptions.length; i++)
				dataoutputstream.writeShort(declaredExceptions[i]);
		}

		public void setMaxStack(int i)
		{
			maxStackx = (short)i;
		}

		public void setMaxLocals(int i)
		{
			maxLocalsx = (short)i;
		}
	}

	private class ProxyMethod
	{
		private MethodInfo generateMethod() throws IOException
		{
			String methodDescriptor = ProxyClassGenerator.getMethodDescriptor(parameterTypes, returnType);
			MethodInfo methodinfo = new MethodInfo(methodName, methodDescriptor, ACC_PUBLIC | ACC_FINAL);
			int ai[] = new int[parameterTypes.length];
			int i = 1;
			for(int j = 0; j < ai.length; j++)
			{
				ai[j] = i;
				i += ProxyClassGenerator.getWordsPerType(parameterTypes[j]);
			}
			int k = i;
			short word1 = 0;
			DataOutputStream dataoutputstream = new DataOutputStream(methodinfo.code);
			code_aload(0, dataoutputstream);
			dataoutputstream.writeByte(opc_getfield/*180*/);
			dataoutputstream.writeShort(cp.getFieldRef(className, objectName, "L" + fromClassName + ";"));
			for(int n = 0; n < parameterTypes.length; n++)
			{
				codeWrapArgument(parameterTypes[n], ai[n], dataoutputstream);
			}
			dataoutputstream.writeByte(opc_invokevirtual);
			dataoutputstream.writeShort(cp.getMethodRef(fromClassName, methodName, methodDescriptor));
			// objref, [arg0, [...]]
			// invokevirtual indexref, indexargs
			// objref, [arg0, [...]]
			// invokeinterface indexref, indexargs, argslength, 0
			codeUnwrapReturnValue(returnType, dataoutputstream);
			short word2 = (short)methodinfo.code.size();
			short word0;
			word0 = (short)methodinfo.code.size();
			methodinfo.exceptionTable.add(new ExceptionTableEntry(word1, word2, word0, cp
				.getClass("java/lang/RuntimeException")));
			code_astore(i, dataoutputstream);
			code_aload(i, dataoutputstream);
			dataoutputstream.writeByte(opc_athrow/*191*/);
			word0 = (short)methodinfo.code.size();
			methodinfo.exceptionTable.add(new ExceptionTableEntry(word1, word2, word0, cp
				.getClass("java/lang/Exception")));
			code_astore(i, dataoutputstream);
			dataoutputstream.writeByte(opc_new/*187*/);
			dataoutputstream.writeShort(cp.getClass("java/lang/RuntimeException"));
			dataoutputstream.writeByte(opc_dup/*89*/);
			code_aload(i, dataoutputstream);
			dataoutputstream.writeByte(opc_invokespecial/*183*/);
			dataoutputstream.writeShort(cp.getMethodRef("java/lang/RuntimeException",
				"<init>",
				"(Ljava/lang/Throwable;)V"));
			dataoutputstream.writeByte(opc_athrow/*191*/);
			if (methodinfo.code.size() > 65535)
				throw new IllegalArgumentException("code size limit exceeded");
			methodinfo.setMaxStack(10);
			methodinfo.setMaxLocals(k + 1);
			methodinfo.declaredExceptions = new short[exceptionTypes.length];
			for(int i1 = 0; i1 < exceptionTypes.length; i1++)
				methodinfo.declaredExceptions[i1] = cp.getClass(ProxyClassGenerator.dotToSlash(exceptionTypes[i1]
					.getName()));
			return methodinfo;
		}

		private void codeWrapArgument(Class class1, int i, DataOutputStream dataoutputstream) throws IOException
		{
			if (class1.isPrimitive())
			{
				PrimitiveTypeInfo primitivetypeinfo = PrimitiveTypeInfo.get(class1);
				if (class1 == Integer.TYPE
					|| class1 == Boolean.TYPE
						|| class1 == Byte.TYPE
						|| class1 == Character.TYPE
						|| class1 == Short.TYPE)
					code_iload(i, dataoutputstream);
				else if (class1 == Long.TYPE)
					code_lload(i, dataoutputstream);
				else if (class1 == Float.TYPE)
					code_fload(i, dataoutputstream);
				else if (class1 == Double.TYPE)
					code_dload(i, dataoutputstream);
				else
					throw new AssertionError();
				dataoutputstream.writeByte(opc_invokestatic/*184*/);
				dataoutputstream.writeShort(cp.getMethodRef(primitivetypeinfo.wrapperClassName,
					"valueOf",
					primitivetypeinfo.wrapperValueOfDesc));
			}
			else
			{
				code_aload(i, dataoutputstream);
			}
		}

		private void codeUnwrapReturnValue(Class class1, DataOutputStream dataoutputstream) throws IOException
		{
			if (class1 == Void.TYPE)
			{
				dataoutputstream.writeByte(opc_return/*177*/);
			}
			else if (class1 == Integer.TYPE
				|| class1 == Boolean.TYPE
					|| class1 == Byte.TYPE
					|| class1 == Character.TYPE
					|| class1 == Short.TYPE)
			{
				dataoutputstream.writeByte(opc_ireturn/*172*/);
			}
			else if (class1 == Long.TYPE)
			{
				dataoutputstream.writeByte(opc_lreturn/*173*/);
			}
			else if (class1 == Float.TYPE)
			{
				dataoutputstream.writeByte(opc_freturn/*174*/);
			}
			else if (class1 == Double.TYPE)
			{
				dataoutputstream.writeByte(opc_dreturn/*175*/);
			}
			else
			{
				// type cast
				// dataoutputstream.writeByte(opc_checkcast/*192*/);
				// dataoutputstream.writeShort(cp.getClass(ProxyClassGenerator.dotToSlash(class1.getName())));
				dataoutputstream.writeByte(opc_areturn/*176*/);
			}
		}

		private void codeFieldInitialization(DataOutputStream dataoutputstream) throws IOException
		{
			codeClassForName(fromClass, dataoutputstream);
			code_ldc(cp.getString(methodName), dataoutputstream);
			code_ipush(parameterTypes.length, dataoutputstream);
			dataoutputstream.writeByte(opc_anewarray/*189*/);
			dataoutputstream.writeShort(cp.getClass("java/lang/Class"));
			for(int i = 0; i < parameterTypes.length; i++)
			{
				dataoutputstream.writeByte(opc_dup/*89*/);
				code_ipush(i, dataoutputstream);
				if (parameterTypes[i].isPrimitive())
				{
					PrimitiveTypeInfo primitivetypeinfo = PrimitiveTypeInfo.get(parameterTypes[i]);
					dataoutputstream.writeByte(opc_getstatic/*178*/);
					dataoutputstream.writeShort(cp.getFieldRef(primitivetypeinfo.wrapperClassName,
						"TYPE",
						"Ljava/lang/Class;"));
				}
				else
				{
					codeClassForName(parameterTypes[i], dataoutputstream);
				}
				dataoutputstream.writeByte(opc_aastore/*83*/);
			}
			dataoutputstream.writeByte(opc_invokevirtual/*182*/);
			dataoutputstream.writeShort(cp.getMethodRef("java/lang/Class",
				"getMethod",
				"(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;"));
			dataoutputstream.writeByte(opc_putstatic/*179*/);
			dataoutputstream.writeShort(cp.getFieldRef(className, methodFieldName, "Ljava/lang/reflect/Method;"));
		}

		public String methodName;
		public Class parameterTypes[];
		public Class returnType;
		public Class exceptionTypes[];
		public Class fromClass;
		public String methodFieldName;
		public String objectName;
		public String fromClassName;

		private ProxyMethod(String methodName, Class parameterTypes[], Class returnType, Class exceptionTypes[],
			Class fromClass, String fromClassName, String objectName)
		{
			super();
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
			this.returnType = returnType;
			this.exceptionTypes = exceptionTypes;
			this.fromClass = fromClass;
			this.methodFieldName = "m" + (proxyMethodCount++);
			this.objectName = objectName;
			this.fromClassName = fromClassName;
		}
	}

	private static HashMap loadedClassesCache = new HashMap();

	private static class ProxyClassLoader extends ClassLoader
	{
		public ProxyClassLoader()
		{
			super();
		}

		protected Class<?> findClass(String className) throws ClassNotFoundException
		{
			Class cls = (Class)loadedClassesCache.get(className);
			if (cls == null)
			{
				cls = super.findClass(className);
			}
			return cls;
		}

		public Class loadClass(String proxyClassName, byte[] proxyClassFile)
		{
			Class proxyClass = defineClass(proxyClassName, proxyClassFile, 0, proxyClassFile.length);
			loadedClassesCache.put(proxyClassName, proxyClass);
			return proxyClass;
		}
	}

	static ProxyClassLoader proxyClassLoader = new ProxyClassLoader();

	static Class getProxyClass(String proxyName, Class superClass, Class[] interfaces, Class[] overrideClass,
		Method[] overrideMethods)
	{
		byte[] proxyClassFile = generateProxyClass(proxyName, superClass, interfaces, overrideClass, overrideMethods);
		Throwable t = null;
		try
		{
			Class proxyClass = proxyClassLoader.loadClass(proxyName, proxyClassFile);
			if (proxyClass != null)
			{
				proxyClass.newInstance();
			}
			return proxyClass;
		}
		catch(Throwable ta)
		{
			t = ta;
			throw new RuntimeException(t);
		}
		finally
		{
			if (t != null || new File("ev.debug").exists())
			{
				try
				{
					//Debug out put file
					FileOutputStream fileoutputstream = new FileOutputStream("proxy.class");
					fileoutputstream.write(proxyClassFile);
					fileoutputstream.close();
				}
				catch(IOException e)
				{
				}
			}
		}
	}

	private static byte[] generateProxyClass(String proxyName, Class superClass, Class[] interfaces,
		Class[] overrideClass, Method[] overrideMethods)
	{
		ProxyClassGenerator proxygenerator = new ProxyClassGenerator(proxyName,
			superClass,
			interfaces,
			overrideClass,
			overrideMethods);
		byte classFile[] = proxygenerator.generateClassFile();
		return classFile;
	}

	private ProxyClassGenerator(String proxyName, Class superClass, Class[] interfaces, Class[] overrideClass,
		Method[] overrideMethods)
	{
		cp = new ConstantPool();
		fields = new ArrayList();
		methods = new ArrayList();
		proxyMethods = new HashMap();
		proxyMethodCount = 0;
		className = dotToSlash(proxyName);
		this.superClass = superClass;
		this.overrideMethods = overrideMethods;
		this.overrideClass = overrideClass;
		this.interfaces = interfaces;
		superclassName = dotToSlash(superClass.getName());
		overrideClassName = new String[overrideClass.length];
		for(int i = 0; i < overrideClass.length; i++)
		{
			overrideClassName[i] = dotToSlash(overrideClass[i].getName());
		}
	}

	private void addProxyOverrideMethod(String classname, String objectname, Class cls, Method[] override_methods)
	{
		Method amethod[] = (override_methods == null)?cls.getDeclaredMethods():override_methods;
		for(int k = 0; k < amethod.length; k++)
		{
			if (amethod[k].getDeclaringClass().isAssignableFrom(cls))
			{
				int m = amethod[k].getModifiers();
				if ((m & (ACC_PUBLIC)) != 0)
				{
					addProxyMethod(classname, objectname, amethod[k], cls);
				}
			}
		}
	}

	private byte[] generateClassFile()
	{
		for(int i = 0; i < overrideClass.length; i++)
		{
			addProxyOverrideMethod(overrideClassName[i], "override" + i, overrideClass[i], overrideMethods);
		}
		List list;
		for(Iterator iterator = proxyMethods.values().iterator(); iterator.hasNext(); checkReturnTypes(list))
			list = (List)iterator.next();
		try
		{
			methods.add(generateConstructorDefault());
			methods.add(generateConstructor());
			for(Iterator iterator1 = proxyMethods.values().iterator(); iterator1.hasNext();)
			{
				List list1 = (List)iterator1.next();
				Iterator iterator2 = list1.iterator();
				while(iterator2.hasNext())
				{
					ProxyMethod proxymethod = (ProxyMethod)iterator2.next();
					//					fields.add(new FieldInfo(proxymethod.methodFieldName, "Ljava/lang/reflect/Method;", ACC_PRIVATE
					//						| ACC_STATIC));
					methods.add(proxymethod.generateMethod());
				}
			}
			for(int i = 0; i < overrideClassName.length; i++)
			{
				fields.add(new FieldInfo("override" + i, "L" + overrideClassName[i] + ";", ACC_PRIVATE));
			}
			methods.add(generateStaticInitializer());
		}
		catch(IOException ioexception)
		{
			throw new InternalError("unexpected I/O Exception");
		}
		if (methods.size() > 65535)
			throw new IllegalArgumentException("method limit exceeded");
		if (fields.size() > 65535)
			throw new IllegalArgumentException("field limit exceeded");
		cp.getClass(className);
		cp.getClass(superclassName);
		for(int l = 0; l < interfaces.length; l++)
		{
			cp.getClass(dotToSlash(interfaces[l].getName()));
		}
		cp.setReadOnly();
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
		try
		{
			dataoutputstream.writeInt(0xcafebabe);
			dataoutputstream.writeShort(CLASSFILE_MINOR_VERSION);
			dataoutputstream.writeShort(CLASSFILE_MAJOR_VERSION);
			cp.write(dataoutputstream);
			dataoutputstream.writeShort(49);
			dataoutputstream.writeShort(cp.getClass(className));
			dataoutputstream.writeShort(cp.getClass(superclassName));
			dataoutputstream.writeShort(interfaces.length);
			for(int l = 0; l < interfaces.length; l++)
			{
				dataoutputstream.writeShort(cp.getClass(dotToSlash(interfaces[l].getName())));
			}
			dataoutputstream.writeShort(fields.size());
			FieldInfo fieldinfo;
			for(Iterator iterator3 = fields.iterator(); iterator3.hasNext(); fieldinfo.write(dataoutputstream))
				fieldinfo = (FieldInfo)iterator3.next();
			dataoutputstream.writeShort(methods.size());
			MethodInfo methodinfo;
			for(Iterator iterator4 = methods.iterator(); iterator4.hasNext(); methodinfo.write(dataoutputstream))
				methodinfo = (MethodInfo)iterator4.next();
			dataoutputstream.writeShort(0);
		}
		catch(IOException ioexception1)
		{
			throw new InternalError("unexpected I/O Exception");
		}
		return bytearrayoutputstream.toByteArray();
	}

	private void addProxyMethod(String fromclassname, String objectname, Method method, Class fromClass)
	{
		String methodname;
		Class aclass[];
		Class class2;
		Class aclass1[];
		Object obj;
		label0:
		{
			methodname = method.getName();
			aclass = method.getParameterTypes();
			class2 = method.getReturnType();
			aclass1 = method.getExceptionTypes();
			String s1 = (methodname + getParameterDescriptors(aclass));
			obj = (List)proxyMethods.get(s1);
			if (obj != null)
			{
				Iterator iterator = ((List)(obj)).iterator();
				ProxyMethod proxymethod;
				do
				{
					if (!iterator.hasNext())
						break label0;
					proxymethod = (ProxyMethod)iterator.next();
				}
				while(class2 != proxymethod.returnType);
				ArrayList arraylist = new ArrayList();
				collectCompatibleTypes(aclass1, proxymethod.exceptionTypes, arraylist);
				collectCompatibleTypes(proxymethod.exceptionTypes, aclass1, arraylist);
				proxymethod.exceptionTypes = new Class[arraylist.size()];
				proxymethod.exceptionTypes = (Class[])arraylist.toArray(proxymethod.exceptionTypes);
				return;
			}
			obj = new ArrayList(3);
			proxyMethods.put(s1, obj);
		}
		((List)(obj)).add(new ProxyMethod(methodname, aclass, class2, aclass1, fromClass, fromclassname, objectname));
	}

	private static void checkReturnTypes(List list)
	{
		if (list.size() < 2)
			return;
		LinkedList linkedlist = new LinkedList();
		Iterator iterator = list.iterator();
		label0: do
		{
			if (!iterator.hasNext())
				break;
			ProxyMethod proxymethod1 = (ProxyMethod)iterator.next();
			Class class1 = proxymethod1.returnType;
			if (class1.isPrimitive())
				throw new IllegalArgumentException("methods with same signature "
					+ (getFriendlyMethodSignature(proxymethod1.methodName, proxymethod1.parameterTypes))
						+ (" but incompatible return types: ")
						+ (class1.getName())
						+ (" and others"));
			boolean flag = false;
			ListIterator listiterator = linkedlist.listIterator();
			do
			{
				if (!listiterator.hasNext())
					break;
				Class class2 = (Class)listiterator.next();
				if (class1.isAssignableFrom(class2))
				{
					if (flag)
						throw new AssertionError();
					continue label0;
				}
				if (class2.isAssignableFrom(class1))
					if (!flag)
					{
						listiterator.set(class1);
						flag = true;
					}
					else
					{
						listiterator.remove();
					}
			}
			while(true);
			if (!flag)
				linkedlist.add(class1);
		}
		while(true);
		if (linkedlist.size() > 1)
		{
			ProxyMethod proxymethod = (ProxyMethod)list.get(0);
			throw new IllegalArgumentException("methods with same signature "
				+ (getFriendlyMethodSignature(proxymethod.methodName, proxymethod.parameterTypes))
					+ (" but incompatible return types: ")
					+ (linkedlist));
		}
		else
		{
			return;
		}
	}

	private MethodInfo generateConstructorDefault() throws IOException
	{
		MethodInfo methodinfo = new MethodInfo("<init>", "()V", ACC_PUBLIC/*1*/);
		DataOutputStream dataoutputstream = new DataOutputStream(methodinfo.code);
		code_aload(0, dataoutputstream);
		dataoutputstream.writeByte(opc_invokespecial/*183*/);
		dataoutputstream.writeShort(cp.getMethodRef(superclassName, "<init>", "()V"));
		//code_aload(0, dataoutputstream);
		dataoutputstream.writeByte(opc_return/*177*/);
		methodinfo.setMaxStack(10);
		methodinfo.setMaxLocals(2);
		methodinfo.declaredExceptions = new short[0];
		return methodinfo;
	}

	private MethodInfo generateConstructor() throws IOException
	{
		String mis = "(";
		for(int i = 0; i < overrideClassName.length; i++)
		{
			mis += "L" + overrideClassName[i] + ";";
		}
		mis += ")V";
		MethodInfo methodinfo = new MethodInfo("<init>", mis, ACC_PUBLIC/*1*/);
		DataOutputStream dataoutputstream = new DataOutputStream(methodinfo.code);
		code_aload(0, dataoutputstream);
		dataoutputstream.writeByte(opc_invokespecial/*183*/);
		dataoutputstream.writeShort(cp.getMethodRef(superclassName, "<init>", "()V"));
		int ai = 1;
		for(int i = 0; i < overrideClassName.length; i++)
		{
			code_aload(0, dataoutputstream);
			code_aload(ai, dataoutputstream);
			dataoutputstream.writeByte(opc_put/*181*/);
			dataoutputstream.writeShort(cp.getFieldRef(className, "override" + i, "L" + overrideClassName[i] + ";"));
			ai += getWordsPerType(overrideClass[i]);
		}
		dataoutputstream.writeByte(opc_return/*177*/);
		methodinfo.setMaxStack(10);
		methodinfo.setMaxLocals(2 + overrideClassName.length);
		methodinfo.declaredExceptions = new short[0];
		return methodinfo;
	}

	private MethodInfo generateStaticInitializer() throws IOException
	{
		MethodInfo methodinfo = new MethodInfo("<clinit>", "()V", 8);
		int i = 1;
		short word1 = 0;
		DataOutputStream dataoutputstream = new DataOutputStream(methodinfo.code);
		//		for(Iterator iterator = proxyMethods.values().iterator(); iterator.hasNext();)
		//		{
		//			List list = (List)iterator.next();
		//			Iterator iterator1 = list.iterator();
		//			while(iterator1.hasNext())
		//			{
		//				ProxyMethod proxymethod = (ProxyMethod)iterator1.next();
		//				proxymethod.codeFieldInitialization(dataoutputstream);
		//			}
		//		}
		dataoutputstream.writeByte(opc_return/*177*/);
		//		short word2 = (short)methodinfo.code.size();
		//		short word0;
		//		word0 = (short)methodinfo.code.size();
		//		methodinfo.exceptionTable.add(new ExceptionTableEntry(word1, word2, word0, cp
		//			.getClass("java/lang/RuntimeException")));
		//		code_astore(i, dataoutputstream);
		//		code_aload(i, dataoutputstream);
		//		dataoutputstream.writeByte(opc_athrow/*191*/);
		//		word0 = (short)methodinfo.code.size();
		//		methodinfo.exceptionTable.add(new ExceptionTableEntry(word1, word2, word0, cp.getClass("java/lang/Exception")));
		//		code_astore(i, dataoutputstream);
		//		dataoutputstream.writeByte(opc_new/*187*/);
		//		dataoutputstream.writeShort(cp.getClass("java/lang/RuntimeException"));
		//		dataoutputstream.writeByte(opc_dup/*89*/);
		//		code_aload(i, dataoutputstream);
		//		dataoutputstream.writeByte(opc_invokespecial/*183*/);
		//		dataoutputstream
		//			.writeShort(cp.getMethodRef("java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V"));
		//		dataoutputstream.writeByte(opc_athrow/*191*/);
		if (methodinfo.code.size() > 65535)
		{
			throw new IllegalArgumentException("code size limit exceeded");
		}
		else
		{
			methodinfo.setMaxStack(10);
			methodinfo.setMaxLocals(i + 1);
			methodinfo.declaredExceptions = new short[0];
			return methodinfo;
		}
	}

	private void code_iload(int i, DataOutputStream dataoutputstream) throws IOException
	{
		codeLocalLoadStore(i, 21, 26, dataoutputstream);
	}

	private void code_lload(int i, DataOutputStream dataoutputstream) throws IOException
	{
		codeLocalLoadStore(i, 22, 30, dataoutputstream);
	}

	private void code_fload(int i, DataOutputStream dataoutputstream) throws IOException
	{
		codeLocalLoadStore(i, 23, 34, dataoutputstream);
	}

	private void code_dload(int i, DataOutputStream dataoutputstream) throws IOException
	{
		codeLocalLoadStore(i, 24, 38, dataoutputstream);
	}

	private void code_aload(int i, DataOutputStream dataoutputstream) throws IOException
	{
		codeLocalLoadStore(i, opc_aload/*25*/, opc_aload_0/*42*/, dataoutputstream);
	}

	private void code_astore(int i, DataOutputStream dataoutputstream) throws IOException
	{
		codeLocalLoadStore(i, opc_astore/*58*/, opc_astore_0/*75*/, dataoutputstream);
	}

	private void codeLocalLoadStore(int i, int j, int k, DataOutputStream dataoutputstream) throws IOException
	{
		if (i < 0 || i > 65535)
			throw new AssertionError();
		if (i <= 3)
			dataoutputstream.writeByte(k + i);
		else if (i <= 255)
		{
			dataoutputstream.writeByte(j);
			dataoutputstream.writeByte(i & 0xff);
		}
		else
		{
			dataoutputstream.writeByte(opc_wide/*196, 0xc4, 11000100*/);
			dataoutputstream.writeByte(j);
			dataoutputstream.writeShort(i & 0xffff);
		}
	}

	private void code_ldc(int i, DataOutputStream dataoutputstream) throws IOException
	{
		if (i < 0 || i > 65535)
			throw new AssertionError();
		if (i <= 255)
		{
			dataoutputstream.writeByte(18);
			dataoutputstream.writeByte(i & 0xff);
		}
		else
		{
			dataoutputstream.writeByte(19);
			dataoutputstream.writeShort(i & 0xffff);
		}
	}

	private void code_ipush(int i, DataOutputStream dataoutputstream) throws IOException
	{
		if (i >= -1 && i <= 5)
			dataoutputstream.writeByte(3 + i);
		else if (i >= -128 && i <= 127)
		{
			dataoutputstream.writeByte(16);
			dataoutputstream.writeByte(i & 0xff);
		}
		else if (i >= -32768 && i <= 32767)
		{
			dataoutputstream.writeByte(17);
			dataoutputstream.writeShort(i & 0xffff);
		}
		else
		{
			throw new AssertionError();
		}
	}

	private void codeClassForName(Class class1, DataOutputStream dataoutputstream) throws IOException
	{
		code_ldc(cp.getString(class1.getName()), dataoutputstream);
		dataoutputstream.writeByte(opc_invokestatic/*184*/);
		dataoutputstream.writeShort(cp.getMethodRef("java/lang/Class",
			"forName",
			"(Ljava/lang/String;)Ljava/lang/Class;"));
	}

	private static String getMethodDescriptor(Class parameterTypes[], Class returnType)
	{
		return getParameterDescriptors(parameterTypes) + (returnType != Void.TYPE?getFieldType(returnType):"V");
	}

	private static String getParameterDescriptors(Class aclass[])
	{
		StringBuffer stringbuilder = new StringBuffer("(");
		for(int i = 0; i < aclass.length; i++)
			stringbuilder.append(getFieldType(aclass[i]));
		stringbuilder.append(')');
		return stringbuilder.toString();
	}

	private static String getFieldType(Class class1)
	{
		if (class1.isPrimitive())
			return PrimitiveTypeInfo.get(class1).baseTypeString;
		if (class1.isArray())
			return class1.getName().replace('.', '/');
		else
			return "L" + (dotToSlash(class1.getName())) + ";";
	}

	private static String getFriendlyMethodSignature(String s, Class aclass[])
	{
		StringBuffer stringbuilder = new StringBuffer(s);
		stringbuilder.append('(');
		for(int i = 0; i < aclass.length; i++)
		{
			if (i > 0)
				stringbuilder.append(',');
			Class class1 = aclass[i];
			int j;
			for(j = 0; class1.isArray(); j++)
				class1 = class1.getComponentType();
			stringbuilder.append(class1.getName());
			while(j-- > 0)
				stringbuilder.append("[]");
		}
		stringbuilder.append(')');
		return stringbuilder.toString();
	}

	private static int getWordsPerType(Class class1)
	{
		return class1 != Long.TYPE && class1 != Double.TYPE?1:2;
	}

	private static void collectCompatibleTypes(Class aclass[], Class aclass1[], List list)
	{
		label0: for(int i = 0; i < aclass.length; i++)
		{
			if (list.contains(aclass[i]))
				continue;
			int j = 0;
			do
			{
				if (j >= aclass1.length)
					continue label0;
				if (aclass1[j].isAssignableFrom(aclass[i]))
				{
					list.add(aclass[i]);
					continue label0;
				}
				j++;
			}
			while(true);
		}
	}

	private static List computeUniqueCatchList(Class aclass[])
	{
		ArrayList arraylist = new ArrayList();
		arraylist.add(Error.class);
		arraylist.add(RuntimeException.class);
		label0: for(int i = 0; i < aclass.length; i++)
		{
			Class class1 = aclass[i];
			if (class1.isAssignableFrom(Throwable.class))
			{
				arraylist.clear();
				break;
			}
			if (!Throwable.class.isAssignableFrom(class1))
				continue;
			for(int j = 0; j < arraylist.size();)
			{
				Class class2 = (Class)arraylist.get(j);
				if (class2.isAssignableFrom(class1))
					continue label0;
				if (class1.isAssignableFrom(class2))
					arraylist.remove(j);
				else
					j++;
			}
			arraylist.add(class1);
		}
		return arraylist;
	}

	private static final int CLASSFILE_MAJOR_VERSION = 49;
	private static final int CLASSFILE_MINOR_VERSION = 0;
	private static final int CONSTANT_UTF8 = 1;
	private static final int CONSTANT_UNICODE = 2;
	private static final int CONSTANT_INTEGER = 3;
	private static final int CONSTANT_FLOAT = 4;
	private static final int CONSTANT_LONG = 5;
	private static final int CONSTANT_DOUBLE = 6;
	private static final int CONSTANT_CLASS = 7;
	private static final int CONSTANT_STRING = 8;
	private static final int CONSTANT_FIELD = 9;
	private static final int CONSTANT_METHOD = 10;
	private static final int CONSTANT_INTERFACEMETHOD = 11;
	private static final int CONSTANT_NAMEANDTYPE = 12;
	private static final int ACC_PUBLIC = 1;
	private static final int ACC_PRIVATE = 2;
	private static final int ACC_STATIC = 8;
	private static final int ACC_FINAL = 16;
	private static final int ACC_SUPER = 32;
	private static final int opc_aconst_null = 1;
	private static final int opc_iconst_0 = 3;
	private static final int opc_bipush = 16;
	private static final int opc_sipush = 17;
	private static final int opc_ldc = 18;
	private static final int opc_ldc_w = 19;
	private static final int opc_iload = 21;/*0x15*, 00010101*/
	private static final int opc_lload = 22;/*0x16*/
	private static final int opc_fload = 23;/*0x17*/
	private static final int opc_dload = 24;/*0x18*/
	private static final int opc_aload = 25;/*0x19, 00011111*/
	private static final int opc_iload_0 = 26;/*0x20, 00100000*/
	private static final int opc_lload_0 = 30;
	private static final int opc_fload_0 = 34;
	private static final int opc_dload_0 = 38;
	private static final int opc_aload_0 = 42;/*0x2A, 00101010*/
	private static final int opc_astore = 58;
	private static final int opc_astore_0 = 75;
	private static final int opc_aastore = 83;
	private static final int opc_pop = 87;
	private static final int opc_dup = 89;
	private static final int opc_ireturn = 172;
	private static final int opc_lreturn = 173;
	private static final int opc_freturn = 174;
	private static final int opc_dreturn = 175;
	private static final int opc_areturn = 176;
	private static final int opc_return = 177;
	private static final int opc_getstatic = 178;
	private static final int opc_putstatic = 179;
	private static final int opc_getfield = 180;
	private static final int opc_put = 181;
	private static final int opc_invokevirtual = 182;
	private static final int opc_invokespecial = 183;
	private static final int opc_invokestatic = 184;
	private static final int opc_invokeinterface = 185;
	private static final int opc_new = 187;
	private static final int opc_anewarray = 189;
	private static final int opc_athrow = 191;
	private static final int opc_checkcast = 192;
	private static final int opc_wide = 196;
	private String className;
	private Class superClass;
	private Class[] overrideClass;
	private String superclassName;
	private String[] overrideClassName;
	private ConstantPool cp;
	private List fields;
	private List methods;
	private Method[] overrideMethods;
	private Map proxyMethods;
	private int proxyMethodCount;
	private Class[] interfaces;

	private static String dotToSlash(String s)
	{
		return s.replace('.', '/');
	}
}