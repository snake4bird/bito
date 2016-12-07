// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 16:55:46
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package d.g;

import java.lang.reflect.Array;

// Referenced classes of package com.gif4j.quantizer:
//            y

abstract class hxx extends yxx
{

    public hxx(int i, double d1, Class class1)
    {
        super(i, d1, class1);
    }

    public hxx(hxx h1)
    {
        super(h1);
    }

    protected abstract void a(boolean aflag[], Object obj);

    protected void d(int i)
    {
        boolean aflag[] = g;
        g = new boolean[i];
        Object obj = a();
        Class class1 = obj.getClass().getComponentType();
        a(Array.newInstance(class1, i));
        a(aflag, obj);
    }
}
