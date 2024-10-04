package ru.terentyev.TaskManager.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIdentityReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comments")
public class Comment extends AbstractEntity {
	
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
}
