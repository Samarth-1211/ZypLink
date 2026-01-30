package com.ZypLink.ZyplinkProj.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ZypLink.ZyplinkProj.entities.UrlMapping;
import com.ZypLink.ZyplinkProj.repositories.ClickEventsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClickEventService {

    private final ClickEventsRepository clickEventsRepo;


    public void DeleteClickEventByUrlmapping(UrlMapping urlMapping){
        if(urlMapping != null){
            clickEventsRepo.deleteByUrlMapping(urlMapping);
        }else{
            throw new IllegalArgumentException("UrlMapping Cannot Be Null");
        }
    }
    
}
