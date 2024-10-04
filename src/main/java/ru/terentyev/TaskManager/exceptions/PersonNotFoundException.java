package ru.terentyev.TaskManager.exceptions;

public class PersonNotFoundException extends CustomException {

	public PersonNotFoundException() {
		super("Пользователь не найден.");
	}
}
