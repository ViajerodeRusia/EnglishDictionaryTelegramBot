package com.dailyenglish.EnglishDictionaryINTD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class EnglishDictionaryIntdApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnglishDictionaryIntdApplication.class, args);
	}

}
