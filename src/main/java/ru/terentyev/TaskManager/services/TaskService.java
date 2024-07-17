package ru.terentyev.TaskManager.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ru.terentyev.TaskManager.entities.Task;
import ru.terentyev.TaskManager.entities.TaskRequest;
import ru.terentyev.TaskManager.entities.TaskResponse;
import ru.terentyev.TaskManager.security.PersonDetails;


public interface TaskService {
	
	Page<Task> findAll(int page);
	List<Task> findAll();
	Task findById(long id);
	Task save(Task task);
	void deleteById(long id);
	Page<Task> findByExecutor(long id, int page);
	Page<Task> findByAuthor(long id, int page);
	List<Task> searchByCriteria(TaskRequest searchCritera);
	Page<Task> findByIdIn(List<Long> ids, int page);
	ResponseEntity<TaskResponse> showTasks(TaskRequest request)  throws JsonProcessingException;
	TaskResponse fillResponse(TaskRequest request)  throws JsonProcessingException, NumberFormatException;
	TaskResponse searchForMeetsAllCriteria(TaskRequest request);
	TaskResponse searchForMeetsAnyCriteria(TaskRequest request);
	TaskResponse getSingleTask(long id, int page) throws JsonProcessingException;
	//JsonNode editCommentsRecord(Task task, int page) throws JsonProcessingException;
	ResponseEntity<TaskResponse> checkIfAddedTaskAlreadyHasId(TaskRequest request) throws JsonProcessingException;
	Task fillAddedTask(TaskRequest taskToAdd, PersonDetails pd);
	//JsonNode removeCommentsRecord(List<Task> tasks) throws JsonMappingException, JsonProcessingException;
	Task fillPatchingFields(TaskRequest request);
	ResponseEntity<TaskResponse> addTask(TaskRequest request, BindingResult br, PersonDetails pd) throws JsonProcessingException;
	ResponseEntity<TaskResponse> updateTask(TaskRequest[] request) throws JsonMappingException, JsonProcessingException;
	ResponseEntity<String> deleteTask(TaskRequest request) throws JsonProcessingException;
}
