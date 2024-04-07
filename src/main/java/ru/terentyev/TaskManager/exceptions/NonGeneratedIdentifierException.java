package ru.terentyev.TaskManager.exceptions;

public class NonGeneratedIdentifierException extends RuntimeException {
	
	private String message;

	public NonGeneratedIdentifierException(String message) {
		super(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

	
}
