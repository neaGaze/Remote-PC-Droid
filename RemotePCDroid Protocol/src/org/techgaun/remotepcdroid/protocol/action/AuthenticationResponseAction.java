package org.techgaun.remotepcdroid.protocol.action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AuthenticationResponseAction extends RemotePCDroidAction
{
	public boolean authenticated;
	
	public AuthenticationResponseAction(boolean authentificated)
	{
		this.authenticated = authentificated;
	}
	
	public static AuthenticationResponseAction parse(DataInputStream dis) throws IOException
	{
		boolean authentificated = dis.readBoolean();
		
		return new AuthenticationResponseAction(authentificated);
	}
	
	public void toDataOutputStream(DataOutputStream dos) throws IOException
	{
		dos.writeByte(AUTHENTICATION_RESPONSE);
		dos.writeBoolean(this.authenticated);
	}
}
