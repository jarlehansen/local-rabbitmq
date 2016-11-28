package com.github.jarlehansen.localrabbit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class Application {

    @Autowired
    private LocalRabbit localRabbit;

    @PostConstruct
    public void init() {
        localRabbit.start();
    }

    @PreDestroy
    public void shutdown() {
        localRabbit.stop();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
