package ru.terentyev.TaskManager.exceptions;

public class TaskIdentifierMismatchException extends CustomException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3122871299777674152L;

	
	private String message;


	public TaskIdentifierMismatchException(String message) {
		super(message);
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
