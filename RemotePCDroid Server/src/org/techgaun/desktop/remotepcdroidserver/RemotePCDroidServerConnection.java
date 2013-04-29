package org.techgaun.desktop.remotepcdroidserver;

import java.awt.Desktop;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.techgaun.remotepcdroid.protocol.RemotePCDroidConnection;
import org.techgaun.remotepcdroid.protocol.action.AuthenticationAction;
import org.techgaun.remotepcdroid.protocol.action.AuthenticationResponseAction;
import org.techgaun.remotepcdroid.protocol.action.FileExploreRequestAction;
import org.techgaun.remotepcdroid.protocol.action.FileExploreResponseAction;
import org.techgaun.remotepcdroid.protocol.action.KeyboardAction;
import org.techgaun.remotepcdroid.protocol.action.MouseClickAction;
import org.techgaun.remotepcdroid.protocol.action.MouseMoveAction;
import org.techgaun.remotepcdroid.protocol.action.MouseWheelAction;
import org.techgaun.remotepcdroid.protocol.action.RemotePCDroidAction;
import org.techgaun.remotepcdroid.protocol.action.ScreenCaptureRequestAction;
import org.techgaun.remotepcdroid.protocol.action.ScreenCaptureResponseAction;
import org.techgaun.remotepcdroid.protocol.action.ShutDownAction;

public class RemotePCDroidServerConnection implements Runnable
{
	private static final int[][] UNICODE_EXCEPTION = {
	        {
	                KeyboardAction.UNICODE_BACKSPACE, KeyEvent.VK_BACK_SPACE
	        }, {
	                10, KeyEvent.VK_ENTER
	        }
	};
	
	private RemotePCDroidServerApp application;
	
	private RemotePCDroidConnection connection;
	
	private boolean authenticated;
	
	private String os = System.getProperty("os.name");
	
	private String cmd;
	
	public RemotePCDroidServerConnection(RemotePCDroidServerApp application, RemotePCDroidConnection connection)
	{
		this.application = application;
		this.connection = connection;
		
		this.authenticated = false;
		
		(new Thread(this)).start();
	}
	
