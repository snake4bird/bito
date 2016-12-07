package bito.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class RegxMatch extends JPanel
{
	private JTextArea jta_text = new JTextArea();
	private JTextArea jta_regx = new JTextArea();
	private JTextArea jta_rets = new JTextArea();
	private JCheckBox jcb_backslash = new JCheckBox("·´Ð±Ïß×ªÒë", true);

	public RegxMatch()
	{
		this.setPreferredSize(new Dimension(800, 600));
		Font f = new Font(null, Font.PLAIN, 16);
		///
		DocumentListener dl = new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e)
			{
				refresh();
			}

			public void insertUpdate(DocumentEvent e)
			{
				refresh();
			}

			public void removeUpdate(DocumentEvent e)
			{
				refresh();
			}
		};
		///
		jta_text.setFont(f);
		jta_regx.setFont(f);
		jta_rets.setFont(f);
		jta_text.getDocument().addDocumentListener(dl);
		jta_regx.getDocument().addDocumentListener(dl);
		jta_rets.setEditable(false);
		jta_text.setAutoscrolls(true);
		jta_regx.setAutoscrolls(true);
		jta_rets.setAutoscrolls(true);
		///
		this.setLayout(new BorderLayout());
		Box boxmain = new Box(BoxLayout.Y_AXIS);
		this.add(boxmain);
		Box boxmenu = new Box(BoxLayout.X_AXIS);
		boxmenu.setAlignmentX(0);
		boxmenu.setAlignmentY(0);
		boxmenu.setBorder(new javax.swing.border.LineBorder(Color.darkGray, 1));
		boxmenu.setPreferredSize(new Dimension(800, 25));
		Box boxempty = new Box(BoxLayout.X_AXIS);
		boxempty.setMinimumSize(new Dimension(Integer.MAX_VALUE, 1));
		boxempty.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
		boxempty.add(new JPanel());
		boxmenu.add(jcb_backslash);
		boxmenu.add(boxempty);
		JScrollPane jspt = new JScrollPane(jta_text);
		jspt.setPreferredSize(new Dimension(800, 600));
		JScrollPane jspr = new JScrollPane(jta_regx);
		jspr.setPreferredSize(new Dimension(800, 600));
		JScrollPane jsps = new JScrollPane(jta_rets);
		jsps.setPreferredSize(new Dimension(800, 600));
		jspt.setBorder(new javax.swing.border.TitledBorder("text"));
		jspr.setBorder(new javax.swing.border.TitledBorder("regx"));
		jsps.setBorder(new javax.swing.border.TitledBorder("rets"));
		///
		boxmain.add(boxmenu);
		boxmain.add(jspt);
		boxmain.add(jspr);
		boxmain.add(jsps);
	}

	private Thread refreshThread = null;

	@SuppressWarnings("deprecation")
	private void refresh()
	{
		try
		{
			if (refreshThread != null)
			{
				refreshThread.stop();
			}
			refreshThread = new Thread()
			{
				public void run()
				{
					do_refresh();
					refreshThread = null;
				}
			};
			refreshThread.start();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	private Pattern pMultiRegx = Pattern.compile("(?s)(?:^|\\n)\\s*(\\d+)\\)(.*)(?:$|\\n)");

	private void do_refresh()
	{
		String text = jta_text.getText();
		String regx = jta_regx.getText();
		String s = "";
		jta_rets.setText("...");
		//
		try
		{
			if (jcb_backslash.isSelected())
			{
				text = EscapeSequence.decode(text, false);
				regx = EscapeSequence.decode(regx, false);
			}
			Matcher mMultiRegx = pMultiRegx.matcher(regx);
			if (mMultiRegx.find())
			{
			}
			{
				String x = regx;
				s += x + "\r\n";
				Pattern p = Pattern.compile(x);
				Matcher m = p.matcher(text);
				String ts = s + "...\r\n";
				jta_rets.setText(ts);
				if (m.matches())
				{
					s += "matches() return true\r\n";
				}
				else
				{
					s += "matches() return false\r\n";
				}
				jta_rets.setText(s);
				m.reset();
				int n = 1;
				while(m.find() && n < 100)
				{
					for(int i = 1; i <= m.groupCount(); i++)
					{
						s += n + "." + i + ". " + m.group(i) + "\r\n";
					}
					n++;
				}
				if (m.find())
				{
					s += "...\r\n";
				}
			}
		}
		catch(Exception e)
		{
			s += e.getMessage() + "\r\n";
		}
		s += "------ end ------\r\n";
		jta_rets.setText(s);
	}
}
