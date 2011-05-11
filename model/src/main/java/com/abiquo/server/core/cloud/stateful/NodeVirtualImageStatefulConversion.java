package com.abiquo.server.core.cloud.stateful;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualImageConversion;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.storage.Tier;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = NodeVirtualImageStatefulConversion.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = NodeVirtualImageStatefulConversion.TABLE_NAME)
public class NodeVirtualImageStatefulConversion extends DefaultEntityBase
{
    public static final String TABLE_NAME = "node_virtual_image_stateful_conversions";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected NodeVirtualImageStatefulConversion()
    {
        // Just for JPA support
    }

    public NodeVirtualImageStatefulConversion(final String newName,
        final VirtualApplicanceStatefulConversion virtualApplicanceStatefulConversion,
        final NodeVirtualImage nodeVirtualImage, final Tier tier)
    {
        setNewName(newName);
        setVirtualApplianceStatefulConversion(virtualApplicanceStatefulConversion);
        setNodeVirtualImage(nodeVirtualImage);
        setTier(tier);
    }

    public final static String DISK_STATEFUL_CONVERSION_PROPERTY = "diskStatefulConversion";

    private final static boolean DISK_STATEFUL_CONVERSION_REQUIRED = false;

    private final static int DISK_STATEFUL_CONVERSION_LENGTH_MIN = 0;

    private final static int DISK_STATEFUL_CONVERSION_LENGTH_MAX = 255;

    private final static boolean DISK_STATEFUL_CONVERSION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED =
        false;

    private final static String DISK_STATEFUL_CONVERSION_COLUMN = "idDiskStatefulConversion";

    @Column(name = DISK_STATEFUL_CONVERSION_COLUMN, nullable = !DISK_STATEFUL_CONVERSION_REQUIRED, length = DISK_STATEFUL_CONVERSION_LENGTH_MAX)
    private DiskStatefulConversion diskStatefulConversion;

