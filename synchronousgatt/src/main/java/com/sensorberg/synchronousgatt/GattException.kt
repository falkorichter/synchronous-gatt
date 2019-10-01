package com.sensorberg.synchronousgatt

sealed class GattException(message: String) : Exception(message) {
	class NoResult : GattException("No Result found")
	class UnexpectedDisconnection : GattException("Bluetooth disconnected unexpectedly")
	class UnexpectedResult(expected: Class<out GattResult>, received: Class<GattResult>) : GattException("Expected result was ${expected.simpleName}, but received ${received.simpleName}")
	class Timeout : GattException("Operation has timed-out")
}