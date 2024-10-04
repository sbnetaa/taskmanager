package ru.terentyev.TaskManager;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.terentyev.TaskManager.entities.Person;
import ru.terentyev.TaskManager.entities.Task;
import ru.terentyev.TaskManager.services.PersonDetailsService;
import ru.terentyev.TaskManager.services.TaskService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
public class TaskManagerApplicationTests {

		private static KafkaContainer kafkaContainer;
	
	 	@Autowired
	    private ObjectMapper objectMapper;

	    @Autowired
	    private PersonDetailsService personDetailsService;

	    @Autowired
	    private TaskService taskService;
	    
	    @Autowired
	    private MockMvc mockMvc;
	        
	    private List<Task> controlTasks;
	    
	    private Person person;
	    
	    private String encodedAuth;
	
	    
	    @BeforeAll
	    public static void startKafka() {
	        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
	        kafkaContainer.start();
	        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
	    }
	    
	@BeforeEach
	public void fillTasksList() {
		controlTasks = new ArrayList<>();
		
		setupPerson();
		
		Task task1 = new Task();
		task1.setTitle("IntegrationTestTask-1");
		task1.setDescription("Task №1 for integration tests description");
		task1.setPriority(Task.Priority.LOW);
		task1.setStatus(Task.Status.AWAITING);
		task1.setAuthor(person);
		task1.setExecutor(person);
		LocalDateTime now = LocalDateTime.now();
		task1.setCreatedAt(now);
		task1.setEditedAt(now);
		controlTasks.add(taskService.save(task1));
		
		Task task2 = new Task();
		task2.setTitle("Random Title");
		task2.setDescription("Random Description");
		task2.setAuthor(person);
		task2.setExecutor(person);
		task2.setPriority(Task.Priority.MEDIUM);
		task2.setStatus(Task.Status.PROCESSING);
		task2.setCreatedAt(now);
		task2.setEditedAt(now);
		controlTasks.add(taskService.save(task2));
		
		Task task3 = new Task();
		task3.setTitle("Название для тестовой задачи");
		task3.setDescription("Описание для тестовой задачи");
		task3.setAuthor(person);
		task3.setExecutor(person);
		task3.setPriority(Task.Priority.HIGH);
		task3.setStatus(Task.Status.COMPLETED);
		task3.setCreatedAt(now);
		task3.setEditedAt(now);
		controlTasks.add(taskService.save(task3));
	}

	public void setupPerson() {
		person = new Person();
		person.setName("userForTests");
		person.setEmail("userForTests@mail.ru");
		String password = "testPassword";
		person.setPassword(password);
		person.setPasswordConfirm(password);	
		person = personDetailsService.registerNewUserAccountRest(person);
		encodedAuth = Base64.getEncoder().encodeToString((person.getName() + ":" + password).getBytes());
	}
	
	
	@Test
	public void correctGetRequestOfSingleExistingTaskMustSuccess() throws JsonProcessingException, Exception {
		  Map<String, String> requestMap = new HashMap<>();
		  long id = controlTasks.get(0).getId();
		  requestMap.put("id", String.valueOf(id));
		 
		  mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/" + id)
				    .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(requestMap)))
		  			//.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id", Matchers.equalTo((int) id)));
	}
	
	
	@Test
	public void correctPostAddRequestMustSuccess() throws JsonProcessingException, Exception {
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("title", "AddRequestTestTaskTitle");
		requestMap.put("description", "AddRequestTestTaskDescription");
		requestMap.put("status", "awaiting");
		requestMap.put("priority", "high");
		requestMap.put("executor", String.valueOf(person.getId()));
		

			  mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tasks")
					.header("Authorization", "Basic " + encodedAuth)
				    .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(requestMap)))
		  			//.andDo(print())
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.tasks[0].title", Matchers.equalTo("AddRequestTestTaskTitle")))
					.andExpect(jsonPath("$.tasks[0].description", Matchers.equalTo("AddRequestTestTaskDescription")))
					.andExpect(jsonPath("$.tasks[0].status", Matchers.equalTo("AWAITING")))
					.andExpect(jsonPath("$.tasks[0].priority", Matchers.equalTo("HIGH")));
		
	}
	
	@Test
	public void correctPostSearchRequestMustReturnTwoTasks() throws JsonProcessingException, Exception {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("id", new Long[]{controlTasks.get(0).getId()
				, controlTasks.get(1).getId()
				, controlTasks.get(2).getId()});
		requestMap.put("title", "Title");
		requestMap.put("description", "Description");
		requestMap.put("priority", new String[] {"lo", "me"});
		requestMap.put("status", new String[] {"awa", "pro"});
		requestMap.put("author", person.getId());
		requestMap.put("executor", person.getId());
		String createdBefore = LocalDateTime.now().plusSeconds(10).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
		requestMap.put("createdBefore", createdBefore);
		requestMap.put("orderBy", "idDESC");
		
		
		  mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tasks/search")
				.header("Authorization", "Basic " + encodedAuth)
			    .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(requestMap)))
	  			//.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tasks[0].title", Matchers.equalTo("Random Title")))
				.andExpect(jsonPath("$.tasks[0].description", Matchers.equalTo("Random Description")))
				.andExpect(jsonPath("$.tasks[0].status", Matchers.equalTo("PROCESSING")))
				.andExpect(jsonPath("$.tasks[0].priority", Matchers.equalTo("MEDIUM")));
	
	}
	
	@Test
	public void correctPathRequestMustSuccess() throws JsonProcessingException, Exception {
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("id", String.valueOf(controlTasks.get(0).getId()));
		requestMap.put("title", "New title of test task");
		requestMap.put("description", "New description of test task");
		requestMap.put("status", "COMPLETED");
		requestMap.put("priority", "HIGH");
		
		  mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/tasks")
				.header("Authorization", "Basic " + encodedAuth)
			    .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(requestMap)))
	  			.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tasks[0].title", Matchers.equalTo("New title of test task")))
				.andExpect(jsonPath("$.tasks[0].description", Matchers.equalTo("New description of test task")))
				.andExpect(jsonPath("$.tasks[0].status", Matchers.equalTo("COMPLETED")))
				.andExpect(jsonPath("$.tasks[0].priority", Matchers.equalTo("HIGH")));
	}
	
	@Test
	public void incorrectPostRequestMustFailWithReport() throws JsonProcessingException, Exception {
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("id", String.valueOf(controlTasks.get(0).getId()));
		requestMap.put("author", String.valueOf(person.getId()));
		requestMap.put("executor", String.valueOf(person.getId() + 1));
		requestMap.put("executorName", "grsefrgsrsawf");
		requestMap.put("status", "");
		requestMap.put("priority", "");
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tasks")
				.header("Authorization", "Basic " + encodedAuth)
			    .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(requestMap)))
	  			.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.ошибка[0]", Matchers.equalTo("Одновременное использование ключей 'executor' и 'executorName' недопустимо.")))
				.andExpect(jsonPath("$.ошибка[1]", Matchers.equalTo("Пользователь с ID " + (person.getId() + 1) + " не найден.")))
				.andExpect(jsonPath("$.ошибка[2]", Matchers.equalTo("Указание автора при добавлении задачи недопустимо.")))
				.andExpect(jsonPath("$.ошибка[3]", Matchers.equalTo("Указание ID для новой записи недопустимо. ID должен быть сгенерирован автоматически.")))
				.andExpect(jsonPath("$.ошибка[4]", Matchers.equalTo("Не найден единственный подходящий статус.")))
				.andExpect(jsonPath("$.ошибка[5]", Matchers.equalTo("Не найден единственный подходящий приоритет.")));
	}
}
