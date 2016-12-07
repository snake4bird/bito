// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2013/8/15 8:16:35
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package d.g;


class c
{

    c(String s)
    {
        a = null;
        if(s == null)
        {
            throw new IllegalArgumentException("Comment String is null");
        } else
        {
            a = s;
            return;
        }
    }

    byte[] a()
    {
        int i = a.length();
        byte abyte0[] = new byte[i];
        for(int j = 0; j < i; j++)
            abyte0[j] = (byte)a.charAt(j);

        int k = i / 255;
        int l = i % 255;
        byte abyte1[] = new byte[i + k + (l <= 0 ? 3 : 4)];
        abyte1[0] = 33;
        abyte1[1] = -2;
        int i1 = 2;
        int j1 = 0;
        for(int k1 = 0; k1 < k; k1++)
        {
            abyte1[i1] = -1;
            i1++;
            System.arraycopy(abyte0, j1, abyte1, i1, 255);
            i1 += 255;
            j1 += 255;
        }

        if(l > 0)
        {
            abyte1[i1] = (byte)l;
            i1++;
            System.arraycopy(abyte0, j1, abyte1, i1, l);
            i1 += l;
        }
        abyte1[i1] = 0;
        return abyte1;
    }

    String a;
}
