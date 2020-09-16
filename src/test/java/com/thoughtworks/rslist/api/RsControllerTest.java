package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    public void should_get_one_rs_event() throws Exception {
        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName", is("第一条事件")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void should_get_rs_event_between_start_and_end() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void should_add_rs_event() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent("猪肉涨价了", "经济", user);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(jsonPath("$[2].eventName", is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord", is("无标签")))
                .andExpect(jsonPath("$[3].eventName", is("猪肉涨价了")))
                .andExpect(jsonPath("$[3].keyWord", is("经济")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void should_modify_rs_event_when_provide_event_name() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent("学校放假了", "经济", user);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/event/4").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(jsonPath("$[2].eventName", is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord", is("无标签")))
                .andExpect(jsonPath("$[3].eventName", is("学校放假了")))
                .andExpect(jsonPath("$[3].keyWord", is("经济")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void should_modify_rs_event_when_provide_key_word() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent("学校放假了", "政策", user);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/event/4").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(jsonPath("$[2].eventName", is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord", is("无标签")))
                .andExpect(jsonPath("$[3].eventName", is("学校放假了")))
                .andExpect(jsonPath("$[3].keyWord", is("政策")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    public void should_modify_rs_event_when_provide_key_word_and_event_name() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent("晚餐", "猪蹄", user);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/event/4").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(jsonPath("$[2].eventName", is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord", is("无标签")))
                .andExpect(jsonPath("$[3].eventName", is("晚餐")))
                .andExpect(jsonPath("$[3].keyWord", is("猪蹄")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    public void should_delete_rs_event() throws Exception {
        mockMvc.perform(delete("/rs/list/4"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(jsonPath("$[2].eventName", is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord", is("无标签")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    public void should_not_add_user_in_user_list_if_user_name_exists() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent("早餐", "稀饭", user);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/user/list"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userName", is("lize")))
                .andExpect(jsonPath("$[0].gender", is("male")))
                .andExpect(jsonPath("$[0].age", is(18)))
                .andExpect(jsonPath("$[0].email", is("a@b.com")))
                .andExpect(jsonPath("$[0].phone", is("10000000000")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    public void event_name_should_not_null() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent(null, "稀饭", user);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(10)
    public void key_word_should_not_null() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        RsEvent rsEvent = new RsEvent("早餐", null, user);
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(11)
    public void should_throw_when_start_or_end_out_of_range() throws Exception {
        mockMvc.perform(get("/rs/list?start=0&end=2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid request param")));
    }
}
