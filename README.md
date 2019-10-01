# Synchronous Gatt
Wraps Android Gatt operations into a sane synchronous API
(just execute it from a background thread)
--------------------------------------

## Setup:

[ ![Download](https://api.bintray.com/packages/sensorberg/maven/synchronous-gatt/images/download.svg) ](https://bintray.com/sensorberg/maven/synchronous-gatt/_latestVersion)

On gradle use `jcenter()` and apply the library `implementation 'com.sensorberg.libs-synchronous-gatt:<latest>'`

## Usage:

Create the `SynchronousGatt` object and execute all gatt operations from Android platform synchronously on a background thread

```Kotlin
with(SynchronousGatt(scanResult.device)) {
    try {
        val connectResult = connectGatt(context, false, BluetoothDevice.TRANSPORT_LE, TIMEOUT)
        val serviceResult = discoverServices(TIMEOUT)

        val characteristicWrite = serviceResult
            .getService(SERVICE_UUID)
            .getCharacteristic(CHARACTERISTIC_WRITE_UUID)
        characteristicWrite.setValue("hello world\n")
        val writeResult = writeCharacteristic(characteristicWrite, TIMEOUT)

        val characteristicRead = serviceResult
            .getService(SERVICE_UUID)
            .getCharacteristic(CHARACTERISTIC_READ_UUID)
        val readResult = readCharacteristic(characteristicRead, TIMEOUT)
        Log.d(TAG, readResult.characteristic.getStringValue(0))

        val disconnectResult = disconnect(TIMEOUT)
    } catch (e: Exception) {
        Log.e("Something went wrong. $e")
    } finally {
        close()
    }
}

```

For a (very basic) example, check MainActivity on the sample app.