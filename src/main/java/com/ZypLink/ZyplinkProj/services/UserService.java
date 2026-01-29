package com.ZypLink.ZyplinkProj.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ZypLink.ZyplinkProj.entities.User;
import com.ZypLink.ZyplinkProj.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       return repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    
    }

    public  User getUserById(Long userId) {
        return repo.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }


    
}