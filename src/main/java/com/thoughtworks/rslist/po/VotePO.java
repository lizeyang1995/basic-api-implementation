package com.thoughtworks.rslist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "Vote")
@AllArgsConstructor
@NoArgsConstructor
public class VotePO {
    @Id
    @GeneratedValue
    private int id;
    private int voteNum;
    @ManyToOne
    @JoinColumn(name = "rsEventId")
    private RsEventPO rsEventPO;
    @ManyToOne
    @JoinColumn(name = "userId")
    private UserPO userPO;
    private LocalDateTime localDateTime;
}
