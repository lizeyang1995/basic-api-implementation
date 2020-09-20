package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.po.VotePO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RsControllerTest {

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
    }

    @Test
    public void should_get_rs_event_between_start_and_end() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[0].voteCount", is(0)))
                .andExpect(jsonPath("$[0].rsEventId", is(rsEventPOS.get(0).getId())))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].voteCount", is(0)))
                .andExpect(jsonPath("$[1].rsEventId", is(rsEventPOS.get(1).getId())))
                .andExpect(status().isOk());
    }

    @Test
    public void should_add_rs_event_when_user_exist() throws Exception {
        UserPO savedOneUser = userRepository.save(UserPO.builder().userName("lize").gender("male").age(18).email("a@b.com").phone("10000000000").voteNumber(10).build());
        String jsonString = "{\"eventName\":\"猪肉涨价了\", \"keyWord\":\"经济\", \"userId\":" + savedOneUser.getId() + "}";
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("index", "3"));
        List<RsEventPO> allRsEvents = rsEventRepository.findAll();
        assertNotNull(allRsEvents);
        assertEquals(4, allRsEvents.size());
        assertEquals("第一条事件", allRsEvents.get(0).getEventName());
        assertEquals("无标签", allRsEvents.get(0).getKeyWord());
        assertEquals(savedOneUser.getId(), allRsEvents.get(allRsEvents.size() - 1).getUserPO().getId());
    }

    @Test
    public void should_get_one_rs_event() throws Exception {
        mockMvc.perform(get("/rs/" + rsEventPOS.get(0).getId()))
                .andExpect(jsonPath("$.eventName", is("第一条事件")))
                .andExpect(jsonPath("$.keyWord", is("无标签")))
                .andExpect(jsonPath("$.voteCount", is(0)))
                .andExpect(jsonPath("$.rsEventId", is(rsEventPOS.get(0).getId())))
                .andExpect(status().isOk());
    }

    @Test
    public void should_add_rs_event_when_user_not_exist() throws Exception {
        String jsonString = "{\"eventName\":\"猪肉涨价了\", \"keyWord\":\"经济\", \"userId\":100}";
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_modify_rs_event_when_provide_event_name() throws Exception {
        int rsEventId = rsEventPOS.get(0).getId();
        int userId = rsEventPOS.get(0).getUserPO().getId();
        ObjectMapper objectMapper = new ObjectMapper();
        RsEvent rsEvent = new RsEvent("学校放假了", null, userId);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/" + rsEventId).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        RsEventPO rsEventPO = rsEventRepository.findById(rsEventId).get();
        assertEquals("学校放假了", rsEventPO.getEventName());
    }

    @Test
    public void should_modify_rs_event_when_provide_key_word() throws Exception {
        int rsEventId = rsEventPOS.get(0).getId();
        int userId = rsEventPOS.get(0).getUserPO().getId();
        ObjectMapper objectMapper = new ObjectMapper();
        RsEvent rsEvent = new RsEvent(null, "政策", userId);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/" + rsEventId).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        RsEventPO rsEventPO = rsEventRepository.findById(rsEventId).get();
        assertEquals("政策", rsEventPO.getKeyWord());
    }

    @Test
    public void should_modify_rs_event_when_provide_key_word_and_event_name() throws Exception {
        int rsEventId = rsEventPOS.get(0).getId();
        int userId = rsEventPOS.get(0).getUserPO().getId();
        ObjectMapper objectMapper = new ObjectMapper();
        RsEvent rsEvent = new RsEvent("夏天", "吃西瓜", userId);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/" + rsEventId).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        RsEventPO rsEventPO = rsEventRepository.findById(rsEventId).get();
        assertEquals("夏天", rsEventPO.getEventName());
        assertEquals("吃西瓜", rsEventPO.getKeyWord());
    }

    @Test
    public void should_delete_rs_event() throws Exception {
        int rsEventId = rsEventPOS.get(0).getId();
        mockMvc.perform(delete("/rs/list/" + rsEventId))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(status().isOk());
    }

    @Test
    public void event_name_should_not_null() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RsEvent rsEvent = new RsEvent(null, "稀饭", 1);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void key_word_should_not_null() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RsEvent rsEvent = new RsEvent("早餐", null, 1);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_not_include_user_field_in_rs_index() throws Exception {
        int rsEventId = rsEventPOS.get(0).getId();
        mockMvc.perform(get("/rs/" + rsEventId))
                .andExpect(jsonPath("$.eventName", is("第一条事件")))
                .andExpect(jsonPath("$", not(hasKey("userId"))))
                .andExpect(status().isOk());
    }

    @Test
    public void should_throw_when_start_or_end_out_of_range() throws Exception {
        mockMvc.perform(get("/rs/list?start=0&end=2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid request param")));

    }

    @Test
    public void should_not_include_user_field_in_rs_list() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$", not(hasKey("userId"))))
                .andExpect(status().isOk());
    }

    @Test
    public void should_throw_when_rs_index_out_of_range() throws Exception {
        mockMvc.perform(get("/rs/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid index")));
    }

    @Test
    void should_success_when_user_votes_enough() throws Exception {
        int rsEventId = rsEventPOS.get(0).getId();
        int userId = userPOS.get(0).getId();
        ObjectMapper objectMapper = new ObjectMapper();
        Vote vote = Vote.builder().userId(userId).voteNum(1).localDate("11:11").build();
        String jsonString = objectMapper.writeValueAsString(vote);
        mockMvc.perform(post("/rs/{rsEventId}/vote", String.valueOf(rsEventId)).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("index", "0"));
        List<VotePO> allVoteRecord = voteRepository.findAll();
        assertEquals(1, allVoteRecord.size());
        assertEquals(1, allVoteRecord.get(0).getVoteNum());
        assertEquals(userId, allVoteRecord.get(0).getUserPO().getId());
    }

    @Test
    void should_throw_when_user_votes_not_enough() throws Exception {
        int rsEventId = rsEventPOS.get(0).getId();
        int userId = userPOS.get(0).getId();
        ObjectMapper objectMapper = new ObjectMapper();
        Vote vote = Vote.builder().userId(userId).voteNum(11).localDate("11:11").build();
        String jsonString = objectMapper.writeValueAsString(vote);
        mockMvc.perform(post("/rs/{rsEventId}/vote", String.valueOf(rsEventId)).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        List<VotePO> allVoteRecord = voteRepository.findAll();
        assertEquals(0, allVoteRecord.size());
    }
}
