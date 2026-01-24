package com.ZypLink.ZyplinkProj.filters;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ZypLink.ZyplinkProj.entities.User;
import com.ZypLink.ZyplinkProj.services.UserService;
import com.ZypLink.ZyplinkProj.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter{

    private final UserService UserService;
    private final JwtUtils jwtUtils;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/register");
    }

    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                try{
                    final String requestTokenHeader = request.getHeader("Authorization");
                    log.info("Request Token Header: {}", requestTokenHeader);

                    if(requestTokenHeader ==null || !requestTokenHeader.startsWith("Bearer ") ){
                        filterChain.doFilter(request, response);
                        return;
                    }

                    String token=  requestTokenHeader.substring(7);
                    Long UserId = jwtUtils.getUserIdFromToken(token);
                    log.info("UserId extracted from token: {}", UserId);


                    if(UserId != null && SecurityContextHolder.getContext().getAuthentication() == null){
                        log.info("User authenticated successfully with userId: {}", UserId);
                        User user = UserService.getUserById(UserId);
                        log.info("User details fetched: {}", user.getEmail());
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user , null, user.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("Security context updated with authentication for user: {}", user.getEmail());
                    }

                    filterChain.doFilter(request, response);
                    
                }catch(Exception e){
                    e.printStackTrace();
                }

    
            }


    
}
