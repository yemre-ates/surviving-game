package com.yea.com.survive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.yea.com.survive.engine.SurviveEngine;

@SpringBootApplication
public class SurviveApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SurviveApplication.class, args);		 
		SurviveEngine engine = context.getBean(SurviveEngine.class);
		engine.fight();
	}
}
