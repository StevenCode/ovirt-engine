package org.ovirt.engine.core.bll;

import java.util.HashMap;

import org.ovirt.engine.core.bll.context.EngineContext;
import org.ovirt.engine.core.common.queries.QueryParametersBase;
import org.ovirt.engine.core.utils.VgpuTypeUtils;

public class GetHostVgpuIdMapQuery<P extends QueryParametersBase> extends QueriesCommandBase<P> {

    public GetHostVgpuIdMapQuery(P parameters, EngineContext engineContext) {
        super(parameters, engineContext);
    }

    @Override
    protected void executeQueryCommand() {
        HashMap<String, String> vgpuNameMap = VgpuTypeUtils.getVgpuNameMap();
        getQueryReturnValue().setReturnValue(vgpuNameMap);
    }
}
