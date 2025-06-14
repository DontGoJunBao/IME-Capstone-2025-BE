package com.bossoverhere.capstone.boss_over_here_backend.domain.user.dao;

import com.bossoverhere.capstone.boss_over_here_backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOauthId(String oauthId);
}
