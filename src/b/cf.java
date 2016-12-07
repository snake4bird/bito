// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:16:48
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;


// Referenced classes of package com.gif4j:
//            MorphingFilter, GifFrame, GifImage

public class cf extends fm
{

    public cf(int i)
    {
        this(i, 8, 10);
    }

    public cf(int i, int j)
    {
        this(i, j, 10);
    }

    public cf(int i, int j, int k)
    {
        if(j <= 1)
            throw new IllegalArgumentException("frames number should be greater than 1.");
        a = j;
        if(k < 1)
            throw new IllegalArgumentException("delay beetwen frames (in 1/100 sec) should be greater than 0.");
        if(i < 0 || i > 7)
        {
            throw new IllegalArgumentException("unknown moveFromTo parameter.");
        } else
        {
            b = k;
            c = i;
            return;
        }
    }

    gf[] a(gi gifimage, gf gifframe)
    {
        gf agifframe[] = new gf[a];
        byte abyte0[] = new byte[gifframe.width <= gifframe.height ? gifframe.height : gifframe.width];
        for(int i = 0; i < abyte0.length; i++)
            abyte0[i] = (byte)gifframe.r;

        switch(c)
        {
        case 0: // '\0'
            int j = gifframe.width / (2 * a);
            agifframe[0] = gifframe.f();
            agifframe[0].o = 1;
            agifframe[0].s = b;
            for(int j2 = 1; j2 < a; j2++)
            {
                int j4 = j2 * j;
                int l6 = 0;
                int l8 = gifframe.width - 2 * j4;
                int l10 = gifframe.height;
                agifframe[j2] = gifframe.a(j4, l6, l8, l10, false);
                agifframe[j2].b = j4 + gifframe.b;
                agifframe[j2].c = l6 + gifframe.c;
                for(int l11 = 0; l11 < l10; l11++)
                    System.arraycopy(abyte0, 0, agifframe[j2 - 1].n, l11 * agifframe[j2 - 1].width + j, l8);

                agifframe[j2].o = 1;
                agifframe[j2].s = b;
            }

            agifframe[a - 1].o = gifframe.o;
            agifframe[a - 1].s = gifframe.s;
            return agifframe;

        case 1: // '\001'
            int k = gifframe.width / (2 * a);
            agifframe[0] = gifframe.f();
            agifframe[0].o = 1;
            agifframe[0].s = b;
            for(int k2 = 1; k2 < a; k2++)
            {
                int k4 = k2 * k;
                int i7 = 0;
                int i9 = gifframe.width - 2 * k4;
                int i11 = gifframe.height;
                agifframe[k2] = gifframe.a(k4, i7, i9, i11, false);
                agifframe[k2].b = k4 + gifframe.b;
                agifframe[k2].c = i7 + gifframe.c;
                for(int i12 = 0; i12 < i11; i12++)
                    System.arraycopy(abyte0, 0, agifframe[k2 - 1].n, i12 * agifframe[k2 - 1].width + k, i9);

                agifframe[k2].o = 1;
                agifframe[k2].s = b;
            }

            gf agifframe1[] = new gf[a];
            for(int l4 = 0; l4 < a; l4++)
                agifframe1[l4] = agifframe[a - l4 - 1];

            agifframe1[a - 1].o = gifframe.o;
            agifframe1[a - 1].s = gifframe.s;
            return agifframe1;

        case 2: // '\002'
            int l = gifframe.width / a;
            int l2 = gifframe.width % a;
            int i5 = 0;
            for(int j7 = 0; j7 < a; j7++)
            {
                int j9 = l + (l2 <= 0 ? 0 : 1);
                agifframe[j7] = gifframe.a(i5, 0, j9, gifframe.height, false);
                agifframe[j7].b = i5 + gifframe.b;
                agifframe[j7].c = gifframe.c;
                i5 += j9;
                l2--;
                agifframe[j7].o = 1;
                agifframe[j7].s = b;
            }

            agifframe[a - 1].o = gifframe.o;
            agifframe[a - 1].s = gifframe.s;
            return agifframe;

        case 3: // '\003'
            int i1 = gifframe.width / a;
            int i3 = gifframe.width % a;
            int j5 = gifframe.width;
            for(int k7 = 0; k7 < a; k7++)
            {
                int k9 = i1 + (i3 <= 0 ? 0 : 1);
                j5 -= k9;
                agifframe[k7] = gifframe.a(j5, 0, k9, gifframe.height, false);
                agifframe[k7].b = j5 + gifframe.b;
                agifframe[k7].c = gifframe.c;
                i3--;
                agifframe[k7].o = 1;
                agifframe[k7].s = b;
            }

            agifframe[a - 1].o = gifframe.o;
            agifframe[a - 1].s = gifframe.s;
            return agifframe;

        case 4: // '\004'
            int j1 = gifframe.height / (2 * a);
            agifframe[0] = gifframe.f();
            agifframe[0].o = 1;
            agifframe[0].s = b;
            for(int j3 = 1; j3 < a; j3++)
            {
                int k5 = 0;
                int l7 = j3 * j1;
                int l9 = gifframe.width;
                int j11 = gifframe.height - 2 * l7;
                agifframe[j3] = gifframe.a(k5, l7, l9, j11, false);
                agifframe[j3].b = k5 + gifframe.b;
                agifframe[j3].c = l7 + gifframe.c;
                for(int j12 = j1; j12 < j1 + j11; j12++)
                    System.arraycopy(abyte0, 0, agifframe[j3 - 1].n, j12 * agifframe[j3 - 1].width, l9);

                agifframe[j3].o = 1;
                agifframe[j3].s = b;
            }

            agifframe[a - 1].o = gifframe.o;
            agifframe[a - 1].s = gifframe.s;
            return agifframe;

        case 5: // '\005'
            int k1 = gifframe.height / (2 * a);
            agifframe[0] = gifframe.f();
            agifframe[0].o = 1;
            agifframe[0].s = b;
            for(int k3 = 1; k3 < a; k3++)
            {
                int l5 = 0;
                int i8 = k3 * k1;
                int i10 = gifframe.width;
                int k11 = gifframe.height - 2 * i8;
                agifframe[k3] = gifframe.a(l5, i8, i10, k11, false);
                agifframe[k3].b = l5 + gifframe.b;
                agifframe[k3].c = i8 + gifframe.c;
                for(int k12 = k1; k12 < k1 + k11; k12++)
                    System.arraycopy(abyte0, 0, agifframe[k3 - 1].n, k12 * agifframe[k3 - 1].width, i10);

                agifframe[k3].o = 1;
                agifframe[k3].s = b;
            }

            gf agifframe2[] = new gf[a];
            for(int i6 = 0; i6 < a; i6++)
                agifframe2[i6] = agifframe[a - i6 - 1];

            agifframe2[a - 1].o = gifframe.o;
            agifframe2[a - 1].s = gifframe.s;
            return agifframe2;

        case 6: // '\006'
            int l1 = gifframe.height / a;
            int l3 = gifframe.height % a;
            int j6 = 0;
            for(int j8 = 0; j8 < a; j8++)
            {
                int j10 = l1 + (l3 <= 0 ? 0 : 1);
                agifframe[j8] = gifframe.a(0, j6, gifframe.width, j10, false);
                agifframe[j8].b = gifframe.b;
                agifframe[j8].c = gifframe.c + j6;
                j6 += j10;
                l3--;
                agifframe[j8].o = 1;
                agifframe[j8].s = b;
            }

            agifframe[a - 1].o = gifframe.o;
            agifframe[a - 1].s = gifframe.s;
            return agifframe;

        case 7: // '\007'
            int i2 = gifframe.height / a;
            int i4 = gifframe.height % a;
            int k6 = gifframe.height;
            for(int k8 = 0; k8 < a; k8++)
            {
                int k10 = i2 + (i4 <= 0 ? 0 : 1);
                k6 -= k10;
                agifframe[k8] = gifframe.a(0, k6, gifframe.width, k10, false);
                agifframe[k8].b = gifframe.b;
                agifframe[k8].c = gifframe.c + k6;
                i4--;
                agifframe[k8].o = 1;
                agifframe[k8].s = b;
            }

            agifframe[a - 1].o = gifframe.o;
            agifframe[a - 1].s = gifframe.s;
            return agifframe;
        }
        return agifframe;
    }

    public static final int MOVE_FROM_LEFT_RIGHT_TO_CENTER = 0;
    public static final int MOVE_FROM_CENTER_TO_LEFT_RIGHT = 1;
    public static final int MOVE_FROM_LEFT_TO_RIGHT = 2;
    public static final int MOVE_FROM_RIGHT_TO_LEFT = 3;
    public static final int MOVE_FROM_TOP_BOTTOM_TO_MIDDLE = 4;
    public static final int MOVE_FROM_MIDDLE_TO_TOP_BOTTOM = 5;
    public static final int MOVE_FROM_TOP_TO_BOTTOM = 6;
    public static final int MOVE_FROM_BOTTOM_TO_TOP = 7;
    private int a;
    private int b;
    private int c;
}
