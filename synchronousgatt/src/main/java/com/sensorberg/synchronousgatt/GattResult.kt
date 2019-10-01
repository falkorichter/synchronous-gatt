package com.sensorberg.synchronousgatt

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import java.util.*

sealed class GattResult {

	lateinit var gatt: BluetoothGatt

	data class OnConnectionStateChange(
		val status: Int,
		val newState: Int) : GattResult()

	data class OnServicesDiscovered(
		val status: Int,
		val services: List<BluetoothGattService>) : GattResult() {
		fun getService(uuid: UUID): BluetoothGattService {
			return gatt.getService(uuid)
		}
	}

	data class OnCharacteristicRead(
		val characteristic: BluetoothGattCharacteristic,
		val status: Int) : GattResult()

	data class OnCharacteristicWrite(
		val characteristic: BluetoothGattCharacteristic,
		val status: Int) : GattResult()

	data class OnCharacteristicChanged(
		val characteristic: BluetoothGattCharacteristic) : GattResult()

	data class OnDescriptorRead(
		val descriptor: BluetoothGattDescriptor,
		val status: Int) : GattResult()

	data class OnDescriptorWrite(
		val descriptor: BluetoothGattDescriptor,
		val status: Int) : GattResult()

	data class OnMtuChanged(
		val mtu: Int,
		val status: Int) : GattResult()

	data class OnReadRemoteRssi(
		val rssi: Int,
		val status: Int) : GattResult()

	data class OnReliableWriteCompleted(
		val status: Int) : GattResult()

}