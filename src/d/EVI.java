package d;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;
import java.util.SortedMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import bito.util.logger.Log;

public interface EVI
{
	/**
	 * 获取指定Class cls的package或其所在jar的所在路径，
	 * 返回值结尾没有“/”
	 */
	String getClassLocation(Class cls);

	Properties loadXMLConfig(InputStream is) throws Exception;

	Class getProxyClass(String proxyName, Class superClass, Class[] interfaces, Class[] overrideClass,
		Method[] overrideMethods);

	/**
	 * 返回不重复的当前微秒近似数，16位10进制数，毫秒数是13位10进制数
	 */
	long getStamp();

	Date parseDateTime(String dtstring);

	Date parseDateTime(String dtstring, String[] dtstyle);

	/**
	 * BLOCK方式传输数据流，InputStream不读完不会返回
	 */
	void pipe(InputStream source, OutputStream target) throws IOException;

	void copyFile(File source, File target) throws IOException;

	/**
	 * 不读完不返回
	 */
	byte[] readBytes(String filename) throws IOException;

	/**
	 * BLOCK方式读取输入流，不读完不会返回，不能用于读取网络传输数据，不会关闭InputStream
	 */
	byte[] readBytes(InputStream is) throws IOException;

	String toJSONString(Object obj);

	Object parseJSONString(String json);

	String objectString(Object o);

	/**
	 * 加密/解密
	 */
	public String MD5(String s);

	public String DESxEncrypt(String data, String key) throws Exception;

	public byte[] DESxEncrypt(byte[] data, byte[] key) throws Exception;

	public String DESxDecrypt(String data, String key) throws Exception;

	public byte[] DESxDecrypt(byte[] data, byte[] key) throws Exception;

	/**
	 * 新建相应接口实现类的对象实例
	 */
	URLReader newURLReader();

	FileAppender newFileAppender(String filename, long maxfilesize, int maxbackindex);

	void evMain(String[] args);

	/**
	 * 判断文本文件的编码格式，无法确定返回null
	 */
	public String checkTextFileCharset(String filename);

	public String checkTextFileCharset(byte[] bs);

	public String readfile(File file) throws IOException;

	public void writefile(File file, String content) throws IOException;

	Comparator getCommonComparator();

	void config();

	Log log(String id);

	EscapeSequence getEscapeSequence();

	int run(String[] command, String[] env, String workdir, OutputStream out, OutputStream err);

	SortedMap newMapSortedByAddTime();

	SQLRunner newSQLRunner(String name);

	HashTree newHashTree();

	HashTree newHashTree(Class componentType);

	String toString(Object o);

	String[] match(String input, String regex);
}
