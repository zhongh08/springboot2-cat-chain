package com.dachen.springboot2catchain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteCallService {

    @Autowired
    private RestTemplate restTemplate;

    public String methodB() {
        return restTemplate.postForObject("http://SPRINGBOOT2-CAT-CHAIN/catChain/methodB",null,String.class);
    }

    public String methodC() {
        return restTemplate.postForObject("http://SPRINGBOOT2-CAT-CHAIN/catChain/methodC",null,String.class);
    }

}
