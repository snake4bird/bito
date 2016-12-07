package bito.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import bito.net.EmailSender.Attechment;
import bito.util.cfg.SystemConfig;
import bito.util.logger.Log;

public class EmailSender
{
	static
	{
		System.setProperty("mail.sender.log.level", System.getProperty("mail.sender.log.level", "info"));
	}
	private Log log = new Log(":mail.sender");
	//
	private String usermail;
	private String username;
	private String password;
	private String[] smtpserver;
	private int[] smtpport;

	//
	public static class EmailAddress
	{
		public final String usermail;
		public final String username;
		public final String domain;
		public final String server;
		public final int port;

		public EmailAddress(String usermail, String username, String domain, String server, int port)
		{
			this.usermail = usermail;
			this.username = username;
			this.domain = domain;
			this.server = server;
			this.port = port;
		}
	}

	private static final Pattern pmail = Pattern
		.compile("^(?:([^\\<]*)\\<(?=[^\\>]+(\\>)))?([^\\<\\@]+)\\@([^\\>\\/\\:\\;]+)(?:\\2)?(?:\\/([^\\:\\;]+))?(?:\\:(\\d+))?\\;+");

	public static EmailAddress checkMailAddress(String usermail)
	{
		Matcher m = pmail.matcher(usermail + ";");
		if (!m.find())
		{
			throw new RuntimeException("Mail address format error: " + usermail);
		}
		String viewname = m.group(1);
		String username = m.group(3);
		String domain = m.group(4);
		String server = m.group(5);
		int port = 0;
		try
		{
			port = Integer.parseInt(m.group(6));
		}
		catch(Exception e)
		{
		}
		if (username == null || domain == null)
		{
			throw new RuntimeException("Mail address format error: " + usermail);
		}
		usermail = ((viewname == null)?username:viewname) + "<" + username + "@" + domain + ">";
		return new EmailAddress(usermail, username, domain, server, port);
	}

	//
	public EmailSender(String usermail, String password)
	{
		this(usermail, password, null, 0);
	}

	public EmailSender(String usermail, String password, String smtpserver, int smtpport)
	{
		init(usermail, password, smtpserver, smtpport);
	}

	private void init(String usermail_ex, String password, String smtpserver, int smtpport)
	{
		EmailAddress ea = checkMailAddress(usermail_ex);
		this.usermail = ea.usermail;
		this.username = ea.username;
		this.password = password;
		if (smtpserver != null && smtpserver.trim().length() > 0)
		{
			this.smtpserver = new String[]{smtpserver};
		}
		else if (ea.server != null && ea.server.trim().length() > 0)
		{
			this.smtpserver = new String[]{ea.server};
		}
		else
		{
			this.smtpserver = new String[]{"smtp." + ea.domain};
		}
		if (smtpport != 0)
		{
			this.smtpport = new int[]{smtpport};
		}
		else if (ea.port != 0)
		{
			this.smtpport = new int[]{ea.port};
		}
		else
		{
			this.smtpport = new int[]{25, 465, 587};
		}
	}

	private static final int socket_read_timeout = 180000;
	private static final DecimalFormat df_long = new DecimalFormat("#,###");

	private class MailSocket
	{
		private Socket mailsocket;
		private OutputStream mailsocketos;
		private InputStream mailsocketis;

		public MailSocket(Socket socket) throws IOException
		{
			this.mailsocket = socket;
			try
			{
				this.mailsocketos = this.mailsocket.getOutputStream();
				this.mailsocketis = this.mailsocket.getInputStream();
				handshake(socket);
			}
			catch(IOException e)
			{
				disconnect();
				throw e;
			}
		}

		public void disconnect()
		{
			try
			{
				mailsocketos.close();
			}
			catch(Exception e)
			{
			}
			try
			{
				mailsocketis.close();
			}
			catch(Exception e)
			{
			}
			try
			{
				mailsocket.close();
			}
			catch(Exception e)
			{
			}
		}

