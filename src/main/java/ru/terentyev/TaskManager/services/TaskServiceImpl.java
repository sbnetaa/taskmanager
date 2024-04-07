package ru.terentyev.TaskManager.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.validation.Valid;
import ru.terentyev.TaskManager.entities.Person;
import ru.terentyev.TaskManager.entities.Task;
import ru.terentyev.TaskManager.entities.TaskRequest;
import ru.terentyev.TaskManager.exceptions.PersonNotFoundException;
import ru.terentyev.TaskManager.exceptions.TaskNotFoundException;
import ru.terentyev.TaskManager.repositories.PersonRepository;
import ru.terentyev.TaskManager.repositories.TaskRepository;
import ru.terentyev.TaskManager.security.PersonDetails;

@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

	
	private TaskRepository taskRepository;
	private PersonRepository personRepository;
	private CommentService commentService;
	private PersonDetailsService personDetailsService;
	private ObjectMapper objectMapper;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public TaskServiceImpl(TaskRepository taskRepository, PersonRepository personRepository
			, CommentService commentService, PersonDetailsService personDetailsService, ObjectMapper objectMapper) {
		super();
		this.taskRepository = taskRepository;
		this.personRepository = personRepository;
		this.commentService = commentService;
		this.personDetailsService = personDetailsService;
		this.objectMapper = objectMapper;
	}
	
	public ResponseEntity<String> showTasks(TaskRequest request) throws JsonProcessingException {
		Map<String, String[]> requestMap = objectMapper.convertValue(request, new TypeReference<Map<String, String[]>>(){});
		if (requestMap == null || requestMap.isEmpty())
			return new ResponseEntity<String>(objectMapper.writeValueAsString(findAll()), HttpStatus.OK);
		
		//Map<String, String[]> requestMap = objectMapper.readValue
				//(request, new TypeReference<Map<String, String[]>>(){});
		Map<String, Object> responseMap = fillResponseMap(requestMap);
			return new ResponseEntity<>(objectMapper.writeValueAsString(removeCommentsRecord((List<Task>)responseMap.get("tasks"))), HttpStatus.OK);

	}
	
	public Map<String, Object> fillResponseMap(Map<String, String[]> requestMap) throws JsonProcessingException, NumberFormatException{
		Map<String, Object> responseMap = null;
		if (requestMap.containsKey("id") && requestMap.get("id").length == 1) 
			responseMap = getSingleTask(Long.valueOf(requestMap.get("id")[0])
					, Integer.valueOf(requestMap.getOrDefault("page", new String[]{"0"})[0]));
		
		if (requestMap.get("meetingCriteria") != null && requestMap.get("meetingCriteria")[0].equals("any"))
			responseMap = searchForMeetsAnyCriteria(requestMap);
		else responseMap = searchForMeetsAllCriteria(requestMap);
		return responseMap;
	}
	
	public Map<String, Object> searchForMeetsAllCriteria(Map<String, String[]> requestMap) {
		Map<String, Object> tasksMap = new LinkedHashMap<>();
		tasksMap.put("tasks", searchByCriteria(requestMap));
		return tasksMap;
	}
	
	// Не уверен, что это стоит делать. Отложил на потом.
	public Map<String, Object> searchForMeetsAnyCriteria(Map<String, String[]> requestMap) {
		return null;
	}
	
	public Map<String, Object> getSingleTask(long id, int page) throws JsonProcessingException {
			Task task = findById(id);
			task.setComments(commentService.findByTask(id, page).getContent());
			return objectMapper.convertValue(editCommentsRecord(task, page), new TypeReference<Map<String, Object>>(){});
	}
	
	public JsonNode editCommentsRecord(Task task, int page) throws JsonProcessingException {
		final long PAGE_SIZE = 10;
		String taskAsJson = objectMapper.writeValueAsString(task);
		ObjectNode root = (ObjectNode) objectMapper.readTree(taskAsJson);
		root.set("comments(page " + page + " of " + (int) Math.ceil(task.getComments().size() / PAGE_SIZE) + ")", root.get("comments"));
		root.remove("comments");
		return root;
	}
	
	public ResponseEntity<String> checkIfAddedTaskAlreadyHasId(Task taskToAdd) throws JsonProcessingException {
		ResponseEntity<String> response = null;
		if (taskToAdd.getId() != null) 
			response = new ResponseEntity<>(objectMapper
			.writeValueAsString(Collections.singletonMap
			("предупреждение", "ID не должен быть указан."
			+ " Он будет сгенерирован автоматически." 
			+ " Для изменения записи используйте PATCH запрос.")), HttpStatus.CREATED);
		else response = new ResponseEntity<>(HttpStatus.OK);
		return response;
	}
	
	public Task fillAddedTask(Task taskToAdd, PersonDetails pd) {
		taskToAdd.setId(null);
		taskToAdd.setAuthor(pd.getPerson());
		taskToAdd.setCreatedAt(LocalDateTime.now());
		return taskToAdd;	
	}
	
	public JsonNode removeCommentsRecord(List<Task> tasks) throws JsonMappingException, JsonProcessingException {
		final long PAGE_SIZE = 10;
		ArrayNode arrayNode = objectMapper.createArrayNode();
		for (Task task : tasks) {
			int commentsCount = commentService.countByTask(task.getId());
	        ObjectNode taskNode = objectMapper.valueToTree(task);
	        taskNode.set("comments", objectMapper.convertValue(commentsCount + " comments, " 
	    	+ (int) (Math.ceil((double) commentsCount / PAGE_SIZE)) + " pages", JsonNode.class));
	        arrayNode.add(taskNode);
	    }		
		return arrayNode;
	}
	
	public Task fillPatchingFields(Map<String, String> map) {
		Task task = findById(Long.parseLong(map.get("id")));
		if (map.containsKey("title")) task.setTitle(map.get("title"));
		if (map.containsKey("description")) task.setDescription(map.get("description"));
		if (map.containsKey("status")) task.setStatus(Task.Status.valueOf(map.get("status")));
		if (map.containsKey("priority")) task.setPriority(Task.Priority.valueOf(map.get("priority")));
		if (map.containsKey("executor")) task.setAuthor(personDetailsService.findById(Long.parseLong(map.get("executor"))));
		return task;
	}
	
	public ResponseEntity<String> addTask(Map<String, String> taskAsMap, BindingResult br
			, PersonDetails pd) throws JsonProcessingException{
		@Valid Task taskToAdd = objectMapper.convertValue(taskAsMap, Task.class);
		ResponseEntity<String> response = checkIfAddedTaskAlreadyHasId(taskToAdd);
		taskToAdd = fillAddedTask(taskToAdd, pd);;
		save(taskToAdd);
		return response;
	}
	
	
	public ResponseEntity<String> updateTask(Map<String, String>[] request) throws JsonMappingException, JsonProcessingException{
		List<Task> tasksToReturn = new ArrayList<>();
		for (Map<String, String> map : request) {
			Task task = fillPatchingFields(map);
			save(task);
			tasksToReturn.add(task);
		}
		JsonNode root = removeCommentsRecord(tasksToReturn);
		return new ResponseEntity<String>(objectMapper.writeValueAsString(Collections.singletonMap("updatedTasks", root)), HttpStatus.OK);
	}
	
	public ResponseEntity<String> deleteTask(Map<String, String[]> requestMap) throws JsonProcessingException{
		List<Long> removedIds = new ArrayList<>();
		for (String idAsString : requestMap.get("id")) {
			long id = Long.parseLong(idAsString);
			deleteById(id);
			removedIds.add(id);			
		}
		return new ResponseEntity<String>(objectMapper.writeValueAsString(Collections.singletonMap("removedIds", removedIds.toArray())), HttpStatus.OK);
	}
	
	public Page<Task> findAll(int page) {
		return taskRepository.findAll(PageRequest.of(page, 5, Sort.by("id").descending()));
	}

	public List<Task> findAll() {
		return taskRepository.findAll();
	}
	
	public Task findById(long id) {
		return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException());
	}
	
	@Transactional(readOnly = false)
	public Task save(Task task) {
		if (task.getCreatedAt() == null) task.setCreatedAt(LocalDateTime.now());
		task.setEditedAt(LocalDateTime.now());
		long executorId = task.getExecutorId();
		if (executorId != 0) task.setExecutor(personRepository.findById(executorId)
				.orElseThrow(() -> new PersonNotFoundException()));
		return taskRepository.save(task);
	}
	
	@Transactional(readOnly = false)
	public void deleteById(long id) {
		taskRepository.deleteById(id);
	}
	
	public Page<Task> findByExecutor(long id, int page){
		return taskRepository.findByExecutor(personRepository.findById(id)
				.orElseThrow(() -> new PersonNotFoundException()), PageRequest.of(page, 5));		
	}
	
	public Page<Task> findByAuthor(long id, int page){
		return taskRepository.findByAuthor(personRepository.findById(id)
				.orElseThrow(() -> new PersonNotFoundException()), PageRequest.of(page, 5));
	}
	
	public Page<Task> findByIdIn(List<Long> ids, int page){
		return taskRepository.findByIdIn(ids, PageRequest.of(page, 5));
	}
	
	public List<Task> searchByCriteria(Map<String, String[]> searchCriteria) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPersistence");
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		List<Predicate> predicates = new ArrayList<>();
		Subquery<Person> sq = cq.subquery(Person.class);
		Root<Person> rootPerson = sq.from(Person.class);
		
		//root.fetch("comments", JoinType.LEFT);
		
		if (searchCriteria.containsKey("id")) {
			List<Predicate> subPredicates = new ArrayList<>();
			for (String id : searchCriteria.get("id")) subPredicates.add(cb.equal(root.get("id"), Long.parseLong(id)));
			predicates.add(cb.or(subPredicates.toArray(new Predicate[0])));
		}
		
		if (searchCriteria.containsKey("executor")) {					
			sq.select(rootPerson).where(rootPerson.get("id").in(Arrays.stream(searchCriteria.get("executor")).mapToLong(e -> Long.valueOf(e)).boxed().toArray()));
			predicates.add(cb.equal(root.get("executor"), sq));
		} else if (searchCriteria.containsKey("executorName")) {
			List<Predicate> subPredicates = new ArrayList<>();
			for (String nameLike : searchCriteria.get("executorName")) subPredicates.add(cb.like(cb.lower(rootPerson.get("name")), "%" + nameLike.toLowerCase() + "%"));
			predicates.add(cb.and(subPredicates.toArray(new Predicate[0])));
			} 
		
		if (searchCriteria.containsKey("author")) {
			sq.select(rootPerson).where(rootPerson.get("id").in(Arrays.stream(searchCriteria.get("author")).mapToLong(e -> Long.valueOf(e)).boxed().toArray()));
			predicates.add(cb.equal(root.get("author"), sq));
		} else if (searchCriteria.containsKey("authorName")) {
			List<Predicate> subPredicates = new ArrayList<>();
			for (String nameLike : searchCriteria.get("authorName")) subPredicates.add(cb.like(cb.lower(rootPerson.get("name")), "%" + nameLike.toLowerCase() + "%"));
			predicates.add(cb.and(subPredicates.toArray(new Predicate[0])));
			//predicates.add(cb.like(cb.lower(rootPerson.get("name")), "%" + searchCriteria.get("authorName") + "%"));
		}
		
		if (searchCriteria.containsKey("title")) {
			List<Predicate> subPredicates = new ArrayList<>();
			for (String titleLike : searchCriteria.get("title")) subPredicates.add(cb.like(cb.lower(root.get("title")), "%" + titleLike.toLowerCase() + "%"));
			predicates.add(cb.and(subPredicates.toArray(new Predicate[0])));
			//predicates.add(cb.like(cb.lower(root.get("title")), "%" + searchCriteria.get("title") + "%"));
		}
		
		if (searchCriteria.containsKey("description")) {
			List<Predicate> subPredicates = new ArrayList<>();
			for (String descriptionLike : searchCriteria.get("description")) subPredicates.add(cb.like(cb.lower(root.get("description")), "%" + descriptionLike.toLowerCase() + "%"));
			predicates.add(cb.and(subPredicates.toArray(new Predicate[0])));
		}
		
		if (searchCriteria.containsKey("status")) 
			predicates.add(root.get("status").in(Task.Status.getStatusesBySubstring(searchCriteria.get("status"))));
		
		if (searchCriteria.containsKey("priority"))
			predicates.add(root.get("priority").in(Task.Priority.getPrioritiesBySubstring(searchCriteria.get("priority"))));
	
		if (searchCriteria.containsKey("createdBefore"))
			predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(searchCriteria.get("createdBefore")[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));

		
		if (searchCriteria.containsKey("createdAfter")) 
			predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(searchCriteria.get("createdAfter")[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
		
		if (searchCriteria.containsKey("editedBefore"))
			predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(searchCriteria.get("editedBefore")[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
		
		if (searchCriteria.containsKey("editedAfter"))
			predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(searchCriteria.get("editedAfter")[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
	
		if (searchCriteria.containsKey("orderBy")) {
			cq.orderBy(Arrays.stream(searchCriteria.get("orderBy")).map(e -> {
			if (e.contains("DESC")) return cb.desc(root.get(e.replace("DESC", "")));
			else return cb.asc(root.get(e));
			}).toList());
		} else {
			cq.orderBy(cb.asc(root.get("id")));
		}
		
		
		//if (!predicates.isEmpty()) 
		cq.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
		TypedQuery<Task> tq = em.createQuery(cq);
		return tq.getResultList();
	}
}
