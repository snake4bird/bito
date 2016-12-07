package bito.evframe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class EVF implements Runnable
{
	private String[] args;
	protected JFrame jframe;
	private JComponent mainpanel;

	public EVF(String[] args)
	{
		this.args = args;
	}

	protected JPanel newMainPanel() throws Exception
	{
		String clsname = "d._.cellcomm.vf.Okm";
		if (args.length > 0 && args[0].startsWith("!") && args[0].length() > 1)
		{
			clsname = args[0].substring(1);
		}
		Class cls = Class.forName(clsname, true, bito.util.E.V().getClass().getClassLoader());
		return (JPanel)cls.newInstance();
	}

	protected JComponent mainComponent() throws Exception
	{
		return newMainPanel();
	}

	public void run()
	{
		try
		{
			openWindow();
			waitForWindowClose();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void initWindow() throws Exception
	{
		if (jframe == null)
		{
			Font f = new Font(null, Font.PLAIN, 16);
			//Dimension screen_size = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize();
			int title_height = 30;
			int border = 8;
			mainpanel = mainComponent();
			Dimension mpsize = mainpanel.getPreferredSize();
			jframe = new JFrame();
			jframe.setTitle(System.getProperty("window.title", "-"));
			jframe.setFont(f);
			//jf.setSize(screen_size.width / 2, screen_size.height / 2);
			jframe.setSize(mpsize.width + border + border, mpsize.height + title_height + border);
			jframe.setLocationRelativeTo(null);
			jframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			jframe.getRootPane().setLayout(new BorderLayout());
			jframe.getRootPane().add(mainpanel, BorderLayout.CENTER);
			jframe.addWindowListener(new WindowAdapter()
			{
				public void windowIconified(WindowEvent e)
				{
					EVF.this.windowIconified();
				}

				public void windowClosing(WindowEvent e)
				{
					closeWindow();
				}
			});
		}
	}

	public void showWindow()
	{
		if (jframe != null)
		{
			synchronized(jframe)
			{
				if (!jframe.isVisible())
				{
					jframe.setVisible(true);
				}
			}
		}
	}

	public void openWindow() throws Exception
	{
		initWindow();
		showWindow();
	}

	public void waitForWindowClose()
	{
		if (jframe != null)
		{
			synchronized(jframe)
			{
				while(jframe.isVisible())
				{
					try
					{
						jframe.wait();
					}
					catch(InterruptedException e1)
					{
					}
				}
			}
			jframe.dispose();
			jframe = null;
		}
	}

	public void hideWindow()
	{
		if (jframe != null)
		{
			synchronized(jframe)
			{
				if (jframe.isVisible())
				{
					jframe.setVisible(false);
					jframe.notify();
				}
			}
		}
	}

	public void closeWindow()
	{
		hideWindow();
	}

	public void windowIconified()
	{
	}
}
