package org.techgaun.android.remotepcdroid.client.connection;

import java.io.IOException;

import org.techgaun.android.remotepcdroid.client.activity.connection.RemotePCDroidConnWifiEditActivity;
import org.techgaun.android.remotepcdroid.client.app.RemotePCDroid;
import org.techgaun.remotepcdroid.protocol.RemotePCDroidConnection;
import org.techgaun.remotepcdroid.protocol.tcp.RemotePCDroidConnectionTcp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author  samar
 */
public class ConnectionWifi extends Connection
{
	/**
	 * @uml.property  name="host"
	 */
	private String host;
	/**
	 * @uml.property  name="port"
	 */
	private int port;
	
	public ConnectionWifi()
	{
		super();
		
		this.host = "";
		this.port = RemotePCDroidConnectionTcp.DEFAULT_PORT;
	}
	
	public static ConnectionWifi load(SharedPreferences preferences, int position)
	{
		ConnectionWifi connection = new ConnectionWifi();
		
		connection.host = preferences.getString("connection_" + position + "_host", null);
		
		connection.port = preferences.getInt("connection_" + position + "_port", 0);
		
		return connection;
	}
	
	public void save(Editor editor, int position)
	{
		super.save(editor, position);
		editor.putString("connection_" + position + "_host", this.host);
		editor.putInt("connection_" + position + "_port", this.port);
	}
	
	public void edit(Context context)
	{
		Intent intent = new Intent(context, RemotePCDroidConnWifiEditActivity.class);
		this.edit(context, intent);
	}
	
	public RemotePCDroidConnection connect(RemotePCDroid application) throws IOException
	{
		return RemotePCDroidConnectionTcp.create(this.host, this.port);
	}
	
	/**
	 * @return
	 * @uml.property  name="host"
	 */
	public String getHost()
	{
		return host;
	}
	
	/**
	 * @param host
	 * @uml.property  name="host"
	 */
	public void setHost(String host)
	{
		this.host = host;
	}
	
	/**
	 * @return
	 * @uml.property  name="port"
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 * @param port
	 * @uml.property  name="port"
	 */
	public void setPort(int port)
	{
		this.port = port;
	}
}
