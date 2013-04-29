package org.techgaun.remotepcdroid.protocol;

import java.awt.Robot;
import java.awt.event.InputEvent;

public class IRobotAdaptor implements IRobot
{
	
	private Robot robot = null;
	
	public IRobotAdaptor(Robot robot)
	{
		this.robot = robot;
	}
	
	@Override
	public void keyPress(int keycode)
	{
		robot.keyPress(keycode);
	}
	@Override
	public void keyRelease(int keycode)
	{
		robot.keyRelease(keycode);
	}
	@Override
	public void mouseMove(int arg0, int arg1)
	{
		robot.mouseMove(arg0, arg1);
	}
	@Override
	public void mousePress(int arg0)
	{
		robot.mousePress(mapButtons(arg0));
	}
	@Override
	public void mouseRelease(int arg0)
	{
		robot.mouseRelease(mapButtons(arg0));
	}
	@Override
	public void mouseWheel(int arg0)
	{
		robot.mouseWheel(arg0);
	}
	@Override
	public void mouseClick(int arg0)
	{
		mousePress(arg0);
		mouseRelease(arg0);
		
	}

	private int mapButtons(int buttons)
	{
		switch(buttons)
		{
		case 1:
			return InputEvent.BUTTON1_MASK;
		case 21:
			return InputEvent.BUTTON2_MASK;
		case 3:
			return InputEvent.BUTTON3_MASK;
		}
		return 0;
	}
}
