package ru.terentyev.TaskManager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.terentyev.TaskManager.security.PersonDetails;
import ru.terentyev.TaskManager.services.CommentService;
import ru.terentyev.TaskManager.services.PersonService;
import ru.terentyev.TaskManager.services.TaskService;


@RequestMapping("/people")
@Controller
public class PersonController {

	private PersonService personService;
	private TaskService taskService;
	private CommentService commentService;
	
	
	@Autowired
	public PersonController(PersonService personService, TaskService taskService, CommentService commentService) {
		super();
		this.personService = personService;
		this.taskService = taskService;
		this.commentService = commentService;
	}



	@GetMapping("/{page}")
	public String showUsers(@PathVariable int page, Model model){
		model.addAttribute("people", personService.findAll(page));
		model.addAttribute("currentPage", page);
		return "people";
	}
	
	@GetMapping("/view/{id}")
	public String viewPerson(@PathVariable long id, Model model){
		model.addAttribute("person", personService.findById(id));
		return "person";
	}
	
	@GetMapping("/view/{id}/executable-tasks/{page}")
	public String showTasksOfExecutor(@PathVariable int page, @PathVariable long id, Model model){
			model.addAttribute("tasks", taskService.findByExecutor(id, page));
			model.addAttribute("executorId", id);
			model.addAttribute("currentPage", page);
			return "executableTasks";
	}
	
	@GetMapping("/view/{id}/created-tasks/{page}")
	public String showTasksOfAuthor(@PathVariable int page, @PathVariable long id, Model model){
			model.addAttribute("tasks", taskService.findByAuthor(id, page));
			model.addAttribute("authorId", id);
			model.addAttribute("currentPage", page);
			return "createdTasks";
	}
	
	@GetMapping("/view/{id}/comments/{page}")
	public String showCommentsOfAuthor(@PathVariable int page, @PathVariable long id, Model model, @AuthenticationPrincipal PersonDetails pd) {
		model.addAttribute("comments", commentService.findByAuthor(id, page));
		model.addAttribute("authorId", id);
		model.addAttribute("currentPage", page);
		if (pd != null) model.addAttribute("personId", pd.getPerson().getId());
		return "comments";
	}
}
