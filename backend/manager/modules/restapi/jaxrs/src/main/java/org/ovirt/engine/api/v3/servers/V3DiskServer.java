/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
*/

package org.ovirt.engine.api.v3.servers;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.ovirt.engine.api.model.Actionable;
import org.ovirt.engine.api.resource.DiskResource;
import org.ovirt.engine.api.v3.V3Server;
import org.ovirt.engine.api.v3.types.V3Action;
import org.ovirt.engine.api.v3.types.V3Disk;

@Produces({"application/xml", "application/json"})
public class V3DiskServer extends V3Server<DiskResource> {
    public V3DiskServer(DiskResource delegate) {
        super(delegate);
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Actionable
    @Path("copy")
    public Response copy(V3Action action) {
        return adaptAction(getDelegate()::copy, action);
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Actionable
    @Path("export")
    public Response export(V3Action action) {
        return adaptAction(getDelegate()::export, action);
    }

    @GET
    public V3Disk get() {
        return adaptGet(getDelegate()::get);
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Actionable
    @Path("move")
    public Response move(V3Action action) {
        return adaptAction(getDelegate()::move, action);
    }

    @DELETE
    public Response remove() {
        return adaptRemove(getDelegate()::remove);
    }

    @Path("permissions")
    public V3AssignedPermissionsServer getPermissionsResource() {
        return new V3AssignedPermissionsServer(getDelegate().getPermissionsResource());
    }

    @Path("statistics")
    public V3StatisticsServer getStatisticsResource() {
        return new V3StatisticsServer(getDelegate().getStatisticsResource());
    }

    @Path("{action: (?:copy|export|move)}/{oid}")
    public V3ActionServer getActionResource(@PathParam("action") String action, @PathParam("oid") String oid) {
        return new V3ActionServer(getDelegate().getActionResource(action, oid));
    }

    @Path("creation_status/{oid}")
    public V3CreationServer getCreationResource(@PathParam("oid") String oid) {
        return new V3CreationServer(getDelegate().getCreationResource(oid));
    }
}
