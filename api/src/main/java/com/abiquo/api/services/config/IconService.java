package com.abiquo.api.services.config;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.config.Icon;
import com.abiquo.server.core.config.IconDAO;
import com.abiquo.server.core.config.IconDto;

@Service
public class IconService extends DefaultApiService
{

    @Autowired
    private IconDAO dao;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Collection<Icon> getIcons()
    {
        return dao.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Icon getIconByPath(final String path)
    {

        Icon icon = dao.findByPath(path);
        if (icon == null)
        {
            addNotFoundErrors(APIError.NON_EXISENT_ICON);
            flushErrors();
        }
        return icon;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Icon findById(final Integer iconId)
    {
        Icon icon = dao.findById(iconId);
        if (icon == null)
        {
            addNotFoundErrors(APIError.NON_EXISENT_ICON);
            flushErrors();
        }
        return icon;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Icon modifyIcon(final Integer iconId, final IconDto iconDto)
    {
        Icon old = dao.findById(iconId);
        if (old == null)
        {
            addNotFoundErrors(APIError.NON_EXISENT_ICON);
            flushErrors();
        }

        old.setName(iconDto.getName());
        old.setPath(iconDto.getPath());

        dao.update(old);

        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteIcon(final Integer iconId)
    {
        Icon icon = dao.findById(iconId);
        if (icon == null)
        {
            addNotFoundErrors(APIError.NON_EXISENT_ICON);
            flushErrors();
        }
        // if (dao.iconInUseByVirtualImages())
        // {
        // addConflictErrors(APIError.NON_EXISENT_ICON);
        // flushErrors();
        // }
        dao.remove(icon);
        dao.flush();

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Icon addIcon(final IconDto iconDto, final IRESTBuilder restBuilder)
    {
        Icon icon = dao.findByPath(iconDto.getPath());
        if (icon != null)
        {
            addConflictErrors(APIError.ICON_DUPLICATED_PATH);
            flushErrors();
        }

        Icon newIcon = new Icon(iconDto.getPath());
        newIcon.setName(iconDto.getName());
        dao.persist(newIcon);
        dao.flush();

        return newIcon;
    }

}
