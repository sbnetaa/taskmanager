package ru.terentyev.TaskManager.exceptions;

public class IdentifierAlreadyExistsException extends CustomException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5859178049809508976L;
	private String message;
	
	
	public IdentifierAlreadyExistsException(String message) {
		super(message);
	}
	
	

}
