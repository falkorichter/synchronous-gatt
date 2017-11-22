package com.sensorberg.synchronousgatt;

import com.sensorberg.synchronousgatt.result.Result;

public class UnexpectedResultException extends Exception {

	private final Result result;

	UnexpectedResultException(Class klass, Result result) {
		super("Expected " + klass.getName() + "and got " + result.getClass().getName());
		this.result = result;
	}

	public final Result getResult() {
		return result;
	}
}
