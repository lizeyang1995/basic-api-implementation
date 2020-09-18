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
    private List<RsEvent> rsEvents;
    private List<User> users = initUserList();
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    VoteRepository voteRepository;

    private List<User> initUserList() {
        users = new ArrayList<>();
        return users;
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
            rsEvent.setVoteCount(rsEventPO.getVoteCount());
            rsEvent.setRsEventId(rsEventPO.getId());
            return ResponseEntity.ok(rsEvent);
        }
        throw new RequestParamNotValid("invalid index");
    }

    @GetMapping("/rs/list")
    @JsonView(RsEvent.UserInfo.class)
    ResponseEntity getRsEventBetween(@RequestParam(required = false) Integer start, @RequestParam(required = false) Integer end) {
        rsEvents = new ArrayList<>();
        List<RsEventPO> allRsEvents = rsEventRepository.findAll();
        if (start == null && end == null && allRsEvents.size() > 0) {
            for (RsEventPO rsEventPO : allRsEvents) {
                rsEvents.add(new RsEvent(rsEventPO.getEventName(),
                        rsEventPO.getKeyWord(),
                        rsEventPO.getUserPO().getId(),
                        rsEventPO.getVoteCount(),
                        rsEventPO.getId()));
            }
            return ResponseEntity.ok(rsEvents);
        } else {
            if (start < 1 || end < 1 || start > allRsEvents.size() || end > allRsEvents.size() || start > end) {
                throw new RequestParamNotValid("invalid request param");
            }
            for (int i = start - 1; i < end; i++) {
                rsEvents.add(new RsEvent(allRsEvents.get(i).getEventName(),
                        allRsEvents.get(i).getKeyWord(),
                        allRsEvents.get(i).getUserPO().getId(),
                        allRsEvents.get(i).getVoteCount(),
                        allRsEvents.get(i).getId()));
            }
        }
        return ResponseEntity.ok(rsEvents);
    }

  @PostMapping("/rs/event")
  ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
      int userId = rsEvent.getUserId();
      Optional<UserPO> foundUserPO = userRepository.findById(userId);
      if (!foundUserPO.isPresent()) {
          return ResponseEntity.badRequest().build();
      }
      RsEventPO rsEventPO = RsEventPO.builder().eventName(rsEvent.getEventName()).keyWord(rsEvent.getKeyWord()).userPO(foundUserPO.get()).build();
      rsEventRepository.save(rsEventPO);
      int eventIndex = rsEventRepository.findAll().size() - 1;
      return ResponseEntity.created(null).header("index", Integer.toString(eventIndex)).build();
  }

    @PatchMapping("/rs/{id}")
    ResponseEntity modifyRsEvent(@RequestBody @Valid RsEvent rsEvent, @PathVariable int id) {
        if (id < 1) {
            throw new IllegalArgumentException();
        }
        Optional<RsEventPO> foundRsEventPO = rsEventRepository.findById(id);
        if (!foundRsEventPO.isPresent() || foundRsEventPO.get().getUserPO().getId() != rsEvent.getUserId()) {
            return ResponseEntity.badRequest().build();
        }
        RsEventPO rsEventPO = foundRsEventPO.get();
        String eventName = rsEvent.getEventName();
        String keyWord = rsEvent.getKeyWord();
        if (eventName != null) {
            rsEventPO.setEventName(eventName);
        }
        if (keyWord != null) {
            rsEventPO.setKeyWord(keyWord);
        }
        rsEventRepository.save(rsEventPO);
        return ResponseEntity.created(null).build();
    }

    @DeleteMapping("/rs/list/{id}")
    ResponseEntity deleteRsEvent(@PathVariable int id) {
        Optional<RsEventPO> foundRsEvent = rsEventRepository.findById(id);
        if (!foundRsEvent.isPresent()) {
            throw new IllegalArgumentException();
        }
        rsEventRepository.deleteById(id);
        return ResponseEntity.created(null).build();
    }

    @PostMapping("/rs/vote/{rsEventId}")
    ResponseEntity voting(@PathVariable int rsEventId, @RequestBody Vote vote) {
        int userId = vote.getUserId();
        Optional<RsEventPO> foundRsEventPO = rsEventRepository.findById(rsEventId);
        Optional<UserPO> foundUserPO = userRepository.findById(userId);
        if (!foundRsEventPO.isPresent()) {
            throw new IllegalArgumentException("invalid rsEventId");
        }
        if (!foundUserPO.isPresent()) {
            throw new IllegalArgumentException("invalid userId");
        }
        UserPO userPO = foundUserPO.get();
        int voteNumber = userPO.getVoteNumber();
        if (vote.getVoteNum() > voteNumber) {
            return ResponseEntity.badRequest().build();
        }
        voteRepository.save(VotePO.builder()
                            .voteNum(vote.getVoteNum())
                            .rsEventPO(foundRsEventPO.get())
                            .userPO(userPO)
                            .localDate(vote.getLocalDate())
                            .build());
        userPO.setVoteNumber(userPO.getVoteNumber() - vote.getVoteNum());
        userRepository.save(userPO);
        return ResponseEntity.created(null).header("index", Integer.toString(voteRepository.findAll().size() - 1)).build();
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
