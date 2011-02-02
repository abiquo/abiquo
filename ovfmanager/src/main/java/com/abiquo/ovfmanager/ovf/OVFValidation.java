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

package com.abiquo.ovfmanager.ovf;

import java.io.File;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.environment._1.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class aims to assure the generated OVF documents are OVFv1 standard compliant. <b> This
 * methods take a full OVF package (envelope and environment) to check all the referenced properties
 * are somewhere defined, so construction process guarantees the OVF is an XML wellformed and schema
 * compliant document TODO <b> TODO : see clusule 10 Internationalization on the OVF specification
 * document. TODO: provide methods to check one specific configuration on the enviromnent.
 */
public class OVFValidation
{

    private final static Logger log = LoggerFactory.getLogger(OVFValidation.class);

    // TODO all checks
    public static boolean checkOVF(EnvelopeType envelope, EnvironmentType environment)
    {
        return false;
    }

    private static boolean checkBoundMessagesId()
    {
        // TODO fing on the Strings the id of all the
        return false;
    }

    private static boolean checkOVFProductPropertiesOnEnvironment(EnvelopeType envelope,
        EnvironmentType environment)
    {
        return false;
    }

    private static boolean checkFileReferences(EnvelopeType envelope, File ovfPackageDirectory)
    {
        // TODO ProductSectionIcon
        return false;
    }

}
