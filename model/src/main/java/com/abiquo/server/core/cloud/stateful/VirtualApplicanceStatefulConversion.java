package com.abiquo.server.core.cloud.stateful;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualApplicanceStatefulConversion.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualApplicanceStatefulConversion.TABLE_NAME)
public class VirtualApplicanceStatefulConversion extends DefaultEntityBase
{
    public static final String TABLE_NAME = "vappstateful_conversions";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected VirtualApplicanceStatefulConversion()
    {
        // Just for JPA support
    }

    public VirtualApplicanceStatefulConversion(final Integer idUser, final State state,
        final State subState, final VirtualAppliance vapp)
    {
        setIdUser(idUser);
        setState(state);
        setSubState(subState);
        setVirtualAppliance(vapp);
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

    private void setId(final int id)
    {
        this.id = id;
    }

    public final static String ID_USER_PROPERTY = "idUser";

    private final static boolean ID_USER_REQUIRED = true;

    private final static String ID_USER_COLUMN = "idUser";

    private final static int ID_USER_MIN = Integer.MIN_VALUE;

    private final static int ID_USER_MAX = Integer.MAX_VALUE;

    @Column(name = ID_USER_COLUMN, nullable = !ID_USER_REQUIRED)
    @Range(min = ID_USER_MIN, max = ID_USER_MAX)
    private int idUser;

    public int getIdUser()
    {
        return this.idUser;
    }

    private void setIdUser(final int idUser)
    {
        this.idUser = idUser;
    }

    public final static String VIRTUAL_APP_PROPERTY = "virtualAppliance";

    private final static boolean VIRTUAL_APP_REQUIRED = true;

    private final static String VIRTUAL_APP_COLUMN = "idVirtualApp";

    private final static int VIRTUAL_APP_MIN = Integer.MIN_VALUE;

    private final static int VIRTUAL_APP_MAX = Integer.MAX_VALUE;

    @Column(name = VIRTUAL_APP_COLUMN, nullable = !VIRTUAL_APP_REQUIRED)
    @Range(min = VIRTUAL_APP_MIN, max = VIRTUAL_APP_MAX)
    private VirtualAppliance virtualAppliance;

    public VirtualAppliance getVirtualAppliance()
    {
        return this.virtualAppliance;
    }

    private void setVirtualAppliance(final VirtualAppliance vapp)
    {
        this.virtualAppliance = vapp;
    }

    public final static String SUB_STATE_PROPERTY = "subState";

    private final static boolean SUB_STATE_REQUIRED = true;

    private final static int SUB_STATE_LENGTH_MIN = 0;

    private final static int SUB_STATE_LENGTH_MAX = 255;

    private final static boolean SUB_STATE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String SUB_STATE_COLUMN = "subState";

    @Column(name = SUB_STATE_COLUMN, nullable = !SUB_STATE_REQUIRED, length = SUB_STATE_LENGTH_MAX)
    private State subState;

    @Required(value = SUB_STATE_REQUIRED)
    @Length(min = SUB_STATE_LENGTH_MIN, max = SUB_STATE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SUB_STATE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public State getSubState()
    {
        return this.subState;
    }

    private void setSubState(final State subState)
    {
        this.subState = subState;
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

}
