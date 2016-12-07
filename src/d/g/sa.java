// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:16:41
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package d.g;


// Referenced classes of package com.gif4j:
//            MorphingFilter, GifFrame, GifImage

public class sa extends fm
{

    public sa()
    {
        this(8, 10);
    }

    public sa(int i)
    {
        this(i, 10);
    }

    public sa(int i, int j)
    {
        if(i <= 1)
            throw new IllegalArgumentException("cell side size (in pixels) should be greater than 1.");
        a = i;
        if(j < 1)
        {
            throw new IllegalArgumentException("delay between frames (in 1/100 sec) should be greater than 0.");
        } else
        {
            b = j;
            return;
        }
    }

    gf[] a(gi gifimage, gf gifframe)
    {
        gf agifframe[] = new gf[4];
        agifframe[0] = gifframe.a(true);
        agifframe[0].s = b;
        agifframe[0].o = 1;
        agifframe[1] = agifframe[0].f();
        agifframe[2] = agifframe[0].f();
        agifframe[3] = agifframe[0].f();
        agifframe[3].s = gifframe.s;
        agifframe[3].o = gifframe.o;
        for(int i = 0; i < gifframe.height; i++)
        {
            for(int j = 0; j < gifframe.width; j++)
            {
                int k = (j / a) % 2;
                int l = (i / a) % 2;
                int i1 = i * gifframe.width + j;
                agifframe[l << 1 | k].n[i1] = gifframe.n[i1];
            }

        }

        return agifframe;
    }

    private int a;
    private int b;
}
