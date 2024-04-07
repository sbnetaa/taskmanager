package ru.terentyev.TaskManager.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator;

@Entity
@Table(name = "tasks")
@JsonIdentityInfo(
		  generator = PropertyGenerator.class, 
		  property = "id")
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "Заголовок не может быть пустым")
	private String title;
	@NotBlank(message = "Описание не может быть пустым")
	private String description;
	@NotNull(message = "У задачи должен быть статус")
	@Enumerated(EnumType.STRING)
	private Status status;
	@NotNull(message = "У задачи должен быть приоритет")
	@Enumerated(EnumType.STRING)
	private Priority priority;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "author_id", referencedColumnName = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private Person author;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "executor_id", referencedColumnName = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private Person executor;
	@NotNull(message = "У задачи должен быть исполнитель")
	@JsonBackReference
	private transient long executorId; // Это поле нужно для web контроллера приложения.
	//При использовании поля 'executor' напрямую возникают ошибки, так как шаблонизатор Thymeleaf не умеет обрабатывать что-то там (забыл :) )
	@OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Comment> comments;
	@CreatedDate
	@Column(name = "created_at")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern="dd-MM-yyyy HH:mm")
	private LocalDateTime createdAt;
	@Column(name = "edited_at")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern="dd-MM-yyyy HH:mm")
	private LocalDateTime editedAt;
	
	public Task(){}
	

	public long getExecutorId() {
		return executorId;
	}

	public void setExecutorId(long executorId) {
		this.executorId = executorId;
	}


	public LocalDateTime getEditedAt() {
		return editedAt;
	}


	public void setEditedAt(LocalDateTime editedAt) {
		this.editedAt = editedAt;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public Person getAuthor() {
		return author;
	}

	public void setAuthor(Person author) {
		this.author = author;
	}

	public Person getExecutor() {
		return executor;
	}

	public void setExecutor(Person executor) {
		this.executor = executor;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}


	@Override
	public String toString() {
		return "Task [id=" + id + ", title=" + title + ", description=" + description + ", status=" + status
				+ ", priority=" + priority + ", createdAt=" + createdAt + ", editedAt=" + editedAt + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(author, comments, createdAt, description, editedAt, executor, id, priority, status, title);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		return Objects.equals(author, other.author) && Objects.equals(comments, other.comments)
				&& Objects.equals(createdAt, other.createdAt) && Objects.equals(description, other.description)
				&& Objects.equals(editedAt, other.editedAt) && Objects.equals(executor, other.executor)
				&& Objects.equals(id, other.id) && priority == other.priority && status == other.status
				&& Objects.equals(title, other.title);
	}




	public enum Status{
		AWAITING("В ожидании"),
		PROCESSING("В процессе"),
		COMPLETED("Завершено");
		
		private String translation;
		
		public static Set<Status> getStatusesBySubstring(String[] parts){
			Set<Status> statuses = new HashSet<>();
			for (String part : parts) {
			for (Status status : Status.values())
				if (status.name().toLowerCase().contains(part.toLowerCase())
						|| status.getTranslation().toLowerCase().contains(part.toLowerCase()))
					statuses.add(status);
			}
				return statuses;
		}
		
		private Status(String translation) {
			this.translation = translation;
		}

		public String getTranslation() {
			return translation;
		}	
		
		
		
	}
	
	public enum Priority{
		LOW("Низкий"),
		MEDIUM("Средний"),
		HIGH("Высокий");
		
		private String translation;
		
		public static Set<Priority> getPrioritiesBySubstring(String[] parts){
			Set<Priority> priorities = new HashSet<>();
			for (String part : parts) {
			for (Priority priority : Priority.values())
				if (priority.name().toLowerCase().contains(part.toLowerCase())
						|| priority.getTranslation().toLowerCase().contains(part.toLowerCase()))
					priorities.add(priority);
			}
				return priorities;
		}

		private Priority(String translation) {
			this.translation = translation;
		}

		public String getTranslation() {
			return translation;
		}	
		
	}

}
