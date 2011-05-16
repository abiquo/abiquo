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

package com.abiquo.abiserver.commands.impl;

import static com.abiquo.tracer.Enterprise.enterprise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.abicloudws.AbiCloudConstants;
import com.abiquo.abiserver.abicloudws.IInfrastructureWS;
import com.abiquo.abiserver.abicloudws.IVirtualApplianceWS;
import com.abiquo.abiserver.abicloudws.InfrastructureWS;
import com.abiquo.abiserver.abicloudws.VirtualApplianceWS;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.BundleCommand;
import com.abiquo.abiserver.exception.BundleException;
import com.abiquo.abiserver.exception.InfrastructureCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.NodeVirtualImageDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.RepositoryDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.UserInfo;
import com.abiquo.tracer.client.TracerFactory;
import com.abiquo.util.resources.ResourceManager;

public class BundleCommandImpl extends BasicCommand implements BundleCommand
{
    private static Logger logger = LoggerFactory.getLogger(BundleCommandImpl.class);

    private IVirtualApplianceWS virtualApplianceWs;

    private IInfrastructureWS infrastructureWS;

    private final static String OVF_BUNDLE_PATH_IDENTIFIER = "-snapshot-";

    protected HibernateDAOFactory factory;

    private UserInfo user;

    private Platform platform;

