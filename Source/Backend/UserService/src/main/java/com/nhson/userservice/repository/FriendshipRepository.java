package com.nhson.userservice.repository;

import com.nhson.userservice.domain.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship,String> {
}
