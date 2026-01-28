package com.sneakerstore.backend.services;

import com.sneakerstore.backend.models.User;


public interface UserService {
    // Controller sẽ map DTO -> User rồi mới truyền vào đây
    User createUser(User user) throws Exception;
    User getUserById(long id);
    User getUserDetails(String username) throws Exception;
    String login(String username, String password) throws Exception; 
}