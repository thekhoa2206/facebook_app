package com.web.services;

import com.web.dto.account.UserPrinciple;
import com.web.model.Account;
import com.web.repositories.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    AccountRepo userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        Account user = userRepo.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with -> username: " + phoneNumber));

        return UserPrinciple.build(user);
    }
}
