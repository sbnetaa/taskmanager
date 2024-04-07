package integration.ru.terentyev.TaskManager;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.terentyev.TaskManager.TaskManagerApplication;
import ru.terentyev.TaskManager.controllers.TaskRestController;
import ru.terentyev.TaskManager.entities.Person;
import ru.terentyev.TaskManager.entities.Task;
import ru.terentyev.TaskManager.services.PersonDetailsService;
import ru.terentyev.TaskManager.services.TaskService;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TaskManagerApplication.class)
public class TaskManagerIntegrationTests {
	
	private ObjectMapper objectMapper;
	private TaskRestController taskRestController;
	private PersonDetailsService personDetailsService;
	private TaskService taskService;
	private MockMvc mockMvc;
	private List<Task> controlTasks;
	
	
	@Autowired
	public TaskManagerIntegrationTests(ObjectMapper objectMapper, TaskRestController taskRestController
			, PersonDetailsService personDetailsService, TaskService taskService, MockMvc mockMvc) {
		super();
		this.objectMapper = objectMapper;
		this.taskRestController = taskRestController;
		this.personDetailsService = personDetailsService;
		this.taskService = taskService;
		this.mockMvc = mockMvc;
	}
	
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
		System.out.println(task1.getId());
		
		Task task2 = new Task();
		task2.setTitle("Random Title");
		task2.setDescription("Random Description");
		task2.setAuthor(person);
		task2.setExecutor(person);
		task2.setPriority(Task.Priority.MEDIUM);
		task2.setStatus(Task.Status.PROCESSING);
		task2 = taskService.save(task2);
		controlTasks.add(task2);
		System.out.println(task2.getId());
	}
	
	
	@AfterEach
	public void clean() {
		taskService.deleteById(controlTasks.get(0).getId());
		taskService.deleteById(controlTasks.get(1).getId());
	}
	
	@Test
	public void correctRequestOfSingleExistingTaskMustSucces() throws JsonProcessingException, Exception {
		  Map<String, String> request = new HashMap<>();
		  long id = controlTasks.get(0).getId();
		  request.put("id", String.valueOf(id));
		  
		  mockMvc.perform(MockMvcRequestBuilders.get("/rest/tasks")
				    .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
					.characterEncoding("utf-8")
					.content(objectMapper.writeValueAsString(request)))
		  			.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id", Matchers.equalTo(id)));
	}
}
