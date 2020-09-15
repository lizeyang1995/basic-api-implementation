package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class RsController {
  private List<RsEvent> rsList = initRsEvent();

  private List<RsEvent> initRsEvent() {
    List<RsEvent> rsEventList = new ArrayList<>();
    rsEventList.add(new RsEvent("第一条事件", "无标签"));
    rsEventList.add(new RsEvent("第二条事件", "无标签"));
    rsEventList.add(new RsEvent("第三条事件", "无标签"));
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
  void addRsEvent(@RequestBody RsEvent rsEvent) {
      rsList.add(rsEvent);
  }

  @PostMapping("/rs/event/{index}")
  void addRsEvent(@RequestBody RsEvent rsEvent, @PathVariable int index) {
      String eventName = rsEvent.getEventName();
      String keyWord = rsEvent.getKeyWord();
      if (eventName != null) {
          rsList.get(index - 1).setEventName(eventName);
      }
      if (keyWord != null) {
          rsList.get(index - 1).setKeyWord(keyWord);
      }
  }
}
