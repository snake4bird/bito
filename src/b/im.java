// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:18:11
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;


// Referenced classes of package com.gif4j:
//            h

class im
{

    public im(byte abyte0[], byte abyte1[], byte abyte2[], int j)
    {
        a = abyte0;
        b = abyte1;
        c = abyte2;
        e = j;
        d = new byte[32768];
        a();
    }

    private void a()
    {
        byte byte0 = 8;
        byte byte1 = 64;
        int j = byte1 + byte1;
        int ai[] = new int[32768];
        for(int k = 0; k < e; k++)
        {
            int l = a[k] & 0xff;
            int i1 = b[k] & 0xff;
            int j1 = c[k] & 0xff;
            int k1 = l - byte0 / 2;
            int l1 = i1 - byte0 / 2;
            int j2 = j1 - byte0 / 2;
            k1 = k1 * k1 + l1 * l1 + j2 * j2;
            int l2 = 2 * (byte1 - (l << 3));
            int i3 = 2 * (byte1 - (i1 << 3));
            int j3 = 2 * (byte1 - (j1 << 3));
            int k3 = 0;
            int l4 = 0;
            for(int i5 = l2; l4 < 32; i5 += j)
            {
                int l3 = 0;
                int i2 = k1;
                for(int j4 = i3; l3 < 32; j4 += j)
                {
                    int i4 = 0;
                    int k2 = i2;
                    for(int k4 = j3; i4 < 32; k4 += j)
                    {
                        if(k == 0 || ai[k3] > k2)
                        {
                            ai[k3] = k2;
                            d[k3] = (byte)k;
                        }
                        k2 += k4;
                        i4++;
                        k3++;
                    }

                    i2 += j4;
                    l3++;
                }

                k1 += i5;
                l4++;
            }

        }

    }

    public final int a(int j, int k, int l)
    {
        return d[(j << 7 & 0x7c00) + (k << 2 & 0x3e0) + (l >> 3 & 0x1f)] & 0xff;
    }

    byte a[];
    byte b[];
    byte c[];
    byte d[];
    int e;
}
