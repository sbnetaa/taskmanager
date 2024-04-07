package ru.terentyev.TaskManager.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import ru.terentyev.TaskManager.entities.Task;
import ru.terentyev.TaskManager.entities.TaskRequest;
import ru.terentyev.TaskManager.security.PersonDetails;


public interface TaskService {
	
	Page<Task> findAll(int page);
	List<Task> findAll();
	Task findById(long id);
	Task save(Task task);
	void deleteById(long id);
	Page<Task> findByExecutor(long id, int page);
	Page<Task> findByAuthor(long id, int page);
	List<Task> searchByCriteria(Map<String, String[]> searchCritera);
	Page<Task> findByIdIn(List<Long> ids, int page);
	ResponseEntity<String> showTasks(TaskRequest request)  throws JsonProcessingException;
	Map<String, Object> fillResponseMap(Map<String, String[]> requestMap)  throws JsonProcessingException, NumberFormatException;
	Map<String, Object> searchForMeetsAllCriteria(Map<String, String[]> requestMap);
	Map<String, Object> searchForMeetsAnyCriteria(Map<String, String[]> requestMap);
	Map<String, Object> getSingleTask(long id, int page) throws JsonProcessingException;
	JsonNode editCommentsRecord(Task task, int page) throws JsonProcessingException;
	ResponseEntity<String> checkIfAddedTaskAlreadyHasId(Task taskToAdd) throws JsonProcessingException;
	Task fillAddedTask(Task taskToAdd, PersonDetails pd);
	JsonNode removeCommentsRecord(List<Task> tasks) throws JsonMappingException, JsonProcessingException;
	Task fillPatchingFields(Map<String, String> map);
	ResponseEntity<String> addTask(Map<String, String> taskAsMap, BindingResult br, PersonDetails pd) throws JsonProcessingException;
	ResponseEntity<String> updateTask(Map<String, String>[] request) throws JsonMappingException, JsonProcessingException;
	ResponseEntity<String> deleteTask(Map<String, String[]> requestMap) throws JsonProcessingException;
}
