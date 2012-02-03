package com.abiquo.appliancemanager.web.servlet;

public class CheckServletTest extends CheckServlet
{

    private static final long serialVersionUID = -6251633108210372629L;

    @Override
    public boolean checkRepositoryMounted()
    {
        return true;
    }
}
