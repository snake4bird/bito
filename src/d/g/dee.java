// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:16:55
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package d.g;

import java.io.IOException;
import java.io.InputStream;

// Referenced classes of package com.gif4j:
//            GifDecoder

class dee extends InputStream
{

    public dee(InputStream inputstream, int i)
    {
        b = inputstream;
        if(i > 0)
        {
            c = new byte[i];
            d = i;
        } else
        {
            throw new IllegalArgumentException();
        }
    }

    public void close()
        throws IOException
    {
        c = null;
        if(b != null)
        {
            b.close();
            b = null;
        }
    }

    public int available()
        throws IOException
    {
        if(c == null)
            throw new IOException();
        else
            return (c.length - d) + b.available();
    }

    public int read()
        throws IOException
    {
        if(c == null)
            throw new IOException();
        a++;
        if(d < c.length)
            return c[d++] & 0xff;
        else
            return b.read();
    }

    public int read(byte abyte0[], int i, int j)
        throws IOException
    {
        int l = j;
        int k = a(abyte0, i, j);
        do
        {
            if(k == -1)
                return -1;
            a += k;
            if(k == l)
                return j;
            l -= k;
            i += k;
            k = a(abyte0, i, l);
        } while(true);
    }

    private int a(byte abyte0[], int i, int j)
        throws IOException
    {
        if(c == null)
            throw new IOException();
        if(i < 0 || i > abyte0.length || j < 0 || j > abyte0.length - i)
            throw new ArrayIndexOutOfBoundsException();
        int k = 0;
        int l = i;
        int i1 = c.length - d;
        if(i1 > 0)
        {
            k = i1 < j ? i1 : j;
            System.arraycopy(c, d, abyte0, l, k);
            l += k;
            d += k;
        }
        if(k == j)
            return j;
        int j1 = b.read(abyte0, l, j - k);
        if(j1 > 0)
            return j1 + k;
        if(k == 0)
            return j1;
        else
            return k;
    }

    public void a(byte abyte0[])
        throws IOException
    {
        int i = abyte0.length;
        if(i > d)
        {
            throw new IOException();
        } else
        {
            a -= i;
            d -= i;
            System.arraycopy(abyte0, 0, c, d, i);
            return;
        }
    }

    int a;
    InputStream b;
    protected byte c[];
    protected int d;
}
