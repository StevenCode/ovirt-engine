/*
 * Copyright oVirt Authors
 * SPDX-License-Identifier: Apache-2.0
*/

package org.ovirt.engine.api.restapi.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.ovirt.engine.api.model.Watchdog;
import org.ovirt.engine.api.model.WatchdogAction;
import org.ovirt.engine.api.model.WatchdogModel;
import org.ovirt.engine.core.common.action.ActionType;
import org.ovirt.engine.core.common.action.WatchdogParameters;
import org.ovirt.engine.core.common.businessentities.VmWatchdog;
import org.ovirt.engine.core.common.businessentities.VmWatchdogAction;
import org.ovirt.engine.core.common.businessentities.VmWatchdogType;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.common.queries.QueryType;
import org.ovirt.engine.core.compat.Guid;

@MockitoSettings(strictness = Strictness.LENIENT)
public class BackendTemplateWatchdogResourceTest
        extends AbstractBackendSubResourceTest<Watchdog, VmWatchdog, BackendTemplateWatchdogResource> {

    private static final Guid TEMPLATE_ID = GUIDS[1];
    private static final Guid WATCHDOG_ID = GUIDS[0];

    public BackendTemplateWatchdogResourceTest() {
        super(new BackendTemplateWatchdogResource(WATCHDOG_ID.toString(), TEMPLATE_ID));
    }

    @Test
    public void testGet() {
        setUriInfo(setUpBasicUriExpectations());
        setUpEntityQueryExpectations(1);
        Watchdog watchdog = resource.get();
        verifyModel(watchdog);
    }

    @Test
    public void testUpdate() {
        setUpEntityQueryExpectations(2);
        setUriInfo(
            setUpActionExpectations(
                ActionType.UpdateWatchdog,
                WatchdogParameters.class,
                new String[] { "Id" },
                new Object[] { TEMPLATE_ID },
                true,
                true
            )
        );
        Watchdog wd = resource.update(getUpdate());
        assertTrue(wd.isSetAction());
    }

    @Test
    public void testRemove() {
        setUpEntityQueryExpectations(1);
        setUriInfo(
            setUpActionExpectations(
                ActionType.RemoveWatchdog,
                WatchdogParameters.class,
                new String[] { "Id" },
                new Object[] { TEMPLATE_ID },
                true,
                true
            )
        );
        verifyRemove(resource.remove());
    }

    private void setUpEntityQueryExpectations(int cnt) {
        for (int i = 0; i < cnt; i++) {
            setUpGetEntityExpectations(
                QueryType.GetWatchdog,
                IdQueryParameters.class,
                new String[] { "Id" },
                new Object[] { TEMPLATE_ID },
                getEntity()
            );
        }
    }

    private VmWatchdog getEntity() {
        VmWatchdog watchdog = new VmWatchdog();
        watchdog.setId(WATCHDOG_ID);
        watchdog.setAction(VmWatchdogAction.RESET);
        watchdog.setModel(VmWatchdogType.i6300esb);
        return watchdog;
    }

    private Watchdog getUpdate() {
        Watchdog watchdog = new Watchdog();
        watchdog.setId(WATCHDOG_ID.toString());
        watchdog.setAction(WatchdogAction.RESET);
        watchdog.setModel(WatchdogModel.I6300ESB);
        return watchdog;
    }

    private void verifyModel(Watchdog model) {
        assertEquals(WATCHDOG_ID.toString(), model.getId());
        verifyLinks(model);
    }
}
