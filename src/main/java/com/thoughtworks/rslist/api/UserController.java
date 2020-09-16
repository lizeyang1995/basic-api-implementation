package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    List<User> userList = initUserList();

    private List<User> initUserList() {
        userList = new ArrayList<>();
        return userList;
    }

    @PostMapping("/user")
    public void addUser(@RequestBody @Valid User user) {
        userList.add(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userList;
    }
}
