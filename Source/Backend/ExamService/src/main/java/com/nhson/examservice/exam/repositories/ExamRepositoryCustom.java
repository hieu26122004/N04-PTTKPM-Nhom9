package com.nhson.examservice.exam.repositories;

import com.nhson.examservice.exam.entities.Exam;
import com.nhson.examservice.exam.entities.Subject;
import com.nhson.examservice.vote.entities.VoteType;

import java.time.LocalDateTime;
import java.util.List;

public interface ExamRepositoryCustom {
    public List<Exam> getNext10ExamsBySubjectOrderByLastUpdateDate(Subject subject, LocalDateTime lastExam, int limit);
    public List<Exam> findByCreatedDateBeforeAndIsActiveFalse(LocalDateTime thresholdDate);
    public List<Exam> getByUserAndFilter(String username,LocalDateTime lastTime,int limit);
}
