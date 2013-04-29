package org.techgaun.android.remotepcdroid.client.activity.connection;

import org.techgaun.android.remotepcdroid.client.R;
import org.techgaun.android.remotepcdroid.client.connection.Connection;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author  samar
 */
public class RemotePCDroidConnEditActivity extends Activity implements OnClickListener
{
	/**
	 * @uml.property  name="connectionParam"
	 * @uml.associationEnd  
	 */
	public static Connection connectionParam;
	
	/**
	 * @uml.property  name="connection"
	 * @uml.associationEnd  
	 */
	private Connection connection;
	
	private Button save;
	
	private EditText name;
	private EditText password;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.connection = connectionParam;
		
		this.save = (Button) this.findViewById(R.id.save);
		this.save.setOnClickListener(this);
		
		this.name = (EditText) this.findViewById(R.id.name);
		this.password = (EditText) this.findViewById(R.id.password);
	}
	
	protected void onResume()
	{
		super.onResume();
		
		this.name.setText(this.connection.getName());
		this.password.setText(this.connection.getPassword());
	}
	
	protected void onPause()
	{
		super.onPause();
		
		this.connection.setName(this.name.getText().toString());
		this.connection.setPassword(this.password.getText().toString());
	}
	
	public void onClick(View v)
	{
		if (v == this.save)
		{
			this.finish();
		}
	}
}
