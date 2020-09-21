package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VoteService {
    private VoteRepository voteRepository;

    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public int getVoteRepositorySize() {
        return voteRepository.findAll().size();
    }

    public List<Vote> getVoteRecode(String startTime, String endTime) {
        List<VotePO> allVotePORecode = voteRepository.findAll();
        return allVotePORecode.stream()
                .filter(item -> item.getLocalDate().compareTo(startTime) >= 0 && item.getLocalDate().compareTo(endTime) <= 0)
                .map(item -> Vote.builder()
                        .rsEventId(item.getRsEventPO().getId())
                        .userId(item.getUserPO().getId())
                        .voteNum(item.getVoteNum())
                        .build()
                ).collect(Collectors.toList());
    }
}
