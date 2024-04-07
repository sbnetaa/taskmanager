package ru.terentyev.TaskManager.exceptions;

public class PersonNotFoundException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6680648809572620136L;
	private static String message = "Пользователь с таким ID не найден";

	public PersonNotFoundException() {
		super(message);
	}


}
