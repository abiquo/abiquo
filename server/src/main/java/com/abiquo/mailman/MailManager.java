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

package com.abiquo.mailman;

import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.config.AbiConfig;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.exception.MailException;

/**
 * @author Diego Parrilla
 * @date 04-dic-2009
 * @version $Revision: 1.2 $
 */
public class MailManager
{

    /** debug. */
    private static final Logger logger = LoggerFactory.getLogger(MailManager.class);

    private static final String DEFAULT_TRANSPORT = "smtp";

    private static final String BAD_JNDI_ERROR = "Bad JNDI Error.";

    private static final String BAD_INTERNET_ADDRESS_ERROR = "Bad Internet Address error.";

    // private static final String BAD_MAIL_ADDRESS_ERROR = "Bad Mail Address error.";

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    private Session session_ = null;

    private String host_, username_, password_, encoding_;

    private Boolean isSmtpAuthentication = true;

    private Boolean isTls = true;

    private Boolean isSsl = true;

    private Integer sslPort = 443;

    private final static boolean STRICT_EMAIL_CHECK = true;

    /**
     * Creates an object that handles the connection to a MTA.
     * 
     * @param host mail host server (IP or hostname)
     * @param username login user name in the mail host server
     * @param password password of the user in the mail host server
     * @param encoding encoding type of the mails
     * @param isAuth use authentication to connect
     * @param isTls start a TLS conversation to connect
     * @param isSsl use SSL to connect
     * @param sslPort port used for SSL connection
     */
    protected MailManager(String host, String username, String password, String encoding,
        Boolean isAuth, Boolean isTls, Boolean isSsl, Integer sslPort)
    {
        this.host_ = host;
        this.username_ = username;
        this.password_ = password;
        this.encoding_ = encoding;
        this.isSmtpAuthentication = isAuth;
        this.isTls = isTls;
        this.isSsl = isSsl;
        this.sslPort = sslPort;
    }

    /**
     * Creates an object that handles the connection to a MTA.
     * 
     * @param host mail host server (IP or hostname)
     * @param username login user name in the mail host server
     * @param password password of the user in the mail host server
     * @param encoding encoding type of the mails
     * @param isAuth use authentication to connect
     * @param isTls start a TLS conversation to connect
     */
    protected MailManager(String host, String username, String password, String encoding,
        Boolean isAuth, Boolean isTls)
    {
        this.host_ = host;
        this.username_ = username;
        this.password_ = password;
        this.encoding_ = encoding;
        this.isSmtpAuthentication = isAuth;
        this.isTls = isTls;
        this.isSsl = false;
        this.sslPort = 443;
    }

    /**
     * Creates an object that handles the connection to a MTA.
     * 
     * @param host mail host server (IP or hostname)
     * @param username login user name in the mail host server
     * @param password password of the user in the mail host server
     * @param encoding encoding type of the mails
     * @param isAuth use authentication to connect
     */
    protected MailManager(String host, String username, String password, String encoding,
        Boolean isAuth)
    {
        this.host_ = host;
        this.username_ = username;
        this.password_ = password;
        this.encoding_ = encoding;
        this.isSmtpAuthentication = isAuth;
        this.isTls = false;
        this.isSsl = false;
        this.sslPort = 443;
    }

    /**
     * Creates an object that handles the connection to a MTA.
     * 
     * @param host mail host server (IP or hostname)
     * @param username login user name in the mail host server
     * @param password password of the user in the mail host server
     * @param encoding encoding type of the mails
     */
    protected MailManager(String host, String username, String password, String encoding)
    {
        this.host_ = host;
        this.username_ = username;
        this.password_ = password;
        this.encoding_ = encoding;
        this.isSmtpAuthentication = false;
        this.isTls = false;
        this.isSsl = false;
        this.sslPort = 443;
    }

    /**
     * Creates an object that handles the connection to a MTA. The emails sent are encoded by
     * default with UTF-8 encoding
     * 
     * @param host mail host server (IP or hostname)
     * @param username login user name in the mail host server
     * @param password password of the user in the mail host server
     */
    private MailManager(String host, String username, String password)
    {
        host_ = host;
        username_ = username;
        password_ = password;
        encoding_ = MailManager.DEFAULT_ENCODING;
        this.isSmtpAuthentication = false;
        this.isTls = false;
        this.isSsl = false;
        this.sslPort = 443;
    }

    /** Singleton isntance. */
    private static MailManager instance;

