package com.musicmaster.main.repositories;

import com.musicmaster.main.models.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserConfigRepository extends JpaRepository<UserConfig, Integer> {
//    public UserConfig findByI
}
