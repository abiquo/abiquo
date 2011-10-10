package com.abiquo.am.services.util;

import java.io.File;
import java.io.FilenameFilter;

public class FormatsFilter implements FilenameFilter
{
    final String baseFile;

    public FormatsFilter(final String baseFile)
    {
        this.baseFile = baseFile;
    }

    @Override
    public boolean accept(final File dir, final String name)
    {
        return name.startsWith(baseFile);
    }
}