package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
  List<RsEvent> getRsEventBetween(@RequestParam int start, @RequestParam int end) {
      return rsList.subList(start - 1, end);
  }
}
