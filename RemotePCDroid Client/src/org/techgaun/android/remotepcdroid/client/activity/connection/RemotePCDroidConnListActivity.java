package org.techgaun.android.remotepcdroid.client.activity.connection;

import org.techgaun.android.remotepcdroid.client.R;
import org.techgaun.android.remotepcdroid.client.app.RemotePCDroid;
import org.techgaun.android.remotepcdroid.client.connection.Connection;
import org.techgaun.android.remotepcdroid.client.connection.ConnectionList;
import org.techgaun.android.remotepcdroid.client.connection.ConnectionWifi;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author  samar
 */
public class RemotePCDroidConnListActivity extends ListActivity implements OnItemClickListener, OnItemLongClickListener, OnClickListener
{
	/**
	 * @uml.property  name="application"
	 * @uml.associationEnd  
	 */
	private RemotePCDroid application;
	
	/**
	 * @uml.property  name="connections"
	 * @uml.associationEnd  
	 */
	private ConnectionList connections;
	
	/**
	 * @uml.property  name="adapter"
	 * @uml.associationEnd  
	 */
	private ConnectionListAdapter adapter;
	
	private AlertDialog alertDialogNew;
	
	private AlertDialog alertDialogItemLongClick;
	
	private int itemLongClickPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.application = (RemotePCDroid) this.getApplication();
		
		this.connections = this.application.getConnections();
		
		this.adapter = new ConnectionListAdapter(this, this.connections);
		
		this.setListAdapter(this.adapter);
		
		this.getListView().setOnItemClickListener(this);
		
		this.getListView().setOnItemLongClickListener(this);
		
		this.init();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		this.refresh();
		
		if (this.connections.getCount() == 0)
		{
			this.application.showToast(R.string.text_no_connection);
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		
		this.connections.save();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.text_new);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case 0:
				this.alertDialogNew.show();
				break;
		}
		
		return true;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		this.useConnection(position);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		this.itemLongClickPosition = position;
		
		this.alertDialogItemLongClick.show();
		
		return true;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if (dialog == this.alertDialogNew)
		{
			this.addConnection(which);
		}
		else if (dialog == this.alertDialogItemLongClick)
		{
			this.menu(which);
		}
	}
	
	private void menu(int which)
	{
		Connection connection = this.connections.get(this.itemLongClickPosition);
		
		switch (which)
		{
			case 0:
				this.useConnection(this.itemLongClickPosition);
				break;
			case 1:
				connection.edit(this);
				break;
			case 2:
				this.removeConnection();
				break;
		}
	}
	
	private void addConnection(int which)
	{
		Connection connection = this.connections.add();
		
		this.refresh();
		
		connection.edit(this);
	}
	
	private void useConnection(int position)
	{
		this.connections.use(position);
		this.refresh();
	}
	
	private void removeConnection()
	{
		this.connections.remove(this.itemLongClickPosition);
		this.refresh();
	}
	
	private void refresh()
	{
		this.connections.sort();
		this.adapter.notifyDataSetChanged();
	}
	
	private void init()
	{
		this.initAlertDialogNew();
		
		this.initAlertDialogItemLongClick();
	}
	
	private void initAlertDialogNew()
	{
		String[] connectionTypeName = {
			this.getResources().getString(R.string.text_wifi)
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Wi-Fi Server");
		builder.setItems(connectionTypeName, this);
		this.alertDialogNew = builder.create();
	}
	
	private void initAlertDialogItemLongClick()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(R.array.connection_action_name, this);
		this.alertDialogItemLongClick = builder.create();
	}
	
	/**
	 * @author  samar
	 */
	private class ConnectionListAdapter extends BaseAdapter
	{
		/**
		 * @uml.property  name="connections"
		 * @uml.associationEnd  
		 */
		private ConnectionList connections;
		private LayoutInflater layoutInflater;
		
		private int connectionUsedPosition;
		
		public ConnectionListAdapter(Context context, ConnectionList connections)
		{
			this.connections = connections;
			
			this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			this.connectionUsedPosition = this.connections.getUsedPosition();
		}
		
		@Override
		public void notifyDataSetChanged()
		{
			super.notifyDataSetChanged();
			
			this.connectionUsedPosition = this.connections.getUsedPosition();
		}
		
		@Override
		public int getCount()
		{
			return this.connections.getCount();
		}
		
		@Override
		public Connection getItem(int position)
		{
			return this.connections.get(position);
		}
		
		@Override
		public long getItemId(int position)
		{
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ConnectionViewHolder holder;
			
			if (convertView == null)
			{
				holder = new ConnectionViewHolder();
				
				convertView = this.layoutInflater.inflate(R.layout.connection, null);
				
				holder.use = (RadioButton) convertView.findViewById(R.id.use);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				
				convertView.setTag(holder);
			}
			else
			{
				holder = (ConnectionViewHolder) convertView.getTag();
			}
			
			Connection connection = this.connections.get(position);
			
			holder.use.setChecked(position == this.connectionUsedPosition);
			
			if (connection instanceof ConnectionWifi)
			{
				holder.icon.setImageResource(R.drawable.wifi);
			}
			
			holder.name.setText(connection.getName());
			
			return convertView;
		}
		
		private class ConnectionViewHolder
		{
			public RadioButton use;
			public ImageView icon;
			public TextView name;
		}
	}
}
