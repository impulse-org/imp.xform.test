/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.xform.test;

import lpg.runtime.IMessageHandler;

public class SystemOutMessageHandler implements IMessageHandler {
    public void handleMessage(int errorCode, int [] msgLocation, int[] errorLocation, String filename, String [] errorInfo) {
        int offset = msgLocation[IMessageHandler.OFFSET_INDEX],
            length = msgLocation[IMessageHandler.LENGTH_INDEX];
        String message = "";
        for (int i = 0; i < errorInfo.length; i++)
            message += (errorInfo[i] + (i < errorInfo.length - 1 ? " " : ""));

	System.out.println("[" + offset + ":" + length + "] " + message);
    }
}
