package org.ovirt.engine.core.bll;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.ovirt.engine.core.bll.context.EngineContext;
import org.ovirt.engine.core.common.businessentities.HostDevice;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.dao.HostDeviceDao;
import org.ovirt.engine.core.utils.VgpuTypeUtils;

public class GetHostVgpuTypeByHostIdQuery<P extends IdQueryParameters> extends QueriesCommandBase<P> {

    public GetHostVgpuTypeByHostIdQuery(P parameters, EngineContext engineContext) {
        super(parameters, engineContext);
    }

    @Inject
    HostDeviceDao hostDeviceDao;

    @Override
    protected void executeQueryCommand() {
        List<HostDevice> hostDevices = hostDeviceDao.getHostDevicesByHostId(getParameters().getId());
        for (HostDevice hostDevice : hostDevices) {
            Set<String> vgpuTypes = new HashSet<>();
            for (String mdevType : hostDevice.getMdevTypes()) {
                vgpuTypes.add(VgpuTypeUtils.getVgpuNamebyType(mdevType));
            }
            hostDevice.setMdevTypes(vgpuTypes);
        }
        getQueryReturnValue().setReturnValue(hostDevices);
    }
}
