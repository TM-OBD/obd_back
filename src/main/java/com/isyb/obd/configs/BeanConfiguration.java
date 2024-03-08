package com.isyb.obd.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class BeanConfiguration {

    @Value("${http.client.connection.timeout:10}")
    private int httpClientConnectionTimeout;

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(httpClientConnectionTimeout))
                .version(HttpClient.Version.HTTP_2)
                .build();
    }


}