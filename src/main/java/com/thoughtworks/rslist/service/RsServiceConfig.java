package com.thoughtworks.rslist.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RsServiceConfig {
    @Bean
    public RsService rsService() {
        return new RsService();
    }
}
