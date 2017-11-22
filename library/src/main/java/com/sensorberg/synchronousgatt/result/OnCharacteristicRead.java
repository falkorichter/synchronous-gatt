package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class OnCharacteristicRead extends Result {

	public final BluetoothGattCharacteristic characteristic;
	public final int status;

	public OnCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		super(ON_CHARACTERISTIC_READ, gatt);
		this.characteristic = characteristic;
		this.status = status;
	}
}
