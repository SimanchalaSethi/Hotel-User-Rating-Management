package com.user.service.UserService.services;

import com.user.service.UserService.entities.User;

import java.util.List;

public interface UserService {
    //create
    User saveUser(User user);
    //get all user
    List<User> getAllUser();
    //get single user of given userId
    User getUser(String userId);
    //delete by user id
    String deleteBYId(String userId);
    //update by user id
    User updateUser(User user);

}
