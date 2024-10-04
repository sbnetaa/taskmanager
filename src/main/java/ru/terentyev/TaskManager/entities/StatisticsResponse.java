package ru.terentyev.TaskManager.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsResponse {

	private Statistics commonStatistics;
	private Statistics authorStatistics;
	private Statistics executorStatistics;
	
	public StatisticsResponse(){}
}