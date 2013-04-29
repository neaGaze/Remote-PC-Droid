package org.techgaun.desktop.remotepcdroidserver.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.techgaun.desktop.remotepcdroidserver.RemotePCDroidServerApp;
import org.techgaun.remotepcdroid.protocol.RemotePCDroidConnection;
import org.techgaun.remotepcdroid.protocol.tcp.RemotePCDroidConnectionTcp;

public class RemotePCDroidServerGui
{
	private Preferences preferences;
	private RemotePCDroidServerApp application;
	private TrayIcon trayIcon;
	private JFrame frame;
	private JLabel lblInfo;
	
	public RemotePCDroidServerGui(RemotePCDroidServerApp application) throws AWTException, IOException
	{
		this.application = application;
		this.preferences = this.application.getPreferences(); // several
		                                                      // preferences for
		                                                      // server
		this.initializeGui();
	}
	
	public void notifyConnection(RemotePCDroidConnection connection)
	{
		String message = "";
		
		if (connection instanceof RemotePCDroidConnectionTcp)
		{
			RemotePCDroidConnectionTcp connectionTcp = (RemotePCDroidConnectionTcp) connection;
			message = connectionTcp.getInetAddress().getHostAddress() + ":" + connectionTcp.getPort();
		}
		
		this.lblInfo.setText("A client has been connected : " + message);
		this.trayIcon.displayMessage("RemotePCDroid", "A client has been connected : " + message, MessageType.INFO);
	}
	
	public void notifyProtocolProblem()
	{
		this.lblInfo.setText("An unknown error occurred. Please contact the developers");
		this.trayIcon.displayMessage("RemotePCDroid", "An unknown error occurred. Please contact the developers", MessageType.INFO);
	}
	
	public void close()
	{
		SystemTray.getSystemTray().remove(this.trayIcon);
	}
	
