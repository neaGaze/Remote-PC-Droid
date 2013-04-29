package org.techgaun.android.remotepcdroid.client.app;

import java.io.IOException;
import java.util.HashSet;

import org.techgaun.android.remotepcdroid.client.R;
import org.techgaun.android.remotepcdroid.client.connection.Connection;
import org.techgaun.android.remotepcdroid.client.connection.ConnectionList;
import org.techgaun.remotepcdroid.protocol.RemotePCDroidActionReceiver;
import org.techgaun.remotepcdroid.protocol.RemotePCDroidConnection;
import org.techgaun.remotepcdroid.protocol.action.AuthenticationAction;
import org.techgaun.remotepcdroid.protocol.action.AuthenticationResponseAction;
import org.techgaun.remotepcdroid.protocol.action.RemotePCDroidAction;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * @author samar
 */
public class RemotePCDroid extends Application implements Runnable
{
	private static final long CONNECTION_CLOSE_DELAY = 3000;
	
	/**
	 * @uml.property name="preferences"
	 */
	private SharedPreferences preferences;
	private Vibrator vibrator;
	
	/**
	 * @uml.property name="connection"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	private RemotePCDroidConnection[] connection;
	
	private HashSet<RemotePCDroidActionReceiver> actionReceivers;
	
	private Handler handler;
	
	/**
	 * @uml.property name="closeConnectionScheduler"
	 * @uml.associationEnd
	 */
	private CloseConnectionScheduler closeConnectionScheduler;
	
	/**
	 * @uml.property name="connections"
	 * @uml.associationEnd
	 */
	private ConnectionList connections;
	
	public void onCreate()
	{
		super.onCreate();
		
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.settings, true);
		
		this.vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		
		this.actionReceivers = new HashSet<RemotePCDroidActionReceiver>();
		
		this.handler = new Handler();
		
		this.connection = new RemotePCDroidConnection[1];
		
		this.closeConnectionScheduler = new CloseConnectionScheduler();
		
		this.connections = new ConnectionList(this.preferences);
	}
	
	/**
	 * @return
	 * @uml.property name="preferences"
	 */
	public SharedPreferences getPreferences()
	{
		return this.preferences;
	}
	
	/**
	 * @return
	 * @uml.property name="connections"
	 */
	public ConnectionList getConnections()
	{
		return this.connections;
	}
	
	public void vibrate(long l)
	{
		if (this.preferences.getBoolean("feedback_vibration", true))
		{
			this.vibrator.vibrate(l);
		}
	}
	
	public synchronized void run()
	{
		Connection co = this.connections.getUsed();
		
		if (co != null)
		{
			RemotePCDroidConnection c = null;
			
			try
			{
				c = co.connect(this);
				
				synchronized (this.connection)
				{
					this.connection[0] = c;
				}
				
				try
				{
					this.showMyAppToast(R.string.text_connection_established);
					
					String password = co.getPassword();
					this.sendAction(new AuthenticationAction(password));
					
					while (true)
					{
						RemotePCDroidAction action = c.receiveAction();
						
						this.receiveAction(action);
					}
				}
				finally
				{
					synchronized (this.connection)
					{
						this.connection[0] = null;
					}
					
					c.close();
				}
			}
			catch (IOException e)
			{
				if (c == null)
				{
					this.showMyAppToast(R.string.text_connection_refused);
				}
				else
				{
					this.showMyAppToast(R.string.text_connection_closed);
				}
			}
			catch (IllegalArgumentException e)
			{
				this.showMyAppToast(R.string.text_illegal_connection_parameter);
			}
		}
		else
		{
			this.showMyAppToast(R.string.text_no_connection_selected);
		}
	}
	
	public void sendAction(RemotePCDroidAction action)
	{
		synchronized (this.connection)
		{
			if (this.connection[0] != null)
			{
				try
				{
					this.connection[0].sendAction(action);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	// overload vako, 2 typeko argument lina sakchha either resource id(string)
	// or string
	public void showMyAppToast(int resId)
	{
		if (this.isInternalToast())
		{
			this.showToast(resId);
		}
	}
	
	public void showMyAppToast(String message)
	{
		if (this.isInternalToast())
		{
			this.showToast(message);
		}
	}
	
	public boolean isInternalToast()
	{
		synchronized (this.actionReceivers)
		{
			return !this.actionReceivers.isEmpty();
		}
	}
	
	public void showToast(int resId)
	{
		this.showToast(this.getResources().getString(resId));
	}
	
	public void showToast(final String message)
	{
		this.handler.post(new Runnable()
		{
			public void run()
			{
				Toast.makeText(RemotePCDroid.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void receiveAction(RemotePCDroidAction action)
	{
		synchronized (this.actionReceivers)
		{
			for (RemotePCDroidActionReceiver actionReceiver : this.actionReceivers)
			{
				actionReceiver.receiveAction(action);
			}
		}
		
		if (action instanceof AuthenticationResponseAction)
		{
			this.receiveAuthenticationResponseAction((AuthenticationResponseAction) action);
		}
	}
	
	private void receiveAuthenticationResponseAction(AuthenticationResponseAction action)
	{
		if (action.authenticated)
		{
			this.showMyAppToast(R.string.text_authenticated);
		}
		else
		{
			this.showMyAppToast(R.string.text_not_authenticated);
		}
	}
	
	public void registerActionReceiver(RemotePCDroidActionReceiver actionReceiver)
	{
		synchronized (this.actionReceivers)
		{
			this.actionReceivers.add(actionReceiver);
			
			if (this.actionReceivers.size() > 0)
			{
				synchronized (this.connection)
				{
					if (this.connection[0] == null)
					{
						(new Thread(this)).start();
					}
				}
			}
		}
	}
	
	public void unregisterActionReceiver(RemotePCDroidActionReceiver actionReceiver)
	{
		synchronized (this.actionReceivers)
		{
			this.actionReceivers.remove(actionReceiver);
			
			if (this.actionReceivers.size() == 0)
			{
				this.closeConnectionScheduler.schedule();
			}
		}
	}
	
	private class CloseConnectionScheduler implements Runnable
	{
		private Thread currentThread;
		
		public synchronized void run()
		{
			try
			{
				this.wait(RemotePCDroid.CONNECTION_CLOSE_DELAY);
				
				synchronized (RemotePCDroid.this.actionReceivers)
				{
					if (RemotePCDroid.this.actionReceivers.size() == 0)
					{
						synchronized (RemotePCDroid.this.connection)
						{
							if (RemotePCDroid.this.connection[0] != null)
							{
								RemotePCDroid.this.connection[0].close();
								
								RemotePCDroid.this.connection[0] = null;
							}
						}
					}
				}
				
				this.currentThread = null;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		public synchronized void schedule()
		{
			if (this.currentThread != null)
			{
				this.currentThread.interrupt();
			}
			
			this.currentThread = new Thread(this);
			
			this.currentThread.start();
		}
	}
}
