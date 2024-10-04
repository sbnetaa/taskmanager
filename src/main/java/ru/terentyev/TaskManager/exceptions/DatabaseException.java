package ru.terentyev.TaskManager.exceptions;

public class DatabaseException extends CustomException {

	private static String message = "Ошибка обращения к базе данных";
	
	public DatabaseException(Throwable cause) {
		super(message, cause);
	}
	
	public DatabaseException() {
		super(message);
	}
	
}
