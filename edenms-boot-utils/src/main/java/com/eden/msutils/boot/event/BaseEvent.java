package com.eden.msutils.boot.event;

import org.springframework.context.ApplicationEvent;

public class BaseEvent<T> extends ApplicationEvent {

	protected T data;

	public BaseEvent(Object source) {
		super(source);
	}

	public BaseEvent(Object source, T data){
		super(source);
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
