package com.abiquo.api.web.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.web.AbiquoHttpServletRequestWrapper;
import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * This class intercepts all the requests to the API and injects the proper version parameter to
 * content-negociation annotations.
 * 
 * @author jaume
 */
public class VersionCheckerFilter implements Filter
{

    /** Register the logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionCheckerFilter.class);

    /** List of released versions of abiquo API. */
    private static final List<String> releasedVersions = Arrays
        .asList(SingleResourceTransportDto.API_VERSION);

    @Override
    public void destroy()
    {
        LOGGER.info("VersionCheckerFilter destroyed");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
        final FilterChain chain) throws IOException, ServletException
    {
        /*
         * Define the ServlerWrapper: we need our own implementation since we need to override some
         * header values.
         */
        AbiquoHttpServletRequestWrapper req =
            new AbiquoHttpServletRequestWrapper((HttpServletRequest) request);
        HttpServletResponse res = (HttpServletResponse) response;

        /*
         * Get the "Accept" and "Content type" headers and append the version number if it is not
         * informed.
         */
        String accept = req.getHeader("Accept");
        if (accept != null)
        {
            MediaType acceptMediaType = MediaType.valueOf(accept);
            String version = acceptMediaType.getParameter("version");
            if (version == null)
            {
                accept += "; version=" + SingleResourceTransportDto.API_VERSION;
                req.setHeader("Accept", accept);
            }
            else
            {
                if (!releasedVersions.contains(version))
                {
                    res.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    res.getWriter().print(
                        createInvalidVersionNumberXmlError(APIError.STATUS_NOT_ACCEPTABLE_VERSION));
                    return;
                }
            }
        }

        String contentType = req.getHeader("Content-type");
        if (contentType != null)
        {
            MediaType contentMediaType = MediaType.valueOf(contentType);
            String version = contentMediaType.getParameter("version");
            if (version == null)
            {
                contentType += "; version=" + SingleResourceTransportDto.API_VERSION;
                req.setHeader("Content-type", contentType);
            }
            else
            {
                if (!releasedVersions.contains(version))
                {
                    res.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                    res.getWriter()
                        .print(
                            createInvalidVersionNumberXmlError(APIError.STATUS_UNSUPPORTED_MEDIA_TYPE_VERSION));
                    return;
                }
            }
        }

        chain.doFilter(req, response);
    }

    @Override
    public void init(final FilterConfig config) throws ServletException
    {
        LOGGER.info("VersionCheckerFilter loaded. Current API version is "
            + SingleResourceTransportDto.API_VERSION);
    }

    /**
     * We are outside WINK! Resource provider for XML is our hands... Invalid version number.
     * 
     * @return the string with the error.
     */
    private String createInvalidVersionNumberXmlError(final APIError error)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<errors>\n");
        builder.append("<error>\n");
        builder.append("<code>").append(error.getCode()).append("</code>\n");
        builder.append("<message>").append(error.getMessage()).append("</message>\n");
        builder.append("</error>\n");
        builder.append("</errors>");
        return builder.toString();
    }

}
