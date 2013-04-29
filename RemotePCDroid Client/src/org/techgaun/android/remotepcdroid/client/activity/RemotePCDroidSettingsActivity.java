package org.techgaun.android.remotepcdroid.client.activity;

import org.techgaun.android.remotepcdroid.client.R;
import org.techgaun.android.remotepcdroid.client.app.RemotePCDroid;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author samar
 */
public class RemotePCDroidSettingsActivity extends PreferenceActivity
{
	private static final String[] tabFloatPreferences = {
	        "control_sensitivity", "control_acceleration"
	};
	private static final String[] tabIntPreferences = {
	        "control_click_delay", "control_hold_delay"
	};
	
	private static final int resetPreferencesMenuItemId = 0;
	
	/**
	 * @uml.property name="application"
	 * @uml.associationEnd
	 */
	private RemotePCDroid application;
	private SharedPreferences preferences;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.addPreferencesFromResource(R.xml.settings);
		
		this.application = (RemotePCDroid) this.getApplication();
		this.preferences = this.application.getPreferences();
	}
	
	protected void onPause()
	{
		super.onPause();
		
		this.checkPreferences();
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, resetPreferencesMenuItemId, Menu.NONE, this.getResources().getString(R.string.text_reset_preferences));
		
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case resetPreferencesMenuItemId:
				this.resetPreferences();
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void checkPreferences()
	{
		Editor editor = this.preferences.edit();
		
		for (String s : tabFloatPreferences)
		{
			try
			{
				Float.parseFloat(this.preferences.getString(s, null));
			}
			catch (NumberFormatException e)
			{
				// this.application.debug(e);
				editor.remove(s);
			}
		}
		
		for (String s : tabIntPreferences)
		{
			try
			{
				Integer.parseInt(this.preferences.getString(s, null));
			}
			catch (NumberFormatException e)
			{
				// this.application.debug(e);
				editor.remove(s);
			}
		}
		
		editor.commit();
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, true);
	}
	
	private void resetPreferences()
	{
		this.setPreferenceScreen(null);
		
		Editor editor = this.preferences.edit();
		editor.clear();
		editor.commit();
		
		PreferenceManager.setDefaultValues(this, R.xml.settings, true);
		
		this.addPreferencesFromResource(R.xml.settings);
	}
}
