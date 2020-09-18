package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class RsEvent {
    public interface UserInfo{};
    public interface UserDetail extends UserInfo{};
    @JsonView(UserInfo.class)
    private String eventName;
    @JsonView(UserInfo.class)
    private String keyWord;
    @JsonView(UserDetail.class)
    @Valid
    @NotNull
    private int userId;
    @JsonView(UserInfo.class)
    private int voteCount;
    @JsonView(UserInfo.class)
    private int rsEventId;

    public RsEvent(String eventName, String keyWord, int userId) {
        this.eventName = eventName;
        this.keyWord = keyWord;
        this.userId = userId;
    }

    public RsEvent(String eventName, String keyWord, @Valid @NotNull int userId, int voteCount, int rsEventId) {
        this.eventName = eventName;
        this.keyWord = keyWord;
        this.userId = userId;
        this.voteCount = voteCount;
        this.rsEventId = rsEventId;
    }

    public int getRsEventId() {
        return rsEventId;
    }

    public void setRsEventId(int rsEventId) {
        this.rsEventId = rsEventId;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public RsEvent() {
    }
}
