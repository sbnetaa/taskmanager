package ru.terentyev.TaskManager.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "comments")
@JsonIdentityInfo(
		  generator = PropertyGenerator.class, 
		  property = "id")
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "Комментарий не может быть пустым")
	private String body;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "task_id", referencedColumnName = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private Task task;
	private transient long taskId;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "author_id", referencedColumnName = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private Person author;
	@CreatedDate
	private LocalDateTime createdAt;
	private LocalDateTime editedAt;
	
	public Comment(){}
	
	

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}



	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}



	public LocalDateTime getEditedAt() {
		return editedAt;
	}



	public void setEditedAt(LocalDateTime editedAt) {
		this.editedAt = editedAt;
	}



	public long getTaskId() {
		return taskId;
	}



	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Person getAuthor() {
		return author;
	}

	public void setAuthor(Person author) {
		this.author = author;
	}



	@Override
	public int hashCode() {
		return Objects.hash(author, body, createdAt, editedAt, id, task);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		return Objects.equals(author, other.author) && Objects.equals(body, other.body)
				&& Objects.equals(createdAt, other.createdAt) && Objects.equals(editedAt, other.editedAt)
				&& Objects.equals(id, other.id) && Objects.equals(task, other.task);
	}



	@Override
	public String toString() {
		return "Comment [id=" + id + ", body=" + body + ", task=" + task + ", author=" + author + ", createdAt="
				+ createdAt + ", editedAt=" + editedAt + "]";
	}
	
	
	
}
