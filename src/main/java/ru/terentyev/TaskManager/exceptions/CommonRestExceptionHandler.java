package ru.terentyev.TaskManager.exceptions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import ru.terentyev.TaskManager.controllers.TaskRestController;
import ru.terentyev.TaskManager.entities.Task;
import ru.terentyev.TaskManager.services.TaskService;
import ru.terentyev.TaskManager.services.TaskServiceImpl;

@RestControllerAdvice(assignableTypes = {TaskRestController.class, TaskServiceImpl.class})
public class CommonRestExceptionHandler {

	private ObjectMapper objectMapper;
	private TaskService taskService;
	
	@Autowired
	public CommonRestExceptionHandler(ObjectMapper objectMapper, TaskService taskService) {
		super();
		this.objectMapper = objectMapper;
		this.taskService = taskService;
	}

	public HttpHeaders putHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.valueOf("application/json;charset=UTF-16"));
		return headers;
	}

	@ExceptionHandler(value = TaskNotFoundException.class)
	public ResponseEntity<String> handleTaskNotFoundExceptionRest(TaskNotFoundException tnfe) throws JsonProcessingException {
		tnfe.printStackTrace();
		Map<String, String> responseMap = new LinkedHashMap<>();
		responseMap.put("время", LocalDateTime.now().toString());
		responseMap.put("статус", "404 NOT FOUND");
		responseMap.put("ошибка", tnfe.getMessage());
		return new ResponseEntity<String>(objectMapper.writeValueAsString(responseMap), putHeaders(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = ConstraintViolationException.class)
	public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException cve) throws JsonProcessingException {
		cve.printStackTrace();
		Map<String, String> responseMap = new LinkedHashMap<>();
		responseMap.put("время", LocalDateTime.now().toString());
		responseMap.put("статус", "400 BAD REQUEST");
		int i = 0;
		for (ConstraintViolation<?> cv : cve.getConstraintViolations()) responseMap.put("error" + ++i, cv.getMessage());
		return new ResponseEntity<String>(objectMapper.writeValueAsString(responseMap), putHeaders(), HttpStatus.BAD_REQUEST);
	}
	

	@ExceptionHandler(value = JsonProcessingException.class)
	public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException jpe, HttpServletRequest request) throws JsonProcessingException {
		jpe.printStackTrace();
		Map<String, Object> responseMap = new LinkedHashMap<>();
		responseMap.put("время", LocalDateTime.now().toString());
		responseMap.put("статус", "400 BAD REQUEST");
		responseMap.put("ошибка", "Некорректно составлен JSON");
		/*
		if (request.getMethod().equals("GET")) {
			responseMap.put("результат", "Выбраны все записи");
			List<Task> foundTasks = taskService.findAll();
			responseMap.put("tasks", (foundTasks));
		}
		*/
		return new ResponseEntity<>(objectMapper.writeValueAsString(responseMap), putHeaders(), HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(value = IncompatibleCriteriaException.class)
	public ResponseEntity<String> handleIncompatibleCriteriaException(IncompatibleCriteriaException ice) throws JsonMappingException, JsonProcessingException{
		ice.printStackTrace();
		Map<String, String> responseMap = new LinkedHashMap<>();
		responseMap.put("время", LocalDateTime.now().toString());
		responseMap.put("статус", "400 BAD REQUEST");
		responseMap.put("предупреждение", ice.getMessage());
		try {
		responseMap.putAll(objectMapper.convertValue(taskService.findById(ice.getObjectId()), new TypeReference<Map<String, String>>(){}));
		} catch (TaskNotFoundException tnfe) {
			throw new DatabaseException(tnfe);
		}
		return new ResponseEntity<>(objectMapper.writeValueAsString(responseMap), putHeaders(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = DatabaseException.class)
	public ResponseEntity<String> handleDatabaseException(DatabaseException de) throws JsonProcessingException {
		de.printStackTrace();
		Map<String, String> responseMap = new LinkedHashMap<>();
		responseMap.put("время", LocalDateTime.now().toString());
		responseMap.put("статус", "400 BAD REQUEST");
		responseMap.put("предупреждение", de.getMessage());
		return new ResponseEntity<String>(objectMapper.writeValueAsString(responseMap), putHeaders(), HttpStatus.BAD_REQUEST);
	}
}
