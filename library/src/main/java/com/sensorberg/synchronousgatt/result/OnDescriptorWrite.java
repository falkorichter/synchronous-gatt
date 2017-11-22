package com.sensorberg.synchronousgatt.result;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

public class OnDescriptorWrite extends Result {

	public final BluetoothGattDescriptor descriptor;
	public final int status;

	public OnDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		super(ON_DESCRIPTOR_WRITE, gatt);
		this.descriptor = descriptor;
		this.status = status;
	}
}
