package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {
    private final RsEventRepository rsEventRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public ServiceConfig(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.rsEventRepository = rsEventRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    @Bean
    public RsService rsService() {
        return new RsService(rsEventRepository, userRepository, voteRepository);
    }

    @Bean
    public UserService userService() {
        return new UserService(userRepository);
    }

    @Bean
    public VoteService voteService() {
        return new VoteService(voteRepository);
    }
}
