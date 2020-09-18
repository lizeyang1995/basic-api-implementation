package com.thoughtworks.rslist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "rsEvent")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsEventPO {
    @GeneratedValue
    @Id
    private int id;
    private String eventName;
    private String keyWord;
    private int voteCount;
    @ManyToOne
    private UserPO userPO;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "rsEventPO")
    private List<VotePO> votePOS;
}
