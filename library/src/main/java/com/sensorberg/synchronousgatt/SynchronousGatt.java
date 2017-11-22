package com.sensorberg.synchronousgatt;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;

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

import java.util.concurrent.TimeoutException;

public class SynchronousGatt {

	private final Callbacks callback;
	private final BluetoothDevice device;
	private BluetoothGatt gatt;

	public SynchronousGatt(BluetoothDevice device) {
		this.device = device;
		this.callback = new Callbacks();
	}

	public BluetoothGatt getBluetoothGatt() {
		return gatt;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public OnConnectionStateChange connectGatt(Context context, boolean autoConnect, int transport, long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		gatt = device.connectGatt(context, autoConnect, callback, transport);
		return getResult(OnConnectionStateChange.class, timeout);
	}

	public OnConnectionStateChange connectGatt(Context context, boolean autoConnect, long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		gatt = device.connectGatt(context, autoConnect, callback);
		return getResult(OnConnectionStateChange.class, timeout);
	}

	public OnServicesDiscovered discoverServices(long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		if (gatt.discoverServices()) {
			return getResult(OnServicesDiscovered.class, timeout);
		}
		throw new NoResultException();
	}

	public OnCharacteristicRead readCharacteristic(BluetoothGattCharacteristic characteristic, long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		if (gatt.readCharacteristic(characteristic)) {
			return getResult(OnCharacteristicRead.class, timeout);
		}
		throw new NoResultException();
	}

	public OnCharacteristicWrite writeCharacteristic(BluetoothGattCharacteristic characteristic, long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		if (gatt.writeCharacteristic(characteristic)) {
			return getResult(OnCharacteristicWrite.class, timeout);
		}
		throw new NoResultException();
	}

	public OnCharacteristicChanged awaitCharacteristicChange(long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		return getResult(OnCharacteristicChanged.class, timeout);
	}

	public OnDescriptorRead readDescriptor(BluetoothGattDescriptor descriptor, long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		if (gatt.readDescriptor(descriptor)) {
			return getResult(OnDescriptorRead.class, timeout);
		}
		throw new NoResultException();
	}

	public OnDescriptorWrite writeDescriptor(BluetoothGattDescriptor descriptor, long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		if (gatt.writeDescriptor(descriptor)) {
			return getResult(OnDescriptorWrite.class, timeout);
		}
		throw new NoResultException();
	}

	public OnReadRemoteRssi readRemoteRssi(long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		if (gatt.readRemoteRssi()) {
			return getResult(OnReadRemoteRssi.class, timeout);
		}
		throw new NoResultException();
	}

	public OnConnectionStateChange disconnect(long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		gatt.disconnect();
		return getResult(OnConnectionStateChange.class, timeout);
	}

	public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
		return gatt.setCharacteristicNotification(characteristic, enable);
	}

	public boolean beginReliableWrite() {
		return gatt.beginReliableWrite();
	}

	public OnReliableWriteCompleted executeReliableWrite(long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		if (gatt.executeReliableWrite()) {
			return getResult(OnReliableWriteCompleted.class, timeout);
		}
		throw new NoResultException();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public OnMtuChanged requestMtu(int mtu, long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		if (gatt.requestMtu(mtu)) {
			return getResult(OnMtuChanged.class, timeout);
		}
		throw new NoResultException();
	}

	public void close() {
		gatt.close();
	}

	public void abortReliableWrite() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			abortReliableWrite_V18(gatt);
		} else {
			abortReliableWrite_Vxx(gatt, device);
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static void abortReliableWrite_V18(BluetoothGatt gatt) {
		gatt.abortReliableWrite();
	}

	private static void abortReliableWrite_Vxx(BluetoothGatt gatt, BluetoothDevice device) {
		gatt.abortReliableWrite(device);
	}

	private <T extends Result> T getResult(Class<T> klass, long timeout) throws UnexpectedResultException, UnexpectedDisconnectionException, NoResultException, TimeoutException {
		Result r = callback.getNextResult(timeout);
		if (r.getClass().equals(klass)) {
			return (T) r;
		} else if ((r instanceof OnConnectionStateChange) && ((OnConnectionStateChange) r).newState != BluetoothProfile.STATE_CONNECTED) {
			throw new UnexpectedDisconnectionException((OnConnectionStateChange) r);
		} else {
			throw new UnexpectedResultException(klass, r);
		}
	}
}
