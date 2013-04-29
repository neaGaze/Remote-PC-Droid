package org.techgaun.remotepcdroid.protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.techgaun.remotepcdroid.protocol.action.RemotePCDroidAction;

public abstract class RemotePCDroidConnection
{
	public static final String DEFAULT_PASSWORD = "qwerty";
	
	private DataInputStream dataInputStream;
	private OutputStream outputStream;
	
	public RemotePCDroidConnection(InputStream inputStream, OutputStream outputStream)
	{
		this.dataInputStream = new DataInputStream(inputStream);
		this.outputStream = outputStream;
	}
	
	public RemotePCDroidAction receiveAction() throws IOException
	{
		synchronized (this.dataInputStream)
		{
			RemotePCDroidAction action = RemotePCDroidAction.parse(this.dataInputStream);
			return action;
		}
	}
	
	public void sendAction(RemotePCDroidAction action) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		action.toDataOutputStream(new DataOutputStream(baos));
		
		synchronized (this.outputStream)
		{
			this.outputStream.write(baos.toByteArray());
			this.outputStream.flush();
		}
	}
	
	public void close() throws IOException
	{
		this.dataInputStream.close();
		this.outputStream.close();
	}
}
