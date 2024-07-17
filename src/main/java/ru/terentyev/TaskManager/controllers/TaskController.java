package ru.terentyev.TaskManager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import ru.terentyev.TaskManager.entities.Comment;
import ru.terentyev.TaskManager.entities.Person;
import ru.terentyev.TaskManager.entities.Task;
import ru.terentyev.TaskManager.exceptions.MismatchIdentifierException;
import ru.terentyev.TaskManager.security.PersonDetails;
import ru.terentyev.TaskManager.services.CommentService;
import ru.terentyev.TaskManager.services.PersonDetailsService;
import ru.terentyev.TaskManager.services.TaskService;

@Controller
@RequestMapping("/tasks")
public class TaskController {

	private TaskService taskService;
	private CommentService commentService;
	private PersonDetailsService personDetailsService;
	
	
	@Autowired
	public TaskController(TaskService taskService, CommentService commentService
			, PersonDetailsService personDetailsService) {
		super();
		this.taskService = taskService;
		this.commentService = commentService;
		this.personDetailsService = personDetailsService;
	}

	@GetMapping("/{page}")
	public String showTasks(@PathVariable int page, Model model, @AuthenticationPrincipal PersonDetails pd){
		model.addAttribute("tasks", taskService.findAll(page));
		model.addAttribute("currentPage", page);
		if (pd != null)	model.addAttribute("personId", pd.getPerson().getId());
		return "tasks";
	}
	
	@GetMapping("/view/{id}/{page}")
	public String showTask(@PathVariable long id, @PathVariable int page, Model model, @AuthenticationPrincipal PersonDetails pd) {
		if (pd != null) model.addAttribute("personId", pd.getPerson().getId());
	model.addAttribute("task", taskService.findById(id));
	model.addAttribute("comments", commentService.findByTask(id, page));
	model.addAttribute("comment", new Comment());
	model.addAttribute("currentPage", page);
	return "task";
	}
	

	
	@GetMapping("/add")
	public String addTaskPage(Model model){
		model.addAttribute("task", new Task());
		model.addAttribute("executors", personDetailsService.findAll());
		return "addTask";
		
	}
	
	@PostMapping("/add")
	public String addTask(@ModelAttribute @Valid Task task, BindingResult br, Model model
			, @AuthenticationPrincipal PersonDetails pd) {
		if (br.hasErrors()) {
			model.addAttribute("executors", personDetailsService.findAll());
			return "addTask";		
		}
		task.setAuthor(pd.getPerson());
		return showTask(taskService.save(task).getId(), 0, model, pd);

	}
	
	@GetMapping("/edit/{id}")
	public String editPage(@PathVariable long id, Model model, @AuthenticationPrincipal PersonDetails pd) {
		Task task = taskService.findById(id);
		if (task.getAuthor() == null || task.getAuthor().getId() != pd.getPerson().getId()) {
			throw new MismatchIdentifierException("ID автора не совпадает с Вашим. Похоже, что Вы не являетесь автором.");
		}
		model.addAttribute("task", task);
		model.addAttribute("executors", personDetailsService.findAll());
		return "editTask";
	}
	
	@PatchMapping("/edit")
	public String edit(@ModelAttribute @Valid Task task, BindingResult br, Model model
			, @AuthenticationPrincipal PersonDetails pd) {
		if (br.hasErrors()) return "editTask";
		if (task.getAuthor().getId() != pd.getPerson().getId()) {
			throw new MismatchIdentifierException("ID автора задачи не совпадает с Вашим. Похоже, что Вы не являетесь автором");
		}
		return showTask(taskService.save(task).getId(), 0, model, pd);
	}
	
	@GetMapping("/change-status/{id}")
	public String changeStatus(@PathVariable long id, Model model, @AuthenticationPrincipal PersonDetails pd) {
		Task task = taskService.findById(id);
		Person executor = task.getExecutor();
		if (executor == null || executor.getId() != pd.getPerson().getId()) 
			throw new MismatchIdentifierException("ID исполнителя задачи не совпадает с Вашим. Похоже, что Вы не являетесь исполнителем.");
		
		model.addAttribute("task", task);
		return "changeStatus";
	}
	
