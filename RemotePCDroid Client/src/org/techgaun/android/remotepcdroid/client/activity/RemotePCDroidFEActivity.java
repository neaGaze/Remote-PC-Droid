package org.techgaun.android.remotepcdroid.client.activity;

import java.util.ArrayList;
import java.util.Arrays;

import org.techgaun.android.remotepcdroid.client.R;
import org.techgaun.android.remotepcdroid.client.app.RemotePCDroid;
import org.techgaun.remotepcdroid.protocol.RemotePCDroidActionReceiver;
import org.techgaun.remotepcdroid.protocol.action.FileExploreRequestAction;
import org.techgaun.remotepcdroid.protocol.action.FileExploreResponseAction;
import org.techgaun.remotepcdroid.protocol.action.RemotePCDroidAction;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author samar
 */
public class RemotePCDroidFEActivity extends Activity implements RemotePCDroidActionReceiver, OnItemClickListener
{
	private static final int REFRESH_MENU_ITEM_ID = 0;
	private static final int EXPLORE_ROOTS_MENU_ITEM_ID = 1;
	
	/**
	 * @uml.property name="application"
	 * @uml.associationEnd
	 */
	private RemotePCDroid application;
	
	private SharedPreferences preferences;
	
	private String directory;
	
	private ArrayList<String> files;
	private ArrayAdapter<String> adapter;
	
	private ListView listView;
	
	private TextView textView;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.application = (RemotePCDroid) this.getApplication();
		
		this.preferences = this.application.getPreferences();
		
		this.setContentView(R.layout.fsexplorer);
		
		this.listView = (ListView) this.findViewById(R.id.files);
		
		this.textView = (TextView) this.findViewById(R.id.directory);
		
		this.files = new ArrayList<String>();
		
		this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.files);
		this.listView.setAdapter(this.adapter);
		
		this.listView.setOnItemClickListener(this);
	}
	
	protected void onResume()
	{
		super.onResume();
		
		this.application.registerActionReceiver(this);
		
		this.directory = this.preferences.getString("fileExplore_directory", "");
		
		this.refresh();
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, REFRESH_MENU_ITEM_ID, Menu.NONE, this.getResources().getString(R.string.text_refresh));
		menu.add(Menu.NONE, EXPLORE_ROOTS_MENU_ITEM_ID, Menu.NONE, this.getResources().getString(R.string.text_explore_roots));
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case REFRESH_MENU_ITEM_ID:
				this.refresh();
				break;
			case EXPLORE_ROOTS_MENU_ITEM_ID:
				this.exploreRoots();
				break;
		}
		
		return true;
	}
	
	protected void onPause()
	{
		super.onPause();
		
		this.application.unregisterActionReceiver(this);
		
		Editor editor = this.preferences.edit();
		editor.putString("fileExplore_directory", this.directory);
		editor.commit();
	}
	
	public void receiveAction(RemotePCDroidAction action)
	{
		if (action instanceof FileExploreResponseAction)
		{
			FileExploreResponseAction fera = (FileExploreResponseAction) action;
			
			this.directory = fera.directory;
			
			this.files.clear();
			this.files.addAll(Arrays.asList(fera.files));
			
			this.runOnUiThread(new Runnable()
			{
				public void run()
				{
					RemotePCDroidFEActivity.this.textView.setText(RemotePCDroidFEActivity.this.directory);
					
					RemotePCDroidFEActivity.this.adapter.notifyDataSetChanged();
					RemotePCDroidFEActivity.this.listView.setSelectionAfterHeaderView();
				}
			});
		}
	}
	
	private void sendFileExploreRequest(String fileString)
	{
		this.application.sendAction(new FileExploreRequestAction(this.directory, fileString));
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		this.sendFileExploreRequest(this.files.get(position));
	}
	
	private void refresh()
	{
		this.sendFileExploreRequest("");
	}
	
	private void exploreRoots()
	{
		this.directory = "";
		this.refresh();
	}
}
