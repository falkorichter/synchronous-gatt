package com.sensorberg.synchronousgatt

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import timber.log.Timber
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

internal class GattCallback : BluetoothGattCallback() {

	private val results: BlockingQueue<GattResult> = LinkedBlockingQueue<GattResult>()
	private lateinit var bluetoothGatt: BluetoothGatt

	private fun newResult(result: GattResult) {
		result.gatt = bluetoothGatt
		results.offer(result)
	}

	internal fun <T : GattResult> getResultByType(type: Class<T>, timeout: Long, isDisconnect: Boolean = false): T {
		val result = getNextResult(timeout)

		if (result == null) { // poll() returns null on timeout
			throw GattException.Timeout()
		}

		if (!isDisconnect &&
			result is GattResult.OnConnectionStateChange &&
			result.newState == BluetoothGatt.STATE_DISCONNECTED) {
			throw GattException.UnexpectedDisconnection()
		}

		if (result.javaClass != type) {
			throw GattException.UnexpectedResult(type, result.javaClass)
		}



		return result as T
	}

	private fun getNextResult(timeout: Long): GattResult? {
		try {
			return results.poll(timeout, TimeUnit.MILLISECONDS)
		} catch (e: InterruptedException) {
			throw GattException.NoResult()
		}
	}

	override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
		Timber.d("onConnectionStateChange(status:$status; newState:$newState)")
		newResult(GattResult.OnConnectionStateChange(status, newState))
	}

	override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
		Timber.d("onServicesDiscovered(status:$status)")
		newResult(GattResult.OnServicesDiscovered(status, gatt.services))
	}

	override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
		Timber.d("onCharacteristicRead(status:$status; uuid:${characteristic.uuid})")
		newResult(GattResult.OnCharacteristicRead(characteristic, status))
	}

	override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
		Timber.d("onCharacteristicWrite(status:$status; uuid:${characteristic.uuid})")
		newResult(GattResult.OnCharacteristicWrite(characteristic, status))
	}

	override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
		Timber.d("onCharacteristicChanged(uuid:${characteristic.uuid})")
		newResult(GattResult.OnCharacteristicChanged(characteristic))
	}

	override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
		Timber.d("onDescriptorRead(status:$status)")
		newResult(GattResult.OnDescriptorRead(descriptor, status))
	}

	override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
		Timber.d("onDescriptorWrite(status:$status; descriptor:${descriptor.uuid})")
		newResult(GattResult.OnDescriptorWrite(descriptor, status))
	}

	override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
		Timber.d("onMtuChanged(status:$status; mtu:$mtu)")
		newResult(GattResult.OnMtuChanged(mtu, status))
	}

	override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
		Timber.d("onReadRemoteRssi(status:$status; rssi:$rssi)")
		newResult(GattResult.OnReadRemoteRssi(rssi, status))
	}

	override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
		Timber.d("onReliableWriteCompleted(status:$status)")
		newResult(GattResult.OnReliableWriteCompleted(status))
	}

	internal fun setGatt(bluetoothGatt: BluetoothGatt) {
		this.bluetoothGatt = bluetoothGatt
	}

}