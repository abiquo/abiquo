package com.abiquo.api.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wink.server.handlers.HandlersChain;
import org.apache.wink.server.handlers.MessageContext;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.resources.cloud.VirtualDatacentersResource;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;

public class VirtualDatacenterEnterpriseSecurityRequestHandler extends SecurityRequestHandler
{

    private static String VIRTUAL_DATACENTER_ID_REGEX = "("
        + VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH + "/)(\\d+)";

    private static String VIRTUAL_DATACENTER_PATH_REGEX = VIRTUAL_DATACENTER_ID_REGEX
        + "((/{0})|(/{1}.*))";

    @Override
    public void handleRequest(final MessageContext context, final HandlersChain chain)
        throws Throwable
    {
        // check if path maches with 'cloud/virtualdatacenter/{id}*'
        String path = context.getUriInfo().getPath();
        if (path.matches(VIRTUAL_DATACENTER_PATH_REGEX))
        {
            // 1. get user from context
            User user = getUserService().getCurrentUser();

            // 2. check user privileges
            Enterprise enterprise = user.getEnterprise();
            getUserService().checkCurrentEnterpriseForPostMethods(enterprise);

            // 3. get and check vdc exists
            Pattern p = Pattern.compile(VIRTUAL_DATACENTER_ID_REGEX);
            Matcher m = p.matcher(path);
            // matcher ALLWAYS must find the vdc id in the second group (remember that group 0 is
            // the original string)
            m.find();
            Integer id = new Integer(m.group(2));
            VirtualDatacenter vdc = getVirtualDatacenterService().getVirtualDatacenter(id);

            // 4. check enterprise
            if (!enterprise.getId().equals(vdc.getEnterprise().getId()))
            {
                // trows hasn't enought permisions
                getUserService().getSecurityService().requirePrivilege(
                    Privileges.USERS_MANAGE_OTHER_ENTERPRISES);
            }

            // 5. check restricted vdc
            // when available vdcs list from user is null means that he hasn't restriction for any
            // vdcs
            boolean isAvailable = true;
            if (user.getAvailableVirtualDatacenters() != null
                && !user.getAvailableVirtualDatacenters().isEmpty())
            {
                isAvailable = false;
                for (String sId : user.getAvailableVirtualDatacenters().split(","))
                {
                    if (vdc.getId().equals(new Integer(sId)))
                    {
                        isAvailable = true;
                        break;
                    }
                }
            }

            if (!isAvailable)
            {
                // trhow restricted
                throw new ConflictException(APIError.USER_VDC_RESTRICTED);
            }
        }

        // finally
        chain.doChain(context);
    }
}
