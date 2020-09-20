package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.service.RsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.thoughtworks.rslist.exception.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    Logger logger = LoggerFactory.getLogger(getClass());
    private final RsService rsService;

    public UserController(RsService rsService) {
        this.rsService = rsService;
    }

    @PostMapping("/user")
    ResponseEntity addUser(@RequestBody @Valid User user) {
        boolean isSuccess = rsService.addUser(user);
        int userRepositorySize = rsService.getUserRepositorySize();
        if (!isSuccess) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.created(null).header("index", Integer.toString(userRepositorySize - 1)).build();
    }

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok(rsService.getAllUsers());
    }

    @GetMapping("/user/{index}")
    public ResponseEntity getUserById(@PathVariable int index) {
        UserPO userPO = rsService.getUserById(index);
        return ResponseEntity.ok(userPO);
    }

    @DeleteMapping("/user/{index}")
    public ResponseEntity deleteUserById(@PathVariable int index) {
        rsService.deleteUserById(index);
        return ResponseEntity.ok(null);
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
