package ru.terentyev.TaskManager.exceptions;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import ru.terentyev.TaskManager.controllers.AuthRestController;
import ru.terentyev.TaskManager.controllers.TaskRestController;
import ru.terentyev.TaskManager.entities.ErrorResponse;
import ru.terentyev.TaskManager.services.TaskService;
import ru.terentyev.TaskManager.services.TaskServiceImpl;

@RestControllerAdvice(assignableTypes = {TaskRestController.class, TaskServiceImpl.class, AuthRestController.class})
public class CommonRestExceptionHandler {

	public HttpHeaders putHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
	
	@ExceptionHandler(value = IncompatibleCriteriaException.class)
	public ResponseEntity<ErrorResponse> handleIncompatibleCriteriaException(IncompatibleCriteriaException ice) throws JsonMappingException, JsonProcessingException{
		ice.printStackTrace();
		ErrorResponse response = new ErrorResponse();
		response.setStatusCode(400);
		response.setError(ice.getMessage());
		try {
		//responseMap.putAll(objectMapper.convertValue(taskService.findById(ice.getObjectId()), new TypeReference<Map<String, String>>(){}));
		} catch (TaskNotFoundException tnfe) {
			throw new DatabaseException(tnfe);
		}
		return new ResponseEntity<>(response, putHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException ce) {
		ce.printStackTrace();
		ErrorResponse response = new ErrorResponse();
		response.setError(ce.getMessage());
		HttpStatus status = HttpStatus.BAD_REQUEST;
		response.setStatusCode(400);
		if (ce instanceof PersonNotFoundException || ce instanceof TaskNotFoundException) {
			status = HttpStatus.NOT_FOUND;
			response.setStatusCode(404);
		}
		return new ResponseEntity<>(response, putHeaders(), status);
	}
	
	
	@ExceptionHandler(value = ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException cve) throws JsonProcessingException {
		cve.printStackTrace();
		ErrorResponse response = new ErrorResponse();
		response.setStatusCode(400);
		int i = 0;
		response.setError("");
		for (ConstraintViolation<?> cv : cve.getConstraintViolations()) response.setError(response.getError() + "\r\n" + ++i + ". " + cv.getMessage());
		return new ResponseEntity<>(response, putHeaders(), HttpStatus.BAD_REQUEST);
	}
	

	@ExceptionHandler(value = JsonProcessingException.class)
	public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException jpe, HttpServletRequest request) throws JsonProcessingException {
		jpe.printStackTrace();
		ErrorResponse response = new ErrorResponse();
		response.setStatusCode(400);
		response.setError(jpe.getMessage());
		return new ResponseEntity<>(response, putHeaders(), HttpStatus.BAD_REQUEST);
		
	}
}
