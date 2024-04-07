package ru.terentyev.TaskManager.entities;

public class TaskRequest {
	
	private long[] id;
	private String[] title;
	private String[] description;
	private String[] status;
	private String[] priority;
	private String[] author;
	private long[] authorId;
	private String[] executor;
	private long[] executorId;
	private String[] orderBy;
	
	public TaskRequest() {}

	
	
	public TaskRequest(long[] id, String[] title, String[] description, String[] status, String[] priority, String[] author,
			long[] authorId, String[] executor, long[] executorId, String[] orderBy) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.status = status;
		this.priority = priority;
		this.author = author;
		this.authorId = authorId;
		this.executor = executor;
		this.executorId = executorId;
		this.orderBy = orderBy;
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

	public String[] getAuthor() {
		return author;
	}

	public void setAuthor(String[] author) {
		this.author = author;
	}

	public long[] getAuthorId() {
		return authorId;
	}

	public void setAuthorId(long[] authorId) {
		this.authorId = authorId;
	}

	public String[] getExecutor() {
		return executor;
	}

	public void setExecutor(String[] executor) {
		this.executor = executor;
	}

	public long[] getExecutorId() {
		return executorId;
	}

	public void setExecutorId(long[] executorId) {
		this.executorId = executorId;
	}

	public String[] getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String[] orderBy) {
		this.orderBy = orderBy;
	}
	
	
	
}
