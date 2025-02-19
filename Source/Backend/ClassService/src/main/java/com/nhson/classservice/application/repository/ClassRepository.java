package com.nhson.classservice.application.repository;

import com.nhson.classservice.application.model.Class;
import com.nhson.classservice.application.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, String> {
    List<Class> findAllByParticipants_UserId(String userId);

}
