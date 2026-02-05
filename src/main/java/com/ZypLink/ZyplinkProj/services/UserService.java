package com.ZypLink.ZyplinkProj.services;

import java.security.Principal;
import java.time.LocalDateTime;

import org.hibernate.mapping.List;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ZypLink.ZyplinkProj.dto.UserProfileUpdates;
import com.ZypLink.ZyplinkProj.entities.UrlMapping;
import com.ZypLink.ZyplinkProj.entities.User;
import com.ZypLink.ZyplinkProj.exceptions.ResourceNotFoundException;
import com.ZypLink.ZyplinkProj.repositories.ClickEventsRepository;
import com.ZypLink.ZyplinkProj.repositories.UrlMappingRepository;
import com.ZypLink.ZyplinkProj.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repo;
    private final ModelMapper mapper;
    private final ClickEventsRepository clickrepo;
    private final UrlMappingRepository urlRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       return repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    
    }

    public  User getUserById(Long userId) {
        return repo.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

       /* ===== GET PROFILE ===== */
    public UserProfileUpdates getCurrentUser(Principal principal) {

        User user = repo.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return mapper.map(user, UserProfileUpdates.class);
    }

    // Delete User Account 
    
    @Transactional
    public Boolean deleteAccount(Principal principal) {
    
        User user = repo.findByEmail(principal.getName())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
        repo.delete(user); //  cascade handles everything
    
        return true;
    }
    
    

    
    




    
}