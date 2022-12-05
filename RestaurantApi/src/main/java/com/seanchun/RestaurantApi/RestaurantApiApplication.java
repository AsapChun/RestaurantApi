package com.seanchun.RestaurantApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestaurantApiApplication {

	/*
		Spring boot can handle simultaneously requests, it can handle 200 simultaneous requests.
	*/
	public static void main(String[] args) {
		SpringApplication.run(RestaurantApiApplication.class, args);
	}

}
