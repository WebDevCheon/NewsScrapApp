package com.myapp.backend.service;

import com.myapp.backend.entity.User;
import com.myapp.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByLoginId(String userId) {
        return userRepository.findByLoginId(userId);
    }

    @Override
    public void signup(String loginId) {
        User user = userRepository.findByLoginId(loginId);
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }
}
