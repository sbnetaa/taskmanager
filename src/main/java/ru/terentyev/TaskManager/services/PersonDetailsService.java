package ru.terentyev.TaskManager.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.terentyev.TaskManager.entities.Person;
import ru.terentyev.TaskManager.repositories.PersonRepository;
import ru.terentyev.TaskManager.security.PersonDetails;

@Service
@Transactional(readOnly = true)
public class PersonDetailsService implements UserDetailsService {
	
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PersonRepository personRepository;
    
    @Autowired
    public PersonDetailsService(@Lazy BCryptPasswordEncoder bCryptPasswordEncoder,
			PersonRepository personRepository) {
		super();	
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.personRepository = personRepository;
	}

    public PersonDetailsService(){}
    
	@Override
    public UserDetails loadUserByUsername(String username) {    	
        Optional<Person> person = personRepository.findByName(username); 
            return new PersonDetails(person.orElseThrow(() -> new UsernameNotFoundException("Username not found")));
  
    }
    
    @Transactional(readOnly = false)
    public Person registerNewUserAccount(Person person) {
        person.setPassword(bCryptPasswordEncoder.encode(person.getPassword()));
        person.setRegistrationDate(LocalDateTime.now());
        return personRepository.save(person);
    }
    
    @Transactional(readOnly = false)
    public Person registerNewUserAccountRest(Person newPerson) {
    	newPerson.setPassword(bCryptPasswordEncoder.encode(newPerson.getPassword()));
    	newPerson.setId(null);
    	newPerson.setExecutableTasks(null);
    	newPerson.setComments(null);
    	newPerson.setCreatedTasks(null);
    	newPerson.setRegistrationDate(LocalDateTime.now());
    	return personRepository.save(newPerson);
    }
    
    public Person findById(Long Id) {
    	Optional<Person> oPerson = personRepository.findById(Id); 
        return oPerson.orElse(null);    
    }
	
    public Person findByUsername(String name) {
    	Optional<Person> oPerson = personRepository.findByName(name);
    	return oPerson.orElse(null);
    }
    
    public Person findByEmail(String email) {
       	Optional<Person> oPerson = personRepository.findByEmail(email);
    	return oPerson.orElse(null);
    }
    
    public List<Person> findAll(){
    	return personRepository.findAll();
    }
}
