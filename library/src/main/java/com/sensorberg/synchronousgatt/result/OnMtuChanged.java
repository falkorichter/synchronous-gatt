package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;

public class OnMtuChanged extends Result {

	public final int mtu;
	public final int status;

	public OnMtuChanged(BluetoothGatt gatt, int mtu, int status) {
		super(ON_MTU_CHANGED, gatt);
		this.mtu = mtu;
		this.status = status;
	}
}
