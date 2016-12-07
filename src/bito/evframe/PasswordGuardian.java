package bito.evframe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextPane;

public class PasswordGuardian
{
	JFrame jframe = new JFrame();;

	public PasswordGuardian()
	{
	}

	public String getPassword(String title, int wait_for_seconds) throws Exception
	{
		String password = null;
		try
		{
			Font f = new Font(null, Font.PLAIN, 16);
			int title_height = 30;
			int border = 8;
			JPasswordField jpf = new JPasswordField();
			jpf.setPreferredSize(new Dimension(600, 30));
			JButton jbtn = new JButton("OK");
			jbtn.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					jframe.setVisible(false);
				}
			});
			Dimension mpsize = jpf.getPreferredSize();
			jframe.setTitle(title);
			jframe.setFont(f);
			jframe.setSize(mpsize.width + border + border, mpsize.height + title_height + border);
			jframe.setLocationRelativeTo(null);
			jframe.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			jframe.getRootPane().setLayout(new BorderLayout());
			jframe.getRootPane().add(jpf, BorderLayout.CENTER);
			jframe.getRootPane().add(jbtn, BorderLayout.EAST);
			jframe.getRootPane().setDefaultButton(jbtn);
			jframe.setVisible(true);
			jframe.setAlwaysOnTop(true);
			long t = System.currentTimeMillis() + 1000L * wait_for_seconds;
			while(jframe.isVisible() && (wait_for_seconds <= 0 || System.currentTimeMillis() < t))
			{
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e1)
				{
				}
				if (wait_for_seconds > 0)
				{
					if (jpf.getPassword().length > 0)
					{
						t = System.currentTimeMillis() + 1000L * wait_for_seconds;
						jframe.setTitle(title);
					}
					else
					{
						jframe.setTitle(title + "[" + ((t - System.currentTimeMillis()) / 1000) + "]");
					}
				}
			}
			password = new String(jpf.getPassword());
		}
		finally
		{
			jframe.dispose();
		}
		return password;
	}
}
