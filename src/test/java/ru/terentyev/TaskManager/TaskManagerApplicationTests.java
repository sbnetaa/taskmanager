package ru.terentyev.TaskManager;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.terentyev.TaskManager.entities.Person;
import ru.terentyev.TaskManager.entities.Task;
import ru.terentyev.TaskManager.services.PersonDetailsService;
import ru.terentyev.TaskManager.services.TaskService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskManagerApplicationTests {

	 	@Autowired
	    private ObjectMapper objectMapper;

	    @Autowired
	    private PersonDetailsService personDetailsService;

	    @Autowired
	    private TaskService taskService;
	    
	    @Autowired
	    private MockMvc mockMvc;
	    
	    private List<Task> controlTasks;
	   
	
	@BeforeEach
	public void fillTasksList() {
		controlTasks = new ArrayList<>();
		Person person = personDetailsService.findById(3L);
		
		Task task1 = new Task();
		task1.setTitle("IntegrationTestTask-1");
		task1.setDescription("Task №1 for integration tests description");
		task1.setPriority(Task.Priority.LOW);
		task1.setStatus(Task.Status.AWAITING);
		task1.setAuthor(person);
		task1.setExecutor(person);
		task1 = taskService.save(task1);
		controlTasks.add(task1);
		
		Task task2 = new Task();
		task2.setTitle("Random Title");
		task2.setDescription("Random Description");
		task2.setAuthor(person);
		task2.setExecutor(person);
		task2.setPriority(Task.Priority.MEDIUM);
		task2.setStatus(Task.Status.PROCESSING);
		task2 = taskService.save(task2);
		controlTasks.add(task2);
	}
	
	
	@AfterEach
	public void clean() {
		taskService.deleteById(controlTasks.get(0).getId());
		taskService.deleteById(controlTasks.get(1).getId());
	}
	
	@Test
	public void correctRequestOfSingleExistingTaskMustSuccess() throws JsonProcessingException, Exception {
		  Map<String, String> requestMap = new HashMap<>();
		  long id = controlTasks.get(0).getId();
		  requestMap.put("id", String.valueOf(id));
		 
		  mockMvc.perform(MockMvcRequestBuilders.get("/rest/tasks")
				    .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(requestMap)))
		  			.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.tasks[0].id", Matchers.equalTo(id)));
	}
}
