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

package net.undf.abicloud.model
{
    import mx.collections.ArrayCollection;

    import net.undf.abicloud.business.managers.*;
    import net.undf.abicloud.business.managers.notification.NotificationManager;
    import net.undf.abicloud.business.managers.virtualimage.VirtualImageManager;

    /**
     * Class representing AbiCloud's Model.
     * It is a Singleton class, since only one instance of the application can exist
     *
     * AbiCloud's view can find here methods to access data and represent it.
     **/
    [Bindable]
    public class AbiCloudModel
    {

        /* ------------- Private atributes ------------- */
        //Unique instance of this class 
        private static var instance:AbiCloudModel;

        /* ------------- Constructor ------------- */
        //Since this class implements the Singleton pattern, we define a private constructor
        public function AbiCloudModel(access:Private)
        {
            if (access == null)
                throw Error("Unable to access the constructor of a Singleton class");
            else
            {
                appVersion = "";


                configurationManager = new ConfigurationManager();
                countdownManager = new CountdownManager();
                notificationManager = new NotificationManager();
                loginManager = new LoginManager();
                infrastructureManager = new InfrastructureManager();
                userManager = new UserManager();
                virtualApplianceManager = new VirtualApplianceManager();
                virtualImageManager = new VirtualImageManager();
                networkingManager = new NetworkingManager();
                meteringManager = new MeteringManager();
            }
        }

        public static function getInstance():AbiCloudModel
        {
            if (instance == null)
                instance = new AbiCloudModel(new Private());

            return instance;
        }


        /* ----------------- ABICLOUD'S MODEL ------------------- */

        public var appVersion:String;

        //Managers. These managers store data, and define methods to manipulate it

        //Application configuration
        public var configurationManager:ConfigurationManager;

        //Countdown manager
        public var countdownManager:CountdownManager;

        //Notification manager
        public var notificationManager:NotificationManager;

        //Login
        public var loginManager:LoginManager;

        //Infrastructure
        public var infrastructureManager:InfrastructureManager;

        //User
        public var userManager:UserManager;

        //Virtual Appliance
        public var virtualApplianceManager:VirtualApplianceManager;

        //Virtual Image
        public var virtualImageManager:VirtualImageManager;

        //Networking
        public var networkingManager:NetworkingManager;

        //Metering
        public var meteringManager:MeteringManager;

        //Others
        public static const GB_TO_BYTES:Number = 1073741824;

        public static const MB_TO_BYTES:Number = 1048576;

        public static const KB_TO_BYTES:Number = 1024;

        public static const GB_TO_MBYTES:Number = 1024;

        //When conversion = 1, means that unit is the default one, and is the unit to store the value in server
        //For example, HardDisk values are always stored in Bytes, so when we receive or send HD values from / to server,
        //must always be in Bytes
        public static const ramUnitsSelectorDP:Array = [ { label: 'MB', conversion: 1, maximum: 9999999, minimum: 1, stepSize: 1 },
                                                         { label: 'GB', conversion: 1024, maximum: 999, minimum: 1, stepSize: 1 } ];

        public static const hdUnitsSelectorDP:Array = [ { label: 'MB', conversion: 1048576, maximum: 999999999, minimum: 1, stepSize: 1 },
                                                        { label: 'GB', conversion: 1073741824, maximum: 999999, minimum: 1, stepSize: 1 } ];


        //Regular expression to validate URL and URI's
        public var urlPattern:RegExp = new RegExp("^(http|https|omapi|ftp|tcp):\/\/");
    }
}

/**
 * Inner class which restricts constructor access to Private
 */
class Private
{
}
