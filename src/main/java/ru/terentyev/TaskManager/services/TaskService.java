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
	
	Page<Task> findAll(Integer page);
	Task findById(long id);
	Task save(Task task);
	void deleteById(long id);
	Page<Task> findByExecutor(long id, int page);
	Page<Task> findByAuthor(long id, int page);
	Page<Task> findByIdIn(List<Long> ids, int page);
	ResponseEntity<TaskResponse> showTasks(TaskRequest request)  throws JsonProcessingException;
	TaskResponse getSingleTask(long id, int page) throws JsonProcessingException;
	ResponseEntity<TaskResponse> addTask(TaskRequest request, BindingResult br, PersonDetails pd) throws JsonProcessingException;
	ResponseEntity<TaskResponse> updateTask(TaskRequest[] request) throws JsonMappingException, JsonProcessingException;
	ResponseEntity<String> deleteTask(TaskRequest request) throws JsonProcessingException;
}
