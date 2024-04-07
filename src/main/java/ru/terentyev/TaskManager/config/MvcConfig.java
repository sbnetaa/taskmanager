package ru.terentyev.TaskManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
   @Override
   public void addViewControllers(ViewControllerRegistry registry) {
      registry.addViewController("/").setViewName("index");
      registry.addViewController("").setViewName("index");
   }
   

   @Bean
   HiddenHttpMethodFilter hiddenHttpMethodFilter() {
       return new HiddenHttpMethodFilter();
   }
   
   @Bean
   public ObjectMapper objectMapper() {
       return JsonMapper.builder()
           .addModule(new JavaTimeModule())
           .addModule(new Hibernate6Module())
           .addModule(new SimpleModule())
           .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
           .configure(SerializationFeature.INDENT_OUTPUT, true)
           //.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
           .build();
   }
   
   /*
   @Bean(name="entityManagerFactory")
   public LocalSessionFactoryBean sessionFactory() {
       LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

       return sessionFactory;
   } 
  */
   @Bean
   public Module datatypeHibernateModule() {
     return new Hibernate6Module();
   }
  
}