/*
 *   $Id$
 *
 *   Copyright 2008 Glencoe Software, Inc. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package ome.services.blitz.impl;

// Java imports
import java.util.List;

import ome.api.IAdmin;
import ome.services.blitz.util.BlitzExecutor;
import omero.RString;
import omero.ServerError;
import omero.api.AMD_IAdmin_addGroups;
import omero.api.AMD_IAdmin_changeExpiredCredentials;
import omero.api.AMD_IAdmin_changeGroup;
import omero.api.AMD_IAdmin_changeOwner;
import omero.api.AMD_IAdmin_changePassword;
import omero.api.AMD_IAdmin_changePermissions;
import omero.api.AMD_IAdmin_changeUserPassword;
import omero.api.AMD_IAdmin_containedExperimenters;
import omero.api.AMD_IAdmin_containedGroups;
import omero.api.AMD_IAdmin_createExperimenter;
import omero.api.AMD_IAdmin_createGroup;
import omero.api.AMD_IAdmin_createSystemUser;
import omero.api.AMD_IAdmin_createUser;
import omero.api.AMD_IAdmin_deleteExperimenter;
import omero.api.AMD_IAdmin_getDefaultGroup;
import omero.api.AMD_IAdmin_getEventContext;
import omero.api.AMD_IAdmin_getExperimenter;
import omero.api.AMD_IAdmin_getGroup;
import omero.api.AMD_IAdmin_getSecurityRoles;
import omero.api.AMD_IAdmin_lookupExperimenter;
import omero.api.AMD_IAdmin_lookupExperimenters;
import omero.api.AMD_IAdmin_lookupGroup;
import omero.api.AMD_IAdmin_lookupGroups;
import omero.api.AMD_IAdmin_lookupLdapAuthExperimenter;
import omero.api.AMD_IAdmin_lookupLdapAuthExperimenters;
import omero.api.AMD_IAdmin_removeGroups;
import omero.api.AMD_IAdmin_reportForgottenPassword;
import omero.api.AMD_IAdmin_setDefaultGroup;
import omero.api.AMD_IAdmin_setGroupOwner;
import omero.api.AMD_IAdmin_synchronizeLoginCache;
import omero.api.AMD_IAdmin_unlock;
import omero.api.AMD_IAdmin_updateExperimenter;
import omero.api.AMD_IAdmin_updateGroup;
import omero.api.AMD_IAdmin_updateSelf;
import omero.api._IAdminOperations;
import omero.model.Experimenter;
import omero.model.ExperimenterGroup;
import omero.model.IObject;
import omero.model.Permissions;
import Ice.Current;

/**
 * Implementation of the {@link omero.api.IAdmin} service.
 * 
 * @author Josh Moore, josh at glencoesoftware.com
 * @since 3.0-Beta4
 * @see ome.api.IAdmin
 */
public class AdminI extends AbstractAmdServant implements _IAdminOperations {

    public AdminI(IAdmin service, BlitzExecutor be) {
        super(service, be);
    }

    // Interface methods
    // =========================================================================

