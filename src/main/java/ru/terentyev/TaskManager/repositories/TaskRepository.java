package ru.terentyev.TaskManager.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.terentyev.TaskManager.entities.Person;
import ru.terentyev.TaskManager.entities.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
	Page<Task> findAll(Pageable pageable);
	List<Task> findAll();
	Page<Task> findByExecutor(Person executor, Pageable pageable);
	Page<Task> findByAuthor(Person author, Pageable pageable);
	Page<Task> findByIdIn(List<Long> ids, Pageable pageable);
}
