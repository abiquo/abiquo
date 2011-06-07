/*
 * Copyright (c) 2008-2011 Citrix Systems, Inc.
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.abiquo.xenserverapplet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.xmlrpc.XmlRpcException;

import com.citrix.xenserver.console.ConnectionListener;
import com.citrix.xenserver.console.ConsoleListener;
import com.citrix.xenserver.console.Main;
import com.citrix.xenserver.console.VNCControls;
import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Console;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.Types.BadServerResponse;
import com.xensource.xenapi.Types.XenAPIException;
import com.xensource.xenapi.VM.Record;

public class XenServerConsole extends JApplet implements ConnectionListener, ConsoleListener
{
    static final long serialVersionUID = 0;

    private Main main;

    private VNCControls controls;

    private JPanel background = new JPanel(new BorderLayout(), true);

    private JPanel errorPanel = new JPanel(true);

    private Thread t;

    public JTextArea console = new JTextArea();

    private int retries = 5;

    private boolean connecting = false;

    private boolean logOnConsole = false;

    private boolean hideCADButton = false;

    private JPanel passPanel = new JPanel(true);

    private JPasswordField passwordField;

    private JLabel passwordError;

    private Connection conn = null;

    private boolean connectError = false;

    private String serverIP;

    private String serverUser;

    private String serverPass;

    private String serverVMName;

    private String serverVMPass;

    private Long serverVMDom;

    private int appletWidth;

    private int appletHeight;

    @Override
    public void init()
    {
        try
        {
            // Get the console parameters from the client
            getConnetionParameters();

            // Set size off the applet
            setSize(getAppletWidth(), getAppletHeight());

            // Connect to the VM
            connect();

            // Add the password field to the applet
            if (serverVMPass != null && serverVMPass.length() > 0)
            {
                writeline("Virtual machine is password protected");
                addPasswordControls();
            }

            // Open the requested console
            openVMConsole();
        }
        catch (Exception e)
        {
            connectError = true;
            e.printStackTrace();
            writeline(e.getMessage());
            throw new RuntimeException("The applet could not be initialized.", e);
        }
    }

    @Override
    public void start()
    {
        if (connectError != true)
        {
            writeline("Starting...");
            main.connect();
        }
    }

    @Override
    public void stop()
    {
        writeline("Stopping...");
        if (main != null && main.stream_ != null && main.stream_.isConnected())
        {
            main.stream_.disconnect();
        }
    }

    @Override
    public void destroy()
    {
        writeline("Destroying...");
    }

    public void ConnectionClosed()
    {
        writeline("Connection closed");
        if (retries > 0)
        {

            controls.consolePanel.remove(main.canvas_);
            controls.consolePanel.setLayout(new BorderLayout());
            controls.consolePanel.add(errorPanel);
            controls.invalidate();
            controls.validate();
            controls.consolePanel.invalidate();
            controls.consolePanel.validate();

            t = new Thread(new Runnable()
            {
                public void run()
                {

                    writeline("Reconnecting in 5 seconds...");
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch (Exception e)
                    {
                        writeline(e.getMessage());
                    }
                    writeline("Retry ".concat(Integer.toString(6 - retries)).concat(" out of 5"));
                    main.connect();
                    retries--;
                };
            });
            t.start();
        }
    }

    public void ConnectionLost(final String reason)
    {
        if (main != null)
        {
            if (reason != null)
            {
                writeline("Connection lost: ".concat(reason));
            }
            else
            {
                writeline("Connection lost");
            }
            if (!connecting)
            {
                connecting = true;
                ConnectionClosed();
            }
        }
    }

    public void ConnectionMade()
    {
        controls.consolePanel.remove(errorPanel);
        controls.setupConsole();
        controls.consolePanel.getParent().repaint();
        controls.consolePanel.invalidate();
        controls.consolePanel.validate();
        main.canvas_.requestFocusInWindow();
        connecting = false;
        retries = 5;
    }

    public void ConnectionFailed(final String reason)
    {
        if (main != null)
        {
            writeline("Connection failed: ".concat(reason));

            connecting = true;
            ConnectionClosed();
        }
    }

    public void writeline(final String line)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                if (logOnConsole && line != null)
                {
                    console.append(line);
                    console.append("\n");
                }
                System.out.println(line);
            }
        });
    }

    /**
     * Get the console parameters from the client
     */
    private void getConnetionParameters()
    {
        setServerIP(getParameter("IP"));
        setServerUser(getParameter("USER"));
        setServerPass(getParameter("PASS"));
        setServerVMname(getParameter("NAME"));
        setServerVMPass(getParameter("VMPASS"));
        setAppletWidth(Integer.parseInt(getParameter("WIDTH")));
        setAppletHeight(Integer.parseInt(getParameter("HEIGHT")));
    }

    /**
     * Connect to the VM, using the client parameters
     * 
     * @throws BadServerResponse
     * @throws XenAPIException
     * @throws XmlRpcException
     * @throws MalformedURLException
     */
    private void connect() throws BadServerResponse, XenAPIException, XmlRpcException,
        MalformedURLException
    {
        writeline("Connecting to http://" + getServerIP() + "...");

        if (conn != null)
            disconnect();

        try
        {
            Connection tmpConn = new Connection(new URL("http://" + getServerIP()));
            Session.loginWithPassword(tmpConn, getServerUser(), new String(getServerPass()),
                APIVersion.latest().toString());
            conn = tmpConn;
        }
        catch (SecurityException e)
        {
            connectError = true;
        }
    }

    /**
     * Disconnect from the VM
     * 
     * @throws BadServerResponse
     * @throws XenAPIException
     * @throws XmlRpcException
     */
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

    /**
     * Open the requested console in the VM
     * 
     * @throws BadServerResponse
     * @throws XenAPIException
     * @throws XmlRpcException
     */
    private void openVMConsole() throws BadServerResponse, XenAPIException, XmlRpcException
    {
        try
        {
            writeline("Connecting to VM: " + getServerVMName() + " ...");

            // Get the VM matching the name given in parameters
            Set<VM> requestedVM = VM.getByNameLabel(conn, getServerVMName());

            if (requestedVM == null || requestedVM.isEmpty())
            {
                throw new Exception("VM " + getServerVMName() + " not found");
            }

            Iterator<VM> iteratorVM = requestedVM.iterator();

            // Get the requested console
            Set<Console> listConsoles = iteratorVM.next().getConsoles(conn);
            Console c = null;
            for (Console currentConsole : listConsoles)
            {
                c = currentConsole;
            }

            writeline("Session reference: " + conn.getSessionReference());
            writeline("Setting up terminal connection to " + c.getLocation(conn) + " for VM ");

            // Configure the console
            String[] args = new String[] {c.getLocation(conn), conn.getSessionReference(), "true"};

            logOnConsole = false;
            hideCADButton = false;
            writeline("");
            writeline("Loading UI...");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            writeline("Initializing...");
            this.setBackground(Color.white);
            writeline("Starting main...");
            main = new Main(args, this, this);
            writeline("Creating controls...");
            controls = new VNCControls(main, background, Color.white, !hideCADButton);
            writeline("Adding controls...");
            background.add(controls);
            this.add(background);

            errorPanel.setBackground(Color.white);
            errorPanel.setLayout(new BorderLayout());
            console.setBackground(Color.white);
            console.setEditable(false);
            JScrollPane areaScrollPane = new JScrollPane(console);
            areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            areaScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            errorPanel.add(areaScrollPane, BorderLayout.CENTER);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.err.println("Exception: " + ex.getMessage());
        }
    }

    private void addPasswordControls()
    {
        JLabel label = new JLabel("Virtual machine password: ");
        label.setLabelFor(passwordField);

        passwordError = new JLabel("Invalid password");
        passwordError.setForeground(Color.RED);
        passwordError.setVisible(false);

        passwordField = new JPasswordField(20);
        passwordField.setActionCommand("checkPassword");
        passwordField.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                if (event.getActionCommand().equals("checkPassword"))
                {
                    char[] input = passwordField.getPassword();
                    boolean isOk = String.valueOf(input).equals(serverVMPass);

                    passwordField.selectAll();

                    background.setVisible(isOk);
                    passPanel.setVisible(!isOk);
                    passwordError.setVisible(!isOk);

                    Arrays.fill(input, '0');
                }
            }
        });

        background.setVisible(false);

        passPanel.setBackground(Color.WHITE);
        passPanel.add(label);
        passPanel.add(passwordField);
        passPanel.add(passwordError);

        this.add(passPanel, BorderLayout.NORTH);
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

    public String getServerVMPass()
    {
        return serverVMPass;
    }

    public void setServerVMPass(final String serverVMPass)
    {
        this.serverVMPass = serverVMPass;
    }

    public void setServerVMDom(final Long serverVMDom)
    {
        this.serverVMDom = serverVMDom;
    }

    public Long getServerVMDom()
    {
        return serverVMDom;
    }

    public void setAppletWidth(final int appletWidth)
    {
        this.appletWidth = appletWidth;
    }

    public int getAppletWidth()
    {
        return appletWidth;
    }

    public void setAppletHeight(final int appletHeight)
    {
        this.appletHeight = appletHeight;
    }

    public int getAppletHeight()
    {
        return appletHeight;
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
