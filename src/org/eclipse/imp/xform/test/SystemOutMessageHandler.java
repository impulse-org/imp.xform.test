package com.ibm.watson.safari.xform.test;


import org.eclipse.uide.editor.IMessageHandler;

public class SystemOutMessageHandler implements IMessageHandler {
    public void handleMessage(int offset, int length, String message) {
	System.out.println("[" + offset + ":" + length + "] " + message);
    }
}
