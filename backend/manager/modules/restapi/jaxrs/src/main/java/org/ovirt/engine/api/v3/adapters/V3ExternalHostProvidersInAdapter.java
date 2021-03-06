/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
*/

package org.ovirt.engine.api.v3.adapters;

import static org.ovirt.engine.api.v3.adapters.V3InAdapters.adaptIn;

import org.ovirt.engine.api.model.ExternalHostProviders;
import org.ovirt.engine.api.v3.V3Adapter;
import org.ovirt.engine.api.v3.types.V3ExternalHostProviders;

public class V3ExternalHostProvidersInAdapter implements V3Adapter<V3ExternalHostProviders, ExternalHostProviders> {
    @Override
    public ExternalHostProviders adapt(V3ExternalHostProviders from) {
        ExternalHostProviders to = new ExternalHostProviders();
        if (from.isSetActions()) {
            to.setActions(adaptIn(from.getActions()));
        }
        if (from.isSetActive()) {
            to.setActive(from.getActive());
        }
        if (from.isSetSize()) {
            to.setSize(from.getSize());
        }
        if (from.isSetTotal()) {
            to.setTotal(from.getTotal());
        }
        to.getExternalHostProviders().addAll(adaptIn(from.getExternalHostProviders()));
        return to;
    }
}
