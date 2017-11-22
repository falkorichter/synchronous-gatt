package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;

public class OnReliableWriteCompleted extends Result {

	public final int status;

	public OnReliableWriteCompleted(BluetoothGatt gatt, int status) {
		super(ON_RELIABLE_WRITE_COMPLETED, gatt);
		this.status = status;
	}
}
