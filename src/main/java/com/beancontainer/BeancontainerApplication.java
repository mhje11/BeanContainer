package com.beancontainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy //AOP
public class BeancontainerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeancontainerApplication.class, args);
    }

}
