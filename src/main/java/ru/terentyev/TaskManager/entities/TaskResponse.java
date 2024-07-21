package ru.terentyev.TaskManager.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponse {
	
	@JsonProperty(value = "время", index = 2)
	@JsonFormat(pattern="dd-MM-yyyy HH:mm")
	private LocalDateTime timestamp;
	@JsonProperty("статус")
	private String status;
	@JsonProperty(value = "ошибка", index = 1)
	private List<String> errors;
	private Exception exception;
	private List<Task> tasks;
	
	public TaskResponse(){}
	
	public void addError(String error) {
		if (errors == null) errors = new ArrayList<>();
		errors.add(error);
		status = "400 BAD REQUEST";
		timestamp = LocalDateTime.now();
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