    public BundleCommandImpl()
    {
        factory = (HibernateDAOFactory) HibernateDAOFactory.instance();
        resourceManager = new ResourceManager(BundleCommandImpl.class);

        try
        {
            virtualApplianceWs =
                (IVirtualApplianceWS) Thread.currentThread().getContextClassLoader().loadClass(
                    "com.abiquo.abiserver.abicloudws.VirtualApplianceWSPremium").newInstance();

            infrastructureWS =
                (IInfrastructureWS) Thread.currentThread().getContextClassLoader().loadClass(
                    "com.abiquo.abiserver.abicloudws.InfrastructureWSPremium").newInstance();
        }
        catch (Exception e)
        {
            virtualApplianceWs = new VirtualApplianceWS();
            infrastructureWS = new InfrastructureWS();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.BundleComman#bundleVirtualAppliance(com.abiquo.abiserver.pojo
     * .authentication.UserSession, com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance,
     * java.util.ArrayList, java.lang.Boolean)
     */
    @Override
    public DataResult<VirtualAppliance> bundleVirtualAppliance(final UserSession userSession,
        final VirtualAppliance va, final ArrayList<Node> nodes)
    {
        Collection<Integer> nodeIds =
            CollectionUtils.collect(nodes, InvokerTransformer.getInstance("getId"));

        VirtualAppliance bundle = null;

        user =
            new UserInfo(userSession.getUser(), new Long(userSession.getId()), userSession
                .getEnterpriseName());

        platform =
            Platform.platform("abicloud").enterprise(
                enterprise(user.getEnterprise()).virtualDatacenter(
                    com.abiquo.tracer.VirtualDatacenter.virtualDatacenter(
                        va.getVirtualDataCenter().getName()).virtualAppliance(
                        com.abiquo.tracer.VirtualAppliance.virtualAppliance(va.getName()))));

        try
        {

            bundle = bundleVirtualAppliance(va.getId(), nodeIds, userSession.getUser());
        }
        catch (BundleException e)
        {
            factory.rollbackConnection();
            traceBundleError("Error bundling VirtualAppliance (" + va.getId() + ") "
                + e.getMessage());

            updateVirtualAppliance(va.toPojoHB(), e.getPreviousState(), e.getPreviousState());
            return reportBundleError(va, e.getMessage(), e.getMessage(), e);
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            traceBundleError("Error bundling VirtualAppliance: " + va.getId());

            State notDeployed = new State(StateEnum.NOT_DEPLOYED);
            updateVirtualAppliance(va.toPojoHB(), notDeployed, notDeployed);

            return reportBundleError(va, "bundleVirtualAppliance.databaseError", e.getMessage(), e);
        }

        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();
        dataResult.setData(bundle);
        dataResult.setSuccess(true);
        dataResult.setMessage(resourceManager.getMessage("bundleVirtualAppliance.succes"));

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.BundleComman#bundleVirtualAppliance(int,
     * java.util.Collection, java.lang.String, boolean)
     */
    @Override
    public VirtualAppliance bundleVirtualAppliance(final int idVirtualApp,
        final Collection<Integer> nodeIds, final String userName) throws BundleException
    {
        //        
        // // Block the virtual appliance
        factory.beginConnection();

        VirtualApplianceDAO virtualappDAO = factory.getVirtualApplianceDAO();
        UserDAO userDAO = factory.getUserDAO();
        UserHB user = userDAO.getUserByUserName(userName);
        int enterpriseId = user.getEnterpriseHB().getIdEnterprise();

        VirtualappHB virtualApp = virtualappDAO.findByIdNamedExtended(idVirtualApp);
        virtualApp = virtualappDAO.blockVirtualAppliance(virtualApp, StateEnum.BUNDLING);

        factory.endConnection();

        // Start!
        checkTransaction();

        virtualappDAO = factory.getVirtualApplianceDAO();
        virtualApp = virtualappDAO.findByIdNamedExtended(idVirtualApp);

        try
        {
            uploadNotManagedMachines(virtualApp, getNodes(nodeIds));
        }
        catch (BundleException e)
        {
            throw new BundleException(e.getMessage(), new State(StateEnum.RUNNING));
        }

        checkTransaction();

        // Power off all selected nodes
        if (!powerOffNodes(getNodes(nodeIds)))
        {
            throw new BundleException("bundleVirtualAppliance.powerOffError",
                new State(StateEnum.RUNNING));
        }

        factory.endConnection();

        checkTransaction();

        Collection<Node> nodes = prepareNodesToBundle(nodeIds, enterpriseId);

        VirtualAppliance virtualAppPojo = virtualApp.toPojo();
        virtualAppPojo.setNodes(nodes);

        if (!nodes.isEmpty())
        {
            virtualApp.setNodesHB(CollectionUtils.collect(nodes, InvokerTransformer
                .getInstance("toPojoHB")));
        }

        boolean completed = completeBundleProcess(virtualAppPojo);

        checkTransaction();

        // Power on the bundled nodes
        if (!powerOnNodes(getNodes(CollectionUtils.collect(nodes, InvokerTransformer
            .getInstance("getId")))))
        {
            throw new BundleException("bundleVirtualAppliance.powerOnError",
                new State(StateEnum.RUNNING));
        }

        if (!completed)
        {
            throw new BundleException("bundleVirtualAppliance", new State(StateEnum.RUNNING));
        }

        factory.endConnection();

        return manageImagesAndUpdateAppliance(virtualApp, nodes, enterpriseId, StateEnum.RUNNING,
            StateEnum.RUNNING);
    }

    protected VirtualAppliance manageImagesAndUpdateAppliance(VirtualappHB virtualApp,
        final Collection<Node> nodes, final int idEnterprise, final StateEnum state,
        final StateEnum subState) throws PersistenceException
    {
        factory.beginConnection();

        for (Node node : nodes)
        {
            NodeVirtualImageHB nodeVirtualImageHB = (NodeVirtualImageHB) node.toPojoHB();
            insertBundleVirtualImage((NodeVirtualImage) node, idEnterprise);
        }

        factory.endConnection();

        virtualApp = updateVirtualAppliance(virtualApp, new State(state), new State(subState));

        return virtualApp.toPojo();
    }

    private boolean powerOffNodes(final Collection<NodeVirtualImageHB> nodes)
    {
        Set<NodeVirtualImageHB> modifieds = new HashSet<NodeVirtualImageHB>();

        for (NodeVirtualImageHB node : nodes)
        {
            if (!powerOffVirtualMachine(node.getVirtualMachineHB()))
            {
                break;
            }

            modifieds.add(node);
        }

        if (modifieds.size() != nodes.size())
        {
            logger.debug("Not all nodes have been powered off. Rollback to power on.");

            for (NodeVirtualImageHB node : modifieds)
            {
                powerOnVirtualMachine(node.getVirtualMachineHB());
            }

            return false;
        }

        return true;
    }

    private boolean powerOffVirtualMachine(final VirtualmachineHB virtualMachine)
    {
        BasicResult poweredOff = new BasicResult();
        poweredOff.setSuccess(false);

        try
        {
            int id = virtualMachine.getIdVm();
            String name = virtualMachine.getName();

            logger.debug(String.format("Powering off the virtual machine %d (%s)", id, name));

            poweredOff =
                infrastructureWS.setVirtualMachineState(virtualMachine.toPojo(),
                    AbiCloudConstants.POWERDOWN_ACTION);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }

        return poweredOff.getSuccess().equals(Boolean.TRUE);
    }

    private boolean powerOnNodes(final Collection<NodeVirtualImageHB> nodes)
    {
        boolean allPoweredOn = true;

        for (NodeVirtualImageHB node : nodes)
        {
            if (!powerOnVirtualMachine(node.getVirtualMachineHB()))
            {
                allPoweredOn = false;
            }
        }

        return allPoweredOn;
    }

    private boolean powerOnVirtualMachine(final VirtualmachineHB virtualMachine)
    {
        BasicResult poweredOn = new BasicResult();
        poweredOn.setSuccess(false);

        try
        {
            int id = virtualMachine.getIdVm();
            String name = virtualMachine.getName();

            logger.debug(String.format("Powering on the virtual machine %d (%s)", id, name));

            poweredOn =
                infrastructureWS.setVirtualMachineState(virtualMachine.toPojo(),
                    AbiCloudConstants.POWERUP_ACTION);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }

        return poweredOn.getSuccess().equals(Boolean.TRUE);
    }

    protected VirtualappHB updateVirtualAppliance(final VirtualappHB virtualApp, final State state,
        final State subState)
    {
        try
        {
            factory.beginConnection();
            VirtualApplianceDAO virtualappDAO = factory.getVirtualApplianceDAO();

            virtualApp.setState(state.toEnum());
            virtualApp.setSubState(subState.toEnum());

            virtualappDAO.makePersistentBasic(virtualApp);
            factory.endConnection();

        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            logger.error(e.getMessage(), e);
        }

        return virtualApp;
    }

    private Collection<NodeVirtualImageHB> getNodes(final Collection<Integer> nodeIds)
    {

        Collection<NodeVirtualImageHB> nodes = null;

        if (nodeIds.isEmpty())
        {
            nodes = new ArrayList<NodeVirtualImageHB>();
        }
        else
        {
            nodes = factory.getNodeVirtualImageDAO().getNodes(nodeIds);
        }

        return nodes;
    }

    protected Collection<Node> prepareNodesToBundle(final Collection<Integer> nodeIds,
        final int enterpriseId)
    {
        NodeVirtualImageDAO nodeDAO = factory.getNodeVirtualImageDAO();

        return (Collection) nodeDAO.getNodesDecorated(nodeIds);
    }

    private boolean completeBundleProcess(final VirtualAppliance virtualApp)
        throws PersistenceException
    {
        boolean done = true;

        try
        {
            if (!virtualApp.getNodes().isEmpty())
            {
                BasicResult result = virtualApplianceWs.bundleVirtualAppliance(virtualApp);
                done = result.getSuccess();
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            traceBundleError("Error bundling virtual pppliance " + virtualApp.getName() + "("
                + virtualApp.getId() + "). " + e.getMessage());
            done = false;
        }

        return done;
    }

    protected VirtualimageHB insertBundleVirtualImage(final NodeVirtualImage node,
        final int idEnterprise) throws PersistenceException
    {
        VirtualImageDAO virtualImageDAO = factory.getVirtualImageDAO();
        RepositoryDAO repositoryDAO = factory.getRepositoryDAO();
        VirtualApplianceDAO vappDao = factory.getVirtualApplianceDAO();

        VirtualappHB vapp = vappDao.findByIdNamedExtended(node.getIdVirtualAppliance());

        VirtualImage image = node.getVirtualImage();

        VirtualimageHB imageHB = image.toPojoHB();

        VirtualimageHB imageBundled = virtualImageDAO.findById(image.getId());
        imageBundled = imageBundled.bundle();

        if (!image.isManaged())
        {
            String realPath = imageHB.getPathName();

            if (!realPath.endsWith("/"))
            {
                realPath += "/";
            }

            realPath += imageHB.getName();
            imageBundled.setPathName(realPath);

            final Integer idDatacenter = vapp.getVirtualDataCenterHB().getIdDataCenter();
            imageBundled.setRepository(repositoryDAO.findByDatacenter(idDatacenter));
            imageBundled.setMaster(null);
        }

        imageBundled.setIdEnterprise(idEnterprise);
        imageBundled.setPathName(imageHB.getPathName());

        if (image.isManaged())
        {
            final String snapshot = getSnapshotFomPath(imageBundled.getPathName());
            final String ovfId = getMasterOvf(imageBundled);
            final String bundleName =
                createBundleName(imageBundled.getMaster().getName(), snapshot);
            final String bundleOvfid = createBundleOvfId(ovfId, snapshot);

            imageBundled.setOvfId(bundleOvfid);
            imageBundled.setName(bundleName);
        }

        virtualImageDAO.makePersistent(imageBundled);

        return imageBundled;
    }

    /**
     * Gets the OVF id of the master virtual image (if master is a bundle look its master)
     * 
     * @throws PersistenceException if the provided vi have not a master.
     */
    public static String getMasterOvf(final VirtualimageHB vi) throws PersistenceException
    {
        if (vi.getMaster() == null)
        {
            final String cause = String.format("Provided [%s] is not a bundle", vi.getName());
            throw new PersistenceException(cause);
        }

        if (vi.getMaster().getMaster() != null)
        {
            return vi.getMaster().getMaster().getOvfId();
        }
        else
        {
            return vi.getMaster().getOvfId();
        }

    }

    /**
     * Extract the bundle UUID from the virtual image path, e.g:
     * 
     * @param bundlePath,
     *            ubuntu-9.04/b4ee69c9-4e11-4fd0-8712-eb1aa539489e-snapshot-ubuntu-9.04-i386-
     *            sparse.vmdk
     * @return bundleUUID, b4ee69c9-4e11-4fd0-8712-eb1aa539489e
     */
    public static String getSnapshotFomPath(final String bundlePath)
    {
        return bundlePath.substring(bundlePath.lastIndexOf('/') + 1, bundlePath
            .indexOf(OVF_BUNDLE_PATH_IDENTIFIER));
    }

    /**
     * Creates the OVF id for a bundle virtual image (containing the bundle UUID and the bundle
     * snapshot mark)
     */
    public static String createBundleOvfId(final String ovfId, final String snapshot)
    {
        final String masterPre = ovfId.substring(0, ovfId.lastIndexOf('/') + 1);
        final String masterPost = ovfId.substring(ovfId.lastIndexOf('/') + 1, ovfId.length());

        final String bundleOvfId = masterPre + snapshot + OVF_BUNDLE_PATH_IDENTIFIER + masterPost;
        return bundleOvfId;
    }

    /**
     * Creates the bundle Name based on the parent name and the bundle UUID.
     */
    public static String createBundleName(final String name, final String snapshot)
    {
        return snapshot + OVF_BUNDLE_PATH_IDENTIFIER + name;
    }

    private void checkTransaction()
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Transaction tx = session.getTransaction();
        if (!tx.isActive())
        {
            tx = session.beginTransaction();
        }
    }

    private DataResult<VirtualAppliance> reportBundleError(final VirtualAppliance va,
        final String bundleName, final String message, final Exception e)
    {
        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();

        // if (e == null)
        // {
        errorManager.reportError(resourceManager, dataResult, bundleName, message);
        logger.error(message);
        // }
        // else
        // {
        // errorManager.reportError(resourceManager, dataResult, bundleName, e, message);
        // logger.error(message);
        // }

        dataResult.setData(va);

        return dataResult;
    }

    private void traceBundleError(final String message)
    {
        TracerFactory.getTracer().log(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
            EventType.VAPP_BUNDLE, message, user, platform);
    }

    private void uploadNotManagedMachines(final VirtualappHB vapp,
        final Collection<NodeVirtualImageHB> nodes) throws BundleException
    {
        String amServiceUri;

        try
        {
            Integer datacenterId = vapp.getVirtualDataCenterHB().getIdDataCenter();
            amServiceUri = getApplianceManagerAddress(datacenterId);
        }
        catch (Exception e)
        {
            final String cause =
                String.format("Error uploading not managed nodes " + "for VirtualAppliance [%s]",
                    vapp.getIdVirtualApp());

            logger.debug(cause);
            throw new BundleException("bundleVirtualAppliance.uploadingError");
        }

        for (NodeVirtualImageHB node : nodes)
        {
            VirtualmachineHB virtualMachine = node.getVirtualMachineHB();

            if (!node.isManaged())
            {
                VirtualimageHB image = virtualMachine.getImage();

                if (image.getType() == DiskFormatType.UNKNOWN)
                {
                    logger.debug("Unknown disk format for virtual image " + image.getName());
                    throw new BundleException("bundleVirtualAppliance.unknownFormatError");
                }

                final Integer idEnterprise = image.getIdEnterprise();
                final String name = image.getName();

                try
                {
                    ApplianceManagerResourceStubImpl amStub =
                        new ApplianceManagerResourceStubImpl(amServiceUri);

                    amStub.preBundleOVFPackage(String.valueOf(idEnterprise), name);
                }
                catch (Exception e)
                {
                    final String cause =
                        String.format("Error uploading not managed nodes "
                            + "for VirtualAppliance [%s]", vapp.getIdVirtualApp());

                    logger.debug(cause);
                    throw new BundleException("bundleVirtualAppliance.uploadingError");
                }
            }
        }
    }

    private String getApplianceManagerAddress(final Integer datacenterId)
        throws InfrastructureCommandException
    {
        List<RemoteServiceHB> remotes;

        try
        {
            factory.beginConnection();

            remotes =
                factory.getRemoteServiceDAO().getRemoteServicesByType(datacenterId,
                    RemoteServiceType.APPLIANCE_MANAGER);

            if (remotes == null || remotes.size() != 1)
            {
                final String cause =
                    String.format("The datacenter [%id] have any ApplianceManager "
                        + "configured as remote service", datacenterId);
                throw new InfrastructureCommandException(cause);
            }

            factory.endConnection();

        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause =
                String.format(
                    "Can not obtain the ApplianceManager remote service on Datacenter [%s]",
                    datacenterId);

            throw new InfrastructureCommandException(cause);
        }

        return remotes.get(0).getUri();
    }

}
