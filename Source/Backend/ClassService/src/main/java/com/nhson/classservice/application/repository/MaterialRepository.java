package com.nhson.classservice.application.repository;

import com.nhson.classservice.application.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material,String> {
}
