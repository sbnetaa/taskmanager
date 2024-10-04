package ru.terentyev.TaskManager.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.terentyev.TaskManager.services.CommentService;

@RestController
@RequestMapping(value = "/api/v1/comments", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
, headers = "Accept=application/json")
public class CommentRestController {
	
	private CommentService commentService;

	public CommentRestController(CommentService commentService) {
		super();
		this.commentService = commentService;
	}
	
}
