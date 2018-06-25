package com.sensorberg.synchronousgatt.sample

import android.bluetooth.BluetoothGattDescriptor
import android.os.Bundle
import android.os.ParcelUuid
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.sensorberg.permissionbitte.BitteBitte
import com.sensorberg.permissionbitte.PermissionBitte
import com.sensorberg.synchronousgatt.SynchronousGatt
import no.nordicsemi.android.support.v18.scanner.*
import java.util.*

class MainActivity : AppCompatActivity(), BitteBitte {

	private var isStarted = false
	private var isScanning = false
	private var hasPermission = false

	private val settings = ScanSettings.Builder()
		.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
		.setUseHardwareFilteringIfSupported(false)
		.build()

	private val serviceUuid = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
	private val characteristicWrite = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
	private val characteristicRead = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
	private val descriptorNotify = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
	private val enableNotify = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE

	private val scanner = BluetoothLeScannerCompat.getScanner()
	private val results = mutableMapOf<String, ScanResult>()
	private val filters = listOf<ScanFilter>(ScanFilter
												 .Builder()
												 .setServiceUuid(ParcelUuid(serviceUuid))
												 .build())

	private val adapter = Adapter { scanResult ->
		Thread {
			Log.i("SyncGatt", "Starting Gatt Connection")
			try {
				SynchronousGatt(scanResult.device).apply {
					connectGatt(this@MainActivity, false, 10000)
					discoverServices(3000)

					val notify = bluetoothGatt
						.getService(serviceUuid)
						.getCharacteristic(characteristicRead)
						.getDescriptor(descriptorNotify)
					notify.value = enableNotify
					writeDescriptor(notify, 3000)

					val writeCharacteristic = bluetoothGatt.getService(serviceUuid).getCharacteristic(characteristicWrite)
					writeCharacteristic.setValue("open door")
					writeCharacteristic(writeCharacteristic, 3000)

					val changed = awaitCharacteristicChange(5000)
					onResult(changed.characteristic.getStringValue(0))
					disconnect(1000)
				}
			} catch (e: Exception) {
				onResult(e.message ?: e.toString())
			}
			Log.i("SyncGatt", "Gatt Connection complete")
		}.start()
	}

	private fun onResult(data: String) {
		runOnUiThread {
			Log.i("SyncGatt", data)
			Toast.makeText(this, data, Toast.LENGTH_LONG).show()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		val rv = findViewById<RecyclerView>(R.id.recycler)
		rv.layoutManager = LinearLayoutManager(this)
		rv.adapter = adapter
		PermissionBitte.ask(this, this)
	}

	override fun onStart() {
		super.onStart()
		isStarted = true
		startIfPossible()
	}

	private fun startIfPossible() {
		if (isStarted && hasPermission && !isScanning) {
			isScanning = true
			scanner.startScan(filters, settings, scanCallback)
		}
	}

	private val scanCallback = object : ScanCallback() {
		override fun onScanResult(callbackType: Int, result: ScanResult) {
			results[result.device.address] = result
			val data = results.values.filter { it.rssi > -55 }.sortedBy { it.rssi }
			Log.v("SyncGatt", "(${data.size}) onScanResult $result")
			adapter.submitList(data)
		}
	}

	override fun onStop() {
		isStarted = false
		if (isScanning) {
			isScanning = false
			scanner.stopScan(scanCallback)
		}
		super.onStop()
	}

	override fun askNicer() {
		PermissionBitte.ask(this, this)
	}

	override fun noYouCant() {
		PermissionBitte.goToSettings(this)
	}

	override fun yesYouCan() {
		hasPermission = true
		startIfPossible()
	}

}
