package org.techgaun.android.remotepcdroid.client.activity;

import org.techgaun.android.remotepcdroid.client.R;
import org.techgaun.android.remotepcdroid.client.app.RemotePCDroid;
import org.techgaun.remotepcdroid.protocol.RemotePCDroidActionReceiver;
import org.techgaun.remotepcdroid.protocol.action.RemotePCDroidAction;
import org.techgaun.remotepcdroid.protocol.action.ShutDownAction;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class RemotePCDroidShortcutsActivity extends Activity implements RemotePCDroidActionReceiver, OnItemClickListener
{
	private RemotePCDroid application;
	
	private SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.application = (RemotePCDroid) this.getApplication();
		this.preferences = this.application.getPreferences();
		this.setContentView(R.layout.shortcuts);
		this.initViews();
		
	}
	
	private void initViews()
	{
		// initializes view
		final Button btnShutDown = (Button) findViewById(R.id.btnShutDown);
		Button btnTaskMgr = (Button) findViewById(R.id.btnTaskMgr);
		Button btnCloseAllWindows = (Button) findViewById(R.id.btnCloseAllWindows);
		Button btnMinimize = (Button) findViewById(R.id.btnMinimizeWindows);
		
		btnShutDown.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				sendShutDownRequest();
			}
		});
		
		btnTaskMgr.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		btnCloseAllWindows.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		btnMinimize.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	protected void sendShutDownRequest()
	{
		this.application.sendAction(new ShutDownAction(0));
	}
	
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		this.application.unregisterActionReceiver(this);
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		this.application.registerActionReceiver(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// this.application.sendAction(new ShutDownAction(0));
	}
	
	@Override
	public void receiveAction(RemotePCDroidAction action)
	{
		if (action instanceof ShutDownAction)
		{
			
		}
	}
}