    public void addGroups_async(AMD_IAdmin_addGroups __cb, Experimenter user,
            List<Experimenter> groups, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, user, groups);
    }

    public void changeExpiredCredentials_async(
            AMD_IAdmin_changeExpiredCredentials __cb, String name,
            String oldCred, String newCred, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current, name, oldCred, newCred);
    }

    public void changeGroup_async(AMD_IAdmin_changeGroup __cb, IObject obj,
            String omeName, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, obj, omeName);
    }

    public void changeOwner_async(AMD_IAdmin_changeOwner __cb, IObject obj,
            String omeName, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, obj, omeName);
    }

    public void changePassword_async(AMD_IAdmin_changePassword __cb,
            RString newPassword, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, newPassword);
    }

    public void changePermissions_async(AMD_IAdmin_changePermissions __cb,
            IObject obj, Permissions perms, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current, obj, perms);
    }

    public void changeUserPassword_async(AMD_IAdmin_changeUserPassword __cb,
            String omeName, RString newPassword, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current, omeName, newPassword);
    }

    public void containedExperimenters_async(
            AMD_IAdmin_containedExperimenters __cb, long groupId,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, groupId);
    }

    public void containedGroups_async(AMD_IAdmin_containedGroups __cb,
            long experimenterId, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, experimenterId);
    }

    public void createExperimenter_async(AMD_IAdmin_createExperimenter __cb,
            Experimenter user, ExperimenterGroup defaultGroup,
            List<ExperimenterGroup> groups, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current, user, defaultGroup, groups);
    }

    public void createGroup_async(AMD_IAdmin_createGroup __cb,
            ExperimenterGroup group, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, group);
    }

    public void createSystemUser_async(AMD_IAdmin_createSystemUser __cb,
            Experimenter experimenter, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, experimenter);
    }

    public void createUser_async(AMD_IAdmin_createUser __cb,
            Experimenter experimenter, String group, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current, experimenter, group);
    }

    public void deleteExperimenter_async(AMD_IAdmin_deleteExperimenter __cb,
            Experimenter user, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, user);
    }

    public void getDefaultGroup_async(AMD_IAdmin_getDefaultGroup __cb,
            long experimenterId, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, experimenterId);
    }

    public void getEventContext_async(AMD_IAdmin_getEventContext __cb,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current);
    }

    public void getExperimenter_async(AMD_IAdmin_getExperimenter __cb, long id,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, id);
    }

    public void getGroup_async(AMD_IAdmin_getGroup __cb, long id,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, id);
    }

    public void getSecurityRoles_async(AMD_IAdmin_getSecurityRoles __cb,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current);
    }

    public void lookupExperimenter_async(AMD_IAdmin_lookupExperimenter __cb,
            String name, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, name);
    }

    public void lookupExperimenters_async(AMD_IAdmin_lookupExperimenters __cb,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current);
    }

    public void lookupGroup_async(AMD_IAdmin_lookupGroup __cb, String name,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, name);
    }

    public void lookupGroups_async(AMD_IAdmin_lookupGroups __cb,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current);
    }

    public void lookupLdapAuthExperimenter_async(
            AMD_IAdmin_lookupLdapAuthExperimenter __cb, long id,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, id);
    }

    public void lookupLdapAuthExperimenters_async(
            AMD_IAdmin_lookupLdapAuthExperimenters __cb, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current);
    }

    public void removeGroups_async(AMD_IAdmin_removeGroups __cb,
            Experimenter user, List<ExperimenterGroup> groups, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current, user, groups);
    }

    public void reportForgottenPassword_async(
            AMD_IAdmin_reportForgottenPassword __cb, String name, String email,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, name, email);
    }

    public void setDefaultGroup_async(AMD_IAdmin_setDefaultGroup __cb,
            Experimenter user, ExperimenterGroup group, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current, user, group);
    }

    public void setGroupOwner_async(AMD_IAdmin_setGroupOwner __cb,
            ExperimenterGroup group, Experimenter owner, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current, group, owner);
    }

    public void synchronizeLoginCache_async(
            AMD_IAdmin_synchronizeLoginCache __cb, Current __current)
            throws ServerError {
        callInvokerOnRawArgs(__cb, __current);
    }

    public void unlock_async(AMD_IAdmin_unlock __cb, List<IObject> objects,
            Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, objects);
    }

    public void updateExperimenter_async(AMD_IAdmin_updateExperimenter __cb,
            Experimenter experimenter, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, experimenter);
    }

    public void updateGroup_async(AMD_IAdmin_updateGroup __cb,
            ExperimenterGroup group, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, group);
    }

    public void updateSelf_async(AMD_IAdmin_updateSelf __cb,
            Experimenter experimenter, Current __current) throws ServerError {
        callInvokerOnRawArgs(__cb, __current, experimenter);
    }

}
