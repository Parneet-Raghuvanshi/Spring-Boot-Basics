package com.developer.mobileappws.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    // Test Mapping
    @GetMapping(path = "/")
    public String testMapping() {
        return "Welcome To My Server TEST !";
    }

    // Test Mapping
    @GetMapping(path = "/customer")
    public String testMappingCustomer() {
        return "Welcome To My Server USER !";
    }

    @GetMapping(path = "/admin")
    public String testMappingAdmin() {
        return "Welcome To My Server ADMIN !";
    }
}
