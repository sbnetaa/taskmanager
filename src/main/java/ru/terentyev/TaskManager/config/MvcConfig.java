package ru.terentyev.TaskManager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ru.terentyev.TaskManager.services.CommentService;
import ru.terentyev.TaskManager.services.PersonDetailsService;

//@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	
	@Value("${microservices.statistics}")
	private String statisticsUri;
	
	private PersonDetailsService personDetailsService;
	private CommentService commentService;
	
	@Autowired
	public MvcConfig(PersonDetailsService personDetailsService, CommentService commentService) {
		super();
		this.personDetailsService = personDetailsService;
		this.commentService = commentService;
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
		registry.addViewController("").setViewName("index");
	}
   
	@Bean
	HiddenHttpMethodFilter hiddenHttpMethodFilter() {
       return new HiddenHttpMethodFilter();
	}
   
	@Primary
	@Bean
	public ObjectMapper myObjectMapper() {
		final ObjectMapper objectMapper =  JsonMapper.builder()
			.addModule(new JavaTimeModule())
           .addModule(new Hibernate6Module())
           .addModule(new SimpleModule().addDeserializer(Long.class, new NumberDeserializers.LongDeserializer(Long.class, null)))
    	   .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
           .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
           .configure(SerializationFeature.INDENT_OUTPUT, true)
           .build(); 
		return objectMapper;
	}
   
	@Bean
	public Hibernate6Module datatypeHibernateModule() {
		return new Hibernate6Module();
	}
   
	@Bean
	public WebClient statisticsWebClient() {
		return WebClient.builder().baseUrl(statisticsUri).build();
	}
}