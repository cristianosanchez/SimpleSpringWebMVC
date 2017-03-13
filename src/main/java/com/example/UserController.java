package com.example;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping(path = "/{id}")
    public User getUser(@PathVariable("id") final int id) {
        return new User(id, "Cristiano");
    }
}
