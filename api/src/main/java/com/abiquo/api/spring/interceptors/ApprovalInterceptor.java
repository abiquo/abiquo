package com.abiquo.api.spring.interceptors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abiquo.api.spring.interceptors.annotations.Approval;

@Aspect
@Service
public class ApprovalInterceptor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ApprovalInterceptor.class);

    // @Around("inServiceLayer() && @annotation(approval)")
    // public Object logAction(final ProceedingJoinPoint pjp, final Approval approval)
    // throws Throwable
    // {
    // String role = approval.role();
    // System.out.println(role);
    // LOGGER.info(role);
    //
    // for (Object object : pjp.getArgs())
    // {
    // System.out.println(object);
    // LOGGER.info(object.toString());
    // }
    //
    // return null;
    // // return pjp.proceed();
    // }

    @Before(value = "com.abiquo.api.spring.interceptors.ApprovalPointcut.pointcut() "
        + "&& target(bean) "
        + "&& @annotation(com.abiquo.api.spring.interceptors.annotations.Approval) "
        + "&& @annotation(approval)", argNames = "bean,approval")
    public void performAudit(final JoinPoint jp, final Object bean, final Approval approval)
    {
        System.out.println(String.format("Approval Role: %s", approval.role()));
        System.out.println(String.format("Approval Required: %s", approval.required()));
        System.out.println(String.format("Bean Called: %s", bean.getClass().getName()));
        System.out.println(String.format("Method Called: %s", jp.getSignature().getName()));

        LOGGER.debug(String.format("Approval Role: %s", approval.role()));
        LOGGER.debug(String.format("Approval Required: %s", approval.required()));
        LOGGER.debug(String.format("Bean Called: %s", bean.getClass().getName()));
        LOGGER.debug(String.format("Method Called: %s", jp.getSignature().getName()));

        LOGGER.info(String.format("Approval Role: %s", approval.role()));
        LOGGER.info(String.format("Approval Required: %s", approval.required()));
        LOGGER.info(String.format("Bean Called: %s", bean.getClass().getName()));
        LOGGER.info(String.format("Method Called: %s", jp.getSignature().getName()));
    }
}
