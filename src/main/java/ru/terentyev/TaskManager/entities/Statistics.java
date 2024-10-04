package ru.terentyev.TaskManager.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Statistics {
	 
	private int total;
	private int urgent;
	private int awaiting;
	private int processing;
	private int completed;
	private int open;
	private int duringDay;
	private int duringWeek;
	private int duringMonth;
	private int completedDuringDay;
	private int completedDuringWeek;
	private int completedDuringMonth;
}
