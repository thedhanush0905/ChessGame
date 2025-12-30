package com.example.IndiChessBackend.controller;

import com.example.IndiChessBackend.security.JwtTokenUtil;
import com.example.IndiChessBackend.service.UserDetailService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;  // AuthenticationManager to authenticate user

    @Autowired
    private UserDetailService userDetailService;  // Custom user details service to load user data

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/hello")
    public String show(){
        return "Hello";
    }

    @GetMapping("/world")
    public String show2(){
        return "World";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password,
                              HttpServletResponse response){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        String jwtToken = jwtTokenUtil.generateToken(authentication);

        response.setHeader("Authorization", "Bearer " + jwtToken);  // Optionally, set token in header
        System.out.println(jwtToken);
        return "Bearer " + jwtToken;

    }


}
