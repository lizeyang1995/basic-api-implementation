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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class RsController {
    private List<RsEvent> rsEvents = initRsEvent();
    private List<User> users = initUserList();
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    UserRepository userRepository;

    private List<User> initUserList() {
        users = new ArrayList<>();
        return users;
    }

    private List<RsEvent> initRsEvent() {
        rsEvents = new ArrayList<>();
        return rsEvents;
    }

    @GetMapping("/rs/{id}")
    @JsonView(RsEvent.UserInfo.class)
    ResponseEntity getOneRsEvent(@PathVariable int id) {
        Optional<RsEventPO> foundRsEvent = rsEventRepository.findById(id);
        if (foundRsEvent.isPresent()) {
            RsEventPO rsEventPO = foundRsEvent.get();
            RsEvent rsEvent = new RsEvent();
            rsEvent.setEventName(rsEventPO.getEventName());
            rsEvent.setKeyWord(rsEventPO.getKeyWord());
            return ResponseEntity.ok(rsEvent);
        }
        throw new RequestParamNotValid("invalid index");
    }

    @GetMapping("/rs/list")
    @JsonView(RsEvent.UserInfo.class)
    ResponseEntity getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end) {
        List<RsEventPO> allRsEvents = rsEventRepository.findAll();
        if (start == null && end == null && allRsEvents.size() > 0) {
            for (RsEventPO rsEventPO : allRsEvents) {
                rsEvents.add(new RsEvent(rsEventPO.getEventName(), rsEventPO.getKeyWord(), rsEventPO.getUserId()));
            }
            return ResponseEntity.ok(rsEvents);
        } else {
            if (start < 1 || end < 1 || start > allRsEvents.size() || end > allRsEvents.size() || start > end) {
                throw new RequestParamNotValid("invalid request param");
            }
            for (int i = start - 1; i < end; i++) {
                rsEvents.add(new RsEvent(allRsEvents.get(i).getEventName(), allRsEvents.get(i).getKeyWord(), allRsEvents.get(i).getUserId()));
            }
        }
        return ResponseEntity.ok(rsEvents);
    }

  @PostMapping("/rs/event")
  ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
      int userId = rsEvent.getUserId();
      if (!userRepository.findById(userId).isPresent()) {
          return ResponseEntity.badRequest().build();
      }
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
      int eventIndex = rsEvents.size() - 1;
      return ResponseEntity.created(null).header("index", Integer.toString(eventIndex)).build();
  }

    @PatchMapping("/rs/event/{index}")
    ResponseEntity modifyRsEvent(@RequestBody @Valid RsEvent rsEvent, @PathVariable int index) {
        if (index < 1 || index > rsEvents.size()) {
            throw new IllegalArgumentException();
        }
        String eventName = rsEvent.getEventName();
        String keyWord = rsEvent.getKeyWord();
        rsEvents.get(index - 1).setEventName(eventName);
        rsEvents.get(index - 1).setKeyWord(keyWord);
        return ResponseEntity.created(null).build();
    }

    @DeleteMapping("/rs/list/{index}")
    ResponseEntity deleteRsEvent(@PathVariable int index) {
        if (index < 1 || index > rsEvents.size()) {
            throw new IllegalArgumentException();
        }
        rsEvents.remove(index - 1);
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/user/list")
    ResponseEntity getUserList() {
        return ResponseEntity.ok(users);
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
