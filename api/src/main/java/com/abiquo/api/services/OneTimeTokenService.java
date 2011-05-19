package com.abiquo.api.services;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.enterprise.EnterpriseRep;

/**
 * Provides the basic functionalities to operate one time token.
 * 
 * @author ssedano
 */
@Service
@Transactional(readOnly = true)
public class OneTimeTokenService extends DefaultApiService
{
    private Logger LOGGER = LoggerFactory.getLogger(OneTimeTokenService.class);

    @Autowired
    EnterpriseRep repo;

    /**
     * Generates a valid and persisted one time token to be used as an authentication header in http
     * petitions.
     */
    @Transactional(readOnly = false)
    public void generateOneTimeToken()
    {
        LOGGER.debug("generateOneTimeToken: generating token... ");
        String token = generateToken();

        repo.persistToken(token);
        LOGGER.debug("generateOneTimeToken: token generated!");
    }

    /**
     * Generates a pseudorandom string to be used as a token.
     * 
     * @return string.
     */
    private String generateToken()
    {
        Md5PasswordEncoder enconde = new Md5PasswordEncoder();

        enconde.setEncodeHashAsBase64(true);
        Long rawPass = new Date().getTime() * new Random().nextLong();
        String token = enconde.encodePassword(rawPass.toString(), null);
        return token;
    }
}
