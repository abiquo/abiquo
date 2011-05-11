package com.abiquo.server.core.cloud.stateful;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = DiskStatefulConversion.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = DiskStatefulConversion.TABLE_NAME)
public class DiskStatefulConversion extends DefaultEntityBase
{
    public static final String TABLE_NAME = "diskstateful_conversions";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected DiskStatefulConversion()
    {
        // Just for JPA support
    }

    public DiskStatefulConversion(final String imagePath, final VolumeManagement volume,
        final State state, final Date timestamp)
    {
        setImagePath(imagePath);
        setVolume(volume);
        setState(state);
        setTimestamp(timestamp);
    }

    public final static String TIMESTAMP_PROPERTY = "timestamp";

    private final static boolean TIMESTAMP_REQUIRED = true;

    private final static int TIMESTAMP_LENGTH_MIN = 0;

    private final static int TIMESTAMP_LENGTH_MAX = 255;

    private final static boolean TIMESTAMP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String TIMESTAMP_COLUMN = "convertionTimestamp";

    @Column(name = TIMESTAMP_COLUMN, nullable = !TIMESTAMP_REQUIRED, length = TIMESTAMP_LENGTH_MAX)
    private Date timestamp;

    @Required(value = TIMESTAMP_REQUIRED)
    @Length(min = TIMESTAMP_LENGTH_MIN, max = TIMESTAMP_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = TIMESTAMP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public Date getTimestamp()
    {
        return this.timestamp;
    }

    private void setTimestamp(final Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public final static String IMAGE_PATH_PROPERTY = "imagePath";

    private final static boolean IMAGE_PATH_REQUIRED = true;

    private final static int IMAGE_PATH_LENGTH_MIN = 0;

    private final static int IMAGE_PATH_LENGTH_MAX = 255;

    private final static boolean IMAGE_PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String IMAGE_PATH_COLUMN = "imagePath";

    @Column(name = IMAGE_PATH_COLUMN, nullable = !IMAGE_PATH_REQUIRED, length = IMAGE_PATH_LENGTH_MAX)
    private String imagePath;

    @Required(value = IMAGE_PATH_REQUIRED)
    @Length(min = IMAGE_PATH_LENGTH_MIN, max = IMAGE_PATH_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = IMAGE_PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getImagePath()
    {
        return this.imagePath;
    }

    private void setImagePath(final String imagePath)
    {
        this.imagePath = imagePath;
    }

    public final static String ID_PROPERTY = "id";

    private final static boolean ID_REQUIRED = true;

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

    public final static String STATE_PROPERTY = "state";

    private final static boolean STATE_REQUIRED = true;

    private final static int STATE_LENGTH_MIN = 0;

    private final static int STATE_LENGTH_MAX = 255;

    private final static boolean STATE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String STATE_COLUMN = "state";

    @Column(name = STATE_COLUMN, nullable = !STATE_REQUIRED, length = STATE_LENGTH_MAX)
    private State state;

    @Required(value = STATE_REQUIRED)
    @Length(min = STATE_LENGTH_MIN, max = STATE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = STATE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public State getState()
    {
        return this.state;
    }

    private void setState(final State state)
    {
        this.state = state;
    }

    public final static String MANAGEMENT_PROPERTY = "volumeManagement";

    private final static boolean MANAGEMENT_REQUIRED = true;

    private final static String MANAGEMENT_COLUMN = "idManagement";

    private final static int MANAGEMENT_MIN = Integer.MIN_VALUE;

    private final static int MANAGEMENT_MAX = Integer.MAX_VALUE;

    @Column(name = MANAGEMENT_COLUMN, nullable = !MANAGEMENT_REQUIRED)
    @Range(min = MANAGEMENT_MIN, max = MANAGEMENT_MAX)
    private VolumeManagement volume;

    public VolumeManagement getVolume()
    {
        return this.volume;
    }

    private void setVolume(final VolumeManagement volume)
    {
        this.volume = volume;
    }

}
