package ru.terentyev.TaskManager.exceptions;

public class PriorityNotFoundException extends RuntimeException {

	public PriorityNotFoundException() {
		super("Не найден единственный подходящий приоритет.");
	}
}
