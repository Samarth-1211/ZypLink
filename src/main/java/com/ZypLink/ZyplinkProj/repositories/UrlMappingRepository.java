package com.ZypLink.ZyplinkProj.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ZypLink.ZyplinkProj.entities.UrlMapping;
import com.ZypLink.ZyplinkProj.entities.User;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping,Long>{
    
    UrlMapping findByShortUrl(String shortUrl);
    List<UrlMapping> findByUser(User user);

    void deleteAllByUserId(Long userId);
    
}
