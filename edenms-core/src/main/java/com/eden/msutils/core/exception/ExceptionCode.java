package com.eden.msutils.core.exception;

public enum ExceptionCode {

	/** JAVA Exception  **/
	BIZEXCEPTION(40000),
	IOEXCEPTION(40010);

	private int value;

	ExceptionCode(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
