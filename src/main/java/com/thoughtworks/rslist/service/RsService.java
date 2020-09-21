package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.exception.RequestParamNotValid;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RsService {
    private RsEventRepository rsEventRepository;
    private UserRepository userRepository;
    private VoteRepository voteRepository;

    public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.rsEventRepository = rsEventRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
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

    public RsEvent getOneRsEvent(int id) {
        Optional<RsEventPO> foundRsEvent = rsEventRepository.findById(id);
        if (foundRsEvent.isPresent()) {
            RsEventPO rsEventPO = foundRsEvent.get();
            RsEvent rsEvent = new RsEvent();
            rsEvent.setEventName(rsEventPO.getEventName());
            rsEvent.setKeyWord(rsEventPO.getKeyWord());
            rsEvent.setVoteCount(rsEventPO.getVoteCount());
            rsEvent.setRsEventId(rsEventPO.getId());
            return rsEvent;
        }
        throw new RequestParamNotValid("invalid index");
    }

    public List<RsEvent> getRsEventBetween(Integer start, Integer end) {
        List<RsEventPO> allRsEvents = rsEventRepository.findAll();
        List<RsEvent> rsEvents = rsEventRepository.findAll().stream()
                .map(
                        item ->
                                RsEvent.builder()
                                        .eventName(item.getEventName())
                                        .keyWord(item.getKeyWord())
                                        .userId(item.getUserPO().getId())
                                        .rsEventId(item.getId())
                                        .voteCount(item.getVoteCount())
                                        .build())
                .collect(Collectors.toList());
        if (start == null && end == null) {
            return rsEvents;
        }
        if (start < 1 || end < 1 || start > allRsEvents.size() || end > allRsEvents.size() || start > end) {
            throw new RequestParamNotValid("invalid request param");
        }
        return rsEvents.subList(start - 1, end);
    }

    public int getRsRepositorySize() {
        return rsEventRepository.findAll().size();
    }

    public boolean addRsEvent(RsEvent rsEvent) {
        int userId = rsEvent.getUserId();
        Optional<UserPO> foundUserPO = userRepository.findById(userId);
        if (!foundUserPO.isPresent()) {
            return false;
        }
        RsEventPO rsEventPO = RsEventPO.builder().eventName(rsEvent.getEventName()).keyWord(rsEvent.getKeyWord()).userPO(foundUserPO.get()).build();
        rsEventRepository.save(rsEventPO);
        return true;
    }

    public boolean modifyRsEvent(RsEvent rsEvent, int id) {
        if (id < 1) {
            throw new IllegalArgumentException("invalid rsEventId");
        }
        Optional<RsEventPO> foundRsEventPO = rsEventRepository.findById(id);
        if (!foundRsEventPO.isPresent() || foundRsEventPO.get().getUserPO().getId() != rsEvent.getUserId()) {
            return false;
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
        return true;
    }

    public void deleteRsEvent(int id) {
        Optional<RsEventPO> foundRsEvent = rsEventRepository.findById(id);
        if (!foundRsEvent.isPresent()) {
            throw new IllegalArgumentException("invalid rsEventId");
        }
        rsEventRepository.deleteById(id);
    }

    public int getVoteRepositorySize() {
        return voteRepository.findAll().size();
    }

    public List<Vote> getVoteRecode(String startTime) {
        List<VotePO> allVotePORecode = voteRepository.findAll();
        return allVotePORecode.stream()
                .filter(item -> item.getLocalDate().compareTo(startTime) >= 0)
                .map(item -> Vote.builder()
                        .rsEventId(item.getRsEventPO().getId())
                        .userId(item.getUserPO().getId())
                        .voteNum(item.getVoteNum())
                        .build()
                ).collect(Collectors.toList());
    }
}
