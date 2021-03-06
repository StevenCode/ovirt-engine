/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
*/

package org.ovirt.engine.api.v3.servers;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.ovirt.engine.api.resource.AssignedPermissionsResource;
import org.ovirt.engine.api.v3.V3Server;
import org.ovirt.engine.api.v3.types.V3Permission;
import org.ovirt.engine.api.v3.types.V3Permissions;

@Produces({"application/xml", "application/json"})
public class V3AssignedPermissionsServer extends V3Server<AssignedPermissionsResource> {
    public V3AssignedPermissionsServer(AssignedPermissionsResource delegate) {
        super(delegate);
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    public Response add(V3Permission permission) {
        return adaptAdd(getDelegate()::add, permission);
    }

    @GET
    public V3Permissions list() {
        return adaptList(getDelegate()::list);
    }

    @Path("{id}")
    public V3PermissionServer getPermissionResource(@PathParam("id") String id) {
        return new V3PermissionServer(getDelegate().getPermissionResource(id));
    }
}
