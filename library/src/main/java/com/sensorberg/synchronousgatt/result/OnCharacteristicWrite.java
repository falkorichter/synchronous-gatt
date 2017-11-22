package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class OnCharacteristicWrite extends Result {

	public final BluetoothGattCharacteristic characteristic;
	public final int status;

	public OnCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		super(ON_CHARACTERISTIC_WRITE, gatt);
		this.characteristic = characteristic;
		this.status = status;
	}
}
