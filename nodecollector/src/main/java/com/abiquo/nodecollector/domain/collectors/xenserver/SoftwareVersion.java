package com.abiquo.nodecollector.domain.collectors.xenserver;

import java.util.Map;

import com.xensource.xenapi.Host;

/**
 * Details of the XenServer version.
 * 
 * @author Ignasi Barrera
 */
public class SoftwareVersion
{
    /** Software version properties. */
    private Map<String, String> props;

    public static SoftwareVersion of(final Host.Record host)
    {
        return new SoftwareVersion(host.softwareVersion);
    }

    private SoftwareVersion(final Map<String, String> props)
    {
        super();
        this.props = props;
    }

    public String productVersion()
    {
        String version = props.get("product_version_text");

        // XenServer 5 and previous version do not have the 'product_version_text' field
        if (version == null)
        {
            version = props.get("product_version");
        }

        return version;
    }

    public String rawProductVersion()
    {
        // In XenServer the "printable" version is stored in another field
        return props.get("product_version");
    }

    public boolean isVersion6OrGreater()
    {
        return Character.getNumericValue(productVersion().charAt(0)) >= 6;
    }

    public String buildNumber()
    {
        return props.get("build_number");
    }

    public boolean hasLinuxPack()
    {
        if (isVersion6OrGreater())
        {
            // XenServer 6 includes by default the Linux pack
            return true;
        }

        String linuxInstallStatus = props.get("package-linux");
        String linuxDetails = props.get("xs:linux");

        return linuxInstallStatus != null && linuxDetails != null
            && linuxInstallStatus.equalsIgnoreCase("installed");
    }

    public String xapiVersion()
    {
        return props.get("xapi");
    }

    public String xenVersion()
    {
        return props.get("xen");
    }
}