	public void run()
	{
		try
		{
			try
			{
				while (true)
				{
					RemotePCDroidAction action = this.connection.receiveAction();
					
					this.action(action);
				}
			}
			finally
			{
				this.connection.close();
			}
		}
		catch (ProtocolException e)
		{
			e.printStackTrace();
			
			this.application.getTrayIcon().notifyProtocolProblem();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void action(RemotePCDroidAction action)
	{
		if (this.authenticated)
		{
			if (action instanceof MouseMoveAction)
			{
				this.moveMouse((MouseMoveAction) action);
			}
			else if (action instanceof MouseClickAction)
			{
				this.mouseClick((MouseClickAction) action);
			}
			else if (action instanceof MouseWheelAction)
			{
				this.mouseWheel((MouseWheelAction) action);
			}
			else if (action instanceof ScreenCaptureRequestAction)
			{
				this.screenCapture((ScreenCaptureRequestAction) action);
			}
			else if (action instanceof FileExploreRequestAction)
			{
				this.fileExplore((FileExploreRequestAction) action);
			}
			else if (action instanceof KeyboardAction)
			{
				this.keyboard((KeyboardAction) action);
			}
			else if (action instanceof ShutDownAction)
			{
				try
				{
					this.shutDownServer((ShutDownAction) action);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			if (action instanceof AuthenticationAction)
			{
				this.authentificate((AuthenticationAction) action);
			}
			
			if (!this.authenticated)
			{
				try
				{
					this.connection.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private void authentificate(AuthenticationAction action)
	{
		if (action.password.equals(this.application.getPreferences().get("password", RemotePCDroidConnection.DEFAULT_PASSWORD)))
		{
			this.authenticated = true;
			
			this.application.getTrayIcon().notifyConnection(this.connection);
		}
		
		this.sendAction(new AuthenticationResponseAction(this.authenticated));
	}
	
	private void moveMouse(MouseMoveAction action)
	{
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		
		if (pointerInfo != null)
		{
			Point mouseLocation = pointerInfo.getLocation();
			
			if (mouseLocation != null)
			{
				int x = mouseLocation.x + action.moveX;
				int y = mouseLocation.y + action.moveY;
				this.application.getRobot().mouseMove(x, y);
			}
		}
	}
	
	private void mouseClick(MouseClickAction action)
	{
		int button;
		
		switch (action.button)
		{
			case MouseClickAction.BUTTON_LEFT:
				button = InputEvent.BUTTON1_MASK;
				break;
			case MouseClickAction.BUTTON_RIGHT:
				button = InputEvent.BUTTON3_MASK;
				break;
			case MouseClickAction.BUTTON_MIDDLE:
				button = InputEvent.BUTTON2_MASK;
				break;
			default:
				return;
		}
		
		if (action.state == MouseClickAction.STATE_DOWN)
		{
			this.application.getRobot().mousePress(button);
		}
		else if (action.state == MouseClickAction.STATE_UP)
		{
			this.application.getRobot().mouseRelease(button);
		}
		
	}
	
	private void mouseWheel(MouseWheelAction action)
	{
		this.application.getRobot().mouseWheel(action.amount);
	}
	
	/*
	 * private void shutDownServer() throws IOException { if
	 * (os.contains("Linux") || os.contains("Mac OS X")) { cmd =
	 * "shutdown -h now"; } else if (os.contains("Windows")) { cmd =
	 * "shutdown.exe -s -t 0"; } else { throw new
	 * RuntimeException("Unsupported operating system"); }
	 * 
	 * Runtime.getRuntime().exec(cmd); }
	 */
	
	private void shutDownServer(ShutDownAction action) throws IOException
	{
		int t = action.time;
		if (os.contains("Linux") || os.contains("Mac OS X"))
		{
			cmd = "shutdown -h " + t;
		}
		else if (os.contains("Windows"))
		{
			cmd = "shutdown.exe -s -t " + t;
			// System.getenv("windir") + "\\system32\\" + cmd
		}
		else
		{
			throw new RuntimeException("Unsupported operating system");
		}
		Runtime.getRuntime().exec(cmd);
	}
	
	private void screenCapture(ScreenCaptureRequestAction action)
	{
		try
		{
			Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
			Rectangle r = new Rectangle(mouseLocation.x - (action.width / 2), mouseLocation.y - (action.height / 2), action.width, action.height);
			BufferedImage capture = this.application.getRobot().createScreenCapture(r);
			
			String format = null;
			if (action.format == ScreenCaptureRequestAction.FORMAT_PNG)
			{
				format = "png";
			}
			else if (action.format == ScreenCaptureRequestAction.FORMAT_JPG)
			{
				format = "jpg";
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(capture, format, baos);
			byte[] data = baos.toByteArray();
			
			this.sendAction(new ScreenCaptureResponseAction(data));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void fileExplore(FileExploreRequestAction action)
	{
		if (action.directory.isEmpty() && action.file.isEmpty())
		{
			this.fileExploreRoots();
		}
		else
		{
			if (action.directory.isEmpty())
			{
				this.fileExplore(new File(action.file));
			}
			else
			{
				File directory = new File(action.directory);
				
				if (directory.getParent() == null && action.file.equals(".."))
				{
					this.fileExploreRoots();
				}
				else
				{
					try
					{
						this.fileExplore(new File(directory, action.file).getCanonicalFile());
					}
					catch (IOException e)
					{
						e.printStackTrace();
						
						this.fileExploreRoots();
					}
				}
			}
		}
	}
	
	private void fileExplore(File file)
	{
		if (file.exists() && file.canRead())
		{
			if (file.isDirectory())
			{
				this.sendFileExploreResponse(file.getAbsolutePath(), file.listFiles(), true);
			}
			else
			{
				if (Desktop.isDesktopSupported())
				{
					Desktop desktop = Desktop.getDesktop();
					
					if (desktop.isSupported(Desktop.Action.OPEN))
					{
						try
						{
							desktop.open(file);
						}
						catch (IOException e)
						{
							e.printStackTrace();
							
							// windows machines require special hack for this
							// tool, I hope this works
							// thanks stackoverflow :D
							
							if (RemotePCDroidServerApp.os_type.contains("windows"))
							{
								System.out.println("windows cmd fix");
								
								try
								{
									Process process = Runtime.getRuntime().exec("cmd /C " + file.getAbsolutePath());
									BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
									
									String line;
									while ((line = br.readLine()) != null)
									{
										System.out.println(line);
									}
								}
								catch (IOException e1)
								{
									e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		else
		{
			this.fileExploreRoots();
		}
	}
	
	private void fileExploreRoots()
	{
		String directory = "";
		
		File[] files = File.listRoots();
		
		this.sendFileExploreResponse(directory, files, false);
	}
	
	private void sendFileExploreResponse(String directory, File[] f, boolean parent)
	{
		if (f != null)
		{
			ArrayList<String> list = new ArrayList<String>();
			
			if (parent)
			{
				list.add("..");
			}
			
			for (int i = 0; i < f.length; i++)
			{
				String name = f[i].getName();
				
				if (!name.isEmpty())
				{
					if (f[i].isDirectory())
					{
						name += File.separator;
					}
				}
				else
				{
					name = f[i].getAbsolutePath();
				}
				
				list.add(name);
			}
			
			String[] files = new String[list.size()];
			
			files = list.toArray(files);
			
			this.sendAction(new FileExploreResponseAction(directory, files));
		}
	}
	
	private void keyboard(KeyboardAction action)
	{
		this.keyboardBackend(action);
	}
	
	private void keyboardBackend(KeyboardAction action)
	{
		int keycode = RemotePCDroidKeyCodeConverter.convert(action.unicode);
		
		if (keycode != RemotePCDroidKeyCodeConverter.NO_SWING_KEYCODE)
		{
			boolean useShift = RemotePCDroidKeyCodeConverter.useShift(action.unicode);
			
			if (useShift)
			{
				this.application.getRobot().keyPress(KeyEvent.VK_SHIFT);
			}
			
			this.application.getRobot().keyPress(keycode);
			this.application.getRobot().keyRelease(keycode);
			
			if (useShift)
			{
				this.application.getRobot().keyRelease(KeyEvent.VK_SHIFT);
			}
		}
	}
	
	private void sendAction(RemotePCDroidAction action)
	{
		try
		{
			this.connection.sendAction(action);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
