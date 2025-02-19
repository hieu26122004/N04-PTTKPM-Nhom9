package com.nhson.classservice.application.repository;

import com.nhson.classservice.application.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,String> {
}
