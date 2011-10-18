package com.abiquo.server.core.cloud;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualImageConversion.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualImageConversion.TABLE_NAME)
public class VirtualImageConversion extends DefaultEntityBase
{
    public static final String TABLE_NAME = "virtualimage_conversions";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected VirtualImageConversion()
    {
        // Just for JPA support
    }

    protected VirtualImageConversion(final VirtualImage image, final DiskFormatType targetType,
        final String targetPath)
    {
        this.virtualImage = image;
        this.targetType = targetType;
        this.targetPath = targetPath;
        this.timestamp = new Date();
        this.state = ConversionState.ENQUEUED;
    }

    private final static String ID_COLUMN = "id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String VIRTUAL_IMAGE_PROPERTY = "virtualImage";

    private final static boolean VIRTUAL_IMAGE_REQUIRED = true;

    private final static String VIRTUAL_IMAGE_ID_COLUMN = "idImage";

    @JoinColumn(name = VIRTUAL_IMAGE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_virtualimage_conversions_virtualimage")
    private VirtualImage virtualImage;

    @Required(value = VIRTUAL_IMAGE_REQUIRED)
    public VirtualImage getVirtualImage()
    {
        return this.virtualImage;
    }

    public void setVirtualImage(final VirtualImage virtualImage)
    {
        this.virtualImage = virtualImage;
    }

    public final static String SOURCE_TYPE_PROPERTY = "sourceType";

    private final static boolean SOURCE_TYPE_REQUIRED = false;

    private final static String SOURCE_TYPE_COLUMN = "sourceType";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = SOURCE_TYPE_COLUMN, nullable = !SOURCE_TYPE_REQUIRED)
    private DiskFormatType sourceType;

    @Required(value = SOURCE_TYPE_REQUIRED)
    public DiskFormatType getSourceType()
    {
        return this.sourceType;
    }

    public void setSourceType(final DiskFormatType sourceType)
    {
        this.sourceType = sourceType;
    }

    public final static String TARGET_TYPE_PROPERTY = "targetType";

    private final static boolean TARGET_TYPE_REQUIRED = true;

    private final static String TARGET_TYPE_COLUMN = "targetType";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = TARGET_TYPE_COLUMN, nullable = !TARGET_TYPE_REQUIRED)
    private DiskFormatType targetType;

    @Required(value = TARGET_TYPE_REQUIRED)
    public DiskFormatType getTargetType()
    {
        return this.targetType;
    }

    public void setTargetType(final DiskFormatType targetType)
    {
        this.targetType = targetType;
    }

    public final static String SOURCE_PATH_PROPERTY = "sourcePath";

    private final static boolean SOURCE_PATH_REQUIRED = false;

    private final static int SOURCE_PATH_LENGTH_MIN = 0;

    private final static int SOURCE_PATH_LENGTH_MAX = 255;

    private final static boolean SOURCE_PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String SOURCE_PATH_COLUMN = "sourcePath";

    @Column(name = SOURCE_PATH_COLUMN, nullable = !SOURCE_PATH_REQUIRED, length = SOURCE_PATH_LENGTH_MAX)
    private String sourcePath;

    @Required(value = SOURCE_PATH_REQUIRED)
    @Length(min = SOURCE_PATH_LENGTH_MIN, max = SOURCE_PATH_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SOURCE_PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getSourcePath()
    {
        return this.sourcePath;
    }

    public void setSourcePath(final String sourcePath)
    {
        this.sourcePath = sourcePath;
    }

    public final static String TARGET_PATH_PROPERTY = "targetPath";

    private final static boolean TARGET_PATH_REQUIRED = false;

    public final static int TARGET_PATH_LENGTH_MIN = 0;

    public final static int TARGET_PATH_LENGTH_MAX = 255;

    private final static boolean TARGET_PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String TARGET_PATH_COLUMN = "targetPath";

    @Column(name = TARGET_PATH_COLUMN, nullable = !TARGET_PATH_REQUIRED, length = TARGET_PATH_LENGTH_MAX)
    private String targetPath;

    @Required(value = TARGET_PATH_REQUIRED)
    @Length(min = TARGET_PATH_LENGTH_MIN, max = TARGET_PATH_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = TARGET_PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getTargetPath()
    {
        return this.targetPath;
    }

    public void setTargetPath(final String targetPath)
    {
        this.targetPath = targetPath;
    }

    public final static String STATE_PROPERTY = "state";

    private final static boolean STATE_REQUIRED = true;

    private final static String STATE_COLUMN = "state";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = STATE_COLUMN, nullable = !STATE_REQUIRED)
    private ConversionState state;

    @Required(value = STATE_REQUIRED)
    public ConversionState getState()
    {
        return this.state;
    }

    public void setState(final ConversionState state)
    {
        this.state = state;
    }

    public final static String SIZE_PROPERTY = "size";

    private final static boolean SIZE_REQUIRED = false;

    private final static String SIZE_COLUMN = "size";

    private final static long SIZE_MIN = Long.MIN_VALUE;

    private final static long SIZE_MAX = Long.MAX_VALUE;

    @Column(name = SIZE_COLUMN, nullable = !SIZE_REQUIRED)
    @Range(min = SIZE_MIN, max = SIZE_MAX)
    private long size;

    public long getSize()
    {
        return this.size;
    }

    public void setSize(final long size)
    {
        this.size = size;
    }

    public final static String TIMESTAMP_PROPERTY = "timestamp";

    private final static boolean TIMESTAMP_REQUIRED = false;

    private final static String TIMESTAMP_COLUMN = "timestamp";

    @Column(name = TIMESTAMP_COLUMN, nullable = !TIMESTAMP_REQUIRED)
    private Date timestamp;

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp)
    {
        this.timestamp = timestamp;
    }

}
