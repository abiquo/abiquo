package com.abiquo.api.spring.interceptors;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

@Aspect
@Service
public class ApprovalPointcut
{
    @Pointcut("bean(*Service)")
    public void pointcut()
    {

    }
}
