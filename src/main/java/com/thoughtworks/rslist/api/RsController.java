package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RequestParamNotValid;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class RsController {
    private List<RsEvent> rsList = initRsEvent();
    private List<User> userList = initUserList();
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    UserRepository userRepository;

    private List<User> initUserList() {
        userList = new ArrayList<>();
        return userList;
    }

    private List<RsEvent> initRsEvent() {
        List<RsEvent> rsEventList = new ArrayList<>();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        rsEventList.add(new RsEvent("第一条事件", "无标签", 1));
        rsEventList.add(new RsEvent("第二条事件", "无标签", 1));
        rsEventList.add(new RsEvent("第三条事件", "无标签", 1));
        return rsEventList;
    }

    @GetMapping("/rs/{index}")
    @JsonView(RsEvent.UserInfo.class)
    ResponseEntity getOneRsEvent(@PathVariable int index) {
        if (index < 1 || index > rsList.size()) {
            throw new RequestParamNotValid("invalid index");
        }
        return ResponseEntity.ok(rsList.get(index - 1));
    }

    @GetMapping("/rs/list")
    @JsonView(RsEvent.UserInfo.class)
    ResponseEntity getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end) {
        if (start == null && end == null) {
            return ResponseEntity.ok(rsList);
        }
        if (start < 1 || start > rsList.size() || end < 1 || end > rsList.size()) {
            throw new RequestParamNotValid("invalid request param");
        }
        return ResponseEntity.ok(rsList.subList(start - 1, end));
    }

  @PostMapping("/rs/event")
  ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
      int userId = rsEvent.getUserId();
      RsEventPO rsEventPO = RsEventPO.builder().eventName(rsEvent.getEventName()).keyWord(rsEvent.getKeyWord()).userId(userId).build();
      UserPO newUserPO = userRepository.findUserNameById(userId);
      List<UserPO> usersPO = userRepository.findByUserName(newUserPO.getUserName());
      if (usersPO.size() > 1) {
          for (UserPO userPO : usersPO) {
              int userIdInRepository = userPO.getId();
              if (userIdInRepository != userId) {
                  userId = userIdInRepository;
              }
          }
      }
      rsEventPO.setUserId(userId);
      rsEventRepository.save(rsEventPO);
      int eventIndex = rsList.size() - 1;
      return ResponseEntity.created(null).header("index", Integer.toString(eventIndex)).build();
  }

    @PatchMapping("/rs/event/{index}")
    ResponseEntity modifyRsEvent(@RequestBody @Valid RsEvent rsEvent, @PathVariable int index) {
        if (index < 1 || index > rsList.size()) {
            throw new IllegalArgumentException();
        }
        String eventName = rsEvent.getEventName();
        String keyWord = rsEvent.getKeyWord();
        rsList.get(index - 1).setEventName(eventName);
        rsList.get(index - 1).setKeyWord(keyWord);
        return ResponseEntity.created(null).build();
    }

    @DeleteMapping("/rs/list/{index}")
    ResponseEntity deleteRsEvent(@PathVariable int index) {
        if (index < 1 || index > rsList.size()) {
            throw new IllegalArgumentException();
        }
        rsList.remove(index - 1);
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/user/list")
    ResponseEntity getUserList() {
        return ResponseEntity.ok(userList);
    }

    @ExceptionHandler({RequestParamNotValid.class, MethodArgumentNotValidException.class})
    public ResponseEntity rsExceptionHandler(Exception paramNotValidError) {
        String errorMessage;
        if (paramNotValidError instanceof MethodArgumentNotValidException) {
            errorMessage = "invalid param";
        } else {
            errorMessage = paramNotValidError.getMessage();
        }
        Error error = new Error();
        error.setError(errorMessage);
        logger.error(errorMessage);
        return ResponseEntity.badRequest().body(error);
    }
}
