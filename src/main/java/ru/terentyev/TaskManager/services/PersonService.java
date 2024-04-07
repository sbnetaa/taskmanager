package ru.terentyev.TaskManager.services;

import org.springframework.data.domain.Page;

import ru.terentyev.TaskManager.entities.Person;

public interface PersonService {
	Page<Person> findAll(int page);
	Person findById(long id);
}
