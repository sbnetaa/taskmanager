package ru.terentyev.TaskManager.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.terentyev.TaskManager.entities.Comment;
import ru.terentyev.TaskManager.entities.Task;
import ru.terentyev.TaskManager.exceptions.TaskNotFoundException;
import ru.terentyev.TaskManager.repositories.CommentRepository;
import ru.terentyev.TaskManager.repositories.TaskRepository;

@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
	private CommentRepository commentRepository;
	private TaskRepository taskRepository;
	
	@Autowired
	public CommentServiceImpl(CommentRepository commentRepository, TaskRepository taskRepository) {
		super();
		this.commentRepository = commentRepository;
		this.taskRepository = taskRepository;
	}
	
	public Page<Comment> findByTask(long id, int page){
		return commentRepository.findByTask(id, PageRequest.of(page, 10, Sort.by("id")));
	}
	
	@Transactional(readOnly = false)
	public Comment save(Comment comment) {
		Task task = comment.getTask();
		if (task == null) {
		task = taskRepository.findById(comment.getTaskId()).orElseThrow(() -> new TaskNotFoundException());
		comment.setTask(task);
		} 
		if (comment.getCreatedAt() == null) comment.setCreatedAt(LocalDateTime.now());
		comment.setEditedAt(LocalDateTime.now());
		task.getComments().add(comment);
		taskRepository.save(task);
		commentRepository.save(comment);
		return comment;
		
	}
	
	
	public Comment findById(long id) {
		return commentRepository.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = false)
	public void deleteById(long id) {
		commentRepository.deleteById(id);
	}
	
	public Page<Comment> findByAuthor(long id, int page) {
		return commentRepository.findByAuthor_id(id, PageRequest.of(page, 10, Sort.by("id")));
	}
	
	public Integer countByTask(long id) {
		return commentRepository.countByTask_id(id);
	}
}
