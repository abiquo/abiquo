<?xml version="1.0" encoding="UTF-8"?>
<%@ page import="javax.servlet.http.HttpUtils,java.util.Enumeration"%>
<params>

    <!-- Languages -->
    <param>
        <name>languages</name>
        <value>
            <languages>
                <language>
                    <name>English</name>
                    <value>en_US</value>
                </language>
                <language>
                    <name>Español</name>
                    <value>es_ES</value>
                </language>
                <language>
                    <name>日本語</name>
                    <value>ja_JP</value>
                </language>
                <language>
                    <name>中文</name>
                    <value>zh_ZH</value>
                </language>
                <language>
                    <name>Português</name>
                    <value>pt_PT</value>
                </language>
            </languages>
        </value>
    </param>

<param>
	<!-- A valid KEY provided by Google is required to run the built in Google Maps API.
			 To obtain a Google Maps Key please follow this link: http://code.google.com/apis/maps/signup.html -->
<name>GOOGLE_MAPS_KEY</name> <value>0</value>
</param>

<param>
	<!--  Time, in seconds, that applications waits Google Maps to load. After that, application considers that
			 Google Maps service is temporarily unavailable, and is not used -->
<name>GOOGLE_MAPS_LOAD_TIMEOUT</name> <value>10</value>
</param>

<param>
	<!-- Google Maps will be centered by default at this latitude value -->
<name>GOOGLE_MAPS_DEFAULT_LONGITUDE</name> <value>42</value>
</param>

<param>
	<!-- Google Maps will be centered by default at this longitude value -->
<name>GOOGLE_MAPS_DEFAULT_LATITUDE</name> <value>-96</value>
</param>

<param>
	<!-- Google Maps will be centered by default with this zoom level value -->
<name>GOOGLE_MAPS_DEFAULT_ZOOM</name> <value>4</value>
</param>

<param>
	<!-- Time interval in seconds -->
<name>INFRASTRUCTURE_UPDATE_INTERVAL</name> <value>30</value>
</param>

<param>
	<!-- Time interval in seconds -->
<name>VIRTUAL_APPLIANCES_UPDATE_INTERVAL</name> <value>30</value>
</param>

<param>
	<!-- Time interval in seconds -->
<name>VIRTUAL_APPLIANCE_DEPLOYING_UPDATE_INTERVAL</name> <value>5</value>
</param>

<param>
	<!-- Time interval in seconds -->
<name>OVFPACKAGES_DOWNLOADING_PROGRESS_UPDATE_INTERVAL</name> <value>10</value>
</param>

<param>
	<!-- Time interval in seconds -->
<name>VIRTUALIMAGE_UPLOAD_PROGRESS_UPDATE_INTERVAL</name> <value>10</value>
</param>

<param>
	<!--  Time interval in seconds -->
<name>METERING_UPDATE_INTERVAL</name> <value>10</value>
</param>

<param>
	<!-- Number of users per page that will appear in User Management -->
<name>NUMBER_USERS_PER_PAGE</name> <value>25</value>
</param>

<param>
	<!-- Number of enterprises per page that will appear in User Management -->
<name>NUMBER_ENTERPRISES_PER_PAGE</name> <value>25</value>
</param>

<param>
	<!-- Number entries that will appear when listing IP addresses in different parts of the application -->
<name>NUMBER_IP_ADDRESSES_PER_PAGE</name> <value>25</value>
</param>

<param>
	<!-- Set to 1 to use an secure amf channel (requires a valid and authenticated HTTPS service from the server) -->
<name>USE_SECURE_CHANNEL_LOGIN</name> <value>0</value>
</param>

<param>
	<!-- Fully qualified name or IP address
			 This parameter is used to test server reachability when a network problem happens  -->
<name>SERVER_ADDRESS</name> <value>127.0.0.1</value>
</param>

<param>
	<!-- A valid port number
			 This parameter is used to test server reachability when a network problem happens  -->
<name>SERVER_PORT</name> <value>80</value>
</param>

<param>
	<!-- Currently, only amf and amfsecure channel types are recognised -->
<name>channels</name> <value> <channels> <channel>
<id>my-amf</id> <type>amf</type> <endpoint>/server/messagebroker/amf</endpoint>
</channel> <channel> <id>my-secure-amf</id> <type>amfsecure</type> <endpoint>https://<%= request.getHeader("host") %>/server/messagebroker/amfsecure</endpoint>
</channel> </channels> </value>
</param>

<param>
	<!-- Set to 1 to show an Alert with the text found in Startup_Alert.txt file -->
<name>SHOW_START_UP_ALERT</name> <value>0</value>
</param>

<param>
	<!-- Allow (1) or deny (0) access to the 'Users' section -->
<name>ALLOW_USERS_ACCESS</name> <value>1</value>
</param>

<param>
	<!-- Allow (1) or deny (0) virtual machine remote access -->
<name>ALLOW_VM_REMOTE_ACCESS</name> <value>0</value>
</param>
<param>
	<!-- URL of Abiquo web page -->
<name>ABIQUO_URL</name> <value>http://www.abiquo.org</value>
</param>
<param>
	<!-- Automatic close browser/tab enable (1) or disable (0) when logout -->
<name>CLOSE_BROWSER_LOGOUT</name> <value>0</value>
</param>
</params>
