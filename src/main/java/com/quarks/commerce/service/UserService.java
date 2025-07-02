package com.quarks.commerce.service;

import com.quarks.commerce.model.User;

public interface UserService {
    User createUser(String username, String email, String password);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
} 