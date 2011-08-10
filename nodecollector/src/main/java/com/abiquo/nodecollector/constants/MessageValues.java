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
package com.abiquo.nodecollector.constants;

/**
 * Provide all String messages used for the module.
 * 
 * @author jdevesa
 */
public final class MessageValues
{
    /**
     * Message thrown by the <code>ConnectionException</code> causes. Used in discovery process in
     * the {@link HypervisorCollectorProxy}.
     */
    public static final String CONN_EXCP_I =
        "Exception thrown trying to connect with the Hypervisor ";

    /**
     * Message thrown by the <code>ConnectionException</code> causes. Used in connect process in the
     * {@link HypervisorCollectorProxy}..
     */
    public static final String CONN_EXCP_II = "Can not connect with the defined Hypervisor ";

    /**
     * Message thrown by the <code>ConnectionException</code> causes. Internal error disconnecting.
     */
    public static final String CONN_EXCP_III = "Internal error disconnecting with the Hypervisor";

    /**
     * Message thrown by the <code>AIMException</code> causes. Error in connection with AIM.
     */
    public static final String CONN_EXCP_IV = "Can not connect with the AIM";

    /**
     * Message thrown by the <code>ResourceNotFoundException</code> causes.
     */
    public static final String NOHYP_EXCP = "No Hypervisors found in the given IP.";

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
    public static final String COLL_EXCP_DC = "Unknown connecting problems collecting information.";

    /**
     * This message will be show when wiseman throws an exception collecting the datastore size.
     */
    public static final String COLL_EXCP_WS = "Could not get datastore size due AIM exception";

    /**
     * This message will be shown when samba is not activated in the network interface.
     */
    public static final String COLL_EXCP_SMB =
        "The network interface in the host doesn't support Samba";

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
        "The machine doesn't respond. Maybe your IP is wrong or the machine is stopped.";

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
    public static final String NOMAN_NFS_I = "The machine doesn't have any NFS in its datastores";

    /**
     * Message thrown by the <code>NoManagedException</code> in the ESXi code.
     */
    public static final String NOMAN_NFS_II =
        "Repository location (NFS) is defined and matches up with the Datacenter repository, but is not mounted.";

    /**
     * Message thrown by the <code>NoManagedException</code> in the ESXi code.
     */
    public static final String NOMAN_NFS_III =
        "The Repositroy location (NFS) found doesn't match with the Datacenter repository";

    /**
     * Message thrown by the <code>NoManagedException</code> when the IP of the defined nfs is
     * unreachable.
     */
    public static final String NOMAN_NFS_IV =
        "The Repository (NFS) informed in Appliance Mangager of current Datacenter is unreachable";

    /**
     * Message thrown by the <code>NoManagedException</code> when the IP of the nfs is unreachable.
     */
    public static final String NOMAN_NFS_V = "The Repository (NFS) mounted is unreachable";

    /**
     * Message thrown by the <code>NoManagedException</code> when the IP of the nfs is unreachable.
     */
    public static final String NOMAN_NFS_VI =
        "The Repository (NFS) is defined in hypervisor, but it is unplugged or broken and can not be used";

    /**
     * Message thrown by the <code>NoManagedException</code> in the ESXi code.
     */
    public static final String NOMAN_ESXI_LIC =
        "The ESXi license is not valid to be managed by Abiquo. Please check if the license has expired or you are using an evaluation license";

    /**
     * Message thrown by the <code>NoManagedException</code> in the hyper-v code.
     */
    public static final String NOMAN_HYPERV_I =
        "The WinRM service is not properly configured. This service is needed to manage external storage";

    /**
     * Message thrown by the <code>NoManagedException</code> in the XenServer code.
     */
    public static final String NOMAN_XEN_SERVER_I = "Linux Guest support package is not installed";

    /**
     * This exception will be thrown if the client tries to access in a non-managed uri.
     */
    public static final String UNCHK_NOT_FOUND = "Address not found. Check your request";

    /**
     * This message exception will be thrown if the client tries to access to a method that the
     * resource not supports.
     */
    public static final String UNCHK_NOT_ALLOWED = "Correct resource with invalid method";

    /**
     * When using the vSphere client we use the provided IP as Host System name. This usually match.
     */
    public static final String HOST_SYSTEM_NOT_FOUND =
        "Can't get the Host System of the vSphere or multiple HostSystems are defined.";

    /**
     * When using the vSphere client we assume the underling datacenter is named as
     * ''ha-datacenter''.
     */
    public static final String DATACENTER_NOT_FOUND =
        "''ha-datacenter'' not found on the vSphere (the datacenter MUST be named ''ha-datacenter'')";

    /**
     * Any problem during the datastore identification.
     */
    public static final String DATASTRORE_MARK =
        "Can't localize or create the datastore folder mark (check all datastores are accessibles)";

    /**
     * There are more than a single folder mark on the datastore.
     */
    public static final String DATASTRORE_MULTIPLE_MARKS =
        "There are more than a single mark folder on the current datastore (remove all the ''datastoreuuid.XXX'' folders)";

    /**
     * Not treated errors.
     */
    public static final String UNCHK_UNK = "Unknown error";

