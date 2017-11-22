package com.sensorberg.synchronousgatt;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sensorberg.synchronousgatt.result.OnCharacteristicChanged;
import com.sensorberg.synchronousgatt.result.OnCharacteristicRead;
import com.sensorberg.synchronousgatt.result.OnCharacteristicWrite;
import com.sensorberg.synchronousgatt.result.OnConnectionStateChange;
import com.sensorberg.synchronousgatt.result.OnDescriptorRead;
import com.sensorberg.synchronousgatt.result.OnDescriptorWrite;
import com.sensorberg.synchronousgatt.result.OnMtuChanged;
import com.sensorberg.synchronousgatt.result.OnReadRemoteRssi;
import com.sensorberg.synchronousgatt.result.OnReliableWriteCompleted;
import com.sensorberg.synchronousgatt.result.OnServicesDiscovered;
import com.sensorberg.synchronousgatt.result.Result;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class Callbacks extends BluetoothGattCallback {

	private static final long TIMEOUT = 1000;
	private final Exchanger<Result> exchanger = new Exchanger<>();

	@NonNull Result getNextResult(long timeout) throws TimeoutException, NoResultException {
		try {
			return exchanger.exchange(null, timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new NoResultException();
		}
	}

	private void btCallbackExchange(@NonNull Result r) {
		try {
			exchanger.exchange(r, TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	private void log(String msg) {
		Log.w("SyncGatt", msg);
	}

	@Override public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
		log("onConnectionStateChange");
		btCallbackExchange(new OnConnectionStateChange(gatt, status, newState));
	}

	@Override public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		log("onServicesDiscovered");
		btCallbackExchange(new OnServicesDiscovered(gatt, status));
	}

	@Override
	public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		log("onCharacteristicRead");
		btCallbackExchange(new OnCharacteristicRead(gatt, characteristic, status));
	}

	@Override
	public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		log("onCharacteristicWrite");
		btCallbackExchange(new OnCharacteristicWrite(gatt, characteristic, status));
	}

	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		log("onCharacteristicChanged");
		btCallbackExchange(new OnCharacteristicChanged(gatt, characteristic));
	}

	@Override
	public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		log("onDescriptorRead");
		btCallbackExchange(new OnDescriptorRead(gatt, descriptor, status));
	}

	@Override
	public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		log("onDescriptorWrite");
		btCallbackExchange(new OnDescriptorWrite(gatt, descriptor, status));
	}

	@Override public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
		log("onMtuChanged");
		btCallbackExchange(new OnMtuChanged(gatt, mtu, status));
	}

	@Override public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		log("onReadRemoteRssi");
		btCallbackExchange(new OnReadRemoteRssi(gatt, rssi, status));
	}

	@Override public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
		log("onReliableWriteCompleted");
		btCallbackExchange(new OnReliableWriteCompleted(gatt, status));
	}
}
