package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;

public class OnServicesDiscovered extends Result {
	public final int status;

	public OnServicesDiscovered(BluetoothGatt gatt, int status) {
		super(ON_SERVICES_DISCOVERED, gatt);
		this.status = status;
	}
}
