package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

public class OnDescriptorRead extends Result {

	public final BluetoothGattDescriptor descriptor;
	public final int status;

	public OnDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		super(ON_DESCRIPTOR_READ, gatt);
		this.descriptor = descriptor;
		this.status = status;
	}
}
