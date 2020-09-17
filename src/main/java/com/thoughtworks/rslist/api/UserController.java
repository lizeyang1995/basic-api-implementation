package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity addUser(@RequestBody @Valid User user) {
        userList.add(user);
        int userIndex = userList.size() - 1;
        return ResponseEntity.created(null).header("index", Integer.toString(userIndex)).build();
    }

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok(userList);
    }
}
