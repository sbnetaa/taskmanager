package ru.terentyev.TaskManager.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompositeKey implements Serializable {

	private long personId;
	private Boolean author;
	
	public CompositeKey() {}
}
