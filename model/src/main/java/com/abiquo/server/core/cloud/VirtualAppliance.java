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

package com.abiquo.server.core.cloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualAppliance.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualAppliance.TABLE_NAME)
public class VirtualAppliance extends DefaultEntityBase
{
    public static final String TABLE_NAME = "virtualapp";

    protected VirtualAppliance()
    {
    }

    public VirtualAppliance(final Enterprise enterprise, final VirtualDatacenter virtualDatacenter,
        final String name, final VirtualApplianceState state)
    {
        setEnterprise(enterprise);
        setVirtualDatacenter(virtualDatacenter);
        setName(name);
        setState(state);
        setPublicApp(0);
        setError(0);
        setHighDisponibility(0);
    }

    private final static String ID_COLUMN = "idVirtualApp";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = false;

    private final static int NAME_LENGTH_MIN = 0;

    private final static int NAME_LENGTH_MAX = 255;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name;

    @Required(value = NAME_REQUIRED)
    @Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public final static String NODECONNECTIONS_PROPERTY = "nodeconnections";

    private final static boolean NODECONNECTIONS_REQUIRED = false;

    private final static boolean NODECONNECTIONS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NODECONNECTIONS_COLUMN = "nodeconnections";

    @Column(name = NODECONNECTIONS_COLUMN, nullable = !NODECONNECTIONS_REQUIRED, columnDefinition = "TEXT")
    private String nodeconnections;

