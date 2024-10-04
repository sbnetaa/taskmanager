package ru.terentyev.TaskManager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.terentyev.TaskManager.entities.TaskRequest;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

	private KafkaTemplate<String, byte[]> kafka;
	private ObjectMapper objectMapper;
	
	@Autowired
	public KafkaProducerServiceImpl(KafkaTemplate<String, byte[]> kafka, ObjectMapper objectMapper) {
		super();
		this.kafka = kafka;
		this.objectMapper = objectMapper;
	}

	@Override
	public void reportAddingTask(TaskRequest postRequest) throws JsonProcessingException {
		kafka.send("post", objectMapper.writeValueAsBytes(postRequest));	
	}

	@Override
	public void reportUpdatingTask(TaskRequest updateRequest) throws JsonProcessingException {
		kafka.send("patch", objectMapper.writeValueAsBytes(updateRequest));
	}

	@Override
	public void reportDeletingTask(TaskRequest deleteRequest) throws JsonProcessingException {
		kafka.send("delete", objectMapper.writeValueAsBytes(deleteRequest));
		
	}
}
