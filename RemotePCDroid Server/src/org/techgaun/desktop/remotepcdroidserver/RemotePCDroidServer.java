package org.techgaun.desktop.remotepcdroidserver;

public abstract class RemotePCDroidServer
{
	protected RemotePCDroidServerApp application;
	
	public RemotePCDroidServer(RemotePCDroidServerApp application)
	{
		this.application = application;
	}
	
	public abstract void close();
}