    /**
     * Wsman exception.
     */
    public static final String WSMAN_NO_PING =
        "An error was occurred when pinging the wsman service";

    /**
     * AIM exception.
     */
    public static final String AIM_NO_PING = "An error was occurred when pinging the AIMservice";

    public static final String AIM_NO_COMM =
        "An error was occurred when communicating with the AIM service";

    /**
     * AIM collector exception.
     */
    public static final String AIM_CHECK = "AIM check do not pass for resource [%s] at [%s]";

    public static final String AIM_GET_DISK_SIZE =
        "AIM can not obtain the disk file size of [%s] at [%s]";

    public static final String AIM_ANY_DATASTORE = "There aren't any available datastore on [%s]";

    public static final String AIM_ANY_NETIFACE =
        "There aren't any available network interface on [%s]";

    /**
     * Missing Hypervisor paramter.
     */
    public static final String MISSING_HYPERVISOR = "Missing 'hyp' parameter";

    /**
     * When the value of the hypervisor is not valid.
     */
    public static final String UNKNOWN_HYPERVISOR = "Unknown 'hyp' parameter";

    /**
     * Missing 'user' parameter.
     */
    public static final String MISSING_USER = "Missing 'user' parameter";

    /**
     * Missing 'password' parameter.
     */
    public static final String MISSING_PASSWORD = "Missing 'password' parameter";

    /**
     * Missing 'repository' parameter.
     */
    public static final String MISSING_REPO = "Missing 'repository' parameter";

    /**
     * Missing 'uuid' parameter.
     */
    public static final String MISSING_UUID = "Missing 'uuid' parameter";

    /**
     * Missing 'port' parameter.
     */
    public static final String MISSING_AIMPORT = "Missing 'port' parameter for this Hypervisor";

    /**
     * Can not retrieve the iSCSI initiator IQN name. The but the host is MANAGED.
     */
    public static final String WARN_INITIATOR_IQN = "Can not retrieve the iSCSI initiator IQN name";

    /**
     * Can not load the plugin because is not loaded.
     */
    public static final String UNLOADED_PLUGIN =
        "Plugin for the hypervisor you request is not loaded";

    /**
     * The hypervisor doesn't contains the provided repository datastore.
     */
    public static final String NOT_MANAGED_REPOSITORY =
        "The NFS repository [%s] is not present on the target hypervisor";

    /**
     * The credentials are not valid to UCS.
     */
    public static final String UCS_LOGIN_ERROR = "Cannot authenticate against UCS";

    /**
     * The blade is not associated to a LS.
     */
    public static final String UCS_LS_ERROR =
        "Cannot change the state if the blade is not associated with any Logic Server";

    /**
     * Invalid blade DN.
     */
    public static final String UCS_BLADE_DN_ERROR = "Invalid blade dn";

    /**
     * The Logic Server is already associated to a LS.
     */
    public static final String UCS_LS_ASSOC_ERROR =
        "Cannot associate the Logic Server because it is already associated";

    /**
     * The Logic Server is already unassociated.
     */
    public static final String UCS_LS_UNASSOC_ERROR =
        "Cannot disassociate the Logic Server because it is already unassociated";

    /**
     * The Logic Server is wan not associated to a LS due to an unknown error.
     */
    public static final String UCS_LS_ASSOC_GEN_ERROR =
        "Cannot associate the Logic Server due to an unknown error. Take a look at the log for more info.";

    /**
     * The Logic Server was not unassociated due unknown error.
     */
    public static final String UCS_LS_UNASSOC_GEN_ERROR =
        "Cannot disassociate the Logic Server due to an unknown error";

    /**
     * The Logic Server does not exists.
     */

    public static final String UCS_LS_NO_EXISTS_ERROR = "The Logic Server does not exists";

    /**
     * The Organization does not exists.
     */
    public static final String UCS_ORGANIZATION_NO_EXISTS_ERROR =
        "The Organization does not exists";

    /** The Logic Server already exists. */
    public static final String UCS_LS_EXISTS_ERROR = "The Logic Server already exists";

    /**
     * The UCS could not be reached.
     */
    public static final String UCS_COMM_ERROR =
        "There was a problem with UCS. Review your network configuration and check that UCS Manager is working";

    /**
     * The Logic Server is not a template.
     */
    public static final String UCS_LS_NO_TEMPLATE_ERROR = "The Logic Server is not a template";

    /**
     * No instance.
     */
    public static final String UCS_LS_TEMPLATE_INSTANTIATION_ERROR =
        "Cannot instantiate the Logic Server due to an unknown error";

    /**
     * The Logic Server is already unassociated.
     */
    public static final String UCS_BLADE_ASSOC_ERROR =
        "Cannot associate the Blade because it is already associated";

    /**
     * The UCS XML could not be parsed.
     */
    public static final String UCS_XML_ERROR =
        "There was a problem with UCS response. Review the logs of the application for more information";

    /**
     * No assoc.
     */
    public static final String UCS_LS_ASSOCIATION_SEE_LOGS_ERROR =
        "Cannot associate the Logic Server due to an unknown error. Review the logs of the application for more information";

    /**
     * Private constructor due it is an utility class.
     */
    private MessageValues()
    {
    };

}
