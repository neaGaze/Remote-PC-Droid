package org.techgaun.android.remotepcdroid.client.connection;

import java.io.IOException;
import java.io.Serializable;

import org.techgaun.android.remotepcdroid.client.activity.connection.RemotePCDroidConnEditActivity;
import org.techgaun.android.remotepcdroid.client.app.RemotePCDroid;
import org.techgaun.remotepcdroid.protocol.RemotePCDroidConnection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author  samar
 */
public abstract class Connection implements Comparable<Connection>, Serializable
{
	/**
	 * @uml.property  name="name"
	 */
	private String name;
	/**
	 * @uml.property  name="password"
	 */
	private String password;
	
	public Connection()
	{
		this.name = "";
		this.password = RemotePCDroidConnection.DEFAULT_PASSWORD;
	}
	
	public static Connection load(SharedPreferences preferences, ConnectionList list, int position)
	{
		Connection connection = null;
		connection = ConnectionWifi.load(preferences, position);
		
		connection.name = preferences.getString("connection_" + position + "_name", null);
		connection.password = preferences.getString("connection_" + position + "_password", null);
		
		return connection;
	}
	
	public void save(Editor editor, int position)
	{
		editor.putString("connection_" + position + "_name", this.name);
		editor.putString("connection_" + position + "_password", this.password);
	}
	
	public abstract RemotePCDroidConnection connect(RemotePCDroid application) throws IOException;
	
	public abstract void edit(Context context);
	
	protected void edit(Context context, Intent intent)
	{
		RemotePCDroidConnEditActivity.connectionParam = this;
		context.startActivity(intent);
	}
	
	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * @return
	 * @uml.property  name="password"
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 * @param password
	 * @uml.property  name="password"
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public int compareTo(Connection c)
	{
		return this.name.compareTo(c.name);
	}
}
