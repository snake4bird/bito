// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:19:32
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;


// Referenced classes of package com.gif4j:
//            MorphingFilter, GifFrame, GifImage

public class tf extends fm
{

    public tf(boolean flag)
    {
        this(flag, 8, 10);
    }

    public tf(boolean flag, int i)
    {
        this(flag, i, 10);
    }

    public tf(boolean flag, int i, int j)
    {
        c = false;
        c = flag;
        if(i <= 1)
            throw new IllegalArgumentException("frames number should be greater than 1.");
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
        int i = gifframe.width / (2 * a);
        int j = gifframe.height / (2 * a);
        gf agifframe[] = new gf[a];
        byte abyte0[] = new byte[gifframe.width];
        for(int k = 0; k < abyte0.length; k++)
            abyte0[k] = (byte)gifframe.r;

        agifframe[0] = gifframe.f();
        agifframe[0].o = 1;
        agifframe[0].s = b;
        for(int l = 1; l < a; l++)
        {
            int j1 = l * i;
            int k1 = l * j;
            int l1 = gifframe.width - 2 * j1;
            int i2 = gifframe.height - 2 * k1;
            agifframe[l] = gifframe.a(j1, k1, l1, i2, false);
            agifframe[l].b = j1 + gifframe.b;
            agifframe[l].c = k1 + gifframe.c;
            for(int j2 = j; j2 < j + i2; j2++)
                System.arraycopy(abyte0, 0, agifframe[l - 1].n, j2 * agifframe[l - 1].width + i, l1);

            agifframe[l].o = 1;
            agifframe[l].s = b;
        }

        if(c)
        {
            for(int i1 = 0; i1 < a / 2; i1++)
            {
                gf gifframe1 = agifframe[i1];
                agifframe[i1] = agifframe[a - i1 - 1];
                agifframe[a - i1 - 1] = gifframe1;
            }

        }
        agifframe[a - 1].o = gifframe.o;
        agifframe[a - 1].s = gifframe.s;
        return agifframe;
    }

    private int a;
    private int b;
    private boolean c;
}
