package com.abiquo.api.web.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

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
    /** The prefix for Abiquo custom media types. */
    private static final String ABIQUO_MIME_TYPE_PREFIX = "application/vnd.abiquo.";

    /** Register the logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionCheckerFilter.class);

    /** List of released versions of abiquo API. */
    private List<String> releasedVersions;

    @Override
    public void init(final FilterConfig config) throws ServletException
    {
        releasedVersions = new ArrayList<String>();
        releasedVersions.add(SingleResourceTransportDto.API_VERSION);

        LOGGER.info("VersionCheckerFilter loaded. Current API version is "
            + SingleResourceTransportDto.API_VERSION);
    }

    @Override
    public void destroy()
    {
        LOGGER.info("VersionCheckerFilter destroyed");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
        final FilterChain chain) throws IOException, ServletException
    {
        // We need to wrap the request to be able to modify the headers
        AbiquoHttpServletRequestWrapper req =
            new AbiquoHttpServletRequestWrapper((HttpServletRequest) request);
        HttpServletResponse res = (HttpServletResponse) response;

        try
        {
            appendVersionToHeader(req, res, HttpHeaders.ACCEPT);
            appendVersionToHeader(req, res, HttpHeaders.CONTENT_TYPE);
            chain.doFilter(req, response);
        }
        catch (UnsupportedVersionInHeaderException ex)
        {
            flushError(res, ex);
            return;
        }
    }

    /**
     * Append the current Abiquo version to the given header if necessary.
     * 
     * @param request The request.
     * @param response The response.
     * @param header The header to parse.
     * @throws UnsupportedVersionInHeaderException If the header already has a version but it is not
     *             supported by Abiquo.
     */
    private void appendVersionToHeader(final AbiquoHttpServletRequestWrapper request,
        final HttpServletResponse response, final String header)
        throws UnsupportedVersionInHeaderException
    {
        String contentType = request.getHeader(header);
        if (isAbiquoMimeType(contentType))
        {
            MediaType contentMediaType = MediaType.valueOf(contentType);
            String version = contentMediaType.getParameter("version");
            if (version == null)
            {
                contentType += ";version=" + SingleResourceTransportDto.API_VERSION;
                request.setHeader(header, contentType);
            }
            else
            {
                if (!releasedVersions.contains(version))
                {
                    throw new UnsupportedVersionInHeaderException(header);
                }
            }
        }
    }

    /**
     * Flush the given error to the response.
     * 
     * @param response The response.
     * @param exception The error.
     * @throws IOException If there is an error flushing the error to the output stream.
     */
    private void flushError(final HttpServletResponse response,
        final UnsupportedVersionInHeaderException exception) throws IOException
    {
        response.setStatus(exception.errorStatus);
        response.getWriter().print(createInvalidVersionNumberXmlError(exception.errorDetails));
        response.getWriter().flush();
    }

    /**
     * We are outside WINK! Resource provider for XML is our hands... Invalid version number.
     * 
     * @return the string with the error.
     */
    private String createInvalidVersionNumberXmlError(final APIError error)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<errors>");
        builder.append("<error>");
        builder.append("<code>").append(error.getCode()).append("</code>");
        builder.append("<message>").append(error.getMessage()).append("</message>");
        builder.append("</error>");
        builder.append("</errors>");
        return builder.toString();
    }

    /**
     * Check if the given mime type is a custom Abiquo mime type.
     * 
     * @param mimeType The mime type to check.
     * @return Boolean indicating if the given mime type is a custom Abiquo mime type.
     */
    private static boolean isAbiquoMimeType(final String mimeType)
    {
        return mimeType != null && mimeType.startsWith(ABIQUO_MIME_TYPE_PREFIX);
    }

    /**
     * Encapsulates invalid version error details.
     */
    private static class UnsupportedVersionInHeaderException extends Exception
    {
        private static final long serialVersionUID = 1L;

        /** The response error status code. */
        private int errorStatus;

        /** The details of the error. */
        private APIError errorDetails;

        public UnsupportedVersionInHeaderException(final String header)
        {
            if (header.equals(HttpHeaders.ACCEPT))
            {
                errorStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
                errorDetails = APIError.STATUS_NOT_ACCEPTABLE_VERSION;
            }
            else if (header.equals(HttpHeaders.CONTENT_TYPE))
            {
                errorStatus = HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
                errorDetails = APIError.STATUS_UNSUPPORTED_MEDIA_TYPE_VERSION;
            }
            else
            {
                throw new IllegalArgumentException("Unsupported header: " + header);
            }
        }

    }

}
