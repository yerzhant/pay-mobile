/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.30 Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.security.view;

import javax.faces.component.UIComponent;

import oracle.adf.view.rich.component.rich.fragment.RichDeclarativeComponent;
import oracle.adf.view.rich.context.AdfFacesContext;


public class MainBean {
    private RichDeclarativeComponent users;
    private RichDeclarativeComponent branchesOfUser;
    private RichDeclarativeComponent branches;
    private RichDeclarativeComponent usersOfBranch;

    public MainBean() {
    }

    private void updateButtons(RichDeclarativeComponent tableDef) {
        UIComponent commit = tableDef.findComponent("dc_pc1:dc_commit");
        UIComponent rollback = tableDef.findComponent("dc_pc1:dc_rollback");
        UIComponent rowsCount = tableDef.findComponent("dc_pc1:dc_rowsCount");
        if (commit != null) {
            AdfFacesContext.getCurrentInstance().addPartialTarget(commit);
        }
        if (rollback != null) {
            AdfFacesContext.getCurrentInstance().addPartialTarget(rollback);
        }
        if (rowsCount != null) {
            AdfFacesContext.getCurrentInstance().addPartialTarget(rowsCount);
        }
    }

    /**
     * @param users
     */
    public void setUsers(RichDeclarativeComponent users) {
        this.users = users;
        updateButtons(users);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getUsers() {
        return users;
    }

    /**
     * @param branchesOfUser
     */
    public void setBranchesOfUser(RichDeclarativeComponent branchesOfUser) {
        this.branchesOfUser = branchesOfUser;
        updateButtons(branchesOfUser);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getBranchesOfUser() {
        return branchesOfUser;
    }

    /**
     * @param branches
     */
    public void setBranches(RichDeclarativeComponent branches) {
        this.branches = branches;
        updateButtons(branches);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getBranches() {
        return branches;
    }

    /**
     * @param usersOfBranch
     */
    public void setUsersOfBranch(RichDeclarativeComponent usersOfBranch) {
        this.usersOfBranch = usersOfBranch;
        updateButtons(usersOfBranch);
    }

    /**
     * @return
     */
    public RichDeclarativeComponent getUsersOfBranch() {
        return usersOfBranch;
    }
}
