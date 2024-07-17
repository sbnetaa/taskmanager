package ru.terentyev.TaskManager.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponse {
	
	@JsonProperty("время")
	private LocalDateTime timestamp;
	@JsonProperty("статус")
	private String status;
	@JsonProperty("ошибка")
	private String error;
	private Exception exception;
	private List<Task> tasks;
	
	public TaskResponse() {}

	

	public TaskResponse(LocalDateTime timestamp, String status, String error, Exception exception, List<Task> tasks) {
		super();
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.exception = exception;
		this.tasks = tasks;
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

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	
}
