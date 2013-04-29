package org.techgaun.android.remotepcdroid.client.activity;

import org.techgaun.android.remotepcdroid.client.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class RemotePCDroidInit extends Activity
{
	private ProgressDialog startProgress;
	
	// private RemotePCDroid application;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init);
		initialize();
		
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		initialize();
	}
	
	protected void onPause()
	{
		super.onPause();
	}
	
	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { //
	 * Handle the back button if (keyCode == KeyEvent.KEYCODE_BACK) { // Ask the
	 * user if they want to quit new
	 * AlertDialog.Builder(this).setIcon(android.R.
	 * drawable.ic_dialog_alert).setTitle
	 * ("Exit").setMessage("Are you sure you want to leave?"
	 * ).setNegativeButton(android.R.string.cancel,
	 * null).setPositiveButton(android.R.string.ok, new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * Exit the activity RemotePCDroidInit.this.finish(); } }).show();
	 * 
	 * // Say that we've consumed the event return true; }
	 * 
	 * return super.onKeyDown(keyCode, event); }
	 */

	private void initialize()
	{
		startProgress = new ProgressDialog(this);
		startProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		startProgress.setCancelable(true);
		
		startProgress.show(RemotePCDroidInit.this, "", "RemotePC Droid is loading...");
		/*
		 * // check if wi-fi is enabled or not. ConnectivityManager connMgr =
		 * (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		 * NetworkInfo netInfo = (NetworkInfo)
		 * connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		 * 
		 * // should be ! but for now we will have to do to check on emulator if
		 * (netInfo.isAvailable()) { // go for intent to enable wi-fi Intent
		 * intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
		 * startActivity(intent); } else {
		 */
		new Handler().postDelayed(new Runnable()
		{
			
			@Override
			public void run()
			{
				startProgress.dismiss();
				Intent intent = new Intent(RemotePCDroidInit.this, RemotePCDroidControlActivity.class);
				startActivity(intent);
			}
		}, 2000);
		// }
	}
}
