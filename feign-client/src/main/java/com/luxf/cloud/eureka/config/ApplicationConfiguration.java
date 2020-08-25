package com.luxf.cloud.eureka.config;

import com.luxf.cloud.eureka.feign.client.helper.ReflectHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 小66
 * @date 2020-08-24 17:56
 **/
@Configuration
public class ApplicationConfiguration {

    @Bean
    public ReflectHelper reflectHelper() {
        return new ReflectHelper();
    }
}