    @Required(value = DISK_STATEFUL_CONVERSION_REQUIRED)
    @Length(min = DISK_STATEFUL_CONVERSION_LENGTH_MIN, max = DISK_STATEFUL_CONVERSION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DISK_STATEFUL_CONVERSION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public DiskStatefulConversion getDiskStatefulConversion()
    {
        return this.diskStatefulConversion;
    }

    private void setDiskStatefulConversion(final DiskStatefulConversion diskStatefulConversion)
    {
        this.diskStatefulConversion = diskStatefulConversion;
    }

    public final static String NEW_NAME_PROPERTY = "newName";

    private final static boolean NEW_NAME_REQUIRED = true;

    private final static int NEW_NAME_LENGTH_MIN = 0;

    private final static int NEW_NAME_LENGTH_MAX = 255;

    private final static boolean NEW_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NEW_NAME_COLUMN = "newName";

    @Column(name = NEW_NAME_COLUMN, nullable = !NEW_NAME_REQUIRED, length = NEW_NAME_LENGTH_MAX)
    private String newName;

    @Required(value = NEW_NAME_REQUIRED)
    @Length(min = NEW_NAME_LENGTH_MIN, max = NEW_NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NEW_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getNewName()
    {
        return this.newName;
    }

    private void setNewName(final String newName)
    {
        this.newName = newName;
    }

    public final static String TIER_PROPERTY = "tier";

    private final static boolean TIER_REQUIRED = true;

    private final static int TIER_LENGTH_MIN = 0;

    private final static int TIER_LENGTH_MAX = 255;

    private final static boolean TIER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String TIER_COLUMN = "idTier";

    @Column(name = TIER_COLUMN, nullable = !TIER_REQUIRED, length = TIER_LENGTH_MAX)
    private Tier tier;

    @Required(value = TIER_REQUIRED)
    @Length(min = TIER_LENGTH_MIN, max = TIER_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = TIER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public Tier getTier()
    {
        return this.tier;
    }

    private void setTier(final Tier tier)
    {
        this.tier = tier;
    }

    public final static String VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_PROPERTY =
        "virtualApplianceStatefulConversion";

    private final static boolean VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_REQUIRED = true;

    private final static int VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_LENGTH_MIN = 0;

    private final static int VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_LENGTH_MAX = 255;

    private final static boolean VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED =
        false;

    private final static String VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_COLUMN =
        "idVirtualApplianceStatefulConversion";

    @Column(name = VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_COLUMN, nullable = !VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_REQUIRED, length = VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_LENGTH_MAX)
    private VirtualApplicanceStatefulConversion virtualApplianceStatefulConversion;

    @Required(value = VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_REQUIRED)
    @Length(min = VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_LENGTH_MIN, max = VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public VirtualApplicanceStatefulConversion getVirtualApplianceStatefulConversion()
    {
        return this.virtualApplianceStatefulConversion;
    }

    private void setVirtualApplianceStatefulConversion(
        final VirtualApplicanceStatefulConversion virtualApplianceStatefulConversion)
    {
        this.virtualApplianceStatefulConversion = virtualApplianceStatefulConversion;
    }

    public final static String ID_PROPERTY = "id";

    private final static boolean ID_REQUIRED = false;

    private final static String ID_COLUMN = "id";

    private final static int ID_MIN = Integer.MIN_VALUE;

    private final static int ID_MAX = Integer.MAX_VALUE;

    @Column(name = ID_COLUMN, nullable = !ID_REQUIRED)
    @Range(min = ID_MIN, max = ID_MAX)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    private void setId(final Integer id)
    {
        this.id = id;
    }

    public final static String NODE_VIRTUAL_IMAGE_PROPERTY = "nodeVirtualImage";

    private final static boolean NODE_VIRTUAL_IMAGE_REQUIRED = true;

    private final static int NODE_VIRTUAL_IMAGE_LENGTH_MIN = 0;

    private final static int NODE_VIRTUAL_IMAGE_LENGTH_MAX = 255;

    private final static boolean NODE_VIRTUAL_IMAGE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NODE_VIRTUAL_IMAGE_COLUMN = "idNodeVirtualImage";

    @Column(name = NODE_VIRTUAL_IMAGE_COLUMN, nullable = !NODE_VIRTUAL_IMAGE_REQUIRED, length = NODE_VIRTUAL_IMAGE_LENGTH_MAX)
    private NodeVirtualImage nodeVirtualImage;

    @Required(value = NODE_VIRTUAL_IMAGE_REQUIRED)
    @Length(min = NODE_VIRTUAL_IMAGE_LENGTH_MIN, max = NODE_VIRTUAL_IMAGE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NODE_VIRTUAL_IMAGE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public NodeVirtualImage getNodeVirtualImage()
    {
        return this.nodeVirtualImage;
    }

    private void setNodeVirtualImage(final NodeVirtualImage nodeVirtualImage)
    {
        this.nodeVirtualImage = nodeVirtualImage;
    }

    public final static String VIRTUAL_IMAGE_CONVERSION_PROPERTY = "virtualImageConversion";

    private final static boolean VIRTUAL_IMAGE_CONVERSION_REQUIRED = false;

    private final static int VIRTUAL_IMAGE_CONVERSION_LENGTH_MIN = 0;

    private final static int VIRTUAL_IMAGE_CONVERSION_LENGTH_MAX = 255;

    private final static boolean VIRTUAL_IMAGE_CONVERSION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED =
        false;

    private final static String VIRTUAL_IMAGE_CONVERSION_COLUMN = "idVirtualImageConversion";

    @Column(name = VIRTUAL_IMAGE_CONVERSION_COLUMN, nullable = !VIRTUAL_IMAGE_CONVERSION_REQUIRED, length = VIRTUAL_IMAGE_CONVERSION_LENGTH_MAX)
    private VirtualImageConversion virtualImageConversion;

    @Required(value = VIRTUAL_IMAGE_CONVERSION_REQUIRED)
    @Length(min = VIRTUAL_IMAGE_CONVERSION_LENGTH_MIN, max = VIRTUAL_IMAGE_CONVERSION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VIRTUAL_IMAGE_CONVERSION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public VirtualImageConversion getVirtualImageConversion()
    {
        return this.virtualImageConversion;
    }

    private void setVirtualImageConversion(final VirtualImageConversion virtualImageConversion)
    {
        this.virtualImageConversion = virtualImageConversion;
    }

}
