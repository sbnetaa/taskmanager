package ru.terentyev.TaskManager.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.terentyev.TaskManager.services.CommentService;

@RestController
@RequestMapping("/rest/comments")
public class CommentRestController {
	
	private CommentService commentService;

	public CommentRestController(CommentService commentService) {
		super();
		this.commentService = commentService;
	}
	
	/*
	//@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE,
	public ResponseEntity<String> showComments
	}
	*/
	
}
