// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:18:04
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;

import java.util.Random;

// Referenced classes of package com.gif4j:
//            GifFrame, i

class h
{
    private static class i
    {

        private void a()
        {
            byte byte0 = 8;
            byte byte1 = 64;
            int i1 = byte1 + byte1;
            int ai[] = new int[32768];
            for(int j1 = 0; j1 < e; j1++)
            {
                int k1 = a[j1] & 0xff;
                int l1 = b[j1] & 0xff;
                int i2 = c[j1] & 0xff;
                int j2 = k1 - byte0 / 2;
                int k2 = l1 - byte0 / 2;
                int i3 = i2 - byte0 / 2;
                j2 = j2 * j2 + k2 * k2 + i3 * i3;
                int k3 = 2 * (byte1 - (k1 << 3));
                int l3 = 2 * (byte1 - (l1 << 3));
                int i4 = 2 * (byte1 - (i2 << 3));
                int j4 = 0;
                int k5 = 0;
                for(int l5 = k3; k5 < 32; l5 += i1)
                {
                    int k4 = 0;
                    int l2 = j2;
                    for(int i5 = l3; k4 < 32; i5 += i1)
                    {
                        int l4 = 0;
                        int j3 = l2;
                        for(int j5 = i4; l4 < 32; j5 += i1)
                        {
                            if(j1 == 0 || ai[j4] > j3)
                            {
                                ai[j4] = j3;
                                d[j4] = (byte)j1;
                            }
                            j3 += j5;
                            l4++;
                            j4++;
                        }

                        l2 += i5;
                        k4++;
                    }

                    j2 += l5;
                    k5++;
                }

            }

        }

        public final int a(int i1, int j1, int k1)
        {
            return d[(i1 << 7 & 0x7c00) + (j1 << 2 & 0x3e0) + (k1 >> 3 & 0x1f)] & 0xff;
        }

        byte a[];
        byte b[];
        byte c[];
        byte d[];
        int e;

        public i(byte abyte0[], byte abyte1[], byte abyte2[], int i1)
        {
            a = abyte0;
            b = abyte1;
            c = abyte2;
            e = i1;
            d = new byte[32768];
            a();
        }
    }


    public h(byte abyte0[], byte abyte1[], byte abyte2[], byte abyte3[], int i1, gf gifframe)
    {
        g = abyte0;
        h = abyte1;
        i = abyte2;
        l = i1;
        n = gifframe;
        m = gifframe.width;
        j = abyte3;
        a();
    }

    void a()
    {
        a = new int[m + 2];
        b = new int[m + 2];
        c = new int[m + 2];
        d = new int[m + 2];
        e = new int[m + 2];
        f = new int[m + 2];
        Random random = new Random();
        for(int i1 = 0; i1 < m + 2; i1++)
        {
            a[i1] = random.nextInt(3) - 2;
            c[i1] = random.nextInt(3) - 2;
            e[i1] = random.nextInt(3) - 2;
        }

        k = true;
        o = new i(g, h, i, l);
    }

    public void b()
    {
        byte abyte0[] = new byte[m];
        byte abyte1[] = new byte[m];
        for(int i1 = 0; i1 < n.height; i1++)
        {
            System.arraycopy(n.n, i1 * m, abyte1, 0, m);
            a(abyte1, abyte0);
            System.arraycopy(abyte0, 0, n.n, i1 * m, m);
        }

    }

