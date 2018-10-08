package com.dachen.springboot2catchain.controller;

import com.dachen.springboot2catchain.service.RemoteCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/catChain")
public class CatChainController {

    @Autowired
    private RemoteCallService remoteCallService;

    @RequestMapping("/methodA")
    public String methodA(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("methodA");

        remoteCallService.methodB();

        return "methodA";
    }

    @RequestMapping("/methodB")
    public String methodB(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("methodB");

        remoteCallService.methodC();

        return "methodB";
    }

    @RequestMapping("/methodC")
    public String methodC() {
        System.out.println("methodC");

        return "methodC";
    }


}
