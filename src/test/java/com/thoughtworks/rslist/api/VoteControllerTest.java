package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.po.RsEventPO;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class VoteControllerTest {
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
}
