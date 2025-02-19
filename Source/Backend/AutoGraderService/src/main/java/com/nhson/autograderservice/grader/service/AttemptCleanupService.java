package com.nhson.autograderservice.grader.service;

import com.nhson.autograderservice.grader.model.Attempt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AttemptCleanupService {
    private final EntityManager entityManager;

    public AttemptCleanupService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void removeOldAttempt() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Attempt> deleteQuery = cb.createCriteriaDelete(Attempt.class);
        Root<Attempt> root = deleteQuery.from(Attempt.class);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        deleteQuery.where(cb.lessThan(root.get("timestamp"), thirtyDaysAgo));
        entityManager.createQuery(deleteQuery).executeUpdate();
    }


}