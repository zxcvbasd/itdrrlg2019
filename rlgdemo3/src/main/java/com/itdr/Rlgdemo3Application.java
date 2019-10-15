package com.itdr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itdr.mappers")
public class Rlgdemo3Application {

    public static void main(String[] args) {
        SpringApplication.run(Rlgdemo3Application.class, args);
    }

}
