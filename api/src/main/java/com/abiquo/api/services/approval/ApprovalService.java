package com.abiquo.api.services.approval;

import java.util.List;

import org.springframework.stereotype.Service;

import com.abiquo.api.services.DefaultApiService;
import com.abiquo.server.core.enterprise.Approval;

@Service
public class ApprovalService extends DefaultApiService
{
    public List<Approval> getApprovals()
    {
        // Implemented in premium
        return null;
    }
}
