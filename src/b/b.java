package b;

public class b
{
	private static char encode(long d)
	{
		d &= 0x3f;
		if (d < 26)
			return (char)(d + 'A');
		else if (d < 52)
			return (char)(d + 'a' - 26);
		else if (d < 62)
			return (char)(d + '0' - 52);
		else if (d == 62)
			return '+';
		else
			return '/';
	}

	private static int decode(byte[] chars, int ci, byte[] ret, int ri)
	{
		if (ci + 3 < chars.length && ri + 2 < ret.length)
		{
			long b = DECODE_TABLE[chars[ci] & 0x7F];
			b = ((b << 6) | (DECODE_TABLE[chars[ci + 1] & 0x7F]));
			b = ((b << 6) | (DECODE_TABLE[chars[ci + 2]] & 0x7F));
			b = ((b << 6) | (DECODE_TABLE[chars[ci + 3]] & 0x7F));
			ret[ri] = (byte)((b >> 16) & 0xFF);
			ret[ri + 1] = (byte)((b >> 8) & 0xFF);
			ret[ri + 2] = (byte)((b) & 0xFF);
			return (chars[ci + 3] == '=' && chars[ci + 2] == '=')?1:(chars[ci + 3] == '=')?2:3;
		}
		else
		{
			return 0;
		}
	}

	public static byte[] decode(String value)
	{
		byte[] ret = new byte[value.length() * 3 / 4];
		byte[] chars = value.replaceAll("\\s", "").getBytes();
		int ri = 0;
		for(int ci = 0; ci < chars.length; ci += 4)
		{
			ri += decode(chars, ci, ret, ri);
		}
		byte[] nret = new byte[ri];
		System.arraycopy(ret, 0, nret, 0, ri);
		return nret;
	}

	public static String encode_standard_format(byte[] value)
	{
		return encode(value, 76);
	}

	public static String encode_no_break(byte[] value)
	{
		return encode(value, 0);
	}

	private static String encode(byte[] value, int break_count)
	{
		StringBuffer cb = new StringBuffer();
		int i = 0;
		int n = 0;
		for(i = 0; i + 2 < value.length; i += 3)
		{
			if (break_count > 0 && n >= break_count)
			{
				cb.append("\r\n");
				n = 0;
			}
			long chunk = value[i];
			chunk = (chunk << 8) | (255 & value[i + 1]);
			chunk = (chunk << 8) | (255 & value[i + 2]);
			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append(encode(chunk >> 6));
			cb.append(encode(chunk));
			n += 4;
		}
		if (i + 1 < value.length)
		{
			if (break_count > 0 && n >= break_count)
			{
				cb.append("\r\n");
				n = 0;
			}
			long chunk = value[i];
			chunk = (chunk << 8) | (255 & value[i + 1]);
			chunk <<= 8;
			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append(encode(chunk >> 6));
			cb.append('=');
			n += 4;
		}
		else if (i < value.length)
		{
			if (break_count > 0 && n >= break_count)
			{
				cb.append("\r\n");
				n = 0;
			}
			long chunk = value[i];
			chunk <<= 16;
			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append('=');
			cb.append('=');
			n += 4;
		}
		return cb.toString();
	}

	private static final byte[] STANDARD_ENCODE_TABLE = {	'A',
															'B',
															'C',
															'D',
															'E',
															'F',
															'G',
															'H',
															'I',
															'J',
															'K',
															'L',
															'M',
															'N',
															'O',
															'P',
															'Q',
															'R',
															'S',
															'T',
															'U',
															'V',
															'W',
															'X',
															'Y',
															'Z',
															'a',
															'b',
															'c',
															'd',
															'e',
															'f',
															'g',
															'h',
															'i',
															'j',
															'k',
															'l',
															'm',
															'n',
															'o',
															'p',
															'q',
															'r',
															's',
															't',
															'u',
															'v',
															'w',
															'x',
															'y',
															'z',
															'0',
															'1',
															'2',
															'3',
															'4',
															'5',
															'6',
															'7',
															'8',
															'9',
															'+',
															'/'};
	private static final byte[] URL_SAFE_ENCODE_TABLE = {	'A',
															'B',
															'C',
															'D',
															'E',
															'F',
															'G',
															'H',
															'I',
															'J',
															'K',
															'L',
															'M',
															'N',
															'O',
															'P',
															'Q',
															'R',
															'S',
															'T',
															'U',
															'V',
															'W',
															'X',
															'Y',
															'Z',
															'a',
															'b',
															'c',
															'd',
															'e',
															'f',
															'g',
															'h',
															'i',
															'j',
															'k',
															'l',
															'm',
															'n',
															'o',
															'p',
															'q',
															'r',
															's',
															't',
															'u',
															'v',
															'w',
															'x',
															'y',
															'z',
															'0',
															'1',
															'2',
															'3',
															'4',
															'5',
															'6',
															'7',
															'8',
															'9',
															'-',
															'_'};
	private static final byte[] DECODE_TABLE = {-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												62,
												-1,
												62,
												-1,
												63,
												52,
												53,
												54,
												55,
												56,
												57,
												58,
												59,
												60,
												61,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1,
												0,
												1,
												2,
												3,
												4,
												5,
												6,
												7,
												8,
												9,
												10,
												11,
												12,
												13,
												14,
												15,
												16,
												17,
												18,
												19,
												20,
												21,
												22,
												23,
												24,
												25,
												-1,
												-1,
												-1,
												-1,
												63,
												-1,
												26,
												27,
												28,
												29,
												30,
												31,
												32,
												33,
												34,
												35,
												36,
												37,
												38,
												39,
												40,
												41,
												42,
												43,
												44,
												45,
												46,
												47,
												48,
												49,
												50,
												51,
												-1,
												-1,
												-1,
												-1,
												-1,
												-1};
}
