// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:18:47
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package d.g;

import java.io.DataOutput;
import java.io.IOException;

class m
{

    m()
    {
        a = -1;
    }

    m(int i)
    {
        a = -1;
        a(i);
    }

    void a(int i)
    {
        if(i < 0 || i > 65535)
            i = 0;
        a = i;
    }

    void a(DataOutput dataoutput)
        throws IOException
    {
        if(a > -1)
        {
            dataoutput.writeLong(0x21ff0b4e45545343L);
            dataoutput.writeLong(0x415045322e300301L);
            dataoutput.writeLong(0x21fe096769L | (long)(a & 0xff) << 56 | (long)(a >> 8 & 0xff) << 48);
            dataoutput.writeLong(0x66346a65766c5000L);
        } else
        {
            dataoutput.writeLong(0x21fe0c676966346aL);
            dataoutput.writeLong(0x65766c5020202000L);
        }
    }

    int a;
}
