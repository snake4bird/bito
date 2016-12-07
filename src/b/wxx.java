// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 17:01:27
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;


class wxx
{

    public wxx(byte abyte0[], byte abyte1[], byte abyte2[])
    {
        a = abyte0;
        b = abyte1;
        c = abyte2;
        e = abyte0.length;
        d = new byte[32768];
        a();
    }

    public wxx(byte abyte0[], byte abyte1[], byte abyte2[], int i)
    {
        a = abyte0;
        b = abyte1;
        c = abyte2;
        e = i;
        d = new byte[32768];
        a();
    }

    private void a()
    {
        byte byte0 = 8;
        byte byte1 = 64;
        int i = byte1 + byte1;
        int ai[] = new int[32768];
        for(int j = 0; j < e; j++)
        {
            int k = a[j] & 0xff;
            int l = b[j] & 0xff;
            int i1 = c[j] & 0xff;
            int j1 = k - byte0 / 2;
            int k1 = l - byte0 / 2;
            int i2 = i1 - byte0 / 2;
            j1 = j1 * j1 + k1 * k1 + i2 * i2;
            int k2 = 2 * (byte1 - (k << 3));
            int l2 = 2 * (byte1 - (l << 3));
            int i3 = 2 * (byte1 - (i1 << 3));
            int j3 = 0;
            int k4 = 0;
            for(int l4 = k2; k4 < 32; l4 += i)
            {
                int k3 = 0;
                int l1 = j1;
                for(int i4 = l2; k3 < 32; i4 += i)
                {
                    int l3 = 0;
                    int j2 = l1;
                    for(int j4 = i3; l3 < 32; j4 += i)
                    {
                        if(j == 0 || ai[j3] > j2)
                        {
                            ai[j3] = j2;
                            d[j3] = (byte)j;
                        }
                        j2 += j4;
                        l3++;
                        j3++;
                    }

                    l1 += i4;
                    k3++;
                }

                j1 += l4;
                k4++;
            }

        }

    }

    public final byte a(int i)
    {
        return d[(i >> 9 & 0x7c00) + (i >> 6 & 0x3e0) + (i >> 3 & 0x1f)];
    }

    byte a[];
    byte b[];
    byte c[];
    byte d[];
    int e;
}
