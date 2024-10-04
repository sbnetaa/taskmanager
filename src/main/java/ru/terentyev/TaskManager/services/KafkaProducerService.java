package ru.terentyev.TaskManager.services;

import com.fasterxml.jackson.core.JsonProcessingException;

import ru.terentyev.TaskManager.entities.TaskRequest;

public interface KafkaProducerService {
		
	void reportAddingTask(TaskRequest postRequest) throws JsonProcessingException;
	void reportUpdatingTask(TaskRequest updateRequest) throws JsonProcessingException;
	void reportDeletingTask(TaskRequest deleteRequest) throws JsonProcessingException;
}
