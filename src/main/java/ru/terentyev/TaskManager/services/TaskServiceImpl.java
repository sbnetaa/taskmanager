package ru.terentyev.TaskManager.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.terentyev.TaskManager.entities.TaskResponse;
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
	
	public ResponseEntity<TaskResponse> showTasks(TaskRequest request) throws JsonProcessingException {
		if (request == null) 
			throw new JsonProcessingException("Передан пустой JSON"){};
		return new ResponseEntity<>(fillResponse(request), HttpStatus.OK);
	}
	
	public TaskResponse fillResponse(TaskRequest request) throws JsonProcessingException, NumberFormatException{
		//if (request.getId() != null && request.getId().length == 1) 
			//return getSingleTask(Long.valueOf(request.getId()[0])
					//, request.getPage());
		return searchForMeetsAllCriteria(request);
	}
	
	public TaskResponse searchForMeetsAllCriteria(TaskRequest request) {
		final int PAGE_SIZE = 10;
		TaskResponse response = new TaskResponse();
		List<Task> foundTasks = searchByCriteria(request);
		for (Task task : foundTasks) {
			int commentsCount = commentService.countByTask(task.getId());
			task.setCommentsCount(commentsCount + " comments, " + (int) Math.ceil(commentsCount / PAGE_SIZE) + " pages");
		}
		response.setTasks(foundTasks);
		return response;
	}
	
	// Не уверен, что это стоит делать. Отложил на потом.
	public TaskResponse searchForMeetsAnyCriteria(TaskRequest request) {
		return null;
	}
	
	public TaskResponse getSingleTask(long id, int page) throws JsonProcessingException {
			Task task = findById(id);
			task.setComments(commentService.findByTask(id, page).getContent());
			//objectMapper.addMixIn(Task.class, SingleTaskMixin.class);
			TaskResponse response = new TaskResponse();
			response.setTasks(List.of(task));
			return response;
	}
	
	public JsonNode editCommentsRecord(Task task, int page) throws JsonProcessingException {
		final long PAGE_SIZE = 10;
		String taskAsJson = objectMapper.writeValueAsString(task);
		ObjectNode root = (ObjectNode) objectMapper.readTree(taskAsJson);
		root.set("comments(page " + page + " of " + (int) Math.ceil(task.getComments().size() / PAGE_SIZE) + ")", root.get("comments"));
		root.remove("comments");
		return root;
	}
	
	public ResponseEntity<TaskResponse> checkIfAddedTaskAlreadyHasId(TaskRequest request) throws JsonProcessingException {
		TaskResponse response = new TaskResponse();
		if (request.getId() != null || request.getAuthor() != null) 
			response.setError("Поля 'id' и 'author' не должны быть указаны."
			+ " Они будут сгенерированы автоматически." 
			+ " Для изменения записи используйте PATCH запрос.");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	public Task fillAddedTask(TaskRequest request, PersonDetails pd) {
		@Valid Task taskToAdd = new Task();
		taskToAdd.setTitle(request.getTitle()[0]);
		taskToAdd.setDescription(request.getDescription()[0]);
		taskToAdd.setAuthor(pd.getPerson());
		taskToAdd.setExecutor(personDetailsService.findById(request.getExecutor()[0]));
		taskToAdd.setPriority(Task.Priority.valueOf(request.getPriority()[0]));
		taskToAdd.setStatus(Task.Status.valueOf(request.getStatus()[0]));
		taskToAdd.setCreatedAt(LocalDateTime.now());
		taskToAdd.setEditedAt(LocalDateTime.now());
		return taskToAdd;	
	}
	
	public ResponseEntity<TaskResponse> addTask(TaskRequest request, BindingResult br
			, PersonDetails pd) throws JsonProcessingException{
		Task taskToAdd = fillAddedTask(request, pd);
		ResponseEntity<TaskResponse> response = checkIfAddedTaskAlreadyHasId(request);
		save(taskToAdd);
		response.getBody().getTasks().add(taskToAdd);
		return response;
	}
	
	
	public Task fillPatchingFields(TaskRequest singlePatchRequest) {
		Task task = findById(singlePatchRequest.getId()[0]);
		if (singlePatchRequest.getTitle() != null) task.setTitle(singlePatchRequest.getTitle()[0]);
		if (singlePatchRequest.getDescription() != null) task.setDescription(singlePatchRequest.getDescription()[0]);
		if (singlePatchRequest.getStatus() != null) task.setStatus(Task.Status.valueOf(singlePatchRequest.getStatus()[0]));
		if (singlePatchRequest.getPriority() != null) task.setPriority(Task.Priority.valueOf(singlePatchRequest.getPriority()[0]));
		if (singlePatchRequest.getExecutor() != null) task.setAuthor(personDetailsService.findById(singlePatchRequest.getExecutor()[0]));
		return task;
	}
	
	public ResponseEntity<TaskResponse> updateTask(TaskRequest[] request) throws JsonMappingException, JsonProcessingException{
		TaskResponse response = new TaskResponse();
		for (TaskRequest singlePatchRequest : request) {
			Task task = fillPatchingFields(singlePatchRequest);
			save(task);
			response.getTasks().add(task);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	public ResponseEntity<String> deleteTask(TaskRequest request) throws JsonProcessingException{
		List<Long> removedIds = new ArrayList<>();
		for (long id : request.getId()) {
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
	
	public List<Task> searchByCriteria(TaskRequest request) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPersistence");
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		List<Predicate> predicates = new ArrayList<>();
		Subquery<Person> sq = cq.subquery(Person.class);
		Root<Person> rootPerson = sq.from(Person.class);
		
		//root.fetch("comments", JoinType.LEFT);
		
		if (request.getId() != null) {
			List<Predicate> subPredicates = new ArrayList<>();
			for (long id : request.getId()) subPredicates.add(cb.equal(root.get("id"), id));
			predicates.add(cb.or(subPredicates.toArray(new Predicate[0])));
		}
		
		if (request.getExecutor() != null) {					
			sq.select(rootPerson).where(rootPerson.get("id").in(request.getExecutor()));
			predicates.add(cb.equal(root.get("executor"), sq));
		} else if (request.getExecutorName() != null) {
			List<Predicate> subPredicates = new ArrayList<>();
			for (String nameLike : request.getExecutorName()) subPredicates.add(cb.like(cb.lower(rootPerson.get("name")), "%" + nameLike.toLowerCase() + "%"));
			predicates.add(cb.and(subPredicates.toArray(new Predicate[0])));
			} 
		
		if (request.getAuthor() != null) {
			sq.select(rootPerson).where(rootPerson.get("id").in(request.getAuthor()));
			predicates.add(cb.equal(root.get("author"), sq));
		} else if (request.getAuthorName() != null) {
			List<Predicate> subPredicates = new ArrayList<>();
			for (String nameLike : request.getAuthorName()) subPredicates.add(cb.like(cb.lower(rootPerson.get("name")), "%" + nameLike.toLowerCase() + "%"));
			predicates.add(cb.and(subPredicates.toArray(new Predicate[0])));
		}
		
		if (request.getTitle() != null) {
			List<Predicate> subPredicates = new ArrayList<>();
			for (String titleLike : request.getTitle()) subPredicates.add(cb.like(cb.lower(root.get("title")), "%" + titleLike.toLowerCase() + "%"));
			predicates.add(cb.and(subPredicates.toArray(new Predicate[0])));
		}
		
		if (request.getDescription() != null) {
			List<Predicate> subPredicates = new ArrayList<>();
			for (String descriptionLike : request.getDescription()) subPredicates.add(cb.like(cb.lower(root.get("description")), "%" + descriptionLike.toLowerCase() + "%"));
			predicates.add(cb.and(subPredicates.toArray(new Predicate[0])));
		}
		
		if (request.getStatus() != null) 
			predicates.add(root.get("status").in(Task.Status.getStatusesBySubstring(request.getStatus())));
		
		if (request.getPriority() != null)
			predicates.add(root.get("priority").in(Task.Priority.getPrioritiesBySubstring(request.getPriority())));
	
		if (request.getCreatedBefore() != null)
			predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getCreatedBefore()[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));

		
		if (request.getCreatedAfter() != null) 
			predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getCreatedAfter()[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
		
		if (request.getEditedBefore() != null)
			predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getEditedBefore()[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
		
		if (request.getEditedAfter() != null)
			predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getEditedAfter()[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
	
		if (request.getOrderBy() != null) {
			cq.orderBy(Arrays.stream(request.getOrderBy()).map(e -> {
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
