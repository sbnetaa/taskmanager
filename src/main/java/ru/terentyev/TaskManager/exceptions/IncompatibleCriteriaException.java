package ru.terentyev.TaskManager.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncompatibleCriteriaException extends CustomException {

	public IncompatibleCriteriaException(String message) {
		super(message);
	}
}
