package com.abiquo.api.spring.security;

import org.springframework.security.userdetails.UserDetails;

import com.abiquo.server.core.enterprise.User;

public interface UserLoginService
{
    public UserDetails getUserDetails(final User user);
}
