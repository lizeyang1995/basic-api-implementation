package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class VoteController {
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/voteRecord")
    ResponseEntity getVoteRecode(@RequestParam String startTime) {
        List<VotePO> allVotePORecode = voteRepository.findAll();
        List<Vote> allVoteRecode = allVotePORecode.stream()
                .filter(item -> item.getLocalDate().compareTo(startTime) >= 0)
                .map(item -> Vote.builder()
                        .rsEventId(item.getRsEventPO().getId())
                        .userId(item.getUserPO().getId())
                        .voteNum(item.getVoteNum())
                        .build()
        ).collect(Collectors.toList());
        return ResponseEntity.ok(allVoteRecode);
    }
}
