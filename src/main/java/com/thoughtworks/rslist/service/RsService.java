package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class RsService {
    private RsEventRepository rsEventRepository;
    private UserRepository userRepository;
    private VoteRepository voteRepository;

    public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.rsEventRepository = rsEventRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    public RsService() {
    }

    public boolean vote(int rsEventId, Vote vote) {
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
            return false;
        }
        voteRepository.save(VotePO.builder()
                .voteNum(vote.getVoteNum())
                .rsEventPO(foundRsEventPO.get())
                .userPO(userPO)
                .localDate(vote.getLocalDate())
                .build());
        userPO.setVoteNumber(userPO.getVoteNumber() - vote.getVoteNum());
        userRepository.save(userPO);
        return true;
    }

    public boolean getOneRsEvent(int id) {
        Optional<RsEventPO> foundRsEvent = rsEventRepository.findById(id);
        return foundRsEvent.isPresent();
    }
}
