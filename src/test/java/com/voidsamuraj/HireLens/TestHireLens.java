package com.voidsamuraj.HireLens;

import org.springframework.boot.SpringApplication;

public class TestHireLens {

	public static void main(String[] args) {
		SpringApplication.from(HireLens::main).with(TestcontainersConfiguration.class).run(args);
	}

}
