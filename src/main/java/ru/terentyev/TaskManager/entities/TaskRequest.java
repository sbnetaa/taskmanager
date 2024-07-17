package ru.terentyev.TaskManager.entities;

import java.util.Arrays;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ru.terentyev.TaskManager.util.StringToIntegerDeserializer;


public class TaskRequest {
	
	//@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@JsonDeserialize(using = StringToIntegerDeserializer.class)
	private long[] id;
	private String[] title;
	private String[] description;
	private String[] status;
	private String[] priority;
	private long[] author;
	private String[] authorName;
	private long[] executor;
	private String[] executorName;
	private String[] orderBy;
	private String[] createdBefore;
	private String[] createdAfter;
	private String[] editedBefore;
	private String[] editedAfter;
	private int[] page;
	
	public TaskRequest(){}


	public TaskRequest(long[] id, String[] title, String[] description, String[] status, String[] priority,
			long[] author, String[] authorName, long[] executor, String[] executorName, String[] orderBy,
			String[] createdBefore, String[] createdAfter, String[] editedBefore, String[] editedAfter, int[] page) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.status = status;
		this.priority = priority;
		this.author = author;
		this.authorName = authorName;
		this.executor = executor;
		this.executorName = executorName;
		this.orderBy = orderBy;
		this.createdBefore = createdBefore;
		this.createdAfter = createdAfter;
		this.editedBefore = editedBefore;
		this.editedAfter = editedAfter;
		this.page = page;
	}


	public long[] getId() {
		return id;
	}

	public void setId(long[] id) {
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

	
	
	public long[] getAuthor() {
		return author;
	}



	public void setAuthor(long[] author) {
		this.author = author;
	}



	public String[] getAuthorName() {
		return authorName;
	}



	public void setAuthorName(String[] authorName) {
		this.authorName = authorName;
	}



	public long[] getExecutor() {
		return executor;
	}



	public void setExecutor(long[] executor) {
		this.executor = executor;
	}



	public String[] getExecutorName() {
		return executorName;
	}



	public void setExecutorName(String[] executorName) {
		this.executorName = executorName;
	}



	public String[] getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String[] orderBy) {
		this.orderBy = orderBy;
	}


	public String[] getCreatedBefore() {
		return createdBefore;
	}


	public void setCreatedBefore(String[] createdBefore) {
		this.createdBefore = createdBefore;
	}


	public String[] getCreatedAfter() {
		return createdAfter;
	}


	public void setCreatedAfter(String[] createdAfter) {
		this.createdAfter = createdAfter;
	}


	public String[] getEditedBefore() {
		return editedBefore;
	}


	public void setEditedBefore(String[] editedBefore) {
		this.editedBefore = editedBefore;
	}


	public String[] getEditedAfter() {
		return editedAfter;
	}


	public void setEditedAfter(String[] editedAfter) {
		this.editedAfter = editedAfter;
	}


	public int[] getPage() {
		return page;
	}


	public void setPage(int[] page) {
		this.page = page;
	}


	@Override
	public String toString() {
		return "TaskRequest [id=" + Arrays.toString(id) + ", title=" + Arrays.toString(title) + ", description="
				+ Arrays.toString(description) + ", status=" + Arrays.toString(status) + ", priority="
				+ Arrays.toString(priority) + ", author=" + Arrays.toString(author) + ", authorName="
				+ Arrays.toString(authorName) + ", executor=" + Arrays.toString(executor) + ", executorName="
				+ Arrays.toString(executorName) + ", orderBy=" + Arrays.toString(orderBy) + ", createdBefore="
				+ Arrays.toString(createdBefore) + ", createdAfter=" + Arrays.toString(createdAfter) + ", editedBefore="
				+ Arrays.toString(editedBefore) + ", editedAfter=" + Arrays.toString(editedAfter) + ", page="
				+ Arrays.toString(page) + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(author);
		result = prime * result + Arrays.hashCode(authorName);
		result = prime * result + Arrays.hashCode(createdAfter);
		result = prime * result + Arrays.hashCode(createdBefore);
		result = prime * result + Arrays.hashCode(description);
		result = prime * result + Arrays.hashCode(editedAfter);
		result = prime * result + Arrays.hashCode(editedBefore);
		result = prime * result + Arrays.hashCode(executor);
		result = prime * result + Arrays.hashCode(executorName);
		result = prime * result + Arrays.hashCode(id);
		result = prime * result + Arrays.hashCode(orderBy);
		result = prime * result + Arrays.hashCode(page);
		result = prime * result + Arrays.hashCode(priority);
		result = prime * result + Arrays.hashCode(status);
		result = prime * result + Arrays.hashCode(title);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskRequest other = (TaskRequest) obj;
		return Arrays.equals(author, other.author) && Arrays.equals(authorName, other.authorName)
				&& Arrays.equals(createdAfter, other.createdAfter) && Arrays.equals(createdBefore, other.createdBefore)
				&& Arrays.equals(description, other.description) && Arrays.equals(editedAfter, other.editedAfter)
				&& Arrays.equals(editedBefore, other.editedBefore) && Arrays.equals(executor, other.executor)
				&& Arrays.equals(executorName, other.executorName) && Arrays.equals(id, other.id)
				&& Arrays.equals(orderBy, other.orderBy) && Arrays.equals(page, other.page)
				&& Arrays.equals(priority, other.priority) && Arrays.equals(status, other.status)
				&& Arrays.equals(title, other.title);
	}
	
}
