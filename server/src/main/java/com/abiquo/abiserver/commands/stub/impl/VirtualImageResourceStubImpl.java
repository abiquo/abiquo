package com.abiquo.abiserver.commands.stub.impl;

import static java.lang.String.valueOf;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.StateConversionEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualImageConversionsHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.VirtualImageResourceStub;
import com.abiquo.abiserver.exception.AppsLibraryCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.VirtualImageDto;
import com.abiquo.server.core.appslibrary.VirtualImagesDto;

public class VirtualImageResourceStubImpl extends AbstractAPIStub implements
    VirtualImageResourceStub
{

    public final static String VIRTUAL_IMAGE_GET_CATEGORY_QUERY_PARAM = "categoryId";

    public final static String VIRTUAL_IMAGE_GET_HYPERVISOR_COMATIBLE_QUERY_PARAM =
        "hypervisorTypeId";

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    @Override
    public DataResult<List<VirtualImage>> getVirtualImageByCategory(Integer idEnterprise,
        Integer datacenterId, Integer idCategory)
    {
        final Integer idHypervisorType = null;
        return getVirtualImageByCategoryAndHypervisorCompatible(idEnterprise, datacenterId,
            idCategory, idHypervisorType);
    }

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    @Override
    public DataResult<List<VirtualImage>> getVirtualImageByCategoryAndHypervisorCompatible(
        Integer idEnterprise, Integer datacenterId, Integer idCategory, Integer idHypervisorType)

    {
        final DataResult<List<VirtualImage>> result = new DataResult<List<VirtualImage>>();

        final String uri = createVirtualImagesLink(idEnterprise, datacenterId);
        Resource vimagesResource = resource(uri);

        if (idHypervisorType != null)
        {
            vimagesResource =
                vimagesResource.queryParam(VIRTUAL_IMAGE_GET_HYPERVISOR_COMATIBLE_QUERY_PARAM,
                    valueOf(idHypervisorType));

        }

        if (idCategory != null)
        {
            vimagesResource =
                vimagesResource.queryParam(VIRTUAL_IMAGE_GET_CATEGORY_QUERY_PARAM,
                    valueOf(idCategory));
        }

        ClientResponse response = vimagesResource.get();

        if (response.getStatusCode() / 200 == 1)
        {
            VirtualImagesDto images = response.getEntity(VirtualImagesDto.class);

            result.setSuccess(true);
            result.setData(transformToFlex(images));
        }
        else
        {
            populateErrors(response, result, "deleteNotManagedVirtualMachines");
        }

        return result;

    }

    private List<VirtualImage> transformToFlex(VirtualImagesDto images)
    {
        List<VirtualImage> vlst = new LinkedList<VirtualImage>();
        for (VirtualImageDto image : images.getCollection())
        {
            vlst.add(transformToFlex(image));
        }

        return vlst;
    }

    private VirtualImage transformToFlex(VirtualImageDto vi)
    {
        VirtualImage img = new VirtualImage();

        img.setId(vi.getId());
        img.setName(vi.getName());
        img.setDescription(vi.getDescription());
        img.setPath(vi.getPathName());
        img.setHdRequired(vi.getHdRequired());
        img.setRamRequired(vi.getRamRequired());
        img.setCpuRequired(vi.getCpuRequired());
        img.setShared(vi.isShared() ? 1 : 0);
        img.setStateful(vi.isShared() ? 1 : 0);
        img.setOvfId(getLink("ovfpackage", vi.getLinks()).getHref());
        img.setDiskFileSize(vi.getDiskFileSize());
        img.setCostCode(vi.getCostCode());
        img.setCategory(createCategoryFromLink(getLink("category", vi.getLinks())));
        img.setIcon(createIconFromLink(getLink("icon", vi.getLinks())));

        /**
         * TODO WIP checkpoint here
         */
        // private Repository repository;
        // private DiskFormatType diskFormatType;
        // private VirtualImage master;
        // private Integer idEnterprise;

        return img;
    }

    private Icon createIconFromLink(RESTLink link)
    {
        Icon i = new Icon();
        i.setId(Integer.valueOf(link.getHref().substring(link.getHref().lastIndexOf("/") + 1)));
        i.setPath(link.getTitle());
        i.setName("defaultIconName"); // TODO default
        return i;
    }

    private Category createCategoryFromLink(RESTLink link)
    {
        Category c = new Category();
        c.setId(Integer.valueOf(link.getHref().substring(link.getHref().lastIndexOf("/") + 1)));
        c.setName(link.getTitle());
        return c;
    }

    private RESTLink getLink(final String rel, List<RESTLink> links)
    {
        for (RESTLink link : links)
        {
            if (link.getRel().equalsIgnoreCase(rel))
            {
                return link;
            }
        }
        return null; // TODO check error. i guess could be null
    }

    /**
     * ###
     */
    public List<VirtualimageHB> XXXgetVirtualImageByCategory(final UserSession userSession,
        final Integer idEnterprise, final Integer idRepo, final Integer idCategory)
        throws AppsLibraryCommandException
    {
        // TODO check the userSession belongs to the same idEnterprise
        return getAvailableVirtualImages(idEnterprise, idRepo, idCategory);
    }

    private List<VirtualimageHB> getAvailableVirtualImages(final Integer idEnterprise,
        final Integer idRepository, final Integer idCategory) throws AppsLibraryCommandException
    {
        List<VirtualimageHB> virtualImages;
        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {
            factory.beginConnection();

            if (idCategory != null && idCategory != 0)
            {
                virtualImages =
                    factory.getVirtualImageDAO().getImagesByEnterpriseAndRepositoryAndCategory(
                        idEnterprise, idRepository, idCategory);
            }
            else
            {
                virtualImages =
                    factory.getVirtualImageDAO().getImagesByEnterpriseAndRepository(idEnterprise,
                        idRepository);
            }

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();
            final String cause =
                String.format("Can not obtain the list of available virtual images "
                    + "for Enterprise[id %s] on Repository[id %s]", idEnterprise, idRepository);
            throw new AppsLibraryCommandException(cause, e);
        }

        return virtualImages;
    }

    public List<VirtualImage> XXXgetVirtualImageByCategoryAndHypervisorCompatible(
        final UserSession userSession, final Integer idEnterprise, final Integer idRepository,
        final Integer idCategory, final Integer idHypervisorType)
        throws AppsLibraryCommandException
    {
        // TODO check the userSession belongs to the same idEnterprise
        final DAOFactory factory = HibernateDAOFactory.instance();
        final List<VirtualImage> virtualImages = new LinkedList<VirtualImage>();

        final HypervisorType hypervisorType = HypervisorType.fromId(idHypervisorType);

        final Collection<VirtualimageHB> virtualImagesHB =
            getAvailableVirtualImages(idEnterprise, idRepository, idCategory);

        try
        {
            factory.beginConnection();

            for (final VirtualimageHB virtualImageHB : virtualImagesHB)
            {
                if (isVirtualImageConvertedOrCompatible(virtualImageHB, hypervisorType))
                {
                    virtualImages.add(virtualImageHB.toPojo());
                }
            }

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause = "Can not obtain the list of compatible virtual images";
            throw new AppsLibraryCommandException(cause, e);
        }

        return virtualImages;
    }

    /**
     * Return true the virtual image format is compatible.
     * 
     * <pre>
     * Premium: if there virtual image conversions check FINISH state.
     * </pre>
     */
    private Boolean isVirtualImageConvertedOrCompatible(final VirtualimageHB vi,
        final HypervisorType hypervisorType)
    {

        final DAOFactory factory = HibernateDAOFactory.instance();
        final DiskFormatType virtualImageFormatType = vi.getType();

        if (hypervisorType.isCompatible(virtualImageFormatType))
        {
            return true;
        }

        final Collection<VirtualImageConversionsHB> conversions =
            factory.getVirtualImageConversionsDAO().getConversion(vi, hypervisorType.baseFormat);

        // the conversion do not exist
        if (conversions == null || conversions.size() == 0)
        {
            return false;
        }

        // Conversion is the *single* conversion of the desired format
        for (final VirtualImageConversionsHB conversion : conversions)
        {
            if (conversion.getState() != StateConversionEnum.FINISHED)
            {
                return false;
            }
            else if (hypervisorType.isCompatible(conversion.getTargetType()))
            {
                return true;
            }
        }

        return false;
    }

    private DiskFormatType getBaseDiskFormatType(final Integer idHypervisorType)
    {
        return HypervisorType.fromId(idHypervisorType).baseFormat;
    }

}
