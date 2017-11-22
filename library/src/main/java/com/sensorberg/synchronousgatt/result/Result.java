package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;

public abstract class Result {

	public static final int ON_CONNECTION_STATE_CHANGED = 1;
	public static final int ON_SERVICES_DISCOVERED = 2;
	public static final int ON_CHARACTERISTIC_READ = 3;
	public static final int ON_CHARACTERISTIC_WRITE = 4;
	public static final int ON_CHARACTERISTIC_CHANGED = 5;
	public static final int ON_DESCRIPTOR_READ = 6;
	public static final int ON_DESCRIPTOR_WRITE = 7;
	public static final int ON_READ_REMOTE_RSSI = 8;
	public static final int ON_RELIABLE_WRITE_COMPLETED = 9;
	public static final int ON_MTU_CHANGED = 10;

	public final int type;
	public final BluetoothGatt gatt;

	public Result(int type, BluetoothGatt gatt) {
		this.type = type;
		this.gatt = gatt;
	}
}
