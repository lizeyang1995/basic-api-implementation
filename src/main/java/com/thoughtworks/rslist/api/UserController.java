package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
    @Autowired
    UserRepository userRepository;


    private List<User> initUserList() {
        userList = new ArrayList<>();
        return userList;
    }

    @PostMapping("/user")
    ResponseEntity addUser(@RequestBody @Valid User user) {
        UserPO userPO = new UserPO();
        userPO.setUserName(user.getUserName());
        userPO.setAge(user.getAge());
        userPO.setGender(user.getGender());
        userPO.setEmail(user.getEmail());
        userPO.setPhone(user.getPhone());
        userPO.setVoteNumber(user.getVoteNumber());
        userRepository.save(userPO);
        int userIndex = userRepository.findAll().size() - 1;
        return ResponseEntity.created(null).header("index", Integer.toString(userIndex)).build();
    }

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok(userList);
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
