package ru.terentyev.TaskManager.exceptions;

public class TaskNotFoundException extends RuntimeException {

	private static String message = "Задача с таким ID не найдена";

	public TaskNotFoundException() {
		super(message);
	}

	public TaskNotFoundException(Throwable cause) {
		super(message, cause);
	}

	

}
