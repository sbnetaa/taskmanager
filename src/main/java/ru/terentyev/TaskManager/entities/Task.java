package ru.terentyev.TaskManager.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Entity
@Table(name = "tasks")
public class Task extends AbstractEntity {
	
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
	//private transient int commentsCount;
	//private transient int commentsPages;
	@CreatedDate
	@Column(name = "created_at")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
	private LocalDateTime createdAt;
	@Column(name = "edited_at")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
	private LocalDateTime editedAt;
	@Column(name = "status_changed_at")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
	private LocalDateTime statusChangedAt;
	
	public Task(){}

	public enum Status{
		AWAITING("В ожидании"),
		PROCESSING("В процессе"),
		COMPLETED("Завершено");
		
		private String translation;
		
		public static Set<Status> getStatusesBySubstring(String[] parts){
			Set<Status> statuses = new HashSet<>();
			for (String part : parts) {
					statuses.add(getStatusBySubstring(part));
			}
			return statuses;
		}
		
		public static Status getStatusBySubstring(String part) {
			for (Status status : Status.values()) {
				if (status.name().toLowerCase().contains(part.toLowerCase())
						|| status.getTranslation().toLowerCase().contains(part.toLowerCase()))
					return status;
			}
			return null;
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
			for (String part : parts) 
				priorities.add(getPriorityBySubstring(part));		
			return priorities;
		}
		
		public static Priority getPriorityBySubstring(String part) {
			for (Priority priority : Priority.values()) {
				if (priority.name().toLowerCase().contains(part.toLowerCase())
						|| priority.getTranslation().toLowerCase().contains(part.toLowerCase()))
					return priority;
			}
			return null;
		}

		private Priority(String translation) {
			this.translation = translation;
		}

		public String getTranslation() {
			return translation;
		}			
	}
}
