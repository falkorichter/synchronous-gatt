package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;

public class OnConnectionStateChange extends Result {

	public final int status;
	public final int newState;

	public OnConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
		super(ON_CONNECTION_STATE_CHANGED, gatt);
		this.status = status;
		this.newState = newState;
	}
}
