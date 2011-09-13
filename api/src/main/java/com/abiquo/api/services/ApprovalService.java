package com.abiquo.api.services;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.tracer.TracerLogger;

@Service
@Transactional(readOnly = true)
public class ApprovalService extends DefaultApiService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ApprovalService.class);

    public ApprovalService()
    {

    }

    public ApprovalService(final EntityManager em)
    {
        tracer = new TracerLogger();
    }
}
