package ru.terentyev.TaskManager.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "people")
public class Person extends AbstractEntity {

	@NotBlank
	private String name;
	@Email
	private String email;
	@Size(min = 5, message = "Пароль должен содержать минимум 5 символов")
	private String password;
	@JsonBackReference
	private transient String passwordConfirm;
	@OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Task> createdTasks;
	@OneToMany(mappedBy = "executor", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Task> executableTasks;
	@CreatedDate
	@Column(name = "registration_date")
	private LocalDateTime registrationDate;
	@OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Comment> comments;

	public Person(){}

	public Person(@NotBlank String name, @Email String email,
			@Size(min = 5, message = "Пароль должен содержать минимум 5 символов") String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
	}
}

