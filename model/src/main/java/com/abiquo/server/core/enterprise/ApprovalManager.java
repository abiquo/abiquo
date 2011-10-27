package com.abiquo.server.core.enterprise;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;

@Entity
@Table(name = ApprovalManager.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = ApprovalManager.TABLE_NAME)
public class ApprovalManager extends DefaultEntityBase implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "approval_manager";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public ApprovalManager()
    {
        // Just for JPA support
    }

    public ApprovalManager(final Integer userId, final Integer managerId)
    {
        setId(userId);
        setIdManager(managerId);
    }

    private final static String ID_COLUMN = "idUser";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public void setId(final Integer userId)
    {
        this.id = userId;
    }

    // public final static String ID_USER_PROPERTY = "idUser";
    //
    // private final static String ID_USER_COLUMN = "idUser";
    //
    // private final static int ID_USER_MIN = Integer.MIN_VALUE;
    //
    // private final static int ID_USER_MAX = Integer.MAX_VALUE;
    //
    // @Column(name = ID_USER_COLUMN, nullable = false)
    // @Range(min = ID_USER_MIN, max = ID_USER_MAX)
    // private int idUser;
    //
    // public int getIdUser()
    // {
    // return this.idUser;
    // }
    //
    // public void setIdUser(final int idUser)
    // {
    // this.idUser = idUser;
    // }

    public final static String ID_MANAGER_PROPERTY = "idUserManager";

    private final static String ID_MANAGER_COLUMN = "idUserManager";

    private final static int ID_MANAGER_MIN = Integer.MIN_VALUE;

    private final static int ID_MANAGER_MAX = Integer.MAX_VALUE;

    @Column(name = ID_MANAGER_COLUMN, nullable = false)
    @Range(min = ID_MANAGER_MIN, max = ID_MANAGER_MAX)
    private int idManager;

    public int getIdManager()
    {
        return this.idManager;
    }

    public void setIdManager(final int idManager)
    {
        this.idManager = idManager;
    }

    // @Id
    // @GeneratedValue
    // private ApprovalUserManager userManager = new ApprovalUserManager();
    //
    // public void setUserManager(final ApprovalUserManager userManager)
    // {
    // this.userManager = userManager;
    // }
    //
    // public ApprovalUserManager getUserManager()
    // {
    // return userManager;
    // }
    //
    // public void setUser(final User user)
    // {
    // this.userManager.setUser(user);
    // }
    //
    // public User getUser()
    // {
    // return this.userManager.getUser();
    // }
    //
    // public void setManager(final User manager)
    // {
    // this.userManager.setManager(manager);
    // }
    //
    // public User getManager()
    // {
    // return this.userManager.getManager();
    // }
}
