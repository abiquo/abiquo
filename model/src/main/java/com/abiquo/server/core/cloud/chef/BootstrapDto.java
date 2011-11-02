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

package com.abiquo.server.core.cloud.chef;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "bootstrap")
@XmlType(propOrder = {"nodeName", "chefConfig"})
public class BootstrapDto implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String nodeName;

    private ChefBootstrap chefConfig;

    @XmlElement(name = "node")
    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(final String nodeName)
    {
        this.nodeName = nodeName;
    }

    @XmlElement(name = "chef")
    public ChefBootstrap getChefConfig()
    {
        return chefConfig;
    }

    public void setChefConfig(final ChefBootstrap chefConfig)
    {
        this.chefConfig = chefConfig;
    }

    @XmlRootElement(name = "chef")
    @XmlType(propOrder = {"chefServerURL", "validatorName", "validationCertificate", "runlist"})
    public static class ChefBootstrap implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private String chefServerURL;

        private String validatorName;

        private String validationCertificate;

        private RunlistDto runlist = new RunlistDto();

        @XmlElement(name = "chef-server-url")
        public String getChefServerURL()
        {
            return chefServerURL;
        }

        public void setChefServerURL(final String chefServerURL)
        {
            this.chefServerURL = chefServerURL;
        }

        @XmlElement(name = "validation-client-name")
        public String getValidatorName()
        {
            return validatorName;
        }

        public void setValidatorName(final String validatorName)
        {
            this.validatorName = validatorName;
        }

        @XmlElement(name = "validation-cert")
        public String getValidationCertificate()
        {
            return validationCertificate;
        }

        public void setValidationCertificate(final String validationCertificate)
        {
            this.validationCertificate = validationCertificate;
        }

        public RunlistDto getRunlist()
        {
            return runlist;
        }

        public void setRunlist(final RunlistDto runlist)
        {
            this.runlist = runlist;
        }

        @XmlRootElement(name = "runlist")
        public static class RunlistDto implements Serializable
        {
            private static final long serialVersionUID = 1L;

            private List<String> elements = new LinkedList<String>();

            public void add(final String element)
            {
                elements.add(element);
            }

            public int size()
            {
                return elements.size();
            }

            @XmlElement(name = "element")
            public List<String> getElements()
            {
                return elements;
            }

            public void setElements(final List<String> elements)
            {
                this.elements = elements;
            }
        }
    }

}
