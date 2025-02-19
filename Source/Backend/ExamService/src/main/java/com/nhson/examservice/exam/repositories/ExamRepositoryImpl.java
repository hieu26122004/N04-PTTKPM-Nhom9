package com.nhson.examservice.exam.repositories;

import com.nhson.examservice.exam.entities.*;
import com.nhson.examservice.vote.entities.Vote;
import com.nhson.examservice.vote.entities.VoteType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ExamRepositoryImpl implements ExamRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    public List<Exam> getNext10ExamsBySubjectOrderByLastUpdateDate(Subject subject, LocalDateTime lastExam, int limit) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Exam> query = cb.createQuery(Exam.class);

        Root<Exam> examRoot = query.from(Exam.class);
        query.select(examRoot).distinct(true);
        Join<Exam, Vote> votesJoin = examRoot.join("votes", JoinType.LEFT);
        Predicate activePredicate = cb.equal(examRoot.get("status"), Status.ACCEPTED);
        Predicate publicPredicate = cb.equal(examRoot.get("type"), Type.PUBLIC);

        Predicate subjectPredicate = (subject != null) ? cb.equal(examRoot.get("subject"), subject) : cb.conjunction();

        Predicate lastUpdateDatePredicate = (lastExam != null)
                ? cb.lessThanOrEqualTo(examRoot.get("lastUpdatedDate"), lastExam)
                : cb.conjunction();

        Predicate finalPredicate = cb.and(activePredicate, subjectPredicate, lastUpdateDatePredicate, publicPredicate);

        query.where(finalPredicate);

        query.orderBy(cb.desc(examRoot.get("lastUpdatedDate")));
        TypedQuery<Exam> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(limit);

        List<Exam> exams = typedQuery.getResultList();

        return exams;
    }

    @Override
    public List<Exam> findByCreatedDateBeforeAndIsActiveFalse(LocalDateTime thresholdDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Exam> query = cb.createQuery(Exam.class);

        Root<Exam> examRoot = query.from(Exam.class);
        Join<Exam, Vote> voteJoin = examRoot.join("votes", JoinType.LEFT);

        Predicate datePredicate = cb.lessThan(examRoot.get("createdDate"), thresholdDate);
        Predicate statusPredicate = cb.equal(examRoot.get("status"), Status.NOT_ACCEPTED);

        query.where(cb.and(datePredicate, statusPredicate));

        TypedQuery<Exam> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    public List<Exam> getByUserAndFilter(String username, LocalDateTime lastExam, int limit) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Exam> query = cb.createQuery(Exam.class);

        Root<Exam> examRoot = query.from(Exam.class);

        Predicate lastUpdateDatePredicate = (lastExam != null)
                ? cb.lessThan(examRoot.get("lastUpdatedDate"), lastExam)
                : cb.conjunction();

        Predicate userPredicate = cb.equal(examRoot.get("provider"), username);

        Predicate finalPredicate = cb.and(userPredicate, lastUpdateDatePredicate);

        query.where(finalPredicate);
        query.orderBy(cb.desc(examRoot.get("lastUpdatedDate")));

        TypedQuery<Exam> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(limit);

        return typedQuery.getResultList();
    }

}