    /**
     * Instantiate the class using the configuration elements (SMTP account).
     */
    public static synchronized MailManager instance()
    {

        if (instance == null)
        {
            AbiConfig conf = AbiConfigManager.getInstance().getAbiConfig();

            logger.info("Created MailManager to work with [{}] using [{}]", conf.getMailServer(),
                conf.getMailUser());

            instance =
                new MailManager(conf.getMailServer(), conf.getMailUser(), conf.getMailPassword());
        }

        return instance;

    }

    /**
     * Sends a email TO & CC the lists given as parameters.
     * 
     * @param from Contains the e-mail of the sender
     * @param to List that contains the TO receivers of the mail
     * @param to List that contains the CC receivers of the mail
     * @param subject Contains the subject of the mail
     * @param body Contains the body of the mail
     * @return if null any error, otherwise an String containing some warning (some bad email
     *         address)
     */
    public String send(String from, Set<String> addressListTO, Set<String> addressListCC,
        String subject, String body) throws MailException
    {
        StringBuilder errorBuilder = new StringBuilder();

        try
        {
            // Creating the message
            MimeMessage myMessage = new MimeMessage(this.getMailSession());

            // Setting the parts of the message ////////////////////////////////
            // Setting the subject of the message. Uses the in parameter subject
            myMessage.setSubject(subject, encoding_);

            // Set from in the emails depending on the brand.
            if ((from != null) && !from.equals(""))
            {
                myMessage.setFrom(new InternetAddress(from, STRICT_EMAIL_CHECK));
            }

            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(body, encoding_);

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);

            myMessage.setContent(mp);

            // Setting the TO addresses of the receivers
            for (String addr : addressListTO)
            {
                try
                {

                    Address addressAux = new InternetAddress(addr, STRICT_EMAIL_CHECK);
                    myMessage.addRecipient(Message.RecipientType.TO, addressAux);

                }
                // capture any malformed address
                catch (AddressException addEx)
                {
                    final String cause =
                        String.format("Invalid email address TO [%s] caused by [%s]", addr, addEx
                            .getLocalizedMessage());
                    errorBuilder.append(cause);
                }
            }

            // Setting the CC addresses of the receivers
            if (addressListCC != null)
            {

                for (String addr : addressListCC)
                {
                    try
                    {

                        Address addressAux = new InternetAddress(addr, STRICT_EMAIL_CHECK);
                        myMessage.addRecipient(Message.RecipientType.CC, addressAux);
                    }
                    // capture any malformed address
                    catch (AddressException addEx)
                    {
                        final String cause =
                            String.format("Invalid email address CC [%s] caused by [%s]", addr, addEx
                                .getLocalizedMessage());
                        
                        errorBuilder.append(cause);
                    }
                }
            }

            // Send the message to receivers
            Transport tr = session_.getTransport(MailManager.DEFAULT_TRANSPORT);
            tr.connect(host_, username_, password_);
            myMessage.saveChanges(); // don't forget this
            tr.sendMessage(myMessage, myMessage.getAllRecipients());
            tr.close();

        }
        // catch (javax.mail.internet.AddressException exc)
        // {
        // throw new MailException(MailManager.BAD_MAIL_ADDRESS_ERROR, exc);
        // }
        catch (AddressException ae)
        {
            throw new MailException(MailManager.BAD_INTERNET_ADDRESS_ERROR, ae);
        }
        catch (javax.mail.MessagingException exc)
        {
            throw new MailException(MailManager.BAD_INTERNET_ADDRESS_ERROR, exc);
        }
        catch (javax.naming.NamingException exc)
        {
            throw new MailException(MailManager.BAD_JNDI_ERROR, exc);
        }

