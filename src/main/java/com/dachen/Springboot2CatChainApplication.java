package com.dachen;

import com.dachen.interceptor.RestUserAgentInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Collections;

@EnableDiscoveryClient
@SpringBootApplication
public class Springboot2CatChainApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot2CatChainApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(15);
        httpRequestFactory.setConnectTimeout(15);
        httpRequestFactory.setReadTimeout(15);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        restTemplate.setInterceptors(Collections.singletonList(new RestUserAgentInterceptor()));
        return restTemplate;
    }
}
