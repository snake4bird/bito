// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:18:53
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;

import java.io.PrintStream;

// Referenced classes of package com.gif4j:
//            MorphingFilter, GifFrame, GifImage

public class mf extends fm
{

    public mf()
    {
        this(8, 10);
    }

    public mf(int i)
    {
        this(i, 10);
    }

    public mf(int i, int j)
    {
        if(i <= 1)
            throw new IllegalArgumentException("frames number should be greater than 1.");
        b = i;
        if(j < 1)
        {
            throw new IllegalArgumentException("delay between frames (in 1/100 sec) should be greater than 0.");
        } else
        {
            a = j;
            return;
        }
    }

    gf[] a(gi gifimage, gf gifframe)
    {
        gf agifframe[] = new gf[b];
        for(int i = 0; i < b; i++)
        {
            agifframe[i] = gifframe.a(true);
            agifframe[i].o = 1;
            agifframe[i].s = a;
        }

        agifframe[b - 1].o = gifframe.o;
        agifframe[b - 1].s = gifframe.s;
        int j = gifframe.width / 2 + gifframe.width % 2;
        int k = gifframe.height / 2 + gifframe.height % 2;
        int l = gifframe.width / b + (gifframe.width % b == 0 ? 0 : 1);
        int i1 = gifframe.height / b + (gifframe.height % b == 0 ? 0 : 1);
        double d = (double)gifframe.width / (double)gifframe.height;
        int j1 = 0;
        for(int k1 = gifframe.height - 1; j1 <= k || k1 >= k; k1--)
        {
            int l1 = 0;
            for(int i2 = gifframe.width - 1; l1 <= j || i2 >= j; i2--)
            {
                int j2 = 0;
                if(j1 != k)
                {
                    double d1 = (double)(j - l1) / (double)(k - j1);
                    if(d1 < d)
                        j2 = b - (int)((d1 * (double)k) / (double)l) - 1;
                    else
                        j2 = (int)(((1.0D / d1) * (double)j) / (double)i1);
                    if(j2 >= b)
                        System.out.println("");
                }
                int k2 = j1 * gifframe.width + l1;
                agifframe[j2].n[k2] = gifframe.n[k2];
                k2 = k1 * gifframe.width + l1;
                agifframe[b - j2 - 1].n[k2] = gifframe.n[k2];
                k2 = j1 * gifframe.width + i2;
                agifframe[b - j2 - 1].n[k2] = gifframe.n[k2];
                k2 = k1 * gifframe.width + i2;
                agifframe[j2].n[k2] = gifframe.n[k2];
                l1++;
            }

            j1++;
        }

        return agifframe;
    }

    private int a;
    private int b;
}
