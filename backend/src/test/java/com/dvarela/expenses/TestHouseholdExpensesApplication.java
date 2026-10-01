package com.dvarela.expenses;

import org.springframework.boot.SpringApplication;

public class TestHouseholdExpensesApplication {

	public static void main(String[] args) {
		SpringApplication.from(HouseholdExpensesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
