package ru.terentyev.TaskManager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import ru.terentyev.TaskManager.entities.StatisticsResponse;

@RestController
@RequestMapping(value = "/api/v1/statistics", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
, headers = "Accept=application/json")
public class StatisticsController {

	private WebClient statisticsWebClient;
	
	@Autowired
	public StatisticsController(WebClient statisticsWebClient) {
		super();
		this.statisticsWebClient = statisticsWebClient;
	}

	@GetMapping
	public Mono<StatisticsResponse> getStatistics() {
		return statisticsWebClient.get().uri("/").retrieve().bodyToMono(StatisticsResponse.class);
	}
	
	@GetMapping("/{id}")
	public Mono<StatisticsResponse> getStatistics(@PathVariable Long id) {
		return statisticsWebClient.get().uri("/" + id).retrieve().bodyToMono(StatisticsResponse.class);
	}
}
