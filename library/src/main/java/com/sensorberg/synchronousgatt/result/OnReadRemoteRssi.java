package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;

public class OnReadRemoteRssi extends Result {

	public final int rssi;
	public final int status;

	public OnReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		super(ON_READ_REMOTE_RSSI, gatt);
		this.rssi = rssi;
		this.status = status;
	}
}
