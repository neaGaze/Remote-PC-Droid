package org.techgaun.remotepcdroid.protocol.action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ProtocolException;

public abstract class RemotePCDroidAction
{
	public static final byte MOUSE_MOVE = 0;
	public static final byte MOUSE_CLICK = 1;
	public static final byte MOUSE_WHEEL = 2;
	public static final byte KEYBOARD = 3;
	public static final byte AUTHENTICATION = 4;
	public static final byte AUTHENTICATION_RESPONSE = 5;
	public static final byte SCREEN_CAPTURE_REQUEST = 6;
	public static final byte SCREEN_CAPTURE_RESPONSE = 7;
	public static final byte FILE_EXPLORE_REQUEST = 8;
	public static final byte FILE_EXPLORE_RESPONSE = 9;
	public static final byte SHUTDOWN_SERVER = 10;
	public static final byte TASK_MANAGER = 11;
	public static final byte CLOSE_WINDOWS = 12;
	public static final byte MINIMIZE_WINDOWS = 13;
	
	public static RemotePCDroidAction parse(DataInputStream dis) throws IOException
	{
		byte type = dis.readByte();
		
		switch (type)
		{
			case MOUSE_MOVE:
				return MouseMoveAction.parse(dis);
			case MOUSE_CLICK:
				return MouseClickAction.parse(dis);
			case MOUSE_WHEEL:
				return MouseWheelAction.parse(dis);
			case KEYBOARD:
				return KeyboardAction.parse(dis);
			case AUTHENTICATION:
				return AuthenticationAction.parse(dis);
			case AUTHENTICATION_RESPONSE:
				return AuthenticationResponseAction.parse(dis);
			case SCREEN_CAPTURE_REQUEST:
				return ScreenCaptureRequestAction.parse(dis);
			case SCREEN_CAPTURE_RESPONSE:
				return ScreenCaptureResponseAction.parse(dis);
			case FILE_EXPLORE_REQUEST:
				return FileExploreRequestAction.parse(dis);
			case FILE_EXPLORE_RESPONSE:
				return FileExploreResponseAction.parse(dis);
			case SHUTDOWN_SERVER:
				return ShutDownAction.parse(dis);
			case TASK_MANAGER:
				return null;
			case CLOSE_WINDOWS:
				return null;
			case MINIMIZE_WINDOWS:
				return null;
			default:
				throw new ProtocolException();
		}
	}
	
	public abstract void toDataOutputStream(DataOutputStream dos) throws IOException;
}
