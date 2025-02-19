package com.nhson.autograderservice.grader.repository;

import com.nhson.autograderservice.grader.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, String> {
    public List<Attempt> findAllByUserId(String userId);
}