    private void a(byte abyte0[], byte abyte1[])
    {
        for(int i1 = b.length; --i1 >= 0;)
            b[i1] = d[i1] = f[i1] = 0;

        int j1;
        int k1;
        int l1;
        if(k)
        {
            l1 = 0;
            j1 = 0;
            k1 = m;
        } else
        {
            l1 = m - 1;
            j1 = m - 1;
            k1 = -1;
        }
label0:
        do
        {
            int j2;
            int k2;
            int l2;
            int i3;
            int j3;
            int k3;
            int l3;
label1:
            {
label2:
                do
                {
                    int i2;
label3:
                    {
label4:
                        do
                        {
                            do
                            {
                                i2 = abyte0[j1] & 0xff;
                                if(i2 != n.r)
                                    break label3;
                                if(!k)
                                    break;
                                abyte1[l1++] = (byte)l;
                                if(++j1 >= k1)
                                    break label4;
                            } while(true);
                            abyte1[l1--] = (byte)l;
                        } while(--j1 > k1);
                        break label2;
                    }
                    j2 = n.k[i2] & 0xff;
                    k2 = n.l[i2] & 0xff;
                    l2 = n.m[i2] & 0xff;
                    if(j2 < 0)
                        j2 = 0;
                    else
                    if(j2 > 255)
                        j2 = 255;
                    if(k2 < 0)
                        k2 = 0;
                    else
                    if(k2 > 255)
                        k2 = 255;
                    if(l2 < 0)
                        l2 = 0;
                    else
                    if(l2 > 255)
                        l2 = 255;
                    i3 = o.a(j2, k2, l2);
                    j3 = g[i3] & 0xff;
                    k3 = h[i3] & 0xff;
                    l3 = i[i3] & 0xff;
                    int i4 = 0;
                    if(!k)
                        break label1;
                    abyte1[l1++] = (byte)i3;
                    i4 = j2 - j3;
                    if(i4 > 32)
                        i4 = 32;
                    else
                    if(i4 < -32)
                        i4 = -32;
                    a[j1 + 2] += i4 * 7 >> 4;
                    b[j1] += i4 >> 3;
                    b[j1 + 1] += i4 >> 2;
                    b[j1 + 2] += i4 >> 4;
                    i4 = k2 - k3;
                    if(i4 > 32)
                        i4 = 32;
                    else
                    if(i4 < -32)
                        i4 = -32;
                    c[j1 + 2] += i4 * 3 >> 3;
                    d[j1] += i4 * 3 >> 5;
                    d[j1 + 1] += i4 * 3 >> 4;
                    d[j1 + 2] += i4 * 3 >> 6;
                    i4 = l2 - l3;
                    if(i4 > 32)
                        i4 = 32;
                    else
                    if(i4 < -32)
                        i4 = -32;
                    e[j1 + 2] += i4 * 7 >> 4;
                    f[j1] += i4 >> 3;
                    f[j1 + 1] += i4 >> 2;
                    f[j1 + 2] += i4 >> 4;
                } while(++j1 < k1);
                break label0;
            }
            abyte1[l1--] = (byte)i3;
            int j4 = j2 - j3;
            if(j4 > 32)
                j4 = 32;
            else
            if(j4 < -32)
                j4 = -32;
            a[j1] += j4 * 7 >> 4;
            b[j1 + 2] += j4 >> 3;
            b[j1 + 1] += j4 >> 2;
            b[j1] += j4 >> 4;
            j4 = k2 - k3;
            if(j4 > 32)
                j4 = 32;
            else
            if(j4 < -32)
                j4 = -32;
            c[j1] += j4 * 3 >> 3;
            d[j1 + 2] += j4 * 3 >> 5;
            d[j1 + 1] += j4 * 3 >> 4;
            d[j1] += j4 * 3 >> 6;
            j4 = l2 - l3;
            if(j4 > 32)
                j4 = 32;
            else
            if(j4 < -32)
                j4 = -32;
            e[j1] += j4 * 7 >> 4;
            f[j1 + 2] += j4 >> 3;
            f[j1 + 1] += j4 >> 2;
            f[j1] += j4 >> 4;
        } while(--j1 > k1);
        int ai[] = a;
        a = b;
        b = ai;
        ai = c;
        c = d;
        d = ai;
        ai = e;
        e = f;
        f = ai;
        k = !k;
    }

    int a[];
    int b[];
    int c[];
    int d[];
    int e[];
    int f[];
    byte g[];
    byte h[];
    byte i[];
    byte j[];
    boolean k;
    int l;
    int m;
    gf n;
    i o;
}
