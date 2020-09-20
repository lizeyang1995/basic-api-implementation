package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RequestParamNotValid;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import com.thoughtworks.rslist.service.RsService;
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
import java.util.stream.Collectors;

@RestController
public class RsController {
    private List<User> users = initUserList();
    Logger logger = LoggerFactory.getLogger(getClass());
    final RsService rsService;

    private List<User> initUserList() {
        users = new ArrayList<>();
        return users;
    }

    public RsController(RsService rsService) {
        this.rsService = rsService;
    }

    @GetMapping("/rs/{id}")
    @JsonView(RsEvent.UserInfo.class)
    ResponseEntity getOneRsEvent(@PathVariable int id) {
        RsEvent rsEvent = rsService.getOneRsEvent(id);
        return ResponseEntity.ok(rsEvent);
    }

    @GetMapping("/rs/list")
    @JsonView(RsEvent.UserInfo.class)
    ResponseEntity getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end) {
        List<RsEvent> rsEvents = rsService.getRsEventBetween(start, end);
        return ResponseEntity.ok(rsEvents);
    }

  @PostMapping("/rs/event")
  ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
      boolean isSuccess = rsService.addRsEvent(rsEvent);
      if (!isSuccess) {
          return ResponseEntity.badRequest().build();
      }
      int rsRepositorySize = rsService.getRsRepositorySize();
      return ResponseEntity.created(null).header("index", Integer.toString(rsRepositorySize - 1)).build();
  }

    @PatchMapping("/rs/{id}")
    ResponseEntity modifyRsEvent(@RequestBody @Valid RsEvent rsEvent, @PathVariable int id) {
        boolean isSuccess = rsService.modifyRsEvent(rsEvent, id);
        if (!isSuccess) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.created(null).build();
    }

    @DeleteMapping("/rs/list/{id}")
    ResponseEntity deleteRsEvent(@PathVariable int id) {
        rsService.deleteRsEvent(id);
        return ResponseEntity.created(null).build();
    }

    @PostMapping("/rs/{rsEventId}/vote")
    ResponseEntity voting(@PathVariable int rsEventId, @RequestBody Vote vote) {
        boolean isSuccess = rsService.vote(rsEventId, vote);
        if (!isSuccess) {
            return ResponseEntity.badRequest().build();
        }
        int voteRepositorySize = rsService.getVoteRepositorySize();
        return ResponseEntity.created(null).header("index", Integer.toString(voteRepositorySize - 1)).build();
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
