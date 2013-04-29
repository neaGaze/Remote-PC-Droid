package org.techgaun.android.remotepcdroid.client.activity;

import org.techgaun.android.remotepcdroid.client.R;
import org.techgaun.android.remotepcdroid.client.activity.connection.RemotePCDroidConnListActivity;
import org.techgaun.android.remotepcdroid.client.app.RemotePCDroid;
import org.techgaun.android.remotepcdroid.client.view.CustomImageView;
import org.techgaun.remotepcdroid.protocol.RemotePCDroidActionReceiver;
import org.techgaun.remotepcdroid.protocol.action.KeyboardAction;
import org.techgaun.remotepcdroid.protocol.action.MouseClickAction;
import org.techgaun.remotepcdroid.protocol.action.MouseMoveAction;
import org.techgaun.remotepcdroid.protocol.action.MouseWheelAction;
import org.techgaun.remotepcdroid.protocol.action.RemotePCDroidAction;
import org.techgaun.remotepcdroid.protocol.action.ScreenCaptureResponseAction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * @author samar
 */
public class RemotePCDroidControlActivity extends Activity implements RemotePCDroidActionReceiver
{
	/**
	 * @uml.property name="application"
	 * @uml.associationEnd
	 */
	private RemotePCDroid application;
	private SharedPreferences preferences;
	
	/**
	 * @uml.property name="controlView"
	 * @uml.associationEnd
	 */
	private CustomImageView controlView;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.application = (RemotePCDroid) this.getApplication();
		
		this.preferences = this.application.getPreferences();
		
		this.checkFullscreen();
		
		this.setContentView(R.layout.control);
		
		// this.setButtonsSize();
		
		this.controlView = (CustomImageView) this.findViewById(R.id.controlView);
		
		this.checkOnCreate();
	}
	
	protected void onResume()
	{
		super.onResume();
		
		this.application.registerActionReceiver(this);
	}
	
	protected void onPause()
	{
		super.onPause();
		this.application.unregisterActionReceiver(this);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		int unicode = event.getUnicodeChar();
		
		/*
		 * if (this.preferences.getBoolean("unicode_nepali", true) == true) { //
		 * unicode support from here
		 * 
		 * this.application.sendAction(new KeyboardAction(unicode)); }
		 */
		
		if (unicode == 0 && event.getKeyCode() == KeyEvent.KEYCODE_DEL)
		{
			unicode = KeyboardAction.UNICODE_BACKSPACE;
		}
		
		if (unicode != 0)
		{
			this.application.sendAction(new KeyboardAction(unicode));
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, 0, Menu.NONE, this.getResources().getString(R.string.text_file_explorer));
		menu.add(Menu.NONE, 1, Menu.NONE, this.getResources().getString(R.string.text_keyboard));
		menu.add(Menu.NONE, 2, Menu.NONE, this.getResources().getString(R.string.text_shortcuts));
		menu.add(Menu.NONE, 3, Menu.NONE, this.getResources().getString(R.string.text_connections));
		menu.add(Menu.NONE, 4, Menu.NONE, this.getResources().getString(R.string.text_settings));
		menu.add(Menu.NONE, 5, Menu.NONE, this.getResources().getString(R.string.text_help));
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case 0:
				this.startActivity(new Intent(this, RemotePCDroidFEActivity.class));
				break;
			case 1:
				this.toggleKeyboard();
				break;
			case 2:
				this.startActivity(new Intent(this, RemotePCDroidShortcutsActivity.class));
				break;
			case 3:
				this.startActivity(new Intent(this, RemotePCDroidConnListActivity.class));
				break;
			case 4:
				this.startActivity(new Intent(this, RemotePCDroidSettingsActivity.class));
				break;
			case 5:
				this.startActivity(new Intent(this, RemotePCDroidHelpActivity.class));
				break;
		}
		
		return true;
	}
	
	public void receiveAction(RemotePCDroidAction action)
	{
		if (action instanceof ScreenCaptureResponseAction)
		{
			this.controlView.receiveAction((ScreenCaptureResponseAction) action);
		}
	}
	
	public void mouseClick(byte button, boolean state)
	{
		this.application.sendAction(new MouseClickAction(button, state));
	}
	
	public void mouseMove(int moveX, int moveY)
	{
		this.application.sendAction(new MouseMoveAction((short) moveX, (short) moveY));
	}
	
	public void mouseWheel(int amount)
	{
		this.application.sendAction(new MouseWheelAction((byte) amount));
	}
	
	private void toggleKeyboard()
	{
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, 0);
	}
	
	private void checkFullscreen()
	{
		if (this.preferences.getBoolean("fullscreen", false))
		{
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
	
	/*
	 * private void setButtonsSize() { LinearLayout clickLayout = (LinearLayout)
	 * this.findViewById(R.id.clickLayout);
	 * 
	 * int orientation = this.getResources().getConfiguration().orientation;
	 * 
	 * // int size = (int) //
	 * (Float.parseFloat(this.preferences.getString("buttons_size", null)) * //
	 * this.getResources().getDisplayMetrics().density); int size = (int) (50 *
	 * this.getResources().getDisplayMetrics().density); if (orientation ==
	 * Configuration.ORIENTATION_PORTRAIT) {
	 * clickLayout.getLayoutParams().height = (int) size; } else if (orientation
	 * == Configuration.ORIENTATION_LANDSCAPE) {
	 * clickLayout.getLayoutParams().width = (int) size; } }
	 */
	
	private void checkOnCreate()
	{
		if (this.isFirstTimeRun())
		{
			this.firstRunDialog();
		}
	}
	
	private boolean isFirstTimeRun()
	{
		return this.preferences.getBoolean("firstRun", true);
	}
	
	private void firstRunDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setMessage(R.string.text_first_run_dialog);
		builder.setPositiveButton(R.string.text_yes, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				RemotePCDroidControlActivity.this.startActivity(new Intent(RemotePCDroidControlActivity.this, RemotePCDroidHelpActivity.class));
				RemotePCDroidControlActivity.this.disableFirstRun();
			}
		});
		builder.setNegativeButton(R.string.text_no, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				RemotePCDroidControlActivity.this.disableFirstRun();
			}
		});
		builder.create().show();
	}
	
	private void disableFirstRun()
	{
		Editor editor = this.preferences.edit();
		editor.putBoolean("firstRun", false);
		editor.commit();
	}
	
}
