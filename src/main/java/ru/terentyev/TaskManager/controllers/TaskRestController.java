package ru.terentyev.TaskManager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
	
	// TODO print executorId and executorName in response (and author)
	// TODO при добавлении задачи executor не найден
	// TODO response commentsCount null (0)
	// TODO регистрация через rest
	
	
	private TaskService taskService;
	
	@Autowired
	public TaskRestController(TaskService taskService) {
		super();
		this.taskService = taskService;
	}
	
	public TaskRestController() {}
	
		@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
			, headers = "Accept=application/json")
		public ResponseEntity<TaskResponse> showAllTasks(@RequestParam(required = false) Integer page, @RequestBody(required = false) String jsonInput) throws JsonMappingException, JsonProcessingException {
			TaskResponse response = new TaskResponse();
			response.setTasks(taskService.findAll(page).getContent());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	
		@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
		, headers = "Accept=application/json")
		public ResponseEntity<TaskResponse> showTasks(@PathVariable(required=false) Long id, @RequestParam(required = false, defaultValue = "0") int page, @RequestBody(required = false) String jsonInput) throws JsonMappingException, JsonProcessingException {
			if (id == null) return taskService.showTasks(null);
				return new ResponseEntity<TaskResponse>(taskService.getSingleTask(id, page), HttpStatus.OK);
		}
		
		@PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
		, headers = "Accept=application/json")
		public ResponseEntity<TaskResponse> searchTasks(@RequestBody(required = false) TaskRequest request) throws JsonMappingException, JsonProcessingException {
				return taskService.showTasks(request);	
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
