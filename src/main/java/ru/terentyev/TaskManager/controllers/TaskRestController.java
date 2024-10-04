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
@RequestMapping(value = "/api/v1/tasks", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
, headers = "Accept=application/json")
public class TaskRestController {

			// TODO OncePerRequestFilter (MediaType.APPLICATION_JSON_VALUE?)
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
	// TODO ? .csrf(csrf -> csrf.ignoringRequestMatchers
			// TODO createdAt editedAt seconds
	// TODO adding task: priority HI status AWA
	
	// TODO task validation
	
	private TaskService taskService;
	
	
	@Autowired
	public TaskRestController(TaskService taskService) {
		super();
		this.taskService = taskService;
	}
	
	public TaskRestController() {}
	
		@GetMapping({"", "/{id}"})
		public ResponseEntity<TaskResponse[]> showAllTasks(@PathVariable(required=false) Long id
				, @RequestParam(required = false, defaultValue = "0") Integer page, @RequestParam(required = false) String sortBy
				, @RequestBody(required = false) String jsonInput) throws JsonMappingException, JsonProcessingException {
			if (id == null) return new ResponseEntity<>(taskService.showAllTasks(page, sortBy), HttpStatus.OK);
			return new ResponseEntity<>(new TaskResponse[]{taskService.getSingleTask(id, page)}, HttpStatus.OK);
		}
	
		
		@PostMapping("/search")
		public ResponseEntity<TaskResponse[]> searchTasks(@RequestBody(required = false) TaskRequest request) throws JsonMappingException, JsonProcessingException {
				return new ResponseEntity<>(taskService.showTasks(request), HttpStatus.OK);	
		}
		
		@PostMapping
		public ResponseEntity<TaskResponse> addTask(@RequestBody TaskRequest request, BindingResult br
				, @AuthenticationPrincipal PersonDetails pd) throws JsonProcessingException {
			return new ResponseEntity<>(taskService.addTask(request, pd), HttpStatus.CREATED);
		}
		
		@PatchMapping("/{id}")
		public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest request, @AuthenticationPrincipal PersonDetails pd) throws JsonProcessingException {
			return new ResponseEntity<>(taskService.updateTask(id, request, pd), HttpStatus.OK);			
		}
				
		@DeleteMapping("/{id}")
		public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal PersonDetails pd) throws JsonProcessingException {
			taskService.deleteTask(id, pd);
			return ResponseEntity.noContent().build();
		}

}
