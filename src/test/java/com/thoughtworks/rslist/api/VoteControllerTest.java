package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class VoteControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    VoteRepository voteRepository;
    List<UserPO> userPOS = new ArrayList<>();
    List<RsEventPO> rsEventPOS = new ArrayList<>();
    List<VotePO> votePOS = new ArrayList<>();
    @BeforeEach
    public void setUp() {
        rsEventRepository.deleteAll();
        userRepository.deleteAll();
        voteRepository.deleteAll();

        UserPO usrPO = UserPO.builder().userName("lize").gender("male").age(18).email("a@b.com").phone("10000000000").voteNumber(10).build();
        userPOS.add(usrPO);
        userPOS.forEach(item -> userRepository.save(item));

        rsEventPOS.add(RsEventPO.builder().eventName("第一条事件").keyWord("无标签").userPO(userPOS.get(0)).voteCount(0).build());
        rsEventPOS.add(RsEventPO.builder().eventName("第二条事件").keyWord("无标签").userPO(userPOS.get(0)).voteCount(0).build());
        rsEventPOS.add(RsEventPO.builder().eventName("第三条事件").keyWord("无标签").userPO(userPOS.get(0)).voteCount(0).build());
        rsEventPOS.forEach(item -> rsEventRepository.save(item));

        votePOS.add(VotePO.builder().userPO(userPOS.get(0)).rsEventPO(rsEventPOS.get(0)).voteNum(1).localDate("2020-1-1").build());
        votePOS.add(VotePO.builder().userPO(userPOS.get(0)).rsEventPO(rsEventPOS.get(1)).voteNum(1).localDate("2020-1-2").build());
        votePOS.add(VotePO.builder().userPO(userPOS.get(0)).rsEventPO(rsEventPOS.get(2)).voteNum(1).localDate("2020-1-3").build());
        votePOS.forEach(item -> voteRepository.save(item));
    }

    @Test
    void should_get_vote_record_when_given_start_time() throws Exception {
        mockMvc.perform(get("/voteRecord").param("startTime", "2020-1-1").param("endTime", "2020-1-2"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(userPOS.get(0).getId())))
                .andExpect(jsonPath("$[0].rsEventId", is(rsEventPOS.get(0).getId())))
                .andExpect(jsonPath("$[0].voteNum", is(votePOS.get(0).getVoteNum())))
                .andExpect(jsonPath("$[1].userId", is(userPOS.get(0).getId())))
                .andExpect(jsonPath("$[1].rsEventId", is(rsEventPOS.get(1).getId())))
                .andExpect(jsonPath("$[1].voteNum", is(votePOS.get(1).getVoteNum())))
                .andExpect(status().isOk());
    }
}
