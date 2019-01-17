package com.sensorberg.synchronousgatt

import android.annotation.TargetApi
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.os.Build
import com.sensorberg.executioner.Executioner.UI
import com.sensorberg.executioner.Executioner.runOn
import java.util.concurrent.Exchanger

class SynchronousGatt(private val device: BluetoothDevice) {

	private val callback = GattCallback()
	private var bluetoothGatt: BluetoothGatt? = null

	fun getBluetoothGatt(): BluetoothGatt? {
		return bluetoothGatt
	}

	fun connectGatt(context: Context,
					autoConnect: Boolean,
					transport: Int,
					timeout: Long): GattResult.OnConnectionStateChange {
		val gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			connectGattM(context, autoConnect, transport)
		} else {
			connectGattCompat(context, autoConnect)
		}
		callback.setGatt(gatt)
		bluetoothGatt = gatt
		return callback.getResultByType(GattResult.OnConnectionStateChange::class.java, timeout)
	}

	fun discoverServices(timeout: Long): GattResult.OnServicesDiscovered {
		executeGatt { it.discoverServices() }
		return callback.getResultByType(GattResult.OnServicesDiscovered::class.java, timeout)
	}

	fun readCharacteristic(characteristic: BluetoothGattCharacteristic, timeout: Long): GattResult.OnCharacteristicRead {
		executeGatt { it.readCharacteristic(characteristic) }
		return callback.getResultByType(GattResult.OnCharacteristicRead::class.java, timeout)
	}

	fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, timeout: Long): GattResult.OnCharacteristicWrite {
		executeGatt { it.writeCharacteristic(characteristic) }
		return callback.getResultByType(GattResult.OnCharacteristicWrite::class.java, timeout)
	}

	fun awaitCharacteristicChange(timeout: Long): GattResult.OnCharacteristicChanged {
		return callback.getResultByType(GattResult.OnCharacteristicChanged::class.java, timeout)
	}

	fun readDescriptor(descriptor: BluetoothGattDescriptor, timeout: Long): GattResult.OnDescriptorRead {
		executeGatt { it.readDescriptor(descriptor) }
		return callback.getResultByType(GattResult.OnDescriptorRead::class.java, timeout)
	}

	fun writeDescriptor(descriptor: BluetoothGattDescriptor, timeout: Long): GattResult.OnDescriptorWrite {
		executeGatt { it.writeDescriptor(descriptor) }
		return callback.getResultByType(GattResult.OnDescriptorWrite::class.java, timeout)
	}

	fun readRemoteRssi(timeout: Long): GattResult.OnReadRemoteRssi {
		executeGatt { it.readRemoteRssi() }
		return callback.getResultByType(GattResult.OnReadRemoteRssi::class.java, timeout)
	}

	fun disconnect(timeout: Long): GattResult.OnConnectionStateChange {
		executeOnUiSync { bluetoothGatt?.disconnect() }
		return callback.getResultByType(GattResult.OnConnectionStateChange::class.java, timeout, true)
	}

	fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic, enable: Boolean): Boolean {
		return executeGatt { it.setCharacteristicNotification(characteristic, enable) }
	}

	fun beginReliableWrite(): Boolean {
		return executeGatt { it.beginReliableWrite() }
	}

	fun executeReliableWrite(timeout: Long): GattResult.OnReliableWriteCompleted {
		executeGatt { it.executeReliableWrite() }
		return callback.getResultByType(GattResult.OnReliableWriteCompleted::class.java, timeout)
	}

	fun requestMtu(mtu: Int, timeout: Long): GattResult.OnMtuChanged {
		executeGatt { it.requestMtu(mtu) }
		return callback.getResultByType(GattResult.OnMtuChanged::class.java, timeout)
	}

	fun close() {
		executeOnUiSync { bluetoothGatt?.close() }
	}

	fun abortReliableWrite() {
		executeOnUiSync { bluetoothGatt?.abortReliableWrite() }
	}

	// helpers
	@TargetApi(Build.VERSION_CODES.M)
	private fun connectGattM(context: Context,
							 autoConnect: Boolean,
							 transport: Int): BluetoothGatt {
		return executeOnUiSync { device.connectGatt(context, autoConnect, callback, transport) }
	}

	private fun connectGattCompat(context: Context,
								  autoConnect: Boolean): BluetoothGatt {
		return executeOnUiSync { device.connectGatt(context, autoConnect, callback) }
	}

	private fun <T> executeOnUiSync(call: () -> T): T {
		val exchanger = Exchanger<T>()
		runOn(UI) { exchanger.exchange(call.invoke()) }
		return exchanger.exchange(null)
	}

	private fun executeGatt(call: (BluetoothGatt) -> Boolean): Boolean {
		val gatt = bluetoothGatt
		if (gatt == null) throw IllegalStateException("Call 'connect()' before any other operation")

		val value = executeOnUiSync { call.invoke(gatt) }
		if (!value) throw GattException.NoResult()
		return value
	}

}
