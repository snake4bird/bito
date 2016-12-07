// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:17:12
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package b;

import java.io.IOException;

// Referenced classes of package com.gif4j:
//            GifDecoder, GifFrame, d

class f
{

    f()
    {
    }

    public void a(dee d1, gf gifframe, int i1)
        throws IOException
    {
        q = d1;
        x = gifframe;
        w = gifframe.h;
        a = i1;
        n = 1;
        o = 0;
        f = a + 1;
        j = 1 << f;
        g = 1 << a;
        h = g + 1;
        i = k = h + 1;
        d = -1;
        b = e = 0;
        c = 0;
        p = v[f - 1];
        s = new int[4096];
        t = new int[4096];
        u = new int[4096];
        r = new byte[256];
        l = gifframe.width;
        m = gifframe.height;
        b();
    }

    int a()
        throws IOException
    {
        int i1;
        if(e == 0)
        {
            if(c >= b)
            {
                b = q.read();
                if(b == -1)
                    throw new IOException("Invalid input stream: more data is expected!");
                r[0] = (byte)b;
                b = q.read(r, 1, b);
                if(b == -1)
                    throw new IOException("Invalid input stream: more data is expected!");
                c = 0;
                if(b == 0)
                    return h;
            }
            c++;
            d = r[c] & 0xff;
            e = 8;
            i1 = d;
        } else
        {
            int j1 = e - 8;
            if(j1 < 0)
                i1 = d >> 0 - j1;
            else
                i1 = d << j1;
        }
        for(; f > e; e += 8)
        {
            if(c >= b)
            {
                b = q.read();
                if(b == -1)
                    throw new IOException("Invalid input stream: more data is expected!");
                r[0] = (byte)b;
                b = q.read(r, 1, b);
                if(b == -1)
                    throw new IOException("Invalid input stream: more data is expected!");
                c = 0;
                if(b == 0)
                    return h;
            }
            c++;
            d = r[c] & 0xff;
            i1 += d << e;
        }

        e -= f;
        return i1 & p;
    }

    void b()
        throws IOException
    {
        int j1 = 0;
        int k1 = 0;
        byte abyte0[] = new byte[l];
        int l1 = 0;
        int i2 = 0;
        int j2;
        while((j2 = a()) != h) 
            if(j2 == g)
            {
                f = a + 1;
                p = v[a];
                k = i;
                j = 1 << f;
                while((j2 = a()) == g) ;
                if(j2 != h)
                {
                    j1 = k1 = j2;
                    abyte0[i2] = (byte)j2;
                    if(++i2 == l)
                    {
                        System.arraycopy(abyte0, 0, x.n, o * l, abyte0.length);
                        if(w)
                        {
                            if(n == 1)
                                o += 8;
                            else
                            if(n == 2)
                                o += 8;
                            else
                            if(n == 3)
                                o += 4;
                            else
                            if(n == 4)
                                o += 2;
                            if(o >= m)
                            {
                                n++;
                                if(n == 2)
                                    o = 4;
                                else
                                if(n == 3)
                                    o = 2;
                                else
                                if(n == 4)
                                    o = 1;
                                else
                                if(n == 5)
                                    o = 0;
                            }
                            if(o >= m)
                                o = 0;
                        } else
                        {
                            o++;
                        }
                        i2 = 0;
                    }
                }
            } else
            {
                int i1 = j2;
                if(i1 >= k)
                {
                    i1 = j1;
                    s[l1] = k1;
                    l1++;
                }
                for(; i1 >= i; i1 = u[i1])
                {
                    s[l1] = t[i1];
                    l1++;
                }

                s[l1] = i1;
                l1++;
                if(k < j)
                {
                    k1 = i1;
                    t[k] = k1;
                    u[k] = j1;
                    k++;
                    j1 = j2;
                }
                if(k >= j && f < 12)
                {
                    p = v[f];
                    f++;
                    j = j + j;
                }
                while(l1 > 0) 
                {
                    l1--;
                    abyte0[i2] = (byte)s[l1];
                    if(++i2 == l)
                    {
                        System.arraycopy(abyte0, 0, x.n, o * l, abyte0.length);
                        if(w)
                        {
                            if(n == 1)
                                o += 8;
                            else
                            if(n == 2)
                                o += 8;
                            else
                            if(n == 3)
                                o += 4;
                            else
                            if(n == 4)
                                o += 2;
                            if(o >= m)
                            {
                                n++;
                                if(n == 2)
                                    o = 4;
                                else
                                if(n == 3)
                                    o = 2;
                                else
                                if(n == 4)
                                    o = 1;
                                else
                                if(n == 5)
                                    o = 0;
                            }
                            if(o >= m)
                                o = 0;
                        } else
                        {
                            o++;
                        }
                        i2 = 0;
                    }
                }
            }
        if(i2 != 0 && o < m)
        {
            System.arraycopy(abyte0, 0, x.n, o * l, abyte0.length);
            if(w)
            {
                if(n == 1)
                    o += 8;
                else
                if(n == 2)
                    o += 8;
                else
                if(n == 3)
                    o += 4;
                else
                if(n == 4)
                    o += 2;
                if(o >= m)
                {
                    n++;
                    if(n == 2)
                        o = 4;
                    else
                    if(n == 3)
                        o = 2;
                    else
                    if(n == 4)
                        o = 1;
                    else
                    if(n == 5)
                        o = 0;
                }
                if(o >= m)
                    o = 0;
            } else
            {
                o++;
            }
        }
    }

    int a;
    int b;
    int c;
    int d;
    int e;
    int f;
    int g;
    int h;
    int i;
    int j;
    int k;
    int l;
    int m;
    int n;
    int o;
    int p;
    dee q;
    byte r[];
    int s[];
    int t[];
    int u[];
    static final int v[] = {
        1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 
        2047, 4095
    };
    boolean w;
    gf x;

}
