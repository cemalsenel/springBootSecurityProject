package com.springJWT.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public String allAccess(){
        return "Herkese açık içreik";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_MODERATOR')")
    public String userAccess(){
        return "Yalnızca kayıtlı kişilere açık içreik";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR')")
    public String modAccess(){
        return "Yalnızca moderatör'e açık içreik";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String adminAccess(){
        return "Yalnızca admin'e açık içreik";
    }
}
