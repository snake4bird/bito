// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:19:05
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;

import java.util.Random;

// Referenced classes of package com.gif4j:
//            MorphingFilter, GifFrame, GifImage

public class mo extends fm
{

    public mo()
    {
        this(4, 4, 10);
    }

    public mo(int i)
    {
        this(i, 4, 10);
    }

    public mo(int i, int j)
    {
        this(i, j, 10);
    }

    public mo(int i, int j, int k)
    {
        if(i <= 1)
            throw new IllegalArgumentException("mozaic box size should be greater than 1");
        if(j <= 1)
            throw new IllegalArgumentException("number of frames should be greater than 1");
        if(k < 1)
        {
            throw new IllegalArgumentException("delay between frames (in 1/100 sec) should be greater than or equal to 1");
        } else
        {
            a = i;
            b = j;
            c = k;
            return;
        }
    }

    gf[] a(gi gifimage, gf gifframe)
    {
        gf agifframe[] = new gf[b];
        agifframe[0] = gifframe.a(true);
        agifframe[0].s = c;
        agifframe[0].o = 1;
        for(int i = 1; i < b; i++)
        {
            agifframe[i] = agifframe[0].f();
            agifframe[i].s = c;
            agifframe[i].o = 1;
        }

        agifframe[b - 1].s = gifframe.s;
        agifframe[b - 1].o = gifframe.o;
        int j = (gifframe.width / a) * a;
        int k = (gifframe.height / a) * a;
        if(j != gifframe.width)
            j += a;
        if(k != gifframe.height)
            k += a;
        Random random = new Random();
        for(int l = 0; l < k; l += a)
        {
            for(int i1 = 0; i1 < j; i1 += a)
            {
                int j1 = random.nextInt(b);
                int k1 = i1 + a > gifframe.width ? gifframe.width - i1 : a;
                int l1 = l + a > gifframe.height ? gifframe.height - l : a;
                for(int i2 = 0; i2 < l1; i2++)
                {
                    for(int j2 = 0; j2 < k1; j2++)
                    {
                        int k2 = (l + i2) * gifframe.width + i1 + j2;
                        agifframe[j1].n[k2] = gifframe.n[k2];
                    }

                }

            }

        }

        return agifframe;
    }

    private int a;
    private int b;
    private int c;
}
