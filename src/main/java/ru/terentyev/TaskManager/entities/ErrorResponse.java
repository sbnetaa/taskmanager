package ru.terentyev.TaskManager.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

	@JsonProperty(value = "ошибка", index = 1)
	private String error;
	@JsonProperty(value = "время", index = 2)
	@JsonFormat(pattern="dd-MM-yyyy HH:mm")
	private LocalDateTime timestamp = LocalDateTime.now();
	@JsonProperty(value = "статус", index = 3)
	private String status;
	@JsonProperty(value = "код", index = 4)
	private int statusCode;

}
