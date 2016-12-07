// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:19:11
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package d.g;


// Referenced classes of package com.gif4j:
//            MorphingFilter, GifFrame, GifImage

public class rf extends fm
{

    public rf()
    {
        this(2, 10);
    }

    public rf(int i)
    {
        this(i, 10);
    }

    public rf(int i, int j)
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
        if(b % 2 == 1)
            b++;
        gf agifframe[] = new gf[b * 4];
        int i = gifframe.width / 2 + gifframe.width % 2;
        int j = gifframe.height / 2 + gifframe.height % 2;
        for(int k = 0; k < b; k++)
        {
            agifframe[k] = gifframe.a(0, 0, i, j, true);
            agifframe[k].b = gifframe.b;
            agifframe[k].c = gifframe.c;
            agifframe[k].o = 1;
            agifframe[k].s = a;
        }

        for(int l = b; l < 2 * b; l++)
        {
            agifframe[l] = gifframe.a(gifframe.width / 2, 0, i, j, true);
            agifframe[l].b = gifframe.width / 2 + gifframe.b;
            agifframe[l].c = gifframe.c;
            agifframe[l].o = 1;
            agifframe[l].s = a;
        }

        for(int i1 = 2 * b; i1 < 3 * b; i1++)
        {
            agifframe[i1] = gifframe.a(gifframe.width / 2, gifframe.height / 2, i, j, true);
            agifframe[i1].b = gifframe.width / 2 + gifframe.b;
            agifframe[i1].c = gifframe.height / 2 + gifframe.c;
            agifframe[i1].o = 1;
            agifframe[i1].s = a;
        }

        for(int j1 = 3 * b; j1 < 4 * b; j1++)
        {
            agifframe[j1] = gifframe.a(0, gifframe.height / 2, i, j, true);
            agifframe[j1].b = gifframe.b;
            agifframe[j1].c = gifframe.height / 2 + gifframe.c;
            agifframe[j1].o = 1;
            agifframe[j1].s = a;
        }

        double d = (double)i / (double)j;
        for(int k1 = 0; k1 < i; k1++)
        {
            double d1 = (double)(i - k1) / d;
            for(int j2 = 0; j2 < b / 2; j2++)
            {
                int l2 = j - (int)((d1 / (double)(b / 2)) * (double)j2);
                for(int j3 = j - (int)((d1 / (double)(b / 2)) * (double)(j2 + 1)); j3 < l2; j3++)
                {
                    agifframe[j2].n[j3 * i + k1] = gifframe.n[j3 * gifframe.width + k1];
                    int l3 = gifframe.width - k1 - 1;
                    agifframe[2 * b - j2 - 1].n[(j3 * i + l3) - gifframe.width / 2] = gifframe.n[j3 * gifframe.width + l3];
                    int j4 = gifframe.height - j3 - 1;
                    agifframe[4 * b - j2 - 1].n[(j4 - gifframe.height / 2) * i + k1] = gifframe.n[j4 * gifframe.width + k1];
                    agifframe[2 * b + j2].n[((j4 - gifframe.height / 2) * i + l3) - gifframe.width / 2] = gifframe.n[j4 * gifframe.width + l3];
                }

            }

        }

        for(int l1 = 0; l1 < j; l1++)
        {
            double d2 = (double)(j - l1) * d;
            for(int k2 = 0; k2 < b / 2; k2++)
            {
                int i3 = (int)(((d2 / (double)(b / 2)) * (double)(k2 + 1) + (double)i) - d2);
                if(k2 + 1 == b / 2)
                    i3 = i;
                for(int k3 = (int)(((d2 / (double)(b / 2)) * (double)k2 + (double)i) - d2); k3 < i3; k3++)
                {
                    agifframe[k2 + b / 2].n[l1 * i + k3] = gifframe.n[l1 * gifframe.width + k3];
                    int i4 = gifframe.width - k3 - 1;
                    agifframe[2 * b - k2 - b / 2 - 1].n[(l1 * i + i4) - gifframe.width / 2] = gifframe.n[l1 * gifframe.width + i4];
                    int k4 = gifframe.height - l1 - 1;
                    agifframe[4 * b - k2 - b / 2 - 1].n[(k4 - gifframe.height / 2) * i + k3] = gifframe.n[k4 * gifframe.width + k3];
                    agifframe[k2 + b / 2 + 2 * b].n[((k4 - gifframe.height / 2) * i + i4) - gifframe.width / 2] = gifframe.n[k4 * gifframe.width + i4];
                }

            }

        }

        gf agifframe1[] = new gf[4 * b];
        for(int i2 = 0; i2 < 4 * b; i2++)
            agifframe1[i2] = agifframe[(i2 + b) % (4 * b)];

        agifframe1[4 * b - 1].s = gifframe.s;
        return agifframe1;
    }

    private int a;
    private int b;
}
