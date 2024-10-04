package ru.terentyev.TaskManager.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.validation.Valid;
import ru.terentyev.TaskManager.entities.Comment;
import ru.terentyev.TaskManager.entities.Person;
import ru.terentyev.TaskManager.entities.Task;
import ru.terentyev.TaskManager.entities.Task.Status;
import ru.terentyev.TaskManager.entities.TaskRequest;
import ru.terentyev.TaskManager.entities.TaskResponse;
import ru.terentyev.TaskManager.exceptions.AlienTaskException;
import ru.terentyev.TaskManager.exceptions.IncompatibleCriteriaException;
import ru.terentyev.TaskManager.exceptions.PersonNotFoundException;
import ru.terentyev.TaskManager.exceptions.PriorityNotFoundException;
import ru.terentyev.TaskManager.exceptions.StatusNotFoundException;
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
	private EntityManager entityManager;
	private KafkaProducerService kafkaProducerService;
	

	@Autowired
	public TaskServiceImpl(TaskRepository taskRepository, PersonRepository personRepository
			, CommentService commentService, PersonDetailsService personDetailsService
			, EntityManager entityManager, KafkaProducerService kafkaProducerService) {
		super();
		this.taskRepository = taskRepository;
		this.personRepository = personRepository;
		this.commentService = commentService;
		this.personDetailsService = personDetailsService;
		this.entityManager = entityManager;
		this.kafkaProducerService = kafkaProducerService;
	}
	
	@Override
	public TaskResponse[] showTasks(TaskRequest request) throws JsonProcessingException {
		List<Task> tasksFound = null;
		List<TaskResponse> responses = new ArrayList<>();
		if (request == null) 
			tasksFound = taskRepository.findAll();
		else
			tasksFound = searchByCriteria(request);
		for (Task task : tasksFound)
			responses.add(fillTaskResponse(task, commentService.countByTask(task.getId())));
		if (responses.size() == 1) 
			responses.get(0).setComments(commentService.findByTask(responses.get(0).getId(), 0).getContent());
		return responses.toArray(new TaskResponse[0]);
	}
	
	public TaskResponse[] showAllTasks(Integer page, String sortBy) throws JsonProcessingException, NumberFormatException {
		List<TaskResponse> responses = new ArrayList<>();
		for (Task task : findAll(page, sortBy).getContent())
			responses.add(fillTaskResponse(task, commentService.countByTask(task.getId())));
		return responses.toArray(new TaskResponse[0]);
	}

	public TaskResponse fillTaskResponse(Task task, int commentsCount) throws JsonProcessingException, NumberFormatException{
		TaskResponse response = new TaskResponse();
		response.setId(task.getId());
		response.setTitle(task.getTitle());
		response.setDescription(task.getDescription());
		response.setStatus(task.getStatus().name());
		response.setPriority(task.getPriority().name());
		response.setAuthorId(task.getAuthor().getId());
		response.setExecutorId(task.getExecutor().getId());
		response.setCreatedAt(task.getCreatedAt());
		response.setEditedAt(task.getEditedAt());
		putCommentsCount(response, commentsCount);
		return response;
	}
	
	// Не уверен, что это нужно. Отложил на потом.
	public TaskResponse searchForMeetsAnyCriteria(TaskRequest request) {
		return null;
	}
	
	@Override
	public TaskResponse getSingleTask(long id, int page) throws JsonProcessingException {
		List<Comment> comments = commentService.findByTask(id, 0).getContent();
		TaskResponse response = fillTaskResponse(findById(id), comments.size());
		response.setComments(comments);
		putCommentsCount(response, comments.size());
		return response;
	}
	
	public void putCommentsCount(TaskResponse response, int commentsCount) {
		response.setCommentsCount(commentsCount);
		final int PAGE_SIZE = 10;
		response.setCommentsPages((int) Math.ceil(commentsCount / PAGE_SIZE));
	}
	
	@Transactional(readOnly = false)
	public Task fillTask(TaskRequest request, Task task, PersonDetails pd) {
		if (request.getTitle() != null) task.setTitle(request.getTitle()[0]);
		if (request.getDescription() != null) task.setDescription(request.getDescription()[0]);
		Status status = Task.Status.getStatusBySubstring(request.getStatus()[0]);
		if (request.getStatus() != null) task.setStatus(status);
		if (request.getPriority() != null) task.setPriority(Task.Priority.getPriorityBySubstring(request.getPriority()[0]));
		task.setAuthor(pd.getPerson());
		if (request.getExecutor() != null)
			task.setExecutor(personDetailsService.findById(request.getExecutor()[0]));
		else if (task.getExecutor() == null)
			task.setExecutor(pd.getPerson());
		LocalDateTime now = LocalDateTime.now();
		if (task.getCreatedAt() == null) task.setCreatedAt(now);
		task.setEditedAt(now);
		if (status != task.getStatus()) task.setStatusChangedAt(now);

		return task;	
	}
	
	@Override
	@Transactional(readOnly = false)
	public TaskResponse addTask(TaskRequest request, PersonDetails pd) throws JsonProcessingException {
		checkValid(request);	
		Task taskToAdd = save(fillTask(request, new Task(), pd));
		//TaskRequest requestForStatistics = new TaskRequest();
		request.setTaskBeforeChanges(taskToAdd);			
		kafkaProducerService.reportAddingTask(request);
		return fillTaskResponse(taskToAdd, 0);
	}
	
	@Transactional(readOnly = false)
	public void checkValid(TaskRequest request) {
		Long[] executorIdFromRequest = request.getExecutor();
		String[] executorNameFromRequest = request.getExecutorName();
		
		if (executorIdFromRequest != null && executorNameFromRequest != null) 
			throw new IncompatibleCriteriaException("Одновременное использование ключей 'executor' и 'executorName' недопустимо.");
		
		if ((executorIdFromRequest != null && executorIdFromRequest.length != 0
				&& personDetailsService.findById(executorIdFromRequest[0]) == null) 
				|| (executorNameFromRequest != null && executorNameFromRequest.length != 0 
				&& personDetailsService.findByUsername(executorNameFromRequest[0]) == null)) 
				throw new PersonNotFoundException();
		
		if (request.getStatus() != null && Task.Status.getStatusesBySubstring(request.getStatus()).size() != 1) 
			throw new StatusNotFoundException();
		
		if (request.getPriority() != null && Task.Priority.getPrioritiesBySubstring(request.getPriority()).size() != 1) 
			throw new PriorityNotFoundException();
	}
	
	@Override
	public TaskResponse updateTask(Long id, TaskRequest request, PersonDetails pd) throws JsonMappingException, JsonProcessingException {
		checkValid(request);
		Task task = findById(id);
		if (task == null) throw new TaskNotFoundException();
		request.setTaskBeforeChanges(task);
		task = save(fillTask(request, task, pd));
		kafkaProducerService.reportUpdatingTask(request);
		return fillTaskResponse(task, commentService.countByTask(id));
	}
	
	@Override
	public void deleteTask(Long id, PersonDetails pd) throws JsonProcessingException {	
		Task task = findById(id);
		if (task == null) 
			throw new TaskNotFoundException();
		if (task.getAuthor().getId() != pd.getPerson().getId()) 
			throw new AlienTaskException(task.getId());
		TaskRequest request = new TaskRequest();
		request.setTaskBeforeChanges(task);
		kafkaProducerService.reportDeletingTask(request);
	}
	
	@Override
	public Page<Task> findAll(Integer page, String sortBy) {
		if (page == null || page == 0) {
			return new PageImpl<>(taskRepository.findAll());
		}
		return taskRepository.findAll(PageRequest.of(page, 10, Sort.by(sortBy == null ? "id" : sortBy)));
	}
	
	@Override
	public Task findById(long id) {
		return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException());
	}
	
	@Override
	@Transactional(readOnly = false)
	public Task save(Task task) {
		if (task.getCreatedAt() == null) task.setCreatedAt(LocalDateTime.now());
		task.setEditedAt(LocalDateTime.now());
		long executorId = task.getExecutorId();
		if (executorId != 0) task.setExecutor(personRepository.findById(executorId)
				.orElseThrow(() -> new PersonNotFoundException()));
		return taskRepository.save(task);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void deleteById(long id) {
		taskRepository.deleteById(id);
	}
	
	@Override
	public Page<Task> findByExecutor(long id, int page){
		return taskRepository.findByExecutor(personRepository.findById(id)
				.orElseThrow(() -> new PersonNotFoundException()), PageRequest.of(page, 5));		
	}
	
	@Override
	public Page<Task> findByAuthor(long id, int page){
		return taskRepository.findByAuthor(personRepository.findById(id)
				.orElseThrow(() -> new PersonNotFoundException()), PageRequest.of(page, 5));
	}
	
	@Override
	public Page<Task> findByIdIn(List<Long> ids, int page){
		return taskRepository.findByIdIn(ids, PageRequest.of(page, 5));
	}
	
	public List<Task> searchByCriteria(TaskRequest request) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
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
			predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getCreatedBefore(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));

		
		if (request.getCreatedAfter() != null) 
			predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getCreatedAfter(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
		
		if (request.getEditedBefore() != null)
			predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getEditedBefore(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
		
		if (request.getEditedAfter() != null)
			predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(request.getEditedAfter(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
	
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
		TypedQuery<Task> tq = entityManager.createQuery(cq);
		return tq.getResultList();
	}	
}
