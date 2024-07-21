package ru.terentyev.TaskManager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ru.terentyev.TaskManager.util.StringToLongDeserializer;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
public class TaskRequest {
	
	//@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	//@JsonDeserialize(using = StringToLongDeserializer.class)
	private Long[] id;
	private String[] title;
	private String[] description;
	private String[] status;
	private String[] priority;
	//@JsonDeserialize(using = StringToLongDeserializer.class)
	private Long[] author;
	private String[] authorName;
	//@JsonDeserialize(using = StringToLongDeserializer.class)
	private Long[] executor;
	private String[] executorName;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private String orderBy;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private String createdBefore;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private String createdAfter;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private String editedBefore;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private String editedAfter;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private Integer page;
	
	public TaskRequest(){}

	public Long[] getId() {
		return id;
	}

	public void setId(Long[] id) {
		this.id = id;
	}

	public String[] getTitle() {
		return title;
	}

	public void setTitle(String[] title) {
		this.title = title;
	}

	public String[] getDescription() {
		return description;
	}

	public void setDescription(String[] description) {
		this.description = description;
	}

	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = status;
	}

	public String[] getPriority() {
		return priority;
	}

	public void setPriority(String[] priority) {
		this.priority = priority;
	}

	public Long[] getAuthor() {
		return author;
	}

	public void setAuthor(Long[] author) {
		this.author = author;
	}

	public String[] getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String[] authorName) {
		this.authorName = authorName;
	}

	public Long[] getExecutor() {
		return executor;
	}

	public void setExecutor(Long[] executor) {
		this.executor = executor;
	}

	public String[] getExecutorName() {
		return executorName;
	}

	public void setExecutorName(String[] executorName) {
		this.executorName = executorName;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getCreatedBefore() {
		return createdBefore;
	}

	public void setCreatedBefore(String createdBefore) {
		this.createdBefore = createdBefore;
	}

	public String getCreatedAfter() {
		return createdAfter;
	}

	public void setCreatedAfter(String createdAfter) {
		this.createdAfter = createdAfter;
	}

	public String getEditedBefore() {
		return editedBefore;
	}

	public void setEditedBefore(String editedBefore) {
		this.editedBefore = editedBefore;
	}

	public String getEditedAfter() {
		return editedAfter;
	}

	public void setEditedAfter(String editedAfter) {
		this.editedAfter = editedAfter;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
}
