package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.annotation.JsonView;
import com.thoughtworks.rslist.domain.RsEvent;
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
import java.util.Optional;

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
        List<UserPO> allUsers = userRepository.findAll();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/users/{index}")
    public ResponseEntity getUserById(@PathVariable int index) {
        Optional<UserPO> foundUser = userRepository.findById(index);
        if (foundUser.isPresent()) {
            UserPO userPO = foundUser.get();
            return ResponseEntity.ok(userPO);
        }
        throw new IllegalArgumentException();
    }

    @DeleteMapping("/users/{index}")
    public ResponseEntity deleteUserById(@PathVariable int index) {
        Optional<UserPO> foundUser = userRepository.findById(index);
        if (foundUser.isPresent()) {
            userRepository.deleteById(index);
            return ResponseEntity.ok(null);
        }
        throw new IllegalArgumentException();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResponseEntity rsExceptionHandler(Exception paramNotValidError) {
        String errorMessage;
        if (paramNotValidError instanceof MethodArgumentNotValidException) {
            errorMessage = "invalid user";
        } else {
            errorMessage = "invalid id";
        }
        Error error = new Error();
        error.setError(errorMessage);
        logger.error(errorMessage);
        return ResponseEntity.badRequest().body(error);

    }
}
