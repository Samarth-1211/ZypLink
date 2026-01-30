package com.ZypLink.ZyplinkProj.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ZypLink.ZyplinkProj.entities.ClickEvents;
import com.ZypLink.ZyplinkProj.entities.UrlMapping;

@Repository
public interface ClickEventsRepository extends JpaRepository<ClickEvents,Long>{

    List<ClickEvents> findByUrlMappingAndClickDateBetween(UrlMapping mapping, LocalDateTime startDateTime, LocalDateTime endDateTime);

    // To get Url click events of all the url mappings for a user between date range
    List<ClickEvents> findByUrlMappingInAndClickDateBetween(List<UrlMapping> mappings , LocalDateTime startDateTime, LocalDateTime endDateTime);

    void deleteByUrlMapping(UrlMapping urlMapping);

}
