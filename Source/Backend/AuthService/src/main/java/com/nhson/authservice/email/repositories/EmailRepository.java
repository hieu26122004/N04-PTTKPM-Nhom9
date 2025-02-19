package com.nhson.authservice.email.repositories;

import com.nhson.authservice.email.entities.Email;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends MongoRepository<Email,Long> {
    Optional<Email> findFirstByOrderByLastTryAtAsc();
}