		private void handshake(Socket socket) throws IOException
		{
			if (!(socket instanceof SSLSocket))
			{
				return;
			}
			SSLSocket sslsocket = (SSLSocket)socket;
			int osotimeout = sslsocket.getSoTimeout();
			sslsocket.setSoTimeout(socket_read_timeout * 5);
			sslsocket.addHandshakeCompletedListener(new HandshakeCompletedListener()
			{
				public void handshakeCompleted(HandshakeCompletedEvent hce)
				{
					log.debug("ciphersuite:" + hce.getCipherSuite());
					log.debug("getLocalPrincipal:" + hce.getLocalPrincipal());
					try
					{
						log.debug("getPeerPrincipal:" + hce.getPeerPrincipal());
					}
					catch(SSLPeerUnverifiedException e)
					{
						log.error(e);
					}
				}
			});
			try
			{
				log.debug("socket.getEnabledCipherSuites():" + Arrays.toString(sslsocket.getEnabledCipherSuites()));
				log.debug("socket.getEnabledProtocols():" + Arrays.toString(sslsocket.getEnabledProtocols()));
				log.debug("socket.getEnableSessionCreation():" + sslsocket.getEnableSessionCreation());
				log.debug("Starting SSL handshake...");
				sslsocket.startHandshake();
				log.debug("handshake ok");
				sslsocket.setSoTimeout(osotimeout);
			}
			catch(IOException e)
			{
				sslsocket.close();
				throw e;
			}
		}

		public void sendString(String sendContent) throws IOException
		{
			if (sendContent.length() > 0)
			{
				mailsocketos.write(sendContent.getBytes());
				mailsocketos.flush();
			}
		}

		public String ProcMessage(String sendContent, String correctFlag) throws Exception
		{
			return ProcMessage(sendContent, correctFlag, false);
		}

		public String ProcMessage(String sendContent, String correctFlag, boolean decode) throws Exception
		{
			if (sendContent.length() > 1024 * 100)
			{
				log.debug("Big data(" + df_long.format(sendContent.length()) + "bytes) is sending ...");
			}
			sendString(sendContent);
			if (sendContent.length() > 1024)
			{
				sendContent = sendContent.substring(0, 1024) + "\r\n......\r\n";
			}
			log.debug("S:" + sendContent);
			String s = readString();
			log.debug("R:" + s);
			if (!s.startsWith(correctFlag + "-") && !s.startsWith(correctFlag + " "))
			{
				if (s.matches("(?s).*[\\s-]STARTTLS\\s.*"))
				{
					throw new SSLException("STARTTLS");
				}
				throw new IOException("SMTP server return error: " + s);
			}
			s = s.replaceAll("^" + correctFlag + "[- ]", "");
			if (decode)
			{
				s = new String(Base64.decode(s));
				log.debug(" :" + s);
			}
			return s;
		}

		public void ProcRcptTo(String mailto) throws Exception
		{
			String[] tos = mailto.split(";");
			for(int i = 0; i < tos.length; i++)
			{
				String to = tos[i].trim();
				if (to.length() > 0)
				{
					try
					{
						EmailAddress ma = checkMailAddress(to);
						ProcMessage("RCPT TO: <" + ma.username + "@" + ma.domain + ">\r\n", "250");
					}
					catch(Exception e)
					{
						log.warn("Ignore mail address: " + tos[i]);
					}
				}
			}
		}

