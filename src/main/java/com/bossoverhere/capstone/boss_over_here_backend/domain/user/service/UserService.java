package com.bossoverhere.capstone.boss_over_here_backend.domain.user.service;

import com.bossoverhere.capstone.boss_over_here_backend.domain.user.dao.UserRepository;
import com.bossoverhere.capstone.boss_over_here_backend.domain.user.domain.User;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.ApplicationException;
import com.bossoverhere.capstone.boss_over_here_backend.global.error.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);


    }
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.NOTFOUND_USER));
    }

}
