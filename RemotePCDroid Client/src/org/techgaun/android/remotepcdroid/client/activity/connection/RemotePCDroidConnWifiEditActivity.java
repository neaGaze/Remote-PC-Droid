package org.techgaun.android.remotepcdroid.client.activity.connection;

import org.techgaun.android.remotepcdroid.client.R;
import org.techgaun.android.remotepcdroid.client.connection.ConnectionWifi;

import android.os.Bundle;
import android.widget.EditText;

/**
 * @author samar
 */
public class RemotePCDroidConnWifiEditActivity extends RemotePCDroidConnEditActivity
{
	/**
	 * @uml.property name="connection"
	 * @uml.associationEnd
	 */
	private ConnectionWifi connection;
	
	private EditText host;
	private EditText port;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		this.setContentView(R.layout.connectionwifiedit);
		
		super.onCreate(savedInstanceState);
		
		this.connection = (ConnectionWifi) connectionParam;
		
		/*
		 * InputFilter[] filters = new InputFilter[1]; filters[0] = new
		 * InputFilter() { public CharSequence filter(CharSequence source, int
		 * start, int end, Spanned dest, int dstart, int dend) { if (end >
		 * start) { String destTxt = dest.toString(); String resultingTxt =
		 * destTxt.substring(0, dstart) + source.subSequence(start, end) +
		 * destTxt.substring(dend); if (!resultingTxt.matches
		 * ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
		 * return ""; } else { String[] splits = resultingTxt.split("\\."); for
		 * (int i=0; i<splits.length; i++) { if (Integer.valueOf(splits[i]) >
		 * 255) { return ""; } } } } return null; } };
		 * 
		 * this.host.setFilters(filters);
		 */
		this.host = (EditText) this.findViewById(R.id.host);
		this.port = (EditText) this.findViewById(R.id.port);
	}
	
	protected void onResume()
	{
		super.onResume();
		
		this.host.setText(this.connection.getHost());
		this.port.setText(Integer.toString(this.connection.getPort()));
	}
	
	protected void onPause()
	{
		super.onPause();
		
		this.connection.setHost(this.host.getText().toString());
		this.connection.setPort(Integer.parseInt(this.port.getText().toString()));
	}
}
