package com.luxf.cloud.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringCloudApplication
public class CustomEurekaApplication {

    public static void main(String[] args) {

        SpringApplication.run(CustomEurekaApplication.class, args);
    }

}
