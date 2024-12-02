package com.jaruiz.casarrubios.candidates.services.applications;

import org.springframework.boot.SpringApplication;

public class TestApplicationsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(ApplicationsServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
