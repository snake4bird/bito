// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 16:54:12
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;


// Referenced classes of package com.gif4j.quantizer:
//            h

class kxx extends hxx
{

    public kxx(int j, double d)
    {
        super(j, d, Integer.TYPE);
    }

    public kxx(int j)
    {
        this(j, 0.29999999999999999D);
    }

    public kxx()
    {
        this(0, 0.29999999999999999D);
    }

    public kxx(kxx k1)
    {
        super(k1);
    }

    protected Object a()
    {
        return a;
    }

    protected void a(Object obj)
    {
        a = (int[])obj;
    }

    protected void a(boolean aflag[], Object obj)
    {
        int ai[] = (int[])obj;
        for(int j = 0; j < aflag.length; j++)
            if(aflag[j])
                b(ai[j]);

    }

    protected final int a(int j)
    {
        return (j * 517 & 0x7fffffff) % g.length;
    }

    protected int b(int j)
    {
        int l = h(a(j));
        g[l] = true;
        a[l] = j;
        return l;
    }

    public boolean c(int j)
    {
        f(d + 1);
        int l = -i(j) - 1;
        if(l >= 0)
        {
            d++;
            g[l] = true;
            a[l] = j;
            return true;
        } else
        {
            return false;
        }
    }

    protected final int i(int j)
    {
        int l;
        for(l = a(j); g[l]; l = g(l))
            if(j == a[l])
                return l;

        return -l - 1;
    }

    public Object clone()
    {
        return new kxx(this);
    }

    protected int a[];
}
