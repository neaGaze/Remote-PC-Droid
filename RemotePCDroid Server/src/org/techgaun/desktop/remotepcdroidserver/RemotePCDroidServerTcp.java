package org.techgaun.desktop.remotepcdroidserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.techgaun.remotepcdroid.protocol.tcp.RemotePCDroidConnectionTcp;

public class RemotePCDroidServerTcp extends RemotePCDroidServer implements Runnable
{
	private ServerSocket serverSocket;
	
	public RemotePCDroidServerTcp(RemotePCDroidServerApp application) throws IOException
	{
		super(application);
		
		int port = this.application.getPreferences().getInt("port", RemotePCDroidConnectionTcp.DEFAULT_PORT);
		this.serverSocket = new ServerSocket(port);
		
		(new Thread(this)).start();
	}
	
	public void run()
	{
		try
		{
			while (true)
			{
				Socket socket = this.serverSocket.accept();
				RemotePCDroidConnectionTcp connection = new RemotePCDroidConnectionTcp(socket);
				new RemotePCDroidServerConnection(this.application, connection);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		try
		{
			this.serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
