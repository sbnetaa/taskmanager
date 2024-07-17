package ru.terentyev.TaskManager.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.terentyev.TaskManager.entities.TaskRequest;
import ru.terentyev.TaskManager.entities.TaskResponse;
import ru.terentyev.TaskManager.security.PersonDetails;
import ru.terentyev.TaskManager.services.TaskService;

@RestController
@RequestMapping("/rest/tasks")
public class TaskRestController {

	// TODO OncePerRequestFilter
	// TODO Integer to Long
	// TODO DatabaseException vs TaskNotFoundException vs ObjectNotFoundException
			// TODO Paging?
	// TODO EnumNotFoundException
			// TODO seconds problem
			// TODO time criteria
			// TODO authorId
			// TODO empty request body for GET
	// TODO many requests
			// TODO remove executorId
			// TODO comments field = pages
	// TODO meetingCriteria any
			// TODO truncated
			// TODO pretty print global
	// TODO page to integer
			// TODO response executorId == 0 !
			// TODO Sort by status, priority and id?
			// TODO handler DatabaseException
			// TODO patch id of updated tasks
			// TODO common pretty printer
	// TODO check wrong createdAt (==editedAt)
			// TODO remove comments global
	// TODO COMMENTS
	// TODO ? Long.valueOf("authorId") ?
			// TODO criteria api array of authors and executors
			// TODO ? LowerCase ?
			// TODO check and rewrite descriptionLike
			// TODO print executor, author and authorId in json response
	// TODO block patch author
	// TODO change PersonDetailsService to UserDetailsService
	
	// TODO LocalDateTime parse Exception and message
	
	// TODO truncatedTo WEB
	
	private TaskService taskService;
	private ObjectMapper objectMapper; // TODO remove objectMapper
	
	@Autowired
	public TaskRestController(TaskService taskService, ObjectMapper objectMapper) {
		super();
		this.taskService = taskService;
		this.objectMapper = objectMapper;
	}
	
	public TaskRestController() {}
	
		@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
		, headers = "Accept=application/json")
		public ResponseEntity<TaskResponse> showTasks(@RequestBody(required = false) TaskRequest request) throws JsonMappingException, JsonProcessingException {
			return taskService.showTasks(request);
		}
		
		
		@GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
		, headers = "Accept=application/json")
		public void test(@RequestBody(required = false) String testRequest){
			Map<String, String[]> map = objectMapper.convertValue(testRequest, new TypeReference<Map<String, String[]>>(){});
			System.out.println("OK");
		}
		
		@GetMapping(value = "/test2", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
				, headers = "Accept=application/json")
		 public void test2(@RequestBody(required = false) String testRequest) {
			TaskRequest taskRequest = objectMapper.convertValue(testRequest, TaskRequest.class);
			System.out.println("OK2");
		}
		
		@PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {"application/json"}
		, headers = "Accept=application/json")
		public ResponseEntity<TaskResponse> addTask(@RequestBody TaskRequest request, BindingResult br
				, @AuthenticationPrincipal PersonDetails pd) throws JsonProcessingException {
			return taskService.addTask(request, br, pd);
		}
		
		@PatchMapping(value = "", consumes = "application/json", headers = "Accept=application/json")
		public ResponseEntity<TaskResponse> updateTask(@RequestBody TaskRequest[] request) throws JsonProcessingException {
			return taskService.updateTask(request);			
		}
				
		@DeleteMapping(value = "", consumes = "application/json", headers = "Accept=application/json")
		public ResponseEntity<String> deleteTask(@RequestBody TaskRequest request) throws JsonProcessingException {
			return taskService.deleteTask(request);
		}
}
