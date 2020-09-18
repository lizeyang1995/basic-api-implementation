package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.event.annotation.BeforeTestClass;
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
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;
    List<UserPO> userPOS = new ArrayList<>();
    List<RsEventPO> rsEventPOS = new ArrayList<>();
    @BeforeEach
    public void setUp() {
        rsEventRepository.deleteAll();
        userRepository.deleteAll();

        UserPO usrPO = UserPO.builder().userName("lize").gender("male").age(18).email("a@b.com").phone("10000000000").voteNumber(10).build();
        userPOS.add(usrPO);
        userPOS.forEach(item -> userRepository.save(item));

        rsEventPOS.add(RsEventPO.builder().eventName("第一条事件").keyWord("无标签").userId(10).build());
        rsEventPOS.add(RsEventPO.builder().eventName("第二条事件").keyWord("无标签").userId(10).build());
        rsEventPOS.add(RsEventPO.builder().eventName("第三条事件").keyWord("无标签").userId(10).build());
        rsEventPOS.forEach(item -> rsEventRepository.save(item));
    }

    @Test
    @Order(1)
    public void should_get_rs_event_between_start_and_end() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
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
        assertEquals(10, allRsEvents.get(0).getUserId());
    }

    @Test
    @Order(3)
    public void should_get_one_rs_event() throws Exception {
        mockMvc.perform(get("/rs/" + rsEventPOS.get(0).getId()))
                .andExpect(jsonPath("$.eventName", is("第一条事件")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void should_add_rs_event_when_user_not_exist() throws Exception {
        String jsonString = "{\"eventName\":\"猪肉涨价了\", \"keyWord\":\"经济\", \"userId\":100}";
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public void should_modify_rs_event_when_provide_event_name() throws Exception {
        int rsEventId = rsEventPOS.get(0).getId();
        ObjectMapper objectMapper = new ObjectMapper();
        RsEvent rsEvent = new RsEvent("学校放假了", null, rsEventId);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/event/" + rsEventId).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("学校放假了")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    public void should_modify_rs_event_when_provide_key_word() throws Exception {
        int rsEventId = rsEventPOS.get(0).getId();
        ObjectMapper objectMapper = new ObjectMapper();
        RsEvent rsEvent = new RsEvent(null, "政策", rsEventId);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/event/" + rsEventId).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("政策")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    public void should_modify_rs_event_when_provide_key_word_and_event_name() throws Exception {
        int rsEventId = rsEventPOS.get(0).getId();
        ObjectMapper objectMapper = new ObjectMapper();
        RsEvent rsEvent = new RsEvent("晚餐", "猪蹄", rsEventId);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/event/" + rsEventId).content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("晚餐")))
                .andExpect(jsonPath("$[0].keyWord", is("猪蹄")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
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
    @Order(9)
    public void should_not_add_user_in_user_list_if_user_name_exists() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent("早餐", "稀饭", 1);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/user/list"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user_name", is("lize")))
                .andExpect(jsonPath("$[0].user_gender", is("male")))
                .andExpect(jsonPath("$[0].user_age", is(18)))
                .andExpect(jsonPath("$[0].user_email", is("a@b.com")))
                .andExpect(jsonPath("$[0].user_phone", is("10000000000")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    public void event_name_should_not_null() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent(null, "稀饭", 1);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(11)
    public void key_word_should_not_null() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent("早餐", null, 1);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(12)
    public void should_not_include_user_field_in_rs_index() throws Exception {
        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName", is("第一条事件")))
                .andExpect(jsonPath("$", not(hasKey("user"))))
                .andExpect(status().isOk());
    }

    @Test
    @Order(13)
    public void should_throw_when_start_or_end_out_of_range() throws Exception {
        mockMvc.perform(get("/rs/list?start=0&end=2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid request param")));

    }

    @Test
    @Order(14)
    public void should_not_include_user_field_in_rs_list() throws Exception {
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$", not(hasKey("user"))))
                .andExpect(status().isOk());
    }

    @Test
    @Order(15)
    public void should_throw_when_rs_index_out_of_range() throws Exception {
        mockMvc.perform(get("/rs/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid index")));
    }

    @Test
    @Order(16)
    public void should_throw_when_method_argument_invalid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lizezzzzz", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent("中餐", "面条", 1);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid param")));

    }
}
