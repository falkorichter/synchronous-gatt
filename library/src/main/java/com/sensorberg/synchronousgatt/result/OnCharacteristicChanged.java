package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class OnCharacteristicChanged extends Result {

	public final BluetoothGattCharacteristic characteristic;

	public OnCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		super(ON_CHARACTERISTIC_CHANGED, gatt);
		this.characteristic = characteristic;
	}
}
