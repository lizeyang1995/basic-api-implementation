package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import com.thoughtworks.rslist.service.RsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class VoteController {
    final private RsService rsService;

    public VoteController(RsService rsService) {
        this.rsService = rsService;
    }

    @GetMapping("/voteRecord")
    ResponseEntity getVoteRecode(@RequestParam String startTime, @RequestParam String endTime) {
        List<Vote> allVoteRecode = rsService.getVoteRecode(startTime, endTime);
        return ResponseEntity.ok(allVoteRecode);
    }
}