        return errorBuilder.toString();
    }

    /**
     * Sends a email with the parameter keys.
     * 
     * @exception AmpliaMarcoException
     * @param mailFrom - mail account from the mail is send
     * @param mailTo - recipients for send to. A String separated by commas
     * @param mailCc - recipients for send in copy. A String separated by commas
     * @param subject - subject of the mail
     * @param body - body of the mail
     * @return if null any error, otherwise an String containing some warning (some bad email
     *         address)
     */
    public String send(String mailFrom, String mailTo, String mailCc, String subject, String body)
        throws MailException
    {
        StringBuilder errorBuilder = new StringBuilder();

        try
        {
            // Creating the message
            MimeMessage myMessage = new MimeMessage(this.getMailSession());

            // Setting the subject of the message. Uses the in parameter subject
            myMessage.setSubject(subject, encoding_);

            if ((mailFrom != null) && (!mailFrom.equals("")))
            {
                Address myAddress = new InternetAddress(mailFrom, STRICT_EMAIL_CHECK);
                myMessage.setFrom(myAddress);
            }
            else
            {
                myMessage.setFrom();
            }

            // Setting the content of the message. Uses the in parameter body
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(body, encoding_);

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);

            myMessage.setContent(mp);

            try
            {

                // Setting the TO addresses of the receivers
                myMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo,
                    STRICT_EMAIL_CHECK));

            }
            // capture any malformed address
            catch (AddressException addEx)
            {
                final String cause =
                    String.format("Invalid email address TO [%s] caused by [%s]", mailTo, addEx
                        .getLocalizedMessage());
                
                errorBuilder.append(cause);
            }

            // Setting the CC addresses of the receivers
            if (mailCc != null)
            {
                try
                {
                    myMessage.addRecipients(Message.RecipientType.CC, InternetAddress.parse(mailCc,
                        STRICT_EMAIL_CHECK));
                }
                // capture any malformed address
                catch (AddressException addEx)
                {
                 
                    final String cause =
                        String.format("Invalid email address CC [%s] caused by [%s]", mailCc, addEx
                            .getLocalizedMessage());
                    errorBuilder.append(cause).append(addEx.getLocalizedMessage());
                }

            }

            // Send the message to receivers
            Transport tr = session_.getTransport(MailManager.DEFAULT_TRANSPORT);
            tr.connect(host_, username_, password_);
            myMessage.saveChanges(); // don't forget this
            tr.sendMessage(myMessage, myMessage.getAllRecipients());
            tr.close();

        }
        // catch (javax.mail.internet.AddressException exc)
        // {
        // throw new MailException(MailManager.BAD_MAIL_ADDRESS_ERROR, exc);
        // }
        catch (javax.mail.MessagingException exc)
        {
            throw new MailException(MailManager.BAD_INTERNET_ADDRESS_ERROR, exc);
        }
        catch (javax.naming.NamingException exc)
        {
            throw new MailException(MailManager.BAD_JNDI_ERROR, exc);
        }

        return errorBuilder.toString();
    }

    /**
     * Sends a email with the parameter keys.
     * 
     * @exception AmpliaMarcoException
     * @param mailFrom - mail account from the mail is send
     * @param mailTo - recipients for send to. A String separated by commas
     * @param subject - subject of the mail
     * @param body - body of the mail
     * @return if null any error, otherwise an String containing some warning (some bad email
     *         address)
     */
    public String send(String mailFrom, String mailTo, String subject, String body)
        throws MailException
    {
        return this.send(mailFrom, mailTo, null, subject, body);
    }

    /**
     * The value of mailFrom is obtained from the property "mail.user". Sends a email with the
     * parameter keys.
     * 
     * @exception AmpliaMarcoException
     * @param mailTo - recipients for send to. A String separated by commas
     * @param mailCc - recipients for send in copy. A String separated by commas
     * @param subject - subject of the mail
     * @param body - body of the mail
     * @return if null any error, otherwise an String containing some warning (some bad email
     *         address)
     */
    public String sendFromMailUser(String mailTo, String mailCc, String subject, String body)
        throws MailException
    {
        return this.send(null, mailTo, mailCc, subject, body);
    }

    /**
     * The value of mailFrom is obtained from the property "mail.user". Sends a email with the
     * parameter keys.
     * 
     * @exception AmpliaMarcoException
     * @param mailTo - recipients for send to. A String separated by commas
     * @param subject - subject of the mail
     * @param body - body of the mail
     * @return if null any error, otherwise an String containing some warning (some bad email
     *         address)
     */
    public String sendFromMailUser(String mailTo, String subject, String body) throws Exception
    {
        return this.sendFromMailUser(mailTo, null, subject, body);
    }

    private Session getMailSession() throws NamingException, MailException
    {

        if (session_ == null)
        {
            Properties props = new Properties();

            // Use the following if you need authentication
            props.put(MailManager.MAIL_SMTP_AUTH, isSmtpAuthentication.toString());

            // Use the following if you need to start TLS
            props.put("mail.smtp.starttls.enable", isTls.toString());

            // Use the following if you need SSL
            if (isSsl)
            {
                props.put("mail.smtp.socketFactory.port", sslPort.toString());
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
            }
            session_ = Session.getDefaultInstance(props, null);
        }
        return session_;
    }
}