	@PatchMapping("/change-status")
	public String changeStatus(@ModelAttribute Task task, Model model, @AuthenticationPrincipal PersonDetails pd) {
		Person executor = task.getExecutor();
		if (executor == null || executor.getId() != pd.getPerson().getId()) 
			throw new MismatchIdentifierException("ID исполнителя задачи не совпадает с Вашим. Похоже, что Вы не являетесь исполнителем.");
		taskService.save(task);
		return showTask(task.getId(), 0, model, pd);
	}
	
	@GetMapping("/view/{id}/comments/{page}")
	public String viewCommentsOfAuthor(@PathVariable long id, @PathVariable int page, Model model) {
		model.addAttribute("comments", commentService.findByAuthor(id, page));
		return "comments";
	}
	
	@PostMapping("/comment")
	public String postComment(@ModelAttribute @Valid Comment comment, BindingResult br, Model model 
			, @AuthenticationPrincipal PersonDetails pd) {
		if (br.hasErrors())	return showTask(comment.getTaskId(), 0, model, pd);
		comment.setAuthor(pd.getPerson());
		return showTask(commentService.save(comment).getTask().getId(), 0, model, pd);
	}
	
	@GetMapping("/comment/edit/{id}")
	public String editCommentPage(@PathVariable long id, Model model, @AuthenticationPrincipal PersonDetails pd) {
		Comment comment = commentService.findById(id);
		if (comment.getAuthor() == null || comment.getAuthor().getId() != pd.getPerson().getId()) 
			throw new MismatchIdentifierException("ID автора комментария и Ваш не совпадают. Похоже, что Вы не являетесь автором");			
		model.addAttribute("comment", comment);
		return "editComment";
	}
	
	@PatchMapping("/comment/edit")
	public String editComment(@ModelAttribute @Valid Comment comment, BindingResult br, Model model
			, @AuthenticationPrincipal PersonDetails pd) {
		if (comment.getAuthor().getId() != pd.getPerson().getId()) return "comments";
		if (br.hasErrors()) return "editComment";
		comment.setAuthor(pd.getPerson());	
		return showTask(commentService.save(comment).getTask().getId(), 0, model, pd);
	}
	
	@GetMapping("/comment/delete/{id}")
	public String deleteCommentPage(@PathVariable long id, Model model, @AuthenticationPrincipal PersonDetails pd) {
		Comment comment = commentService.findById(id);
		if (pd == null || comment.getAuthor().getId() != pd.getPerson().getId()) 
			throw new MismatchIdentifierException("ID автора комментария и Ваш не совпадают. Похоже, что Вы не являетесь автором.");
		model.addAttribute("comment", comment);
		return "confirm";
	}
	
	@DeleteMapping("/comment/delete/confirm/{id}")
	public String deleteComment(@PathVariable long id, Model model, @AuthenticationPrincipal PersonDetails pd) {
		Comment comment = commentService.findById(id);
		if (pd == null || comment.getAuthor().getId() != pd.getPerson().getId()) 
			throw new MismatchIdentifierException("ID автора комментария и Ваш не совпадают. Похоже, что Вы не являетесь автором.");
		commentService.deleteById(id);
		System.out.println("\n\n\n\ndeleting succesful");
		model.addAttribute("task", comment.getTask());
		return showTask(comment.getTask().getId(), 0, model, pd);
	}
	
	@DeleteMapping("/delete/{id}")
	public String delete(@PathVariable long id, @AuthenticationPrincipal PersonDetails pd) {
		if (taskService.findById(id).getAuthor().getId() != pd.getPerson().getId()) return "tasks";
		taskService.deleteById(id);
		return "redirect:/tasks/0";
	}
}