	private void initializeGui() throws AWTException, IOException
	{
		this.frame = new JFrame();
		this.frame.setVisible(true);
		Color bg = new Color(255, 255, 255);
		this.frame.getContentPane().setBackground(bg);
		/*
		 * lets start the server automatically, no overhead of threads and other
		 * stuffs for me frustu garyo thread.stop() use garna hudaina!!!
		 * 
		 * final JButton btnStartServer = new JButton("Start Server");
		 * btnStartServer.setBounds(164, 5, 120, 25);
		 * this.frame.getContentPane().setLayout(null);
		 * this.frame.getContentPane().add(btnStartServer);
		 */

		this.lblInfo = new JLabel("This is a Remote PC Droid Server running at " + RemotePCDroidServerGui.this.getTcpListenAddresses());
		// this.lblInfo.setBounds(92, 106, 270, 50);
		// this.lblInfo.setBorder();
		this.lblInfo.setHorizontalAlignment(JLabel.CENTER);
		this.lblInfo.setVerticalAlignment(JLabel.CENTER);
		this.frame.getContentPane().add(lblInfo);
		this.frame.setBounds(100, 100, 450, 300);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(UIManager.getColor("MenuBar.foreground"));
		this.frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnServer = new JMenu("Server");
		menuBar.add(mnServer);
		
		JMenuItem mntmPass = new JMenuItem("Change Password");
		mnServer.add(mntmPass);
		mntmPass.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String password = RemotePCDroidServerGui.this.preferences.get("password", RemotePCDroidConnection.DEFAULT_PASSWORD);
				password = JOptionPane.showInputDialog("Enter new server password", password);
				if (password != null)
				{
					RemotePCDroidServerGui.this.preferences.put("password", password);
				}
				// password vayena vane empty password allow garne ki nagarne...
				// if forcing is necessary, remove the else condition here.
				else
				{
					RemotePCDroidServerGui.this.preferences.put("password", "");
				}
			}
		});
		
		JMenuItem mntmStatus = new JMenuItem("Server Status");
		mnServer.add(mntmStatus);
		mntmStatus.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				StringBuilder message = new StringBuilder();
				
				if (RemotePCDroidServerGui.this.application.getServerTcp() != null)
				{
					message.append("Remote PC Droid Server running on :\n");
					message.append(RemotePCDroidServerGui.this.getTcpListenAddresses());
				}
				else
				{
					message.append("Remote PC Droid Server is not running");
				}
				lblInfo.setText(message.toString());
				JOptionPane.showMessageDialog(null, message.toString());
			}
		});
		
		JMenuItem mntmPort = new JMenuItem("Change Port");
		mnServer.add(mntmPort);
		mntmPort.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					int port = RemotePCDroidServerGui.this.preferences.getInt("port", RemotePCDroidConnectionTcp.DEFAULT_PORT);
					String newPortString = JOptionPane.showInputDialog("Port", port);
					int newPort = Integer.parseInt(newPortString);
					RemotePCDroidServerGui.this.preferences.putInt("port", newPort);
					JOptionPane.showMessageDialog(null, "Restart your server to apply the new port setting.");
					lblInfo.setText("Restart your server to apply the new port setting.");
				}
				catch (NumberFormatException nfe)
				{
					nfe.printStackTrace();
				}
			}
		});
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmHelpTopicsf = new JMenuItem("Help Topics(F1)");
		mnHelp.add(mntmHelpTopicsf);
		
		mntmHelpTopicsf.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// help information
				JFrame helpFrame = new JFrame("Instruction on RemotePCDroid");
				helpFrame.setSize(300, 300);
				helpFrame.setVisible(true);
				// helpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				helpFrame.setLayout(new GridLayout());
				helpFrame.setResizable(false);
				
				JTextArea help = new JTextArea();
				help.setBackground(null);
				help.setBorder(null);
				help.setEditable(false);
				help.setLineWrap(true);
				help.setWrapStyleWord(true);
				help.setFocusable(false);
				help.setBorder(BorderFactory.createTitledBorder("RemotePCDroid Help"));
				String info = "Usage Instruction\n";
				info += "1 - Start the server on your computer\n";
				info += "2 - Open RemotePCDroid client on your computer\n";
				info += "3 - Provide Ip address of your computer and now you can use your android as remote controller";
				help.setText(info);
				help.setVisible(true);
				helpFrame.add(help);
				
			}
		});
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		mntmAbout.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// help information
				JFrame helpFrame = new JFrame("About RemotePCDroid");
				helpFrame.setSize(300, 200);
				helpFrame.setVisible(true);
				// helpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				helpFrame.setLayout(new GridLayout());
				helpFrame.setResizable(false);
				
				JTextArea help = new JTextArea();
				help.setBackground(null);
				help.setBorder(null);
				help.setEditable(false);
				help.setLineWrap(true);
				help.setWrapStyleWord(true);
				help.setFocusable(false);
				help.setBorder(BorderFactory.createTitledBorder("RemotePCDroid Developers"));
				String info = "This project was developed as a part of COMP 303 for 3rd year 1st semester.\n";
				info += "Members of the project group were:";
				info += "Samar Dhwoj Acharya (samar@techgaun.com)\n";
				info += "Anuj Shrestha (anujrockstar@gmail.com)\n";
				info += "Saroj Thapa (sarojthapa@gmail.com)\n";
				info += "Nigesh Shakya (nizeshshakya@gmail.com)\n\n";
				info += "- RemotePCDroid Team Rocks";
				
				help.setText(info);
				help.setVisible(true);
				helpFrame.add(help);
				
			}
		});
		// click listeners will be below:
		
		PopupMenu menu = new PopupMenu();
		
		MenuItem menuItemPassword = new MenuItem("Change Password");
		menuItemPassword.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String password = RemotePCDroidServerGui.this.preferences.get("password", RemotePCDroidConnection.DEFAULT_PASSWORD);
				password = JOptionPane.showInputDialog("Enter new server password", password);
				if (password != null)
				{
					RemotePCDroidServerGui.this.preferences.put("password", password);
				}
				// password vayena vane empty password allow garne ki nagarne...
				// if forcing is necessary, remove the else condition here.
				else
				{
					RemotePCDroidServerGui.this.preferences.put("password", "");
				}
			}
		});
		menu.add(menuItemPassword);
		
		menu.addSeparator();
		
		MenuItem menuItemWifiServer = new MenuItem("Server Status");
		menuItemWifiServer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				StringBuilder message = new StringBuilder();
				
				if (RemotePCDroidServerGui.this.application.getServerTcp() != null)
				{
					message.append("Remote PC Droid Server running on :\n");
					message.append(RemotePCDroidServerGui.this.getTcpListenAddresses());
				}
				else
				{
					message.append("Remote PC Droid Server is not running");
				}
				lblInfo.setText(message.toString());
				JOptionPane.showMessageDialog(null, message.toString());
			}
		});
		menu.add(menuItemWifiServer);
		menu.addSeparator();
		
		MenuItem menuItemPort = new MenuItem("Change Port");
		menuItemPort.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					int port = RemotePCDroidServerGui.this.preferences.getInt("port", RemotePCDroidConnectionTcp.DEFAULT_PORT);
					String newPortString = JOptionPane.showInputDialog("Port", port);
					int newPort = Integer.parseInt(newPortString);
					RemotePCDroidServerGui.this.preferences.putInt("port", newPort);
					JOptionPane.showMessageDialog(null, "Restart your server to apply the new port setting.");
					lblInfo.setText("Restart your server to apply the new port setting.");
				}
				catch (NumberFormatException nfe)
				{
					nfe.printStackTrace();
				}
			}
		});
		menu.add(menuItemPort);
		
		menu.addSeparator();
		
		MenuItem menuItemExit = new MenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				RemotePCDroidServerGui.this.application.exit();
			}
		});
		menu.add(menuItemExit);
		
		this.trayIcon = new TrayIcon(ImageIO.read(this.getClass().getResourceAsStream("icon.png")));
		this.trayIcon.setImageAutoSize(true);
		this.trayIcon.setToolTip("RemotePCDroid server");
		this.trayIcon.setPopupMenu(menu);
		
		SystemTray.getSystemTray().add(this.trayIcon);
		
		StringBuilder message = new StringBuilder("Server has been started on \n");
		message.append(this.getTcpListenAddresses());
		
		this.trayIcon.displayMessage("RemotePCDroid", message.toString(), TrayIcon.MessageType.INFO);
		lblInfo.setText(message.toString());
	}
	
	// yo function le haamilai chahine IPv4 addresses string banayera return
	// garchha dinchha. loopback ra ipv6
	// filter gareko 6 ahile lai
	private String getTcpListenAddresses()
	{
		int port = this.preferences.getInt("port", RemotePCDroidConnectionTcp.DEFAULT_PORT);
		
		StringBuilder message = new StringBuilder();
		
		try
		{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements())
			{
				NetworkInterface currentInterface = interfaces.nextElement();
				
				Enumeration<InetAddress> addresses = currentInterface.getInetAddresses();
				
				while (addresses.hasMoreElements())
				{
					InetAddress currentAddress = addresses.nextElement();
					
					if (!currentAddress.isLoopbackAddress() && !(currentAddress instanceof Inet6Address))
					{
						message.append(currentAddress.getHostAddress() + ":" + port + "\n");
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return message.toString();
	}
}
