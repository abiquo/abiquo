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

package com.abiquo.appliancemanager.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abiquo.am.resources.handler.CheckRepositoryHandler;
import com.abiquo.am.services.TemplateConventions;
import com.abiquo.appliancemanager.config.AMConfiguration;

// TODO add authentication support
public class StaticRepositoryServlet extends HttpServlet
{
    private static final long serialVersionUID = 2983718598265271809L;

    protected String pathPrefix = AMConfiguration.getRepositoryPath();

    public static interface LookupResult
    {
        public void respondGet(HttpServletResponse resp) throws IOException;

        public void respondHead(HttpServletResponse resp);

        public long getLastModified();
    }

    public static class Error implements LookupResult
    {
        protected final int statusCode;

        protected final String message;

        public Error(final int statusCode, final String message)
        {
            this.statusCode = statusCode;
            this.message = message;
        }

        @Override
        public long getLastModified()
        {
            return -1;
        }

        @Override
        public void respondGet(final HttpServletResponse resp) throws IOException
        {
            resp.sendError(statusCode, message);
        }

        @Override
        public void respondHead(final HttpServletResponse resp)
        {
            throw new UnsupportedOperationException();
        }
    }

    public static class StaticFile implements LookupResult
    {
        protected final long lastModified;

        protected final String mimeType;

        protected final int contentLength;

        protected final boolean acceptsDeflate;

        protected final URL url;

        public StaticFile(final long lastModified, final String mimeType, final int contentLength,
            final boolean acceptsDeflate, final URL url)
        {
            this.lastModified = lastModified;
            this.mimeType = mimeType;
            this.contentLength = contentLength;
            this.acceptsDeflate = acceptsDeflate;
            this.url = url;
        }

        @Override
        public long getLastModified()
        {
            return lastModified;
        }

        protected boolean willDeflate()
        {
            return acceptsDeflate && deflatable(mimeType) && contentLength >= deflateThreshold;
        }

        protected void setHeaders(final HttpServletResponse resp)
        {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(mimeType);
            if (contentLength >= 0 && !willDeflate())
            {
                resp.setContentLength(contentLength);
            }
        }

        @Override
        public void respondGet(final HttpServletResponse resp) throws IOException
        {
            setHeaders(resp);
            final OutputStream os;
            if (willDeflate())
            {
                resp.setHeader("Content-Encoding", "gzip");
                os = new GZIPOutputStream(resp.getOutputStream(), bufferSize);
            }
            else
            {
                os = resp.getOutputStream();
            }
            transferStreams(url.openStream(), os);
        }

        @Override
        public void respondHead(final HttpServletResponse resp)
        {
            if (willDeflate())
            {
                throw new UnsupportedOperationException();
            }
            setHeaders(resp);
        }
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
        throws IOException
    {
        lookup(req).respondGet(resp);
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp)
        throws IOException
    {
        doGet(req, resp);
    }

    @Override
    protected void doHead(final HttpServletRequest req, final HttpServletResponse resp)
        throws IOException, ServletException
    {
        try
        {
            lookup(req).respondHead(resp);
        }
        catch (UnsupportedOperationException e)
        {
            super.doHead(req, resp);
        }
    }

    @Override
    protected long getLastModified(final HttpServletRequest req)
    {
        return lookup(req).getLastModified();
    }

    protected LookupResult lookup(final HttpServletRequest req)
    {
        new CheckRepositoryHandler().canUseRepository();

        LookupResult r = (LookupResult) req.getAttribute("lookupResult");
        if (r == null)
        {
            r = lookupNoCache(req);
            req.setAttribute("lookupResult", r);
        }
        return r;
    }

