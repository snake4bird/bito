package bito.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESX
{
	public static String MD5(String s)
	{
		String ts = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bs = md.digest(s.getBytes());
			BigInteger bi = new BigInteger(1, bs);
			ts = bi.toString(16).toUpperCase();
			while(ts.length() < bs.length * 2)
			{
				ts = "0" + ts;
			}
		}
		catch(NoSuchAlgorithmException e)
		{
		}
		return ts;
	}

	public static String Encrypt(String data, String key) throws Exception
	{
		byte[] encodedata = Encrypt(data.getBytes(), key.getBytes());
		BigInteger bi = new BigInteger(1, encodedata);
		String ret = bi.toString(16);
		if (ret.length() < encodedata.length * 2)
		{
			char[] retcs = new char[encodedata.length * 2];
			Arrays.fill(retcs, '0');
			ret = new String(retcs, 0, encodedata.length * 2 - ret.length()) + ret;
		}
		return ret.toUpperCase();
	}

	public static byte[] Encrypt(byte[] data, byte[] key) throws Exception
	{
		// 【用密钥加密原始数据】
		IvParameterSpec ips = new IvParameterSpec("12345678".getBytes());
		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec edks = new DESKeySpec(Makekey(key));
		// 创建一个密钥工厂，然后用它把DESKeySpec转换成Secret Key对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey skey = keyFactory.generateSecret(edks);
		// Cipher对象实际完成加密操作
		Cipher ecipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		// 用密钥初始化Cipher对象
		ecipher.init(Cipher.ENCRYPT_MODE, skey, ips);
		// 执行加密操作
		byte encryptedClassData[] = ecipher.doFinal(data);
		return encryptedClassData;
	}

	public static String Decrypt(String data, String key) throws Exception
	{
		BigInteger bi = new BigInteger(data, 16);
		byte[] bytes = bi.toByteArray();
		if (bytes.length < (data.length() + 1) / 2)
		{
			byte[] tmp = new byte[(data.length() + 1) / 2];
			Arrays.fill(tmp, (byte)0);
			System.arraycopy(bytes, 0, tmp, ((data.length() + 1) / 2 - bytes.length), bytes.length);
			bytes = tmp;
		}
		else if (bytes.length > (data.length() + 1) / 2)
		{
			byte[] tmp = new byte[(data.length() + 1) / 2];
			Arrays.fill(tmp, (byte)0);
			System.arraycopy(bytes, (bytes.length - (data.length() + 1) / 2), tmp, 0, (data.length() + 1) / 2);
			bytes = tmp;
		}
		return new String(Decrypt(bytes, key.getBytes()));
	}

	public static byte[] Decrypt(byte[] data, byte[] key) throws Exception
	{
		// 【用密钥解密数据】
		IvParameterSpec ips = new IvParameterSpec("12345678".getBytes());
		// 创建一个DESKeySpec对象
		DESKeySpec ddks = new DESKeySpec(Makekey(key));
		// 创建一个密钥工厂，然后用它把DESKeySpec对象转换成Secret Key对象
		SecretKeyFactory dkeyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey dkey = dkeyFactory.generateSecret(ddks);
		// Cipher对象实际完成解密操作
		Cipher dcipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		// 用密钥初始化Cipher对象
		dcipher.init(Cipher.DECRYPT_MODE, dkey, ips);
		// 执行解密操作
		byte decryptedData[] = dcipher.doFinal(data);
		return decryptedData;
	}

	private static byte[] Makekey(byte[] key)
	{
		byte[] stdkey = new byte[8];
		int i;
		for(i = 0; i < key.length; i++)
		{
			stdkey[i % 8] ^= key[i];
		}
		return stdkey;
	}
}
