package ru.terentyev.TaskManager.exceptions;

public class StatusNotFoundException extends RuntimeException {

	public StatusNotFoundException() {
		super("Не найден единственный подходящий статус.");
	}
}
