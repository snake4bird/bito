// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:19:17
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package d.g;


// Referenced classes of package com.gif4j:
//            MorphingFilter, GifFrame, GifImage

public class sf extends fm
{

    public sf(int i)
    {
        this(i, 5, 6);
    }

    public sf(int i, int j)
    {
        this(i, j, 10);
    }

    public sf(int i, int j, int k)
    {
        if(i < 0 || i > 4)
            throw new IllegalArgumentException("Unknown layMethod");
        if(j < 2)
            throw new IllegalArgumentException("devide factor must be greater than 1");
        if(k < 0)
        {
            throw new IllegalArgumentException("delay between frames must be greater than 1");
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
        int i = b * b;
        gf agifframe[] = new gf[i];
        int j = gifframe.width / b;
        int k = gifframe.width % b;
        int l = gifframe.height / b;
        int i1 = gifframe.height % b;
        int j1 = 0;
        int k1 = 0;
        for(; j1 < b; j1++)
        {
            k1 += j1 != 0 ? agifframe[j1 * b - 1].height : 0;
            int l1 = 0;
            int k2 = 0;
            for(; l1 < b; l1++)
            {
                int i4 = j1 * b + l1;
                gf gifframe1 = new gf();
                gifframe1.width = j + (l1 >= k ? 0 : 1);
                gifframe1.height = l + (j1 >= i1 ? 0 : 1);
                gifframe1.n = new byte[gifframe1.width * gifframe1.height];
                gifframe1.c = k1;
                gifframe1.b = k2;
                k2 += gifframe1.width;
                gifframe1.s = c;
                gifframe1.o = 1;
                gifframe1.x = true;
                gifframe1.i = gifframe.i;
                gifframe1.j = gifframe.j;
                if(gifframe.f)
                {
                    gifframe1.k = new byte[gifframe.k.length];
                    gifframe1.l = new byte[gifframe.l.length];
                    gifframe1.m = new byte[gifframe.m.length];
                    System.arraycopy(gifframe.k, 0, gifframe1.k, 0, gifframe.k.length);
                    System.arraycopy(gifframe.l, 0, gifframe1.l, 0, gifframe.l.length);
                    System.arraycopy(gifframe.m, 0, gifframe1.m, 0, gifframe.m.length);
                } else
                {
                    gifframe1.f = false;
                }
                gifframe1.q = gifframe.q;
                gifframe1.r = gifframe.r;
                int l7 = gifframe1.c + gifframe1.height;
                int i8 = gifframe1.b + gifframe1.width;
                int j8 = 0;
                for(int k8 = gifframe1.c; k8 < l7; k8++)
                {
                    int l8 = 0;
                    for(int i9 = gifframe1.b; i9 < i8; i9++)
                    {
                        gifframe1.n[j8 * gifframe1.width + l8] = gifframe.n[k8 * gifframe.width + i9];
                        l8++;
                    }

                    j8++;
                }

                gifframe1.c += gifframe.c;
                gifframe1.b += gifframe.b;
                agifframe[i4] = gifframe1;
            }

        }

        gf agifframe1[] = new gf[i];
        int ai[] = new int[i];
        int i2 = 0;
        switch(a)
        {
        case 0: // '\0'
            for(int l2 = 1; l2 <= (b + 1) / 2; l2++)
            {
                for(int j4 = l2; j4 <= (b - l2) + 1; j4++)
                    ai[i2++] = ((j4 - 1) * b + l2) - 1;

                for(int k4 = l2 + 1; k4 <= (b - l2) + 1; k4++)
                    ai[i2++] = ((b - l2) * b + k4) - 1;

                for(int l4 = b - l2; l4 >= l2; l4--)
                    ai[i2++] = ((l4 - 1) * b + b) - l2;

                for(int i5 = b - l2; i5 >= l2 + 1; i5--)
                    ai[i2++] = ((l2 - 1) * b + i5) - 1;

            }

            for(i2 = 0; i2 < agifframe1.length; i2++)
                agifframe1[i2] = agifframe[ai[i - i2 - 1]];

            agifframe1[i - 1].s = gifframe.s;
            agifframe1[i - 1].o = gifframe.o;
            return agifframe1;

        case 1: // '\001'
            for(int i3 = 1; i3 <= (b + 1) / 2; i3++)
            {
                for(int j5 = i3; j5 <= (b - i3) + 1; j5++)
                    ai[i2++] = ((j5 - 1) * b + i3) - 1;

                for(int k5 = i3 + 1; k5 <= (b - i3) + 1; k5++)
                    ai[i2++] = ((b - i3) * b + k5) - 1;

                for(int l5 = b - i3; l5 >= i3; l5--)
                    ai[i2++] = ((l5 - 1) * b + b) - i3;

                for(int i6 = b - i3; i6 >= i3 + 1; i6--)
                    ai[i2++] = ((i3 - 1) * b + i6) - 1;

            }

            for(i2 = 0; i2 < agifframe1.length; i2++)
                agifframe1[i2] = agifframe[ai[i2]];

            agifframe1[i - 1].s = gifframe.s;
            agifframe1[i - 1].o = gifframe.o;
            return agifframe1;

        case 2: // '\002'
            for(int j3 = 0; j3 < b; j3++)
            {
                if(j3 % 2 == 1)
                {
                    for(int j6 = 0; j6 <= j3; j6++)
                        ai[i2++] = j3 + j6 * (b - 1);

                    continue;
                }
                for(int k6 = j3; k6 >= 0; k6--)
                    ai[i2++] = j3 + k6 * (b - 1);

            }

            for(int k3 = 2; k3 <= b; k3++)
            {
                if(k3 % 2 != b % 2)
                {
                    for(int l6 = 0; l6 <= b - k3; l6++)
                        ai[i2++] = (k3 * b - 1) + l6 * (b - 1);

                    continue;
                }
                for(int i7 = b - k3; i7 >= 0; i7--)
                    ai[i2++] = (k3 * b - 1) + i7 * (b - 1);

            }

            for(i2 = 0; i2 < agifframe1.length; i2++)
                agifframe1[i2] = agifframe[ai[i2]];

            agifframe1[i - 1].s = gifframe.s;
            agifframe1[i - 1].o = gifframe.o;
            return agifframe1;

        case 4: // '\004'
            for(int l3 = 0; l3 < b; l3++)
            {
                if(l3 % 2 == 0)
                {
                    for(int j7 = 0; j7 < b; j7++)
                        ai[i2++] = l3 * b + j7;

                    continue;
                }
                for(int k7 = b - 1; k7 >= 0; k7--)
                    ai[i2++] = l3 * b + k7;

            }

            for(int j2 = 0; j2 < agifframe1.length; j2++)
                agifframe1[j2] = agifframe[ai[j2]];

            agifframe1[i - 1].s = gifframe.s;
            agifframe1[i - 1].o = gifframe.o;
            return agifframe1;

        case 3: // '\003'
            agifframe[i - 1].s = gifframe.s;
            agifframe[i - 1].o = gifframe.o;
            break;
        }
        return agifframe;
    }

    public static final int LAY_METHOD_SPIRAL_FROM_CENTER = 0;
    public static final int LAY_METHOD_SPIRAL_FROM_CORNER = 1;
    public static final int LAY_METHOD_ZIG_ZAG = 2;
    public static final int LAY_METHOD_LEFT_RIGHT_DIRECT = 3;
    public static final int LAY_METHOD_LEFT_RIGHT_INVERSE = 4;
    int a;
    int b;
    int c;
}
