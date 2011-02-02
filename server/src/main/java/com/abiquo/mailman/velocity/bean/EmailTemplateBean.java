/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.mailman.velocity.bean;

import java.io.Serializable;

/**
 * Email Template Bean
 */

public class EmailTemplateBean extends TemplateBean implements Serializable
{

    private String candidateName;

    private String candidateEmail;

    private String candidatePass;

    private String sender;

    private String url;

    private String branchName;

    private String branchPhone;

    private String branchPass;

    private String colleagueName;

    private String testName;

    private String expiredDate;

    private String AssignmentName;

    /**
     * @return
     */
    public String getAssignmentName()
    {
        return AssignmentName;
    }

    /**
     * @return
     */
    public String getBranchName()
    {
        return branchName;
    }

    /**
     * @return
     */
    public String getBranchPass()
    {
        return branchPass;
    }

    /**
     * @return
     */
    public String getBranchPhone()
    {
        return branchPhone;
    }

    /**
     * @return
     */
    public String getCandidateEmail()
    {
        return candidateEmail;
    }

    /**
     * @return
     */
    public String getCandidateName()
    {
        return candidateName;
    }

    /**
     * @return
     */
    public String getCandidatePass()
    {
        return candidatePass;
    }

    /**
     * @return
     */
    public String getColleagueName()
    {
        return colleagueName;
    }

    /**
     * @return
     */
    public String getExpiredDate()
    {
        return expiredDate;
    }

    /**
     * @return
     */
    public String getSender()
    {
        return sender;
    }

    /**
     * @return
     */
    public String getTestName()
    {
        return testName;
    }

    /**
     * @return
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @param string
     */
    public void setAssignmentName(String string)
    {
        AssignmentName = string;
    }

    /**
     * @param string
     */
    public void setBranchName(String string)
    {
        branchName = string;
    }

    /**
     * @param string
     */
    public void setBranchPass(String string)
    {
        branchPass = string;
    }

    /**
     * @param string
     */
    public void setBranchPhone(String string)
    {
        branchPhone = string;
    }

    /**
     * @param string
     */
    public void setCandidateEmail(String string)
    {
        candidateEmail = string;
    }

    /**
     * @param string
     */
    public void setCandidateName(String string)
    {
        candidateName = string;
    }

    /**
     * @param string
     */
    public void setCandidatePass(String string)
    {
        candidatePass = string;
    }

    /**
     * @param string
     */
    public void setColleagueName(String string)
    {
        colleagueName = string;
    }

    /**
     * @param string
     */
    public void setExpiredDate(String string)
    {
        expiredDate = string;
    }

    /**
     * @param string
     */
    public void setSender(String string)
    {
        sender = string;
    }

    /**
     * @param string
     */
    public void setTestName(String string)
    {
        testName = string;
    }

    /**
     * @param string
     */
    public void setUrl(String string)
    {
        url = string;
    }

}
