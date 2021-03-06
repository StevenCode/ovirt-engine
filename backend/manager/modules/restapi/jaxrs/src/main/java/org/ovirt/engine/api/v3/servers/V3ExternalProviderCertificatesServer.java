/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
*/

package org.ovirt.engine.api.v3.servers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.ovirt.engine.api.resource.ExternalProviderCertificatesResource;
import org.ovirt.engine.api.v3.V3Server;
import org.ovirt.engine.api.v3.types.V3Certificates;

@Produces({"application/xml", "application/json"})
public class V3ExternalProviderCertificatesServer extends V3Server<ExternalProviderCertificatesResource> {
    public V3ExternalProviderCertificatesServer(ExternalProviderCertificatesResource delegate) {
        super(delegate);
    }

    @GET
    public V3Certificates list() {
        return adaptList(getDelegate()::list);
    }

    @Path("{id}")
    public V3ExternalProviderCertificateServer getCertificateResource(@PathParam("id") String id) {
        return new V3ExternalProviderCertificateServer(getDelegate().getCertificateResource(id));
    }
}
