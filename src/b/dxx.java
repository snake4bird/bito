// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 16:56:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;


// Referenced classes of package com.gif4j.quantizer:
//            v

class dxx extends vxx
{

    public dxx(int i, double d1)
    {
        super(i, d1, Integer.TYPE, Integer.TYPE);
    }

    public dxx(int i)
    {
        this(i, 0.29999999999999999D);
    }

    public dxx()
    {
        this(0, 0.29999999999999999D);
    }

    public dxx(dxx d1)
    {
        super(d1);
    }

    protected final Object a()
    {
        return a;
    }

    protected final void a(Object obj)
    {
        a = (int[])obj;
    }

    protected final Object b()
    {
        return b;
    }

    protected final void b(Object obj)
    {
        b = (int[])obj;
    }

    protected void a(boolean aflag[], Object obj, Object obj1)
    {
        int ai[] = (int[])obj;
        int ai1[] = (int[])obj1;
        for(int i = 0; i < aflag.length; i++)
            if(aflag[i])
                a(ai[i], ai1[i]);

    }

    protected final int a(int i)
    {
        return (i * 517 & 0x7fffffff) % g.length;
    }

    protected int a(int i, int j)
    {
        int k = h(a(i));
        g[k] = true;
        a[k] = i;
        b[k] = j;
        return k;
    }

    public int b(int i, int j)
    {
        f(d + 1);
        int k = b(i);
        if(k >= 0)
        {
            int l = b[k];
            b[k] = j;
            return l;
        } else
        {
            d++;
            k = -k - 1;
            g[k] = true;
            a[k] = i;
            b[k] = j;
            return 0x80000000;
        }
    }

    protected final int b(int i)
    {
        int j;
        for(j = a(i); g[j]; j = g(j))
            if(i == a[j])
                return j;

        return -j - 1;
    }

    public final int c(int i)
    {
        int j = b(i);
        if(j >= 0)
            return b[j];
        else
            return 0x80000000;
    }

    public Object clone()
    {
        return new dxx(this);
    }

    protected int a[];
    protected int b[];
}
