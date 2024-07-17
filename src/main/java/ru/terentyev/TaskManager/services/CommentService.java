package ru.terentyev.TaskManager.services;

import org.springframework.data.domain.Page;

import ru.terentyev.TaskManager.entities.Comment;

public interface CommentService {

		Page<Comment> findByTask(long id, int page);
		Comment save(Comment comment);
		Comment findById(long id);
		void deleteById(long id);
		Page<Comment> findByAuthor(long id, int page);
		Integer countByTask(long id);
	
}
