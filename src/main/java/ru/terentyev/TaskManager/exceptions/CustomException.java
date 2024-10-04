package ru.terentyev.TaskManager.exceptions;

public abstract class CustomException extends RuntimeException {
	
	public CustomException(String message) {
		super(message);
	}
	
	public CustomException(String message, Throwable cause) {
		super(message, cause);
	}

}
