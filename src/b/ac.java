// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov Date: 2013/8/15
// 8:16:18
// Home Page: http://members.fortunecity.com/neshkov/dj.html
// http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
package b;

import java.io.DataOutput;
import java.io.IOException;

// Referenced classes of package com.gif4j:
// GifEncoder
class ac
{
	public ac(DataOutput dataoutput)
	{
		d = dataoutput;
		b = new int[5003];
		c = new int[5003];
		a = new byte[256];
	}

	public void a(byte abyte0[], int i) throws IOException
	{
		if (abyte0.length == 0)
			return;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = i;
		int j1 = (1 << i1) - 1;
		int k1 = 1 << i - 1;
		int l1 = k1 + 1;
		int i2 = k1 + 2;
		for(int j2 = 5003; --j2 >= 0;)
			b[j2] = -1;
		j |= k1 << k;
		k += i1;
		int k2 = 0;
		int l2 = abyte0[k2++] & 0xff;
		int i3 = abyte0.length;
		do
		{
			if (k2 >= i3)
				break;
			int j3 = abyte0[k2++] & 0xff;
			int k3 = (j3 << 12) + l2;
			int l3 = j3 << 4 ^ l2;
			if (b[l3] == k3)
			{
				l2 = c[l3];
				continue;
			}
			if (b[l3] != -1)
			{
				int i4 = 5003 - l3;
				if (l3 == 0)
					i4 = 1;
				do
					if ((l3 -= i4) < 0)
						l3 += 5003;
				while(b[l3] != k3 && b[l3] != -1);
				if (b[l3] == k3)
				{
					l2 = c[l3];
					continue;
				}
			}
			j |= l2 << k;
			for(k += i1; k >= 8; k -= 8)
			{
				a[l++] = (byte)j;
				if (l >= 254)
				{
					d.write(l);
					d.write(a, 0, l);
					l = 0;
				}
				j >>= 8;
			}
			if (i2 > j1)
				if (++i1 == 12)
					j1 = 4096;
				else
					j1 = (1 << i1) - 1;
			l2 = j3;
			if (i2 < 4096)
			{
				c[l3] = i2++;
				b[l3] = k3;
			}
			else
			{
				for(int j4 = 5003; --j4 >= 0;)
					b[j4] = -1;
				i2 = k1 + 2;
				j |= k1 << k;
				for(k += i1; k >= 8; k -= 8)
				{
					a[l++] = (byte)j;
					if (l >= 254)
					{
						d.write(l);
						d.write(a, 0, l);
						l = 0;
					}
					j >>= 8;
				}
				i1 = i;
				j1 = (1 << i1) - 1;
			}
		}
		while(true);
		j |= l2 << k;
		for(k += i1; k >= 8; k -= 8)
		{
			a[l++] = (byte)j;
			if (l >= 254)
			{
				d.write(l);
				d.write(a, 0, l);
				l = 0;
			}
			j >>= 8;
		}
		if (i2 > j1)
			if (++i1 == 12)
				j1 = 4096;
			else
				j1 = (1 << i1) - 1;
		j |= l1 << k;
		for(k += i1; k >= 8; k -= 8)
		{
			a[l++] = (byte)j;
			if (l >= 254)
			{
				d.write(l);
				d.write(a, 0, l);
				l = 0;
			}
			j >>= 8;
		}
		for(; k > 0; k -= 8)
		{
			a[l++] = (byte)j;
			if (l >= 254)
			{
				d.write(l);
				d.write(a, 0, l);
				l = 0;
			}
			j >>= 8;
		}
		if (l > 0)
		{
			d.write(l);
			d.write(a, 0, l);
			l = 0;
		}
	}

	private void a()
	{
		c = null;
		b = null;
		a = null;
	}

	static void a(ac a1)
	{
		a1.a();
	}

	private byte a[];
	private int b[];
	private int c[];
	private DataOutput d;
}
