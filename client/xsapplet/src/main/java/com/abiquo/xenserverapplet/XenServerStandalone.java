package com.abiquo.xenserverapplet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import org.apache.xmlrpc.XmlRpcException;

import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Console;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.Types.BadServerResponse;
import com.xensource.xenapi.Types.XenAPIException;
import com.xensource.xenapi.VM.Record;

public class XenServerStandalone
{
    private static final long serialVersionUID = 1L;

    private Connection conn = null;

    private String serverIP;

    private String serverUser;

    private String serverPass;

    private String serverVMName;

    private Long serverVMDom;

    public XenServerStandalone()
    {
        try
        {
            getConnexionParameters();

            System.out.println("Connecting to " + getServerIP() + "...");
            connect();
            System.out.println("Connected to " + getServerIP() + " with success !");

            openVMConsole();
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
        }
    }

    public static void main(final String[] args)
    {
        XenServerStandalone stand = new XenServerStandalone();
    }

    /**
     * Get the console parameters from the client
     */
    private void getConnexionParameters()
    {
        setServerIP("http://10.60.1.77");
        setServerUser("root");
        setServerPass("temporal");
        setServerVMname("ABQ_341db6b5-5393-4601-8e7e-eb18dad725b0");
        setServerVMDom(265L);
    }

    private void connect() throws BadServerResponse, XenAPIException, XmlRpcException,
        MalformedURLException
    {
        if (conn != null)
            disconnect();
        Connection tmpConn = new Connection(new URL(getServerIP()));
        Session.loginWithPassword(tmpConn, getServerUser(), new String(getServerPass()), APIVersion
            .latest().toString());
        conn = tmpConn;
    }

    private void disconnect() throws BadServerResponse, XenAPIException, XmlRpcException
    {
        try
        {
            Session.logout(conn);
        }
        catch (XenAPIException e)
        {
            // Ignore
        }
        conn = null;
    }

    private void openVMConsole() throws BadServerResponse, XenAPIException, XmlRpcException
    {
        try
        {
            // Get the VM matching the name given in parameters
            Set<VM> requestedVM = VM.getByNameLabel(conn, getServerVMName());
            Iterator<VM> iteratorVM = requestedVM.iterator();

            Iterator<Console> iteratorConsoles = iteratorVM.next().getConsoles(conn).iterator();
            Console c = iteratorConsoles.next();

            System.out.println("Session reference: " + conn.getSessionReference());
            System.out.println("Setting up terminal connection to " + c.getLocation(conn)
                + " for VM ");

            // Open the requested console
            XenServerConsole frameConsole = new XenServerConsole();
            frameConsole.init();
            frameConsole.start();
            frameConsole.setSize(800, 600);
            frameConsole.setVisible(true);
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
        }
    }

    public void setServerIP(final String serverIP)
    {
        this.serverIP = serverIP;
    }

    public String getServerIP()
    {
        return serverIP;
    }

    public void setServerUser(final String serverUser)
    {
        this.serverUser = serverUser;
    }

    public String getServerUser()
    {
        return serverUser;
    }

    public void setServerPass(final String serverPass)
    {
        this.serverPass = serverPass;
    }

    public String getServerPass()
    {
        return serverPass;
    }

    public void setServerVMname(final String serverVM)
    {
        this.serverVMName = serverVM;
    }

    public String getServerVMName()
    {
        return serverVMName;
    }

    public void setServerVMDom(final Long serverVMDom)
    {
        this.serverVMDom = serverVMDom;
    }

    public Long getServerVMDom()
    {
        return serverVMDom;
    }

    static class VMEntry
    {
        Record record = null;

        public VMEntry(final Record record)
        {
            this.record = record;
        }

        @Override
        public String toString()
        {
            return "dom " + record.domid + ": " + record.nameLabel;
        }

    }
}
