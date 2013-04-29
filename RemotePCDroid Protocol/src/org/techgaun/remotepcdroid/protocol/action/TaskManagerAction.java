package org.techgaun.remotepcdroid.protocol.action;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TaskManagerAction extends RemotePCDroidAction
{
	
	private String os;
	private String cmd;
	
	@Override
	public void toDataOutputStream(DataOutputStream dos) throws IOException
	{
		// TODO Auto-generated method stub
		
	}
	
	private void getTaskList() throws IOException
	{
		os = System.getProperty("os.name");
		if (os.contains("Linux") || os.contains("Mac OS X"))
		{
			cmd = "ps -A";
		}
		else if (os.contains("Windows"))
		{
			cmd = "tasklist";
		}
		else
		{
			throw new RuntimeException("Unsupported operating system");
		}
		ArrayList<String> processes = null;
		String pid;
		Process p = Runtime.getRuntime().exec(cmd);
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((pid = br.readLine()) != null)
		{
			processes.add(pid);
		}
	}
	
}