    @Required(value = NODECONNECTIONS_REQUIRED)
    @LeadingOrTrailingWhitespace(allowed = NODECONNECTIONS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getNodeconnections()
    {
        return this.nodeconnections;
    }

    public void setNodeconnections(final String nodeconnections)
    {
        this.nodeconnections = nodeconnections;
    }

    public final static String PUBLIC_APP_PROPERTY = "publicApp";

    private final static String PUBLIC_APP_COLUMN = "public";

    private final static int PUBLIC_APP_MIN = Integer.MIN_VALUE;

    private final static int PUBLIC_APP_MAX = Integer.MAX_VALUE;

    @Column(name = PUBLIC_APP_COLUMN, nullable = true)
    @Range(min = PUBLIC_APP_MIN, max = PUBLIC_APP_MAX)
    private int publicApp;

    public int getPublicApp()
    {
        return this.publicApp;
    }

    public void setPublicApp(final int publicApp)
    {
        this.publicApp = publicApp;
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = false;

    private final static String ENTERPRISE_ID_COLUMN = "idEnterprise";

    @JoinColumn(name = ENTERPRISE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_enterprise")
    private Enterprise enterprise;

    @Required(value = ENTERPRISE_REQUIRED)
    public Enterprise getEnterprise()
    {
        return this.enterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public final static String VIRTUAL_DATACENTER_PROPERTY = "virtualDatacenter";

    private final static boolean VIRTUAL_DATACENTER_REQUIRED = true;

    private final static String VIRTUAL_DATACENTER_ID_COLUMN = "idVirtualDataCenter";

    @JoinColumn(name = VIRTUAL_DATACENTER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualDatacenter")
    private VirtualDatacenter virtualDatacenter;

    @Required(value = VIRTUAL_DATACENTER_REQUIRED)
    public VirtualDatacenter getVirtualDatacenter()
    {
        return this.virtualDatacenter;
    }

    public void setVirtualDatacenter(final VirtualDatacenter virtualDatacenter)
    {
        this.virtualDatacenter = virtualDatacenter;
    }

    public final static String HIGH_DISPONIBILITY_PROPERTY = "highDisponibility";

    private final static String HIGH_DISPONIBILITY_COLUMN = "high_disponibility";

    private final static int HIGH_DISPONIBILITY_MIN = Integer.MIN_VALUE;

    private final static int HIGH_DISPONIBILITY_MAX = Integer.MAX_VALUE;

    @Column(name = HIGH_DISPONIBILITY_COLUMN, nullable = true)
    @Range(min = HIGH_DISPONIBILITY_MIN, max = HIGH_DISPONIBILITY_MAX)
    private int highDisponibility;

    public int getHighDisponibility()
    {
        return this.highDisponibility;
    }

    public void setHighDisponibility(final int highDisponibility)
    {
        this.highDisponibility = highDisponibility;
    }

    public final static String ERROR_PROPERTY = "error";

    private final static String ERROR_COLUMN = "error";

    private final static int ERROR_MIN = Integer.MIN_VALUE;

    private final static int ERROR_MAX = Integer.MAX_VALUE;

    @Column(name = ERROR_COLUMN, nullable = true)
    @Range(min = ERROR_MIN, max = ERROR_MAX)
    private int error;

    public int getError()
    {
        return this.error;
    }

    private void setError(final int error)
    {
        this.error = error;
    }

    public final static String SUB_STATE_PROPERTY = "subState";

    private final static boolean SUB_STATE_REQUIRED = true;

    private final static String SUB_STATE_COLUMN = "subState";

    public final static String STATE_PROPERTY = "state";

    private final static boolean STATE_REQUIRED = true;

    private final static String STATE_COLUMN = "state";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = STATE_COLUMN, nullable = !STATE_REQUIRED)
    // @Formula(value =
    // "select case when exists (select virtualmac4_.state from virtualapp virtualapp1_ inner join node nodesvirtu2_ on virtualapp1_.idVirtualApp=nodesvirtu2_.idVirtualApp cross join nodevirtualimage nodevirtua3_ inner join node nodevirtua3_1_ on nodevirtua3_.idNode=nodevirtua3_1_.idNode inner join virtualmachine virtualmac4_ on nodevirtua3_.idVM=virtualmac4_.idVM where virtualapp1_.idVirtualApp=4 and nodesvirtu2_.idNode=nodevirtua3_.idNode and virtualmac4_.state='UNKNOWN') then 'UNKNOWN' when exists (select virtualmac8_.state from virtualapp virtualapp5_ inner join node nodesvirtu6_ on virtualapp5_.idVirtualApp=nodesvirtu6_.idVirtualApp cross join nodevirtualimage nodevirtua7_ inner join node nodevirtua7_1_ on nodevirtua7_.idNode=nodevirtua7_1_.idNode inner join virtualmachine virtualmac8_ on nodevirtua7_.idVM=virtualmac8_.idVM where virtualapp5_.idVirtualApp=4 and nodesvirtu6_.idNode=nodevirtua7_.idNode and virtualmac8_.state='LOCKED') then 'LOCKED' when not (exists (select virtualmac12_.state from virtualapp virtualapp9_ inner join node nodesvirtu10_ on virtualapp9_.idVirtualApp=nodesvirtu10_.idVirtualApp cross join nodevirtualimage nodevirtua11_ inner join node nodevirtua11_1_ on nodevirtua11_.idNode=nodevirtua11_1_.idNode inner join virtualmachine virtualmac12_ on nodevirtua11_.idVM=virtualmac12_.idVM where virtualapp9_.idVirtualApp=4 and nodesvirtu10_.idNode=nodevirtua11_.idNode and (virtualmac12_.state in ('NOT_ALLOCATED' , 'ALLOCATED' , 'UNKNOWN')))) then 'DEPLOYED' when not (exists (select virtualmac16_.state from virtualapp virtualapp13_ inner join node nodesvirtu14_ on virtualapp13_.idVirtualApp=nodesvirtu14_.idVirtualApp cross join nodevirtualimage nodevirtua15_ inner join node nodevirtua15_1_ on nodevirtua15_.idNode=nodevirtua15_1_.idNode inner join virtualmachine virtualmac16_ on nodevirtua15_.idVM=virtualmac16_.idVM where virtualapp13_.idVirtualApp=4 and nodesvirtu14_.idNode=nodevirtua15_.idNode and (virtualmac16_.state in ('OFF' , 'PAUSED' , 'ON' , 'UNKNOWN')))) then 'NOT_DEPLOYED' when not (exists (select virtualmac20_.idVM from virtualapp virtualapp17_ inner join node nodesvirtu18_ on virtualapp17_.idVirtualApp=nodesvirtu18_.idVirtualApp cross join nodevirtualimage nodevirtua19_ inner join node nodevirtua19_1_ on nodevirtua19_.idNode=nodevirtua19_1_.idNode inner join virtualmachine virtualmac20_ on nodevirtua19_.idVM=virtualmac20_.idVM where virtualapp17_.idVirtualApp=4 and nodesvirtu18_.idNode=nodevirtua19_.idNode)) then 'NOT_DEPLOYED' else 'NEEDS_SYNCHRONIZE' end as col_0_0_ from virtualapp virtualapp0_ where virtualapp0_.idVirtualApp=virtualapp0_.idVirtualApp")
    private VirtualApplianceState state;

    @Required(value = STATE_REQUIRED)
    public VirtualApplianceState getState()
    {
        return this.state;
    }

    public void setState(final VirtualApplianceState state)
    {
        this.state = state;
    }

    //

    public final static String NODE_VIRTUALIMAGE_PROPERTY = "nodesVirtualImage";

    @OneToMany(mappedBy = Node.VIRTUAL_APPLIANCE_PROPERTY, fetch = FetchType.LAZY)
    protected List<Node> nodesVirtualImage = new ArrayList<Node>();

    @Required(value = true)
    public List<Node> getNodesVirtualImage()
    {
        return Collections.unmodifiableList(this.nodesVirtualImage);
    }

    public List<NodeVirtualImage> getNodes()
    {
        List<Node> nodes = getNodesVirtualImage();
        List<NodeVirtualImage> nodesvi = new LinkedList<NodeVirtualImage>();
        for (Node node : nodes)
        {
            if (node instanceof NodeVirtualImage)
            {
                nodesvi.add((NodeVirtualImage) node);
            }
        }

        return nodesvi;
    }

    public void addToNodeVirtualImages(final NodeVirtualImage value)
    {
        assert value != null;
        assert !this.nodesVirtualImage.contains(value);

        this.nodesVirtualImage.add(value);
        value.setVirtualAppliance(this);
    }

    public void removeFromNodeVirtualImages(final NodeVirtualImage value)
    {
        assert value != null;
        assert this.nodesVirtualImage.contains(value);

        this.nodesVirtualImage.remove(value);
        // value.removeFromDatastores(this);
    }

}
