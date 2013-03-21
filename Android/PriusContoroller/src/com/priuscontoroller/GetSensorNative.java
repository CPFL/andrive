package com.priuscontoroller;
public class GetSensorNative {
	static{
		System.loadLibrary("sensordatatransmission");
	}
	public static native void connectServer(String address, int port_number);
	public static native void closeConnect();
	public static native void sendSensorValue(int pitch, int accelerator, int brake);
}