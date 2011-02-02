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

package net.undf.abicloud.vo.configuration
{
    import com.adobe.fileformats.vcard.Phone;

    [Bindable]
    [RemoteClass(alias="com.abiquo.heartbeat.shared.dto.RegisterDTO")]
    public class Registration
    {
        /* ------------- Public atributes ------------- */
        public var id:String;

        public var companyName:String = "";

        public var companyAddress:String = "";

        public var companyState:String = "";

        public var companyCountryCode:String = "";

        public var companySizeRevenue:String = "";

        public var companySizeEmployees:String = "";

        public var companyIndustry:String = "";

        public var contactTitle:String = "";

        public var contactName:String = "";

        public var contactEmail:String = "";

        public var contactPhone:String = "";

        public var subscribeDevelopmentNews:Boolean = true;

        public var subscribeCommercialNews:Boolean = true;

        public var allowCommercialContact:Boolean = true;



        /* ------------- Constructor ------------- */
        public function Registration()
        {
        }

    }
}