package com.nhson.userservice.repository;

import com.nhson.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserProfileRepository extends JpaRepository<User,String> {
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.userId = :userId")
    int updateLastLogin(@Param("userId") String userId, @Param("lastLogin") LocalDateTime lastLogin);
}
