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

package com.abiquo.nodecollector.resource.test.todelete;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

public class UcsExperiment
{
    public static enum NN
    {
        NN, NNNN
    };

    public static void main(String[] args) throws Exception
    {
        HttpClient client = new HttpClient();
        String uri = "http://192.168.176.129/";
        PostMethod method = new PostMethod(uri);

        String xmlLogin = "<aaaLogin inName=\"config\" inPassword=\"config\" />";

        String auth = "1306249384/46404f3e-8d02-44e3-9ba6-692eda4bd016";
        String powerOff = shut(auth);
        String powerOn = powerOn(auth);

        String bladeDn = blade(auth);
        String subscribe = subscrib(auth);
        String chassis = chassis(auth);

        String infrastructure = infrastructure(auth);

        String bladesDn = bladesDn(auth);

        String reset = reset(auth);
        String ip = ips(auth);
        String byDn = getByDns(auth);
        String sub = subelements(auth);
        method.setPath("/nuova");
        try
        {
            System.out.println(client.executeMethod(method));
            // Create socket
            int port = 80;
            InetAddress addr = InetAddress.getByName("192.168.176.129");
            Socket sock = new Socket(addr, port);

            // Send header
            String path = "/nuova";
            BufferedWriter wr =
                new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
            // You can use "UTF8" for compatibility with the Microsoft virtual machine.
            wr.write("POST " + path + " HTTP/1.0\r\n");
            wr.write("Host: localhost\r\n");
            wr.write("Content-Length: " + powerOn.length() + "\r\n");
            wr.write("Content-Type: text/xml; charset=\"utf-8\"\r\n");
            wr.write("\r\n");

            // Send data
            wr.write(powerOn);
            wr.flush();

            // DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // factory.setNamespaceAware(false); // never forget this!
            // DocumentBuilder builder;
            // builder = factory.newDocumentBuilder();
            // Document doc = builder.parse(sock.getInputStream());
            // XPathFactory xpathFactory = XPathFactory.newInstance();
            // XPath xpath = xpathFactory.newXPath();
            // xpath.
            // Response
            BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null)
                System.out.println(line);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static String subelements(String auth)
    {
        return "<configResolveChildren cookie=\"" + auth
            + "\" inHierarchical=\"false\" inDn=\"sys/chassis-1/blade-1/mgmt\"/>";
    }

    private static String reset(String auth)
    {

        return "<configConfMos cookie=\"" + auth + "\" inHierarchical=\"no\">" + "<inConfigs>"
            + "<pair key=\"org-root/ls-14/power\">" + "<lsPower dn=\"org-root/ls-14/power\""
            + " state=\"hard-reset-immediate\" " + "status=\"modified\" >" + "</lsPower>"
            + "</pair>" + "</inConfigs>" + "</configConfMos>";
    }

    private static String getByDns(String auth)
    {
        return "<configResolveDns cookie=\"" + auth + "\" inHierarchical=\"false\">" + "<inDns> "
            + "<dn value=\"sys/chassis-1/blade-1/mgmt/If-2\" />" + "</inDns>"
            + "</configResolveDns>" + "";
    }

    private static String powerOn(String auth)
    {

        return "<configConfMos cookie=\"" + auth + "\" inHierarchical=\"no\">" + "<inConfigs>"
            + "<pair key=\"org-root/ls-14/power\">" + "<lsPower dn=\"org-root/ls-14/power\""
            + " state=\"up\" " + "status=\"modified\" >" + "</lsPower>" + "</pair>"
            + "</inConfigs>" + "</configConfMos>";
    }

    private static String infrastructure(String auth)
    {
        return "<configResolveClasses cookie=\""
            + auth
            + "\" inHierarchical=\"false\"><inIds><classId value=\"computeBlade\" /><classId value=\"equipmentChassis\"/></inIds></configResolveClasses>";
    }

    private static String subscrib(String auth)
    {
        String subscribe = "<eventSubscribe cookie=\"" + auth + "\">" + "</eventSubscribe>";
        return subscribe;
    }

    private static String blade(String auth)
    {
        return "<configResolveClass cookie=\"" + auth
            + "\" inHierarchical=\"false\" classId=\"computeBlade\"></configResolveClass>";
    }

    private static String shut(String auth)
    {
        return "<configConfMos cookie=\"" + auth + "\" inHierarchical=\"no\">" + "<inConfigs>"
            + "<pair key=\"org-root/ls-11/power\">" + "<lsPower dn=\"org-root/ls-11/power\""
            + " state=\"down\" " + "status=\"modified\" >" + "</lsPower>" + "</pair>"
            + "</inConfigs>" + "</configConfMos>";
    }

    private static String bladesDn(String auth)
    {
        return "<configFindDnsByClassId classId=\"computeBlade\" cookie=\"" + auth + "\" />";

    }

    private static String ips(String auth)
    {
        return "<configResolveChildren cookie=\"" + auth
            + "\" inHierarchical=\"false\" inDn=\"ip\"/>";
    }

    private static String chassis(String auth)
    {
        return "<configResolveClass " + "cookie=\"" + auth + "\" " + "inHierarchical=\"false\""
            + "classId=\"equipmentChassis\"/>";

    }

}
