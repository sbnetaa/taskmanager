package ru.terentyev.TaskManager.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "people")
@JsonIdentityInfo(
		  generator = PropertyGenerator.class, 
		  property = "id")
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	private String name;
	@Email
	private String email;
	@Size(min = 5, message = "Пароль должен содержать минимум 5 символов")
	@JsonIgnore
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

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getPasswordConfirm() {
		return passwordConfirm;
	}


	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}


	public List<Task> getCreatedTasks() {
		return createdTasks;
	}


	public void setCreatedTasks(List<Task> createdTasks) {
		this.createdTasks = createdTasks;
	}


	public List<Task> getExecutableTasks() {
		return executableTasks;
	}


	public void setExecutableTasks(List<Task> executableTasks) {
		this.executableTasks = executableTasks;
	}


	public List<Comment> getComments() {
		return comments;
	}


	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdTasks, email, executableTasks, id, name, password, registrationDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		return Objects.equals(createdTasks, other.createdTasks) && Objects.equals(email, other.email)
				&& Objects.equals(executableTasks, other.executableTasks) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name) && Objects.equals(password, other.password)
				&& Objects.equals(registrationDate, other.registrationDate);
	}


	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", createdTasks="
				+ createdTasks + ", executableTasks=" + executableTasks + ", registrationDate=" + registrationDate
				+ ", comments=" + comments + "]";
	}	
}

