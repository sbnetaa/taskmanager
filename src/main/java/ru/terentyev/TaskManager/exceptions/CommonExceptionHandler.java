package ru.terentyev.TaskManager.exceptions;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.terentyev.TaskManager.controllers.AuthController;
import ru.terentyev.TaskManager.controllers.PersonController;
import ru.terentyev.TaskManager.controllers.TaskController;

@ControllerAdvice(assignableTypes = {TaskController.class, PersonController.class, AuthController.class})
public class CommonExceptionHandler {
	
	@Autowired
	public CommonExceptionHandler(ObjectMapper objectMapper) {
		super();
	}

	@ExceptionHandler(value = TaskNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public String handleNotFoundException(TaskNotFoundException tnfe, Model model) throws JsonProcessingException {	
		model.addAttribute("error", tnfe.getMessage());
		System.out.println(tnfe.getMessage() + "\n TaskNotFoundException");
		return "tasks";
	}
	

	
	@ExceptionHandler(value = AuthenticationException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public String handleAccessException(AuthenticationException ae, Model model) {
		model.addAttribute("error", ae.getMessage());
		System.out.println(ae.getMessage() + "\n AuthenticationException");
		return "tasks";
	}
	
	@ExceptionHandler(value = PersonNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public String handlePersonException(PersonNotFoundException pnfe, Model model) {
		model.addAttribute("error", pnfe.getMessage());
		System.out.println(pnfe.getMessage() + "\n PersonNotFoundException");
		return "people";
	}
	
	@ExceptionHandler(value = MismatchIdentifierException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public String handleMismatchException(MismatchIdentifierException mie, Model model) {
		model.addAttribute("error", mie.getMessage());
		System.out.println(mie.getMessage() + "\n MismatchIdentifierException");
		return "task";
	}
	

}
