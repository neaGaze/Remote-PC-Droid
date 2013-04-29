package org.techgaun.remotepcdroid.protocol.action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ShutDownAction extends RemotePCDroidAction
{
	public int time;
	
	public ShutDownAction(int time)
	{
		this.time = time;
	}
	
	@Override
	public void toDataOutputStream(DataOutputStream dos) throws IOException
	{
		dos.writeByte(SHUTDOWN_SERVER);
		dos.writeInt(this.time);
	}
	
	public static ShutDownAction parse(DataInputStream dis) throws IOException
	{
		int time = dis.readInt();
		return new ShutDownAction(time);
	}
}
