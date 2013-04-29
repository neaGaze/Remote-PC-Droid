package org.techgaun.android.remotepcdroid.client.view;

import org.techgaun.android.remotepcdroid.client.R;
import org.techgaun.android.remotepcdroid.client.activity.RemotePCDroidControlActivity;
import org.techgaun.android.remotepcdroid.client.app.RemotePCDroid;
import org.techgaun.remotepcdroid.protocol.action.MouseClickAction;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * @author samar
 */
public class CustomButtonView extends Button
{
	/**
	 * @uml.property name="controlActivity"
	 * @uml.associationEnd
	 */
	private RemotePCDroidControlActivity controlActivity;
	/**
	 * @uml.property name="application"
	 * @uml.associationEnd
	 */
	private RemotePCDroid application;
	
	private byte button;
	/**
	 * @uml.property name="hold"
	 */
	private boolean hold;
	private long holdDelay;
	
	public CustomButtonView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.controlActivity = (RemotePCDroidControlActivity) context;
		this.application = (RemotePCDroid) this.controlActivity.getApplication();
		
		switch (this.getId())
		{
			case R.id.leftClickView:
				this.button = MouseClickAction.BUTTON_LEFT;
				this.setText(" Left ");
				break;
			/*
			 * case R.id.middleClickView: this.button =
			 * MouseClickAction.BUTTON_MIDDLE; this.setText(" Wheel ");
			 * this.setBackgroundDrawable
			 * (this.getResources().getDrawable(R.drawable.mouse_wheel)); break;
			 */
			case R.id.rightClickView:
				this.button = MouseClickAction.BUTTON_RIGHT;
				this.setText(" Right ");
				break;
			default:
				this.button = MouseClickAction.BUTTON_NONE;
				this.setText(" Button ");
				break;
		}
		
		this.hold = false;
		this.holdDelay = Long.parseLong(this.application.getPreferences().getString("control_hold_delay", null));
	}
	
	/**
	 * @return
	 * @uml.property name="hold"
	 */
	public boolean isHold()
	{
		return hold;
	}
	
	/**
	 * @param hold
	 * @uml.property name="hold"
	 */
	public void setHold(boolean hold)
	{
		this.hold = hold;
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_MOVE:
			{
				this.onTouchMove(event);
				break;
			}
				
			case MotionEvent.ACTION_DOWN:
			{
				this.onTouchDown(event);
				break;
			}
				
			case MotionEvent.ACTION_UP:
			{
				this.onTouchUp(event);
				break;
			}
				
			default:
				break;
		}
		
		return true;
	}
	
	private void onTouchDown(MotionEvent event)
	{
		if (!this.hold)
		{
			this.controlActivity.mouseClick(this.button, MouseClickAction.STATE_DOWN);
			this.setPressed(true);
			this.application.vibrate(50);
		}
		else
		{
			this.hold = false;
		}
	}
	
	private void onTouchMove(MotionEvent event)
	{
		if (!this.hold && event.getEventTime() - event.getDownTime() >= this.holdDelay)
		{
			this.hold = true;
			this.application.vibrate(100);
		}
	}
	
	private void onTouchUp(MotionEvent event)
	{
		if (!this.hold)
		{
			this.controlActivity.mouseClick(this.button, MouseClickAction.STATE_UP);
			this.setPressed(false);
		}
	}
}