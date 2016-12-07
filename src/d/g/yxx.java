// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 16:56:04
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package d.g;

import java.lang.reflect.Array;

abstract class yxx
{

    public yxx(int i, double d1, Class class1)
    {
        if(d1 <= 0.0D || d1 >= 1.0D)
        {
            throw new IllegalArgumentException("fill value out of range");
        } else
        {
            c = d1;
            int j = Math.max((int)((double)i / c), 31);
            j += (j + 1) % 2;
            e = (int)((double)j * c);
            f = j / 2;
            g = new boolean[j];
            a(Array.newInstance(class1, j));
            return;
        }
    }

    public yxx(yxx y1)
    {
        c = y1.c;
        d = y1.d;
        e = y1.e;
        f = y1.f;
        int i = y1.g.length;
        g = new boolean[i];
        System.arraycopy(y1.g, 0, g, 0, g.length);
        Class class1 = y1.a().getClass().getComponentType();
        Object obj = Array.newInstance(class1, i);
        System.arraycopy(y1.a(), 0, obj, 0, i);
        a(obj);
    }

    protected abstract Object a();

    protected abstract void a(Object obj);

    protected abstract void d(int i);

    protected void e(int i)
    {
        int j = g.length;
        int k;
        for(k = e; k < i; k = (int)((double)j * c))
            j = j * 2 + 1;

        e = k;
        f = j / 2;
        d(j);
    }

    public final void f(int i)
    {
        if(i > e)
            e(i);
    }

    protected final int g(int i)
    {
        return (i + f) % g.length;
    }

    protected final int h(int i)
    {
        for(; g[i]; i = g(i));
        return i;
    }

    protected double c;
    protected int d;
    protected int e;
    protected int f;
    protected boolean g[];
}
