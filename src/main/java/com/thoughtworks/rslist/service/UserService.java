package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.po.UserPO;
import com.thoughtworks.rslist.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean addUser(User user) {
        UserPO userPO = new UserPO();
        List<UserPO> foundByUserName = userRepository.findByUserName(user.getUserName());
        if (foundByUserName.size() > 0) {
            return false;
        }
        userPO.setUserName(user.getUserName());
        userPO.setAge(user.getAge());
        userPO.setGender(user.getGender());
        userPO.setEmail(user.getEmail());
        userPO.setPhone(user.getPhone());
        userPO.setVoteNumber(user.getVoteNumber());
        userRepository.save(userPO);
        return true;
    }

    public int getUserRepositorySize() {
        return userRepository.findAll().size();
    }

    public List<UserPO> getAllUsers() {
        return userRepository.findAll();
    }

    public UserPO getUserById(int id) {
        Optional<UserPO> foundUser = userRepository.findById(id);
        if (foundUser.isPresent()) {
            return foundUser.get();
        }
        throw new IllegalArgumentException("invalid userId");
    }

    public void deleteUserById(int id) {
        Optional<UserPO> foundUser = userRepository.findById(id);
        if (foundUser.isPresent()) {
            userRepository.deleteById(id);
            return;
        }
        throw new IllegalArgumentException("invalid userId");
    }
}
