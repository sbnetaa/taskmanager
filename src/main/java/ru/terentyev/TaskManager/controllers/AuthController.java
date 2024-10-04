package ru.terentyev.TaskManager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import ru.terentyev.TaskManager.entities.Person;
import ru.terentyev.TaskManager.security.PersonDetails;
import ru.terentyev.TaskManager.services.PersonDetailsService;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private PersonDetailsService personDetailsService;
    
    @Autowired
    public AuthController(PersonDetailsService personDetailsService) {
		super();
		this.personDetailsService = personDetailsService;
	}

	@GetMapping("/login")
    public String loginPage(@AuthenticationPrincipal PersonDetails personDetails, Model model) {  	
    	if (personDetails == null) return "login";
    	model.addAttribute("error", "Вы уже аутентифицированы");
    	return "redirect:/";
    }
   
    
    
    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("registrationForm", new Person());
        return "registration";
    }

    
    @PostMapping("/registration")
    public String saveUser(@ModelAttribute("registrationForm") @Valid Person registringPerson
    		, BindingResult bindingResult, Model model) {
    	
        if (bindingResult.hasErrors()) return "registration";              
        if (!registringPerson.getPassword().equals(registringPerson.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }              
        if (personDetailsService.findByUsername(registringPerson.getName()) != null) {
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "registration";
        }        
        personDetailsService.registerNewUserAccount(registringPerson);

        return "redirect:/";
    }
}
