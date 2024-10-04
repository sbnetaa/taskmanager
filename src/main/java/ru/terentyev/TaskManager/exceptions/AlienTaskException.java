package ru.terentyev.TaskManager.exceptions;

public class AlienTaskException extends CustomException {
	
	public AlienTaskException(Long id) {
		super("Задача с ID " + id + " не принадлежит Вам.");
	}

}
