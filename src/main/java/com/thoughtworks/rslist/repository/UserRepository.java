package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.po.UserPO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends CrudRepository<UserPO, Integer> {
    @Override
    List<UserPO> findAll();
    List<UserPO> findByUserName(String userName);
    UserPO findUserNameById(int id);
}
