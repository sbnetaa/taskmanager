package ru.terentyev.TaskManager.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskResponse {
	
	private long id;
	private String title;
	private String description;
	private String status;
	private String priority;
	private long authorId;
	private String authorName;
	private long executorId;
	private String executorName;
	private List<Comment> comments;
	private int commentsCount;
	private int commentsPages;
	@JsonFormat(pattern="dd-MM-yyyy HH:mm")
	private LocalDateTime createdAt;
	@JsonFormat(pattern="dd-MM-yyyy HH:mm")
	private LocalDateTime editedAt;
}
