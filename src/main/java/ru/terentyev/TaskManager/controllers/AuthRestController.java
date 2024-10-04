package ru.terentyev.TaskManager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ru.terentyev.TaskManager.entities.Person;
import ru.terentyev.TaskManager.services.PersonDetailsService;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
, headers = "Accept=application/json")
public class AuthRestController {

	private PersonDetailsService personDetailsService;

	@Autowired
	public AuthRestController(PersonDetailsService personDetailsService) {
		super();
		this.personDetailsService = personDetailsService;
	}
	
	@PostMapping("/registration")
	public ResponseEntity<String> registerNewUser(@Valid @RequestBody Person newPerson){
		if (newPerson.getName() == null || newPerson.getName().isBlank() || newPerson.getPassword() == null || newPerson.getPassword().isBlank())
			return new ResponseEntity<>("Поля 'name', 'password' и 'passwordConfirm' обязательны.", HttpStatus.BAD_REQUEST);		
		if (!newPerson.getPassword().equals(newPerson.getPasswordConfirm()))
			return new ResponseEntity<>("Пароль и его подтверждение не совпадают.", HttpStatus.BAD_REQUEST);
		if (personDetailsService.findByUsername(newPerson.getName()) != null)
			return new ResponseEntity<>("Пользователь с именем " + newPerson.getName() + " уже существует.", HttpStatus.BAD_REQUEST);
		if (personDetailsService.findByEmail(newPerson.getEmail()) != null)
			return new ResponseEntity<>("Пользователь с email " + newPerson.getEmail() + " уже существует.", HttpStatus.BAD_REQUEST);
		personDetailsService.registerNewUserAccountRest(newPerson);
		return new ResponseEntity<>("Пользователь успешно зарегистрирован.", HttpStatus.OK);
	}
}
