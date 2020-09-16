package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class RsController {
  private List<RsEvent> rsList = initRsEvent();
  private List<User> userList = initUserList();

    private List<User> initUserList() {
        userList = new ArrayList<>();
        return userList;
    }

    private List<RsEvent> initRsEvent() {
    List<RsEvent> rsEventList = new ArrayList<>();
    User user = new User("lize", "male", 18, "a@b.com", "10000000000");
    rsEventList.add(new RsEvent("第一条事件", "无标签", user));
    rsEventList.add(new RsEvent("第二条事件", "无标签", user));
    rsEventList.add(new RsEvent("第三条事件", "无标签", user));
    return rsEventList;
  }

  @GetMapping("/rs/{index}")
  RsEvent getOneRsEvent(@PathVariable int index) {
    return rsList.get(index - 1);
  }

  @GetMapping("/rs/list")
  List<RsEvent> getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end) {
      if (start == null && end == null) {
          return rsList;
      }
      return rsList.subList(start - 1, end);
  }

  @PostMapping("/rs/event")
  void addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
      String userName = rsEvent.getUser().getName();
      boolean notExist = true;
      for (User user : userList) {
        if (user.getName().equals(userName)) {
            notExist = false;
        }
      }
      if (notExist) {
          userList.add(rsEvent.getUser());
      }
      rsList.add(rsEvent);
  }

  @PatchMapping("/rs/event/{index}")
  void modifyRsEvent(@RequestBody @Valid RsEvent rsEvent, @PathVariable int index) {
      if (index < 1 || index > rsList.size()) {
          throw new IllegalArgumentException();
      }
      String eventName = rsEvent.getEventName();
      String keyWord = rsEvent.getKeyWord();
      rsList.get(index - 1).setEventName(eventName);
      rsList.get(index - 1).setKeyWord(keyWord);
  }

  @DeleteMapping("/rs/list/{index}")
  void deleteRsEvent(@PathVariable int index) {
      if (index < 1 || index > rsList.size()) {
          throw new IllegalArgumentException();
      }
      rsList.remove(index - 1);
  }

  @GetMapping("/user/list")
  List<User> getUserList() {
        return userList;
  }
}
