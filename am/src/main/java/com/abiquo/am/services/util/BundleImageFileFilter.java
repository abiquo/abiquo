package com.abiquo.am.services.util;

import static com.abiquo.am.services.OVFPackageConventions.OVF_BUNDLE_PATH_IDENTIFIER;

import java.io.File;
import java.io.FilenameFilter;

public class BundleImageFileFilter  implements FilenameFilter
{
    @Override
    public boolean accept(final File dir, final String name)
    {
        return name.contains(OVF_BUNDLE_PATH_IDENTIFIER);
    }
}