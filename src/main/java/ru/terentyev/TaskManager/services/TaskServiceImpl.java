package ru.terentyev.TaskManager.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
		return new ResponseEntity<>(fillResponse(request), HttpStatus.OK);
	}
	
	public TaskResponse fillResponse(TaskRequest request) throws JsonProcessingException, NumberFormatException{
		if (request == null) {
			TaskResponse response = new TaskResponse();
			response.setTasks(findAll(null).getContent());
			return response;
		}
		return searchForMeetsAllCriteria(request);
	}
	
	public TaskResponse searchForMeetsAllCriteria(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		List<Task> foundTasks = searchByCriteria(request);
		for (Task task : foundTasks) {
			setTaskCommentsCount(task);
		}
		response.setTasks(foundTasks);
		return response;
	}
	
	// Не уверен, что это нужно. Отложил на потом.
	public TaskResponse searchForMeetsAnyCriteria(TaskRequest request) {
		return null;
	}
	
	public TaskResponse getSingleTask(long id, int page) throws JsonProcessingException {
			Task task = findById(id);
			task.setComments(commentService.findByTask(id, page).getContent());
			setTaskCommentsCount(task);
			TaskResponse response = new TaskResponse();
			response.setTasks(List.of(task));
			return response;
	}
	
	
	@Transactional(readOnly = false)
	public Task fillAddedTask(TaskRequest request, PersonDetails pd) {
		@Valid Task taskToAdd = new Task();
		taskToAdd.setTitle(request.getTitle()[0]);
		taskToAdd.setDescription(request.getDescription()[0]);
		taskToAdd.setAuthor(pd.getPerson());
		taskToAdd.setAuthorName(pd.getPerson().getName());
		Long[] executorId = request.getExecutor();
		if (executorId != null && executorId.length != 0) {
			Person executor = personDetailsService.findById(executorId[0]);
			taskToAdd.setExecutor(executor);
			if (executor != null) taskToAdd.setExecutorName(executor.getName());
		}
		taskToAdd.setPriority(Task.Priority.valueOf(request.getPriority()[0]));
		taskToAdd.setStatus(Task.Status.valueOf(request.getStatus()[0]));
		taskToAdd.setCreatedAt(LocalDateTime.now());
		taskToAdd.setEditedAt(LocalDateTime.now());
		return taskToAdd;	
	}
	
	@Transactional(readOnly = false)
	public ResponseEntity<TaskResponse> addTask(TaskRequest request, BindingResult br
			, PersonDetails pd) throws JsonProcessingException {
		TaskResponse response = checkValid(request);
		if (response.getErrors() != null && !response.getErrors().isEmpty()) 
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		Task taskToAdd = fillAddedTask(request, pd);
		response.setTasks(List.of(save(taskToAdd)));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@Transactional(readOnly = false)
	public TaskResponse checkValid(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		Long[] executorIdFromRequest = request.getExecutor();
		String[] executorNameFromRequest = request.getExecutorName();
		
		if (executorIdFromRequest != null && executorNameFromRequest != null) {
			response.addError("Одновременное использование ключей 'executor' и 'executorName' недопустимо.");
		}
		
		if (executorIdFromRequest != null && executorIdFromRequest.length != 0 && personDetailsService.findById(executorIdFromRequest[0]) == null) 
				response.addError("Пользователь с ID " + executorIdFromRequest[0] + " не найден.");
		else if (executorNameFromRequest != null && executorNameFromRequest.length != 0 && personDetailsService.findByUsername(executorNameFromRequest[0]) == null)
				response.addError("Пользователь с именем " + executorNameFromRequest[0] + " не найден.");

		if (request.getAuthor() != null || request.getAuthorName() != null)
			response.addError("Указанание автора при добавлении задачи недопустимо.");
		
		if (request.getId() != null)
			response.addError("Указание ID для новой записи недопустимо. ID должен быть сгенерирован автоматически.");
		
		if (Task.Status.getStatusesBySubstring(request.getStatus()).size() != 1) 
			response.addError("Не найден единственный подходящий статус.");
		
		if (Task.Priority.getPrioritiesBySubstring(request.getPriority()).size() != 1) 
			response.addError("Не найден единственный подходящий приоритет.");
		
		return response;
	}
	
	
	public Task fillPatchingFields(TaskRequest singlePatchRequest) {
		Task task = findById(singlePatchRequest.getId()[0]);
		if (singlePatchRequest.getTitle() != null) task.setTitle(singlePatchRequest.getTitle()[0]);
		if (singlePatchRequest.getDescription() != null) task.setDescription(singlePatchRequest.getDescription()[0]);
		if (singlePatchRequest.getStatus() != null) task.setStatus(Task.Status.valueOf(singlePatchRequest.getStatus()[0]));
		if (singlePatchRequest.getPriority() != null) task.setPriority(Task.Priority.valueOf(singlePatchRequest.getPriority()[0]));
		if (singlePatchRequest.getExecutor() != null) task.setExecutor(personDetailsService.findById(singlePatchRequest.getExecutor()[0]));
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
	
	public Page<Task> findAll(Integer page) {
		if (page == null || page == 0) {
			return new PageImpl<>(taskRepository.findAll());
		}
		return taskRepository.findAll(PageRequest.of(page, 5, Sort.by("id").descending()));
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
			predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getCreatedBefore(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));

		
		if (request.getCreatedAfter() != null) 
			predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getCreatedAfter(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
		
		if (request.getEditedBefore() != null)
			predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getEditedBefore(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
		
		if (request.getEditedAfter() != null)
			predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getEditedAfter(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
	
		/*
		if (request.getOrderBy() != null) {
			cq.orderBy(Arrays.stream(request.getOrderBy()).map(e -> {
			if (e.contains("DESC")) return cb.desc(root.get(e.replace("DESC", "")));
			else return cb.asc(root.get(e));
			}).toList());
		} else {
			cq.orderBy(cb.asc(root.get("id")));
		}
		*/
		String order = request.getOrderBy();
		
		if (order != null) {
			if (order.contains("DESC"))
				cq.orderBy(cb.desc(root.get(order.replace("DESC", ""))));
			else
				cq.orderBy(cb.asc(root.get(order)));
		} else {
			cq.orderBy(cb.asc(root.get("id")));
		}
		
		//if (!predicates.isEmpty()) 
		cq.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
		TypedQuery<Task> tq = em.createQuery(cq);
		return tq.getResultList();
	}
	
	public void setTaskCommentsCount(Task task) {
		final int PAGE_SIZE = 10;
		final int commentsCount = commentService.countByTask(task.getId());
		task.setCommentsCount(commentsCount);
		task.setCommentsPages((int) Math.ceil(commentsCount / PAGE_SIZE));
	}
}
