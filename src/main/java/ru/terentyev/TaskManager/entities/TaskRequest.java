package ru.terentyev.TaskManager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
public class TaskRequest {
	
	private Long[] id;
	private String[] title;
	private String[] description;
	private String[] status;
	private String[] priority;
	private Long[] author;
	private String[] authorName;
	private Long[] executor;
	private String[] executorName;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private String orderBy;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private String createdBefore;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private String createdAfter;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private String editedBefore;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private String editedAfter;
	@JsonFormat(without = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private Integer page;
	private Task taskBeforeChanges;
	
	public TaskRequest(){}
}
