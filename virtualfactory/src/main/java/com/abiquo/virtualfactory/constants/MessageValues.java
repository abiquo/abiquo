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

/**
 * Abiquo premium edition
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
package com.abiquo.virtualfactory.constants;

/**
 * Provide all String messages used for the module.
 * 
 * @author jdevesa
 */
public final class MessageValues
{
    /**
     * Message thrown by the <code>ConnectionException</code> causes. Used in discovery process in
     */
    public static final String CONN_EXCP_I = "Could not connect to the Hypervisor ";

    /**
     * Message thrown by the <code>ConnectionException</code> causes. Used in discovery process in
     */
    public static final String CONN_EXCP_II = "Could not disconnect from the Hypervisor ";

    /**
     * Message thrown by the operations.
     */
    public static final String OP_EXCP = "An error occurred when executing the operation: ";

    public static final String OP_EXCP_I = "An error occurred when POWERING ON the virtual machine";

    public static final String OP_EXCP_II =
        "An error occurred when POWERING OFF the virtual machine";

    public static final String OP_EXCP_III = "An error occurred when RESUMING the virtual machine";

    public static final String OP_EXCP_IV = "An error occurred when RESUMING the virtual machine";

    public static final String VM_NOT_FOUND = "The virtual machine was not found: ";

    /**
     * Message throws by the <code>ResourceNotFoundException</code> when we request a single virtual
     * system.
     */
    public static final String NOVS_EXCP = "No VirtualSystem found with the provisioned UUID";

    /**
     * Message thrown by the <code>CollectorException</code> causes.
     */
    public static final String COLL_EXCP_PH = "Could not get node's Physical Capabilities.";

    /**
     * Message thrown by the <code>CollectorException</code> causes.
     */
    public static final String COLL_EXCP_VM = "Could not get Virtual Machine information.";

    /**
     * This message will be retrieved in disconnect exceptions (strange cases).
     */
    public static final String COLL_EXCP_DC = "Unknown connection problems collecting information.";

    /**
     * This message will be show when wiseman throws an exception collecting the datastore size.
     */
    public static final String COLL_EXCP_WS =
        "Could not get datastore size due to an AIM exception";

    /**
     * The hypervisor version is not supported and nodecollector can not retrieve the information.
     */
    public static final String COLL_VER_NS = "Hypervisor version not supported";

    /**
     * This message is shown for the login exceptions.
     */
    public static final String LOG_EXCP = "Access denied. Check your access parameters";

    /**
     * Message thrown by the <code>UnprovisionedException</code>.
     */
    public static final String UNP_EXCP =
        "The machine does not respond. Maybe your IP is wrong or the machine is stopped.";

    /**
     * Message thrown by the <code>CollectorException</code>.
     */
    public static final String HYP_CONN_EXCP = "Could not connect to the Hypervisor.";

    /**
     * Message thrown by the <code>InvalidIPAddressException</code>.
     */
    public static final String INV_IP_EXCP = "Invalid IP address";

    /**
     * Message thrown if the query parameters are not correct.
     */
    public static final String INV_QRY_PRM = "Invalid query params";

    /**
     * Message thrown by the <code>NoManagedException</code> in the ESXi code.
     */
    public static final String NOMAN_NFS_I = "The machine does not have any NFS in its datastores";

    /**
     * Message thrown by the <code>NoManagedException</code> in the ESXi code.
     */
    public static final String NOMAN_NFS_II =
        "Repository location (NFS) is defined and matches up with the Datacenter repository, but is not mounted.";

    /**
     * Message thrown by the <code>NoManagedException</code> in the ESXi code.
     */
    public static final String NOMAN_NFS_III =
        "The Repositroy location (NFS) found does not match with the Datacenter repository";

    /**
     * Message thrown by the <code>NoManagedException</code> when the IP of the defined nfs is
     * unreachable.
     */
    public static final String NOMAN_NFS_IV =
        "The Repository (NFS) configured in Appliance Mangager of the current Datacenter is unreachable";

    /**
     * Message thrown by the <code>NoManagedException</code> when the IP of the nfs is unreachable.
     */
    public static final String NOMAN_NFS_V = "The mounted Repository (NFS) is unreachable";

    /**
     * Message thrown by the <code>NoManagedException</code> when the IP of the nfs is unreachable.
     */
    public static final String NOMAN_NFS_VI =
        "The Repository (NFS) is defined in hypervisor, but it is unplugged or broken and can not be used";

    /**
     * Message thrown by the <code>NoManagedException</code> in the ESXi code.
     */
    public static final String NOMAN_ESXI_LIC =
        "The ESXi license is not valid to be managed by Abiquo. Please check if the license has expired or if you are using an evaluation license";

    /**
     * Message thrown by the <code>NoManagedException</code> in the hyper-v code.
     */
    public static final String NOMAN_HYPERV_I =
        "The WinRM service is not properly configured and it is required to enable external storage features";

    /**
     * Message thrown by the <code>NoManagedException</code> in the XENServer code.
     */
    public static final String NOMAN_XEN_SERVER_I =
        "Abiquo Storage Repository is not properly configured and it is required to enable external storage features";

    /**
     * This exception will be thrown if the client tries to access in a non-managed uri.
     */
    public static final String UNCHK_NOT_FOUND = "Address not found. Check your request";

    /**
     * This message exception will be thrown if the client tries to access to a method that the
     * resource not supports.
     */
    public static final String UNCHK_NOT_ALLOWED = "Invalid method accessing resource";

    /**
     * Not treated errors.
     */
    public static final String UNCHK_UNK = "Unknown error";

    /**
     * Wsman exception.
     */
    public static final String WSMAN_NO_PING =
        "An error was occurred when connecting to the wsman service";

    /**
     * AIM exception.
     */
    public static final String AIM_NO_PING =
        "An error was occurred when connecting to the AIM service";

    public static final String AIM_NO_COMM =
        "An error was occurred when communicating with the AIM service";

    /**
     * Can not retrieve the iSCSI initiator IQN name. The but the host is MANAGED.
     */
    public static final String WARN_INITIATOR_IQN = "Can not retrieve the iSCSI initiator IQN name";

    /**
     * Private constructor due it is an utility class.
     */
    private MessageValues()
    {
    };

}
