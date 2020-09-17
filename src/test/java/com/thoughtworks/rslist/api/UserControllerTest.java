package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    @Test
    @Order(1)
    public void should_add_a_user() throws Exception {
        User user = new User("lize", "male", 18, "a@b.com", "10000000000");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("index", "0"));
        List<UserPO> allUsers = userRepository.findAll();
        assertEquals(1, allUsers.size());
        assertEquals("lize", allUsers.get(0).getUserName());
        assertEquals("male", allUsers.get(0).getGender());
        assertEquals(18, allUsers.get(0).getAge());
        assertEquals("a@b.com", allUsers.get(0).getEmail());
        assertEquals("10000000000", allUsers.get(0).getPhone());
    }

    @Test
    @Order(2)
    public void name_should_less_than_8() throws Exception {
        User user = new User("lizezzzzz", "male", 18, "a@b.com", "10000000000");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    public void age_should_between_18_and_100() throws Exception {
        User user = new User("lize", "male", 15, "a@b.com", "10000000000");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    public void phone_number_should_less_than_11() throws Exception {
        User user = new User("lize", "male", 18, "a@b.com", "100000000001");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    public void email_should_suit_format() throws Exception {
        User user = new User("lize", "male", 18, "ab.com", "10000000000");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public void gender_should_not_null() throws Exception {
        User user = new User("lize", null, 18, "a@b.com", "10000000000");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    public void should_get_all_users() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user_name", is("lize")))
                .andExpect(jsonPath("$[0].user_age", is(18)))
                .andExpect(jsonPath("$[0].user_gender", is("male")))
                .andExpect(jsonPath("$[0].user_email", is("a@b.com")))
                .andExpect(jsonPath("$[0].user_phone", is("10000000000")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    public void should_throw_when_user_argument_invalid() throws Exception {
        User user = new User("lizezzzzz", null, 18, "ab.com", "10000000000");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid user")));

    }

    @Test
    @Order(8)
    public void should_return_user_information_when_given_id() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(jsonPath("$.userName", is("lize")))
                .andExpect(jsonPath("$.gender", is("male")))
                .andExpect(jsonPath("$.age", is(18)))
                .andExpect(jsonPath("$.email", is("a@b.com")))
                .andExpect(jsonPath("$.phone", is("10000000000")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    public void should_delete_user_when_given_id() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        List<UserPO> allUser = userRepository.findAll();
        assertEquals(0, allUser.size());
    }
}
