package com.thoughtworks.rslist.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Vote {
    private int rsEventId;
    private int voteNum;
    private int userId;
    private LocalDateTime localDateTime;
}
