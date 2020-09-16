package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RequestParamNotValid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    List<User> userList = initUserList();
    Logger logger = LoggerFactory.getLogger(getClass());

    private List<User> initUserList() {
        userList = new ArrayList<>();
        return userList;
    }

    @PostMapping("/user")
    public void addUser(@RequestBody @Valid User user) {
        userList.add(user);
    }

    @GetMapping("/user")
    public List<User> getUserList() {
        return userList;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity rsExceptionHandler(Exception paramNotValidError) {
        String errorMessage;
        errorMessage = "invalid user";
        Error error = new Error();
        error.setError(errorMessage);
        logger.error(errorMessage);
        return ResponseEntity.badRequest().body(error);
    }
}
