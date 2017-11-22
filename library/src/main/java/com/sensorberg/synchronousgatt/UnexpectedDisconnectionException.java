package com.sensorberg.synchronousgatt;

import com.sensorberg.synchronousgatt.result.OnConnectionStateChange;

public class UnexpectedDisconnectionException extends Exception {

	private final OnConnectionStateChange result;

	UnexpectedDisconnectionException(OnConnectionStateChange result) {
		super("Bluetooth disconnected unexpectedly");
		this.result = result;
	}

	public OnConnectionStateChange getResult() {
		return result;
	}
}
