package com.andrive;
public class GetSensorNative {
	static{
		System.loadLibrary("sensordatatransmission");
	}
	public static native void connectServer(String address, int port_number);
	public static native void closeConnect();
	public static native void sendSensorValue(float pitch, float accelerator, float brake, int gearNum);
	public static native void getSignal();
}