		/**
		 * 不能使用InputStream.available()判断是否有数据
		 * 数据结束条件严格遵循SMTP。
		 */
		public String readString() throws Exception
		{
			byte[] recvBuffer = new byte[8192];
			String ret = "";
			for(int i = 0;; i++)
			{
				{
					int c = mailsocketis.read();
					if (i >= recvBuffer.length)
					{
						// 增加Buffer
						byte[] buf = new byte[(int)(recvBuffer.length * 1.1) + 8192];
						System.arraycopy(recvBuffer, 0, buf, 0, recvBuffer.length);
						recvBuffer = buf;
					}
					recvBuffer[i] = (byte)c;
				}
				{
					if (recvBuffer[i] == '\n')
					{
						String sline = new String(recvBuffer, 0, i + 1);
						i = -1; // 每行Buffer，从头开始
						ret += sline;
						if (sline.matches("(?s)\\d+\\s.*"))
						{
							return ret;
						}
					}
				}
			}
		}
	}

	private MailSocket connect(String server, int port, boolean tls) throws Exception
	{
		Socket socket;
		if (tls)
		{
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, null, null);
			socket = context.getSocketFactory().createSocket(server, port);
		}
		else
		{
			socket = new Socket(server, port);
		}
		socket.setSoTimeout(socket_read_timeout);
		return new MailSocket(socket);
	}

	public static class Attechment
	{
		public String name;
		public String type;
		public byte[] data;

		public Attechment(String name, String type, byte[] data)
		{
			this.name = name;
			this.type = type;
			this.data = data;
		}
	}

	private String buildContent(String m_From, String m_To, String m_CC, String m_BCC, String m_Subject, String m_Body,
		boolean htmlBody, Collection<Attechment> attechments)
	{
		String m_Charset = "UTF-8";
		String m_BodyFormat = htmlBody?"text/html":"text/plain";
		String sBoundary = "----0123456789ABCDEF.FEDCBA9876543210";
		String sContent = "";
		//
		byte[] bs;
		try
		{
			bs = m_Body.getBytes(m_Charset);
		}
		catch(UnsupportedEncodingException e)
		{
			m_Charset = Charset.defaultCharset().name();
			bs = m_Body.getBytes();
		}
		m_Body = Base64.encode_standard_format(bs) + "\r\n";
		//
		sContent = "From: " + m_From + "\r\n";
		sContent += "To: " + m_To + "\r\n";
		sContent += "CC: " + m_CC + "\r\n";
		sContent += "BCC: " + m_BCC + "\r\n";
		sContent += "Subject: " + m_Subject + "\r\n";
		sContent += "Date: " + new Date().toString() + "\r\n";
		sContent += "MIME-Version: 1.0\r\n";
		sContent += "Content-Type: multipart/mixed; boundary=" + sBoundary + "\r\n";
		sContent += "\r\n";
		sContent += "This message is in MIME format. Since your mail reader does not understand\r\n";
		sContent += "this format, some or all of this message may not be legible.  Contact your\r\n";
		sContent += "mail administrator for information about upgrading your reader to a version\r\n";
		sContent += "that supports MIME.\r\n";
		sContent += "\r\n";
		sContent += "--" + sBoundary + "\r\n";
		sContent += "Content-Type: " + m_BodyFormat + "; charset=\"" + m_Charset + "\"\r\n";
		sContent += "Content-Transfer-Encoding: base64\r\n";
		sContent += "\r\n";
		sContent += m_Body;
		sContent += "\r\n";
		//
		if (attechments != null && attechments.size() > 0)
		{
			int i = 0;
			Iterator<Attechment> ai = attechments.iterator();
			while(ai.hasNext())
			{
				Attechment a = ai.next();
				sContent += "--" + sBoundary + "\r\n";
				sContent += "Content-Type: "
					+ (a.type != null && a.type.length() > 0?a.type:"text/plain")
						+ "; charset=\""
						+ m_Charset
						+ "\"; name=\""
						+ (a.name != null?a.name.replaceAll("\\s", "_"):("" + i))
						+ "\"\r\n";
				sContent += "Content-Transfer-Encoding: base64\r\n";
				sContent += "\r\n";
				sContent += (a.data == null?"":Base64.encode_standard_format(a.data));
				sContent += "\r\n";
				sContent += "\r\n";
			}
		}
		sContent += "--" + sBoundary + "--\r\n";
		sContent += "\r\n.\r\n";
		return sContent;
	}

	private void sendMail_kernel(MailSocket mailsocket, String username, String password, String m_From, String m_To,
		String m_CC, String m_BCC, String mailContent) throws Exception
	{
		mailsocket.ProcMessage("AUTH LOGIN\r\n", "334", true);
		mailsocket.ProcMessage(Base64.encode_standard_format(username.getBytes()) + "\r\n", "334", true);
		mailsocket.ProcMessage(Base64.encode_standard_format(password.getBytes()) + "\r\n", "235");
		EmailAddress ma = checkMailAddress(m_From);
		mailsocket.ProcMessage("MAIL FROM: <" + ma.username + "@" + ma.domain + ">\r\n", "250");
		mailsocket.ProcRcptTo((m_To + ";" + m_CC + ";" + m_BCC).replaceAll("(?:\\s*;\\s*)+", ";"));
		mailsocket.ProcMessage("DATA\r\n", "354");
		mailsocket.ProcMessage(mailContent, "250");
	}

	private void sendMail(String server, int port, boolean ssl, String username, String password, String m_From,
		String m_To, String m_CC, String m_BCC, String mailContent) throws Exception
	{
		MailSocket mailsocket = null;
		try
		{
			mailsocket = connect(server, port, ssl);
			mailsocket.ProcMessage("", "220");
			try
			{
				mailsocket.ProcMessage("HELO zerone\r\n", "250");
			}
			catch(IOException e)
			{
				mailsocket.ProcMessage("EHLO zerone\r\n", "250");
			}
			sendMail_kernel(mailsocket, username, password, m_From, m_To, m_CC, m_BCC, mailContent);
			mailsocket.ProcMessage("QUIT\r\n", "221");
		}
		catch(SSLException e)
		{
			if (!ssl && "STARTTLS".equals(e.getMessage()))
			{
				mailsocket.ProcMessage("STARTTLS\r\n", "220");
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, null, null);
				Socket starttls_mailsocket = context.getSocketFactory().createSocket(mailsocket.mailsocket,
					server,
					port,
					true);
				mailsocket = new MailSocket(starttls_mailsocket);
				mailsocket.ProcMessage("EHLO zerone\r\n", "250");
				sendMail_kernel(mailsocket, username, password, m_From, m_To, m_CC, m_BCC, mailContent);
				mailsocket.ProcMessage("QUIT\r\n", "221");
			}
			else
			{
				throw e;
			}
		}
		finally
		{
			if (mailsocket != null)
			{
				mailsocket.disconnect();
			}
		}
	}

	public String send(String m_To, String m_CC, String m_BCC, String m_Subject, String m_Body, boolean htmlBody,
		Collection<Attechment> attechments) throws Exception
	{
		String mailcontent = buildContent(usermail, m_To, m_CC, m_BCC, m_Subject, m_Body, htmlBody, attechments);
		boolean sent = false;
		LinkedHashSet message = new LinkedHashSet();
		for(int namei = 0; !sent && namei < smtpserver.length; namei++)
		{
			for(int porti = 0; !sent && porti < smtpport.length; porti++)
			{
				try
				{
					sendMail(smtpserver[namei],
						smtpport[porti],
						smtpport[porti] == 465,
						username,
						password,
						usermail,
						m_To,
						m_CC,
						m_BCC,
						mailcontent);
					return "OK";
				}
				catch(Exception e)
				{
					String s = e.getClass().getName()
						+ ": ["
							+ smtpserver[namei]
							+ ":"
							+ smtpport[porti]
							+ "] "
							+ e.getMessage();
					if (!message.contains(s))
					{
						log.debug(e);
						message.add(s);
					}
				}
			}
		}
		throw new Exception("Send mail failed. " + message.toString());
	}
}