    protected LookupResult lookupNoCache(final HttpServletRequest req)
    {
        String path = getPath(req);

        path = TemplateConventions.customEncode(path);
        path = path.replace("files/", "");

        // XXX if(isForbidden(path))
        // return new Error(HttpServletResponse.SC_FORBIDDEN, "Forbidden");

        final URL url;
        try
        {
            // XXX url = getServletContext().getResource(path); XXX
            url = new File(pathPrefix + path).toURI().toURL();

        }
        catch (MalformedURLException e)
        {
            return new Error(HttpServletResponse.SC_BAD_REQUEST, "Malformed path");
        }
        if (url == null)
        {
            return new Error(HttpServletResponse.SC_NOT_FOUND, "Not found");
        }

        final String mimeType = getMimeType(path);

        final String realpath = new File(pathPrefix + path).getAbsolutePath();
        // XXX getServletContext().getRealPath(path);
        if (realpath != null)
        {
            // Try as an ordinary file
            File f = new File(realpath);
            if (!f.isFile())
            {
                return new Error(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            }
            else
            {
                return new StaticFile(f.lastModified(),
                    mimeType,
                    (int) f.length(),
                    acceptsDeflate(req),
                    url);
            }
        }
        else
        {
            try
            {
                // Try as a JAR Entry
                final ZipEntry ze = ((JarURLConnection) url.openConnection()).getJarEntry();
                if (ze != null)
                {
                    if (ze.isDirectory())
                    {
                        return new Error(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                    }
                    else
                    {
                        return new StaticFile(ze.getTime(),
                            mimeType,
                            (int) ze.getSize(),
                            acceptsDeflate(req),
                            url);
                    }
                }
                else
                {
                    // Unexpected?
                    return new StaticFile(-1, mimeType, -1, acceptsDeflate(req), url);
                }
            }
            catch (ClassCastException e)
            {
                // Unknown resource type
                return new StaticFile(-1, mimeType, -1, acceptsDeflate(req), url);
            }
            catch (IOException e)
            {
                return new Error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error");
            }
        }
    }

    protected String getPath(final HttpServletRequest req)
    {
        String servletPath = req.getServletPath();
        String pathInfo = coalesce(req.getPathInfo(), "");
        return servletPath + pathInfo;
    }

    protected boolean isForbidden(final String path)
    {
        String lpath = path.toLowerCase();
        return lpath.startsWith("/web-inf/") || lpath.startsWith("/meta-inf/");
    }

    protected String getMimeType(final String path)
    {
        return "application/octet-stream";
        // XXX return coalesce(getServletContext().getMimeType(path), "application/octet-stream");
    }

    protected static boolean acceptsDeflate(final HttpServletRequest req)
    {
        final String ae = req.getHeader("Accept-Encoding");
        return ae != null && ae.contains("gzip");
    }

    protected static boolean deflatable(final String mimetype)
    {
        return mimetype.startsWith("text/") || mimetype.equals("application/postscript")
            || mimetype.startsWith("application/ms") || mimetype.startsWith("application/vnd")
            || mimetype.endsWith("xml");
    }

    protected static final int deflateThreshold = 4 * 1024;

    protected static final int bufferSize = 4 * 1024;

    protected static void transferStreams(final InputStream is, final OutputStream os)
        throws IOException
    {
        try
        {
            byte[] buf = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = is.read(buf)) != -1)
            {
                os.write(buf, 0, bytesRead);
            }
        }
        finally
        {
            is.close();
            os.close();
        }
    }

    public static <T> T coalesce(final T... ts)
    {
        for (T t : ts)
        {
            if (t != null)
            {
                return t;
            }
        }
        return null;
    }

}

// TODO this can not be getServletContext().getMimeType
//
// extends DefaultServlet
// {
// private static final long serialVersionUID = 5741560066901267141L;
//
// protected String pathPrefix = AMConfigurationManager.getInstance().getAMConfiguration()
// .getRepositoryPath();
//
//
//
// public void init(ServletConfig config) throws ServletException
// {
// super.init(config);
//
// // if (config.getInitParameter("pathPrefix") != null)
// // {
// // pathPrefix = config.getInitParameter("pathPrefix");
// // }
// }
//
// protected String getRelativePath(HttpServletRequest req)
// {
// String relativePath = super.getRelativePath(req);
// if(relativePath.startsWith("/"))
// {
// relativePath = relativePath.substring(1);
// }
//
// return pathPrefix + relativePath;
// }
// }
