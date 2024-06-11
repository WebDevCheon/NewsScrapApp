package com.myapp.backend.service;

import com.myapp.backend.entity.User;

public interface UserService {
    public User findByLoginId(String userId);
    public void signup(String loginId);
}
