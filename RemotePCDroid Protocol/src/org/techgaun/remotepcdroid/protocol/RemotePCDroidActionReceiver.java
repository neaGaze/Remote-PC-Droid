package org.techgaun.remotepcdroid.protocol;

import org.techgaun.remotepcdroid.protocol.action.RemotePCDroidAction;

public interface RemotePCDroidActionReceiver
{
	public void receiveAction(RemotePCDroidAction action);
}
