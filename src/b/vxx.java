// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 16:56:43
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;

import java.lang.reflect.Array;

// Referenced classes of package com.gif4j.quantizer:
//            y

abstract class vxx extends yxx
{

    public vxx(int i, double d1, Class class1, Class class2)
    {
        super(i, d1, class1);
        b(Array.newInstance(class2, g.length));
    }

    public vxx(vxx v1)
    {
        super(v1);
        int i = v1.g.length;
        Class class1 = v1.b().getClass().getComponentType();
        Object obj = Array.newInstance(class1, i);
        System.arraycopy(v1.b(), 0, obj, 0, i);
        b(obj);
    }

    protected abstract Object b();

    protected abstract void b(Object obj);

    protected abstract void a(boolean aflag[], Object obj, Object obj1);

    protected void d(int i)
    {
        boolean aflag[] = g;
        g = new boolean[i];
        Object obj = a();
        Class class1 = obj.getClass().getComponentType();
        a(Array.newInstance(class1, i));
        Object obj1 = b();
        class1 = obj1.getClass().getComponentType();
        b(Array.newInstance(class1, i));
        a(aflag, obj, obj1);
    }
}
