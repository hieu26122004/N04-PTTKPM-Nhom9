package com.nhson.examservice.vote.repositories;

import com.nhson.examservice.vote.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> , VoteRepositoryCustom {
}
