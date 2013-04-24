package org.ovirt.engine.ui.uicommonweb.models.userportal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.ovirt.engine.core.common.VdcActionUtils;
import org.ovirt.engine.core.common.action.AddVmFromScratchParameters;
import org.ovirt.engine.core.common.action.AddVmFromTemplateParameters;
import org.ovirt.engine.core.common.action.AddVmTemplateParameters;
import org.ovirt.engine.core.common.action.ChangeDiskCommandParameters;
import org.ovirt.engine.core.common.action.ChangeVMClusterParameters;
import org.ovirt.engine.core.common.action.RemoveVmParameters;
import org.ovirt.engine.core.common.action.VdcActionParametersBase;
import org.ovirt.engine.core.common.action.VdcActionType;
import org.ovirt.engine.core.common.action.VmManagementParametersBase;
import org.ovirt.engine.core.common.businessentities.DiskImage;
import org.ovirt.engine.core.common.businessentities.DisplayType;
import org.ovirt.engine.core.common.businessentities.MigrationSupport;
import org.ovirt.engine.core.common.businessentities.Quota;
import org.ovirt.engine.core.common.businessentities.StorageDomain;
import org.ovirt.engine.core.common.businessentities.StoragePool;
import org.ovirt.engine.core.common.businessentities.UsbPolicy;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VDSGroup;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.businessentities.VMStatus;
import org.ovirt.engine.core.common.businessentities.VmOsType;
import org.ovirt.engine.core.common.businessentities.VmPool;
import org.ovirt.engine.core.common.businessentities.VmTemplate;
import org.ovirt.engine.core.common.businessentities.VmType;
import org.ovirt.engine.core.common.queries.VdcQueryParametersBase;
import org.ovirt.engine.core.common.queries.VdcQueryReturnValue;
import org.ovirt.engine.core.common.queries.VdcQueryType;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.compat.NGuid;
import org.ovirt.engine.core.compat.StringHelper;
import org.ovirt.engine.core.compat.Version;
import org.ovirt.engine.ui.frontend.AsyncQuery;
import org.ovirt.engine.ui.frontend.Frontend;
import org.ovirt.engine.ui.frontend.INewAsyncCallback;
import org.ovirt.engine.ui.uicommonweb.Cloner;
import org.ovirt.engine.ui.uicommonweb.ConsoleOptionsFrontendPersister.ConsoleContext;
import org.ovirt.engine.ui.uicommonweb.Linq;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicommonweb.dataprovider.AsyncDataProvider;
import org.ovirt.engine.ui.uicommonweb.models.ConfirmationModel;
import org.ovirt.engine.ui.uicommonweb.models.EntityModel;
import org.ovirt.engine.ui.uicommonweb.models.ListModel;
import org.ovirt.engine.ui.uicommonweb.models.Model;
import org.ovirt.engine.ui.uicommonweb.models.configure.UserPortalPermissionListModel;
import org.ovirt.engine.ui.uicommonweb.models.pools.PoolDiskListModel;
import org.ovirt.engine.ui.uicommonweb.models.pools.PoolGeneralModel;
import org.ovirt.engine.ui.uicommonweb.models.pools.PoolInterfaceListModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.ConsoleModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.NewTemplateVmModelBehavior;
import org.ovirt.engine.ui.uicommonweb.models.vms.RunOnceModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.TimeZoneModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.UnitVmModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.UserPortalExistingVmModelBehavior;
import org.ovirt.engine.ui.uicommonweb.models.vms.UserPortalNewVmModelBehavior;
import org.ovirt.engine.ui.uicommonweb.models.vms.UserPortalRunOnceModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.UserPortalVmEventListModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.UserPortalVmSnapshotListModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.VmAppListModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.VmDiskListModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.VmGeneralModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.VmInterfaceListModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.VmMonitorModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.VmSessionsModel;
import org.ovirt.engine.ui.uicompat.ConstantsManager;
import org.ovirt.engine.ui.uicompat.Event;
import org.ovirt.engine.ui.uicompat.EventArgs;
import org.ovirt.engine.ui.uicompat.EventDefinition;
import org.ovirt.engine.ui.uicompat.FrontendActionAsyncResult;
import org.ovirt.engine.ui.uicompat.FrontendMultipleActionAsyncResult;
import org.ovirt.engine.ui.uicompat.IFrontendActionAsyncCallback;
import org.ovirt.engine.ui.uicompat.IFrontendMultipleActionAsyncCallback;
import org.ovirt.engine.ui.uicompat.ObservableCollection;
import org.ovirt.engine.ui.uicompat.PropertyChangedEventArgs;

public class UserPortalListModel extends IUserPortalListModel implements IVmPoolResolutionService
{

    public static EventDefinition SearchCompletedEventDefinition;
    private Event privateSearchCompletedEvent;

    public Event getSearchCompletedEvent()
    {
        return privateSearchCompletedEvent;
    }

    private void setSearchCompletedEvent(Event value)
    {
        privateSearchCompletedEvent = value;
    }

    private UICommand privateNewDesktopCommand;

    public UICommand getNewDesktopCommand()
    {
        return privateNewDesktopCommand;
    }

    private void setNewDesktopCommand(UICommand value)
    {
        privateNewDesktopCommand = value;
    }

    private UICommand privateNewServerCommand;

    public UICommand getNewServerCommand()
    {
        return privateNewServerCommand;
    }

    private void setNewServerCommand(UICommand value)
    {
        privateNewServerCommand = value;
    }

    private UICommand privateEditCommand;

    @Override
    public UICommand getEditCommand()
    {
        return privateEditCommand;
    }

    private void setEditCommand(UICommand value)
    {
        privateEditCommand = value;
    }

    private UICommand privateRemoveCommand;

    public UICommand getRemoveCommand()
    {
        return privateRemoveCommand;
    }

    private void setRemoveCommand(UICommand value)
    {
        privateRemoveCommand = value;
    }

    private UICommand privateSaveCommand;

    public UICommand getSaveCommand()
    {
        return privateSaveCommand;
    }

    private void setSaveCommand(UICommand value)
    {
        privateSaveCommand = value;
    }

    private UICommand privateRunOnceCommand;

    public UICommand getRunOnceCommand()
    {
        return privateRunOnceCommand;
    }

    private void setRunOnceCommand(UICommand value)
    {
        privateRunOnceCommand = value;
    }

    private UICommand privateChangeCdCommand;

    public UICommand getChangeCdCommand()
    {
        return privateChangeCdCommand;
    }

    private void setChangeCdCommand(UICommand value)
    {
        privateChangeCdCommand = value;
    }

    private UICommand privateNewTemplateCommand;

    public UICommand getNewTemplateCommand()
    {
        return privateNewTemplateCommand;
    }

    private void setNewTemplateCommand(UICommand value)
    {
        privateNewTemplateCommand = value;
    }

    private EntityModel vmGeneralModel;
    private EntityModel vmSessionsModel;
    private ListModel vmSnapshotListModel;
    private EntityModel vmMonitorModel;
    private ListModel vmDiskListModel;
    private ListModel vmInterfaceListModel;
    private ListModel permissionListModel;
    private ListModel vmEventListModel;
    private ListModel vmAppListModel;
    private EntityModel poolGeneralModel;
    private ListModel poolDiskListModel;
    private ListModel poolInterfaceListModel;
    private ArrayList<VM> privatevms;

    public ArrayList<VM> getvms()
    {
        return privatevms;
    }

    public void setvms(ArrayList<VM> value)
    {
        privatevms = value;
    }

    private ArrayList<VmPool> privatepools;

    public ArrayList<VmPool> getpools()
    {
        return privatepools;
    }

    public void setpools(ArrayList<VmPool> value)
    {
        privatepools = value;
    }

    private VM privatetempVm;

    public VM gettempVm()
    {
        return privatetempVm;
    }

    public void settempVm(VM value)
    {
        privatetempVm = value;
    }

    private StorageDomain privatestorageDomain;

    public StorageDomain getstorageDomain()
    {
        return privatestorageDomain;
    }

    public void setstorageDomain(StorageDomain value)
    {
        privatestorageDomain = value;
    }

    private HashMap<Version, ArrayList<String>> CustomPropertiesKeysList;

    public HashMap<Version, ArrayList<String>> getCustomPropertiesKeysList() {
        return CustomPropertiesKeysList;
    }

    public void setCustomPropertiesKeysList(HashMap<Version, ArrayList<String>> customPropertiesKeysList) {
        CustomPropertiesKeysList = customPropertiesKeysList;
    }

    static
    {
        SearchCompletedEventDefinition = new EventDefinition("SearchCompleted", UserPortalListModel.class); //$NON-NLS-1$
    }

    public UserPortalListModel()
    {
        setSearchCompletedEvent(new Event(SearchCompletedEventDefinition));

        setNewDesktopCommand(new UICommand("NewDesktop", this)); //$NON-NLS-1$
        setNewServerCommand(new UICommand("NewServer", this)); //$NON-NLS-1$
        setEditCommand(new UICommand("Edit", this)); //$NON-NLS-1$
        setRemoveCommand(new UICommand("Remove", this)); //$NON-NLS-1$
        setSaveCommand(new UICommand("Save", this)); //$NON-NLS-1$
        setRunOnceCommand(new UICommand("RunOnce", this)); //$NON-NLS-1$
        setChangeCdCommand(new UICommand("ChangeCD", this)); //$NON-NLS-1$
        setNewTemplateCommand(new UICommand("NewTemplate", this)); //$NON-NLS-1$
        setTitle(ConstantsManager.getInstance().getConstants().virtualMachinesTitle());

        updateActionAvailability();

        if (getCustomPropertiesKeysList() == null) {
            AsyncDataProvider.getCustomPropertiesList(new AsyncQuery(this,
                    new INewAsyncCallback() {
                        @Override
                        public void onSuccess(Object target, Object returnValue) {
                            UserPortalListModel model = (UserPortalListModel) target;
                            if (returnValue != null) {
                                model.setCustomPropertiesKeysList((HashMap<Version, ArrayList<String>>) returnValue);
                            }
                        }
                    }));
        }
    }

    @Override
    public void setItems(Iterable value)
    {
        if (items != value)
        {
            itemsChanging(value, items);
            items = value;
            getItemsChangedEvent().raise(this, EventArgs.Empty);
            onPropertyChanged(new PropertyChangedEventArgs("Items")); //$NON-NLS-1$

            UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
            if (items != null && selectedItem != null) {
                for (Object object : items) {
                    UserPortalItemModel itemModel = (UserPortalItemModel) object;
                    if (itemModel.getEntity().equals(selectedItem.getEntity())) {
                        this.selectedItem = itemModel;
                        break;
                    }
                }
            }
            onSelectedItemChanged();
        }
    }

    @Override
    protected void syncSearch()
    {
        super.syncSearch();
        VdcQueryParametersBase queryParameters = new VdcQueryParametersBase();
        queryParameters.setRefresh(getIsQueryFirstTime());
        Frontend.RunQuery(VdcQueryType.GetAllVmsAndVmPools, queryParameters,
                new AsyncQuery(this, new INewAsyncCallback() {

                    @Override
                    public void onSuccess(Object model, Object returnValue) {
                        UserPortalListModel userPortalListModel = (UserPortalListModel) model;
                        ArrayList<VM> vms = new ArrayList<VM>();
                        ArrayList<VmPool> pools = new ArrayList<VmPool>();

                        VdcQueryReturnValue retValue = (VdcQueryReturnValue) returnValue;
                        if (retValue != null && retValue.getSucceeded()) {
                            List<Object> list = (ArrayList<Object>) retValue.getReturnValue();
                            if (list != null) {
                                for (Object object : list) {
                                    if (object instanceof VM) {
                                        vms.add((VM) object);
                                    } else if (object instanceof VmPool) {
                                        pools.add((VmPool) object);
                                    }
                                }
                            }
                        }

                        userPortalListModel.setvms(vms);
                        userPortalListModel.setpools(pools);
                        userPortalListModel.onVmAndPoolLoad();
                    }
                }));
    }

    @Override
    protected void initDetailModels()
    {
        super.initDetailModels();

        vmGeneralModel = new VmGeneralModel();
        vmGeneralModel.setIsAvailable(false);

        vmSessionsModel = new VmSessionsModel();
        vmSessionsModel.setIsAvailable(false);

        vmSnapshotListModel = new UserPortalVmSnapshotListModel();
        vmSnapshotListModel.setIsAvailable(false);

        vmMonitorModel = new VmMonitorModel();
        vmMonitorModel.setIsAvailable(false);

        vmDiskListModel = new VmDiskListModel();
        vmDiskListModel.setIsAvailable(false);

        vmInterfaceListModel = new VmInterfaceListModel();
        vmInterfaceListModel.setIsAvailable(false);

        permissionListModel = new UserPortalPermissionListModel();
        permissionListModel.setIsAvailable(false);

        vmEventListModel = new UserPortalVmEventListModel();
        vmEventListModel.setIsAvailable(false);

        vmAppListModel = new VmAppListModel();
        vmAppListModel.setIsAvailable(false);

        poolGeneralModel = new PoolGeneralModel();
        poolGeneralModel.setIsAvailable(false);

        poolDiskListModel = new PoolDiskListModel();
        poolDiskListModel.setIsAvailable(false);

        poolInterfaceListModel = new PoolInterfaceListModel();
        poolInterfaceListModel.setIsAvailable(false);

        ObservableCollection<EntityModel> list = new ObservableCollection<EntityModel>();
        list.add(vmGeneralModel);
        list.add(poolGeneralModel);
        list.add(vmInterfaceListModel);
        list.add(poolInterfaceListModel);
        list.add(vmDiskListModel);
        list.add(poolDiskListModel);
        list.add(vmSnapshotListModel);
        list.add(permissionListModel);
        list.add(vmEventListModel);
        list.add(vmAppListModel);
        list.add(vmMonitorModel);
        list.add(vmSessionsModel);

        setDetailModels(list);

        permissionListModel.setIsAvailable(true);
        vmEventListModel.setIsAvailable(true);
        vmAppListModel.setIsAvailable(true);
        vmSessionsModel.setIsAvailable(true);
    }

    @Override
    protected Object provideDetailModelEntity(Object selectedItem)
    {
        // Each item in this list model is not a business entity,
        // therefore select an Entity property to provide it to
        // the detail models.

        EntityModel model = (EntityModel) selectedItem;
        if (model == null)
        {
            return null;
        }

        return model.getEntity();
    }

    @Override
    protected void updateDetailsAvailability()
    {
        super.updateDetailsAvailability();

        UserPortalItemModel item = (UserPortalItemModel) getSelectedItem();

        vmGeneralModel.setIsAvailable(item != null && !item.isPool());
        vmSnapshotListModel.setIsAvailable(item != null && !item.isPool());
        vmMonitorModel.setIsAvailable(item != null && !item.isPool());
        vmDiskListModel.setIsAvailable(item != null && !item.isPool());
        vmInterfaceListModel.setIsAvailable(item != null && !item.isPool());
        vmEventListModel.setIsAvailable(item != null && !item.isPool());

        poolGeneralModel.setIsAvailable(item != null && item.isPool());
        poolDiskListModel.setIsAvailable(item != null && item.isPool());
        poolInterfaceListModel.setIsAvailable(item != null && item.isPool());
    }

    @Override
    public void executeCommand(UICommand command)
    {
        super.executeCommand(command);

        if (command == getNewDesktopCommand())
        {
            newDesktop();
        }
        if (command == getNewServerCommand())
        {
            newServer();
        }
        else if (command == getEditCommand())
        {
            edit();
        }
        else if (command == getRemoveCommand())
        {
            remove();
        }
        else if (command == getSaveCommand())
        {
            onSave();
        }
        else if (command == getRunOnceCommand())
        {
            runOnce();
        }
        else if (command == getChangeCdCommand())
        {
            changeCD();
        }
        else if (command == getNewTemplateCommand())
        {
            newTemplate();
        }
        else if (StringHelper.stringsEqual(command.getName(), "OnRemove")) //$NON-NLS-1$
        {
            onRemove();
        }
        else if (StringHelper.stringsEqual(command.getName(), "OnRunOnce")) //$NON-NLS-1$
        {
            cancel();
        }
        else if (StringHelper.stringsEqual(command.getName(), "OnChangeCD")) //$NON-NLS-1$
        {
            onChangeCD();
        }
        else if (StringHelper.stringsEqual(command.getName(), "OnNewTemplate")) //$NON-NLS-1$
        {
            onNewTemplate();
        }
        else if (StringHelper.stringsEqual(command.getName(), "OnSave")) //$NON-NLS-1$
        {
            onSave();
        }
        else if (command.getName().equals("closeVncInfo")) { //$NON-NLS-1$
            setWindow(null);
        }
    }

    private void newTemplate()
    {
        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
        if (selectedItem == null)
        {
            return;
        }

        VM vm = (VM) selectedItem.getEntity();
        UnitVmModel windowModel = new UnitVmModel(new NewTemplateVmModelBehavior(vm));
        setWindow(windowModel);
        windowModel.setTitle(ConstantsManager.getInstance().getConstants().newTemplateTitle());
        windowModel.setHashName("new_template"); //$NON-NLS-1$
        windowModel.setIsNew(true);
        windowModel.setVmType(vm.getVmType());
        windowModel.initialize(null);
        windowModel.getIsTemplatePublic().setEntity(false);

        UICommand tempVar = new UICommand("OnNewTemplate", this); //$NON-NLS-1$
        tempVar.setTitle(ConstantsManager.getInstance().getConstants().ok());
        tempVar.setIsDefault(true);
        windowModel.getCommands().add(tempVar);
        UICommand tempVar2 = new UICommand("Cancel", this); //$NON-NLS-1$
        tempVar2.setTitle(ConstantsManager.getInstance().getConstants().cancel());
        tempVar2.setIsCancel(true);
        windowModel.getCommands().add(tempVar2);
    }

    private void onNewTemplate()
    {
        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
        if (selectedItem == null)
        {
            cancel();
            return;
        }

        UnitVmModel model = (UnitVmModel) getWindow();

        if (!model.validate())
        {
            model.setIsValid(false);
        }
        else
        {
            model.startProgress(null);
            String name = (String) model.getName().getEntity();

            // Check name unicitate.
            AsyncDataProvider.isTemplateNameUnique(new AsyncQuery(this,
                    new INewAsyncCallback() {
                        @Override
                        public void onSuccess(Object target, Object returnValue) {

                            UserPortalListModel userPortalListModel = (UserPortalListModel) target;
                            boolean isNameUnique = (Boolean) returnValue;
                            if (!isNameUnique)
                            {

                                UnitVmModel vmModel = (UnitVmModel) userPortalListModel.getWindow();
                                vmModel.getName().getInvalidityReasons().clear();
                                vmModel
                                        .getName()
                                        .getInvalidityReasons()
                                        .add(ConstantsManager.getInstance()
                                                .getConstants()
                                                .nameMustBeUniqueInvalidReason());
                                vmModel.getName().setIsValid(false);
                                vmModel.setIsValid(false);
                                stopProgress(target);
                            }
                            else
                            {
                                userPortalListModel.postNameUniqueCheck(userPortalListModel);
                            }

                        }
                    }),
                    name);
        }
    }

    public void postNameUniqueCheck(UserPortalListModel userPortalListModel)
    {
        UnitVmModel model = (UnitVmModel) userPortalListModel.getWindow();
        UserPortalItemModel selectedItem = (UserPortalItemModel) userPortalListModel.getSelectedItem();
        VM vm = (VM) selectedItem.getEntity();

        VM tempVar = new VM();
        tempVar.setId(vm.getId());
        tempVar.setVmType(model.getVmType());
        tempVar.setVmOs((VmOsType) model.getOSType().getSelectedItem());
        tempVar.setNumOfMonitors((Integer) model.getNumOfMonitors().getSelectedItem());
        tempVar.setAllowConsoleReconnect((Boolean) model.getAllowConsoleReconnect().getEntity());
        tempVar.setVmDomain(model.getDomain().getIsAvailable() ? (String) model.getDomain().getSelectedItem() : ""); //$NON-NLS-1$
        tempVar.setVmMemSizeMb((Integer) model.getMemSize().getEntity());
        tempVar.setMinAllocatedMem((Integer) model.getMinAllocatedMemory().getEntity());
        tempVar.setVdsGroupId(((VDSGroup) model.getCluster().getSelectedItem()).getId());
        tempVar.setTimeZone(model.getTimeZone().getIsAvailable() && model.getTimeZone().getSelectedItem() != null ? ((TimeZoneModel) model.getTimeZone()
                .getSelectedItem()).getTimeZoneKey()
                : ""); //$NON-NLS-1$
        tempVar.setNumOfSockets((Integer) model.getNumOfSockets().getSelectedItem());
        tempVar.setCpuPerSocket(Integer.parseInt(model.getTotalCPUCores().getEntity().toString())
                / (Integer) model.getNumOfSockets().getSelectedItem());
        tempVar.setDeleteProtected((Boolean) model.getIsDeleteProtected().getEntity());
        tempVar.setUsbPolicy((UsbPolicy) model.getUsbPolicy().getSelectedItem());
        tempVar.setAutoSuspend(false);
        tempVar.setStateless((Boolean) model.getIsStateless().getEntity());
        tempVar.setRunAndPause(((Boolean) model.getIsRunAndPause().getEntity()));
        tempVar.setSmartcardEnabled((Boolean) model.getIsSmartcardEnabled().getEntity());
        tempVar.setDefaultBootSequence(model.getBootSequence());
        tempVar.setAutoStartup((Boolean) model.getIsHighlyAvailable().getEntity());
        tempVar.setIsoPath(model.getCdImage().getIsChangable() ? (String) model.getCdImage().getSelectedItem() : ""); //$NON-NLS-1$
        tempVar.setInitrdUrl(vm.getInitrdUrl());
        tempVar.setKernelUrl(vm.getKernelUrl());
        tempVar.setKernelParams(vm.getKernelParams());
        tempVar.setVncKeyboardLayout(vm.getVncKeyboardLayout());
        VM newvm = tempVar;

        EntityModel displayProtocolSelectedItem = (EntityModel) model.getDisplayProtocol().getSelectedItem();
        newvm.setDefaultDisplayType((DisplayType) displayProtocolSelectedItem.getEntity());

        EntityModel prioritySelectedItem = (EntityModel) model.getPriority().getSelectedItem();
        newvm.setPriority((Integer) prioritySelectedItem.getEntity());

        AddVmTemplateParameters addVmTemplateParameters =
                new AddVmTemplateParameters(newvm,
                        (String) model.getName().getEntity(),
                        (String) model.getDescription().getEntity());

        addVmTemplateParameters.setPublicUse((Boolean) model.getIsTemplatePublic().getEntity());

        if (model.getQuota().getSelectedItem() != null) {
            newvm.setQuotaId(((Quota) model.getQuota().getSelectedItem()).getId());
        }

        addVmTemplateParameters.setDiskInfoDestinationMap(model.getDisksAllocationModel()
                .getImageToDestinationDomainMap());

        Frontend.RunAction(VdcActionType.AddVmTemplate, addVmTemplateParameters,
                new IFrontendActionAsyncCallback() {
                    @Override
                    public void executed(FrontendActionAsyncResult result) {
                        stopProgress(result.getState());
                        cancel();
                    }
                }, this);
    }

    @Override
    protected void onSelectedItemChanged()
    {
        super.onSelectedItemChanged();
        updateActionAvailability();
    }

    private void runOnce()
    {
        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
        if (selectedItem == null || selectedItem.getEntity() == null) {
            return;
        }

        VM vm = (VM) selectedItem.getEntity();

        RunOnceModel model = new UserPortalRunOnceModel(vm,
                getCustomPropertiesKeysList().get(vm.getVdsGroupCompatibilityVersion()),
                this);
        setWindow(model);

        model.init();
    }

    private void updateActionAvailability()
    {
        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();

        getEditCommand().setIsExecutionAllowed(selectedItem != null && !selectedItem.isPool());

        getRemoveCommand().setIsExecutionAllowed(selectedItem != null
                && !selectedItem.isPool()
                && VdcActionUtils.CanExecute(new ArrayList<VM>(Arrays.asList(new VM[] { (VM) selectedItem.getEntity() })),
                        VM.class,
                        VdcActionType.RemoveVm));

        getRunOnceCommand().setIsExecutionAllowed(selectedItem != null
                && !selectedItem.isPool()
                && VdcActionUtils.CanExecute(new ArrayList<VM>(Arrays.asList(new VM[] { (VM) selectedItem.getEntity() })),
                        VM.class,
                        VdcActionType.RunVmOnce));

        getChangeCdCommand().setIsExecutionAllowed(selectedItem != null
                && !selectedItem.isPool()
                && VdcActionUtils.CanExecute(new ArrayList<VM>(Arrays.asList(new VM[] { (VM) selectedItem.getEntity() })),
                        VM.class,
                        VdcActionType.ChangeDisk));

        getNewTemplateCommand().setIsExecutionAllowed(selectedItem != null
                && !selectedItem.isPool()
                && VdcActionUtils.CanExecute(new ArrayList<VM>(Arrays.asList(new VM[] { (VM) selectedItem.getEntity() })),
                        VM.class,
                        VdcActionType.AddVmTemplate));
    }

    private void newDesktop()
    {
        newInternal(VmType.Desktop);
    }

    private void newServer()
    {
        newInternal(VmType.Server);
    }

    private void newInternal(VmType vmType)
    {
        UnitVmModel model = new UnitVmModel(new UserPortalNewVmModelBehavior());
        setWindow(model);
        model.setTitle(ConstantsManager.getInstance()
                .getMessages()
                .newVmTitle(vmType == VmType.Server ? ConstantsManager.getInstance().getConstants().serverVmType()
                        : ConstantsManager.getInstance().getConstants().desktopVmType()));
        model.setHashName("new_" + (vmType == VmType.Server ? "server" : "desktop")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        model.setIsNew(true);
        model.setVmType(vmType);
        model.setCustomPropertiesKeysList(CustomPropertiesKeysList);

        model.initialize(null);

        // Ensures that the default provisioning is "Clone" for a new server and "Thin" for a new desktop.
        boolean selectValue = model.getVmType() == VmType.Server;
        model.getProvisioning().setEntity(selectValue);

        UICommand tempVar = new UICommand("OnSave", this); //$NON-NLS-1$
        tempVar.setTitle(ConstantsManager.getInstance().getConstants().ok());
        tempVar.setIsDefault(true);
        model.getCommands().add(tempVar);
        UICommand tempVar2 = new UICommand("Cancel", this); //$NON-NLS-1$
        tempVar2.setTitle(ConstantsManager.getInstance().getConstants().cancel());
        tempVar2.setIsCancel(true);
        model.getCommands().add(tempVar2);
    }

    private void edit()
    {
        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
        if (selectedItem == null)
        {
            return;
        }

        if (getWindow() != null)
        {
            return;
        }

        VM vm = (VM) selectedItem.getEntity();

        UnitVmModel model = new UnitVmModel(new UserPortalExistingVmModelBehavior(vm));

        model.setTitle(ConstantsManager.getInstance()
                .getMessages()
                .editVmTitle(vm.getVmType() == VmType.Server ? ConstantsManager.getInstance()
                        .getConstants()
                        .serverVmType()
                        : ConstantsManager.getInstance().getConstants().desktopVmType()));
        model.setHashName("edit_" + (vm.getVmType() == VmType.Server ? "server" : "desktop")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        model.setVmType(vm.getVmType());
        model.setCustomPropertiesKeysList(CustomPropertiesKeysList);

        setWindow(model);

        model.initialize(null);

        UICommand tempVar = new UICommand("OnSave", this); //$NON-NLS-1$
        tempVar.setTitle(ConstantsManager.getInstance().getConstants().ok());
        tempVar.setIsDefault(true);
        model.getCommands().add(tempVar);
        UICommand tempVar2 = new UICommand("Cancel", this); //$NON-NLS-1$
        tempVar2.setTitle(ConstantsManager.getInstance().getConstants().cancel());
        tempVar2.setIsCancel(true);
        model.getCommands().add(tempVar2);

    }

    private void remove()
    {
        if (getConfirmWindow() != null)
        {
            return;
        }

        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
        VM vm = (VM) selectedItem.getEntity();

        ConfirmationModel confirmModel = new ConfirmationModel();
        setConfirmWindow(confirmModel);

        confirmModel.setTitle(ConstantsManager.getInstance().getConstants().removeVirtualMachineTitle());
        confirmModel.setHashName("remove_virtual_machine"); //$NON-NLS-1$
        confirmModel.setMessage(ConstantsManager.getInstance().getConstants().virtualMachineMsg());

        ArrayList<String> list = new ArrayList<String>();
        list.add(vm.getName());
        confirmModel.setItems(list);

        UICommand tempVar = new UICommand("OnRemove", this); //$NON-NLS-1$
        tempVar.setTitle(ConstantsManager.getInstance().getConstants().ok());
        tempVar.setIsDefault(true);
        getConfirmWindow().getCommands().add(tempVar);
        UICommand tempVar2 = new UICommand("Cancel", this); //$NON-NLS-1$
        tempVar2.setTitle(ConstantsManager.getInstance().getConstants().cancel());
        tempVar2.setIsCancel(true);
        getConfirmWindow().getCommands().add(tempVar2);
    }

    private void onRemove()
    {
        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
        VM vm = (VM) selectedItem.getEntity();

        getConfirmWindow().startProgress(null);

        Frontend.RunAction(VdcActionType.RemoveVm, new RemoveVmParameters(vm.getId(), false),
                new IFrontendActionAsyncCallback() {
                    @Override
                    public void executed(FrontendActionAsyncResult result) {
                        ConfirmationModel model =
                                (ConfirmationModel) ((UserPortalListModel) result.getState()).getConfirmWindow();
                        model.stopProgress();
                        cancel();
                    }
                },
                this);

    }

    private void changeCD()
    {
        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
        if (selectedItem == null || selectedItem.getEntity() == null)
        {
            return;
        }

        VM vm = (VM) selectedItem.getEntity();

        AttachCdModel model = new AttachCdModel();
        setWindow(model);
        model.setTitle(ConstantsManager.getInstance().getConstants().changeCDTitle());
        model.setHashName("change_cd"); //$NON-NLS-1$

        ArrayList<String> defaultImages =
                new ArrayList<String>(Arrays.asList(new String[] { ConstantsManager.getInstance()
                        .getConstants()
                        .noCds() }));
        model.getIsoImage().setItems(defaultImages);
        model.getIsoImage().setSelectedItem(Linq.firstOrDefault(defaultImages));

        AsyncQuery getImagesQuery = new AsyncQuery();
        getImagesQuery.setModel(this);

        getImagesQuery.asyncCallback = new INewAsyncCallback() {
            @Override
            public void onSuccess(Object model1, Object result)
            {
                UserPortalListModel userPortalListModel = (UserPortalListModel) model1;
                AttachCdModel _attachCdModel = (AttachCdModel) userPortalListModel.getWindow();
                List<String> images = (List<String>) result;
                images.add(0, ConsoleModel.EjectLabel);
                _attachCdModel.getIsoImage().setItems(images);
                if (_attachCdModel.getIsoImage().getIsChangable())
                {
                    _attachCdModel.getIsoImage().setSelectedItem(Linq.firstOrDefault(images));
                }
            }
        };

        AsyncDataProvider.getIrsImageList(getImagesQuery, vm.getStoragePoolId());

        UICommand tempVar = new UICommand("OnChangeCD", this); //$NON-NLS-1$
        tempVar.setTitle(ConstantsManager.getInstance().getConstants().ok());
        tempVar.setIsDefault(true);
        model.getCommands().add(tempVar);
        UICommand tempVar2 = new UICommand("Cancel", this); //$NON-NLS-1$
        tempVar2.setTitle(ConstantsManager.getInstance().getConstants().cancel());
        tempVar2.setIsCancel(true);
        model.getCommands().add(tempVar2);
    }

    private void onChangeCD()
    {
        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
        if (selectedItem == null || selectedItem.getEntity() == null)
        {
            cancel();
            return;
        }

        VM vm = (VM) selectedItem.getEntity();
        AttachCdModel model = (AttachCdModel) getWindow();
        model.startProgress(null);
        String isoName =
                (StringHelper.stringsEqual(model.getIsoImage().getSelectedItem().toString(), ConsoleModel.EjectLabel)) ? "" //$NON-NLS-1$
                        : model.getIsoImage().getSelectedItem().toString();

        Frontend.RunAction(VdcActionType.ChangeDisk, new ChangeDiskCommandParameters(vm.getId(), isoName),
                new IFrontendActionAsyncCallback() {
                    @Override
                    public void executed(FrontendActionAsyncResult result) {
                        stopProgress(result.getState());
                        cancel();
                    }
                }, this);
    }

    private void onSave()
    {

        final UnitVmModel model = (UnitVmModel) getWindow();
        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
        if (!model.getIsNew() && selectedItem.getEntity() == null)
        {
            cancel();
            return;
        }

        settempVm(model.getIsNew() ? new VM() : (VM) Cloner.clone(selectedItem.getEntity()));

        if (!model.validate())
        {
            return;
        }

        model.startProgress(null);
        // Check name uniqueness.
        AsyncDataProvider.isVmNameUnique(new AsyncQuery(this,
                new INewAsyncCallback() {
                    @Override
                    public void onSuccess(Object target, Object returnValue) {

                        UserPortalListModel userPortalListModel = (UserPortalListModel) target;
                        boolean isNameUnique = (Boolean) returnValue;
                        String newName = (String) model.getName().getEntity();
                        String currentName = userPortalListModel.gettempVm().getName();
                        if (!isNameUnique && newName.compareToIgnoreCase(currentName) != 0)
                        {
                            UnitVmModel unitModel = (UnitVmModel) userPortalListModel.getWindow();
                            unitModel.getName().getInvalidityReasons().clear();
                            unitModel
                                    .getName()
                                    .getInvalidityReasons()
                                    .add(ConstantsManager.getInstance().getConstants().nameMustBeUniqueInvalidReason());
                            unitModel.getName().setIsValid(false);
                            unitModel.setIsValid(false);
                            unitModel.setIsGeneralTabValid(false);
                            stopProgress(target);
                        }
                        else
                        {
                            userPortalListModel.postVmNameUniqueCheck(userPortalListModel);
                        }

                    }
                }),
                (String) model.getName().getEntity());
    }

    private void stopProgress(Object target) {
        if (target instanceof UserPortalListModel) {
            Model window = ((UserPortalListModel) target).getWindow();
            if (window != null) {
                window.stopProgress();
            }
        }
    }

    public void postVmNameUniqueCheck(UserPortalListModel userPortalListModel)
    {

        UnitVmModel model = (UnitVmModel) getWindow();
        UserPortalItemModel selectedItem = (UserPortalItemModel) userPortalListModel.getSelectedItem();
        String name = (String) model.getName().getEntity();

        // Save changes.
        VmTemplate template = (VmTemplate) model.getTemplate().getSelectedItem();

        gettempVm().setVmType(model.getVmType());
        gettempVm().setVmtGuid(template.getId());
        gettempVm().setName(name);
        gettempVm().setVmOs((VmOsType) model.getOSType().getSelectedItem());
        gettempVm().setNumOfMonitors((Integer) model.getNumOfMonitors().getSelectedItem());
        gettempVm().setAllowConsoleReconnect((Boolean) model.getAllowConsoleReconnect().getEntity());
        gettempVm().setVmDescription((String) model.getDescription().getEntity());
        gettempVm().setVmDomain(model.getDomain().getIsAvailable() ? (String) model.getDomain()
                .getSelectedItem() : ""); //$NON-NLS-1$
        gettempVm().setVmMemSizeMb((Integer) model.getMemSize().getEntity());
        gettempVm().setMinAllocatedMem((Integer) model.getMinAllocatedMemory().getEntity());
        Guid newClusterID = ((VDSGroup) model.getCluster().getSelectedItem()).getId();
        gettempVm().setVdsGroupId(newClusterID);
        gettempVm().setTimeZone((model.getTimeZone().getIsAvailable() && model.getTimeZone()
                .getSelectedItem() != null) ? ((TimeZoneModel) model.getTimeZone()
                .getSelectedItem()).getTimeZoneKey() : ""); //$NON-NLS-1$
        gettempVm().setNumOfSockets((Integer) model.getNumOfSockets().getSelectedItem());
        gettempVm().setCpuPerSocket(Integer.parseInt(model.getTotalCPUCores().getEntity().toString())
                / (Integer) model.getNumOfSockets().getSelectedItem());
        gettempVm().setUsbPolicy((UsbPolicy) model.getUsbPolicy().getSelectedItem());
        gettempVm().setAutoSuspend(false);
        gettempVm().setStateless((Boolean) model.getIsStateless().getEntity());
        gettempVm().setRunAndPause(((Boolean) model.getIsRunAndPause().getEntity()));
        gettempVm().setDeleteProtected((Boolean) model.getIsDeleteProtected().getEntity());
        gettempVm().setSmartcardEnabled((Boolean) model.getIsSmartcardEnabled().getEntity());
        gettempVm().setDefaultBootSequence(model.getBootSequence());
        gettempVm().setIsoPath(model.getCdImage().getIsChangable() ? (String) model.getCdImage()
                .getSelectedItem() : ""); //$NON-NLS-1$
        gettempVm().setAutoStartup((Boolean) model.getIsHighlyAvailable().getEntity());
        gettempVm().setVncKeyboardLayout((String) model.getVncKeyboardLayout().getSelectedItem());

        gettempVm().setInitrdUrl((String) model.getInitrd_path().getEntity());
        gettempVm().setKernelUrl((String) model.getKernel_path().getEntity());
        gettempVm().setKernelParams((String) model.getKernel_parameters().getEntity());

        gettempVm().setCustomProperties(model.getCustomPropertySheet().getEntity());

        EntityModel displayProtocolSelectedItem = (EntityModel) model.getDisplayProtocol().getSelectedItem();
        gettempVm().setDefaultDisplayType((DisplayType) displayProtocolSelectedItem.getEntity());

        EntityModel prioritySelectedItem = (EntityModel) model.getPriority().getSelectedItem();
        gettempVm().setPriority((Integer) prioritySelectedItem.getEntity());

        if (model.getQuota().getSelectedItem() != null) {
            gettempVm().setQuotaId(((Quota) model.getQuota().getSelectedItem()).getId());
        }

        gettempVm().setCpuPinning((String) model.getCpuPinning()
                .getEntity());

        VDS defaultHost = (VDS) model.getDefaultHost().getSelectedItem();
        if ((Boolean) model.getIsAutoAssign().getEntity())
        {
            gettempVm().setDedicatedVmForVds(null);
        }
        else
        {
            gettempVm().setDedicatedVmForVds(defaultHost.getId());
        }

        gettempVm().setMigrationSupport((MigrationSupport) model.getMigrationMode().getSelectedItem());

        if (model.getIsNew())
        {
            if (gettempVm().getVmtGuid().equals(NGuid.Empty))
            {
                AddVmFromScratchParameters parameters =
                        new AddVmFromScratchParameters(gettempVm(),
                                new ArrayList<DiskImage>(),
                                NGuid.Empty);
                parameters.setMakeCreatorExplicitOwner(true);

                Frontend.RunAction(VdcActionType.AddVmFromScratch, parameters,
                        new IFrontendActionAsyncCallback() {
                            @Override
                            public void executed(FrontendActionAsyncResult result) {
                                stopProgress(result.getState());
                                cancel();
                            }
                        }, this);
            }
            else
            {
                setstorageDomain((StorageDomain) model.getStorageDomain().getSelectedItem());

                if ((Boolean) model.getProvisioning().getEntity())
                {
                    AsyncQuery _asyncQuery = new AsyncQuery();
                    _asyncQuery.setModel(this);
                    _asyncQuery.asyncCallback = new INewAsyncCallback() {
                        @Override
                        public void onSuccess(Object model, Object result)
                        {
                            UserPortalListModel userPortalListModel1 = (UserPortalListModel) model;
                            UnitVmModel unitVmModel = (UnitVmModel) userPortalListModel1.getWindow();

                            AddVmFromTemplateParameters param = new AddVmFromTemplateParameters(gettempVm(),
                                    unitVmModel.getDisksAllocationModel().getImageToDestinationDomainMap(),
                                    Guid.Empty);
                            param.setMakeCreatorExplicitOwner(true);

                            ArrayList<VdcActionParametersBase> parameters = new ArrayList<VdcActionParametersBase>();
                            parameters.add(param);

                            Frontend.RunMultipleAction(VdcActionType.AddVmFromTemplate, parameters,
                                    new IFrontendMultipleActionAsyncCallback() {
                                        @Override
                                        public void executed(FrontendMultipleActionAsyncResult a) {
                                            stopProgress(a.getState());
                                            cancel();
                                        }
                                    },
                                    this);
                        }
                    };
                    AsyncDataProvider.getTemplateDiskList(_asyncQuery, template.getId());
                }
                else
                {
                    VmManagementParametersBase param = new VmManagementParametersBase(gettempVm());
                    param.setDiskInfoDestinationMap(model.getDisksAllocationModel().getImageToDestinationDomainMap());
                    param.setMakeCreatorExplicitOwner(true);

                    ArrayList<VdcActionParametersBase> parameters = new ArrayList<VdcActionParametersBase>();
                    parameters.add(param);
                    Frontend.RunMultipleAction(VdcActionType.AddVm, parameters,
                            new IFrontendMultipleActionAsyncCallback() {
                        @Override
                        public void executed(FrontendMultipleActionAsyncResult a) {
                            stopProgress(a.getState());
                            cancel();
                        }
                    },
                    this);
                }
            }
        }
        else
        {
            Guid oldClusterID = ((VM) selectedItem.getEntity()).getVdsGroupId();
            if (oldClusterID.equals(newClusterID) == false)
            {
                Frontend.RunAction(VdcActionType.ChangeVMCluster, new ChangeVMClusterParameters(newClusterID,
                        gettempVm().getId()),
                        new IFrontendActionAsyncCallback() {
                    @Override
                    public void executed(FrontendActionAsyncResult result) {

                        Frontend.RunAction(VdcActionType.UpdateVm, new VmManagementParametersBase(gettempVm()),
                                new IFrontendActionAsyncCallback() {
                            @Override
                            public void executed(FrontendActionAsyncResult a) {
                                stopProgress(a.getState());
                                cancel();
                            }
                        }, this);

                    }
                }, this);
            }
            else
            {
                Frontend.RunAction(VdcActionType.UpdateVm, new VmManagementParametersBase(gettempVm()),
                        new IFrontendActionAsyncCallback() {
                            @Override
                            public void executed(FrontendActionAsyncResult a) {
                                stopProgress(a.getState());
                                cancel();
                            }
                        }, this);
            }
        }
    }

    private void vmModel_DataCenter_ItemsChanged()
    {
        UnitVmModel model = (UnitVmModel) getWindow();
        StoragePool dataCenter = null;
        for (Object item : model.getDataCenter().getItems())
        {
            StoragePool a = (StoragePool) item;

            if (model.getIsNew())
            {
                dataCenter = a;
                break;
            }
            else
            {
                UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
                VM vm = (VM) selectedItem.getEntity();

                if (a.getId().equals(vm.getStoragePoolId()))
                {
                    dataCenter = a;
                    break;
                }
            }
        }

        if (!model.getIsNew() && dataCenter == null)
        {
            UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
            VM vm = (VM) selectedItem.getEntity();
            AsyncQuery _asyncQuery = new AsyncQuery();
            _asyncQuery.setModel(this);
            _asyncQuery.asyncCallback = new INewAsyncCallback() {
                @Override
                public void onSuccess(Object model, Object result)
                {
                    UserPortalListModel userPortalListModel = (UserPortalListModel) model;
                    ArrayList<StoragePool> list =
                            new ArrayList<StoragePool>(Arrays.asList(new StoragePool[] { (StoragePool) result }));
                    UnitVmModel unitModel = (UnitVmModel) userPortalListModel.getWindow();
                    unitModel.getDataCenter().setItems(list);
                    unitModel.getDataCenter().setSelectedItem(Linq.firstOrDefault(list));

                }
            };
            AsyncDataProvider.getDataCenterById(_asyncQuery, vm.getStoragePoolId());
        }
        else
        {
            model.getDataCenter().setSelectedItem(dataCenter);
        }
    }

    private void vmModel_Cluster_ItemsChanged()
    {

        UnitVmModel model = (UnitVmModel) getWindow();
        if (!model.getIsNew())
        {
            UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
            VM vm = (VM) selectedItem.getEntity();
            VDSGroup cluster = null;

            for (Object item : model.getCluster().getItems())
            {
                VDSGroup a = (VDSGroup) item;
                if (a.getId().equals(vm.getVdsGroupId()))
                {
                    cluster = a;
                    break;
                }
            }
            model.getCluster().setSelectedItem(cluster);

            model.getCluster().setIsChangable(vm.getStatus() == VMStatus.Down);
        }
    }

    private void vmModel_DefaultHost_ItemsChanged()
    {
        UnitVmModel model = (UnitVmModel) getWindow();
        if (!model.getIsNew())
        {
            UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
            VM vm = (VM) selectedItem.getEntity();
            VDS host = null;

            for (Object item : model.getDefaultHost().getItems())
            {
                VDS a = (VDS) item;
                if (a.getId().equals(((vm.getDedicatedVmForVds() != null) ? vm.getDedicatedVmForVds()
                        : NGuid.Empty)))
                {
                    host = a;
                    break;
                }
            }
            if (host == null)
            {
                model.getIsAutoAssign().setEntity(true);
            }
            else
            {
                model.getDefaultHost().setSelectedItem(host);
                model.getIsAutoAssign().setEntity(false);
            }
        }
    }

    private void vmModel_DisplayProtocol_ItemsChanged()
    {
        UnitVmModel model = (UnitVmModel) getWindow();
        if (!model.getIsNew())
        {
            UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
            VM vm = (VM) selectedItem.getEntity();
            EntityModel displayType = null;

            for (Object item : model.getDisplayProtocol().getItems())
            {
                EntityModel a = (EntityModel) item;
                DisplayType dt = (DisplayType) a.getEntity();
                if (dt == vm.getDefaultDisplayType())
                {
                    displayType = a;
                    break;
                }
            }
            model.getDisplayProtocol().setSelectedItem(displayType);
        }
    }

    private Integer cachedMaxPriority;

    private void vmModel_Priority_ItemsChanged()
    {
        UnitVmModel model = (UnitVmModel) getWindow();
        if (!model.getIsNew())
        {
            if (cachedMaxPriority == null) {
                AsyncDataProvider.getMaxVmPriority(new AsyncQuery(model,
                        new INewAsyncCallback() {
                            @Override
                            public void onSuccess(Object target, Object returnValue) {
                                cachedMaxPriority = (Integer) returnValue;
                                updatePriority((UnitVmModel) target);
                            }
                        }, model.getHash()));
            } else {
                updatePriority(model);
            }
        }
    }

    private void updatePriority(UnitVmModel model) {
        UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
        VM vm = (VM) selectedItem.getEntity();
        int roundPriority = AsyncDataProvider.getRoundedPriority(vm.getPriority(), cachedMaxPriority);
        EntityModel priority = null;

        for (Object item : model.getPriority().getItems())
        {
            EntityModel a = (EntityModel) item;
            int p = (Integer) a.getEntity();
            if (p == roundPriority)
            {
                priority = a;
                break;
            }
        }
        ((UnitVmModel) model.getWindow()).getPriority().setSelectedItem(priority);
    }

    private void vmModel_TimeZone_ItemsChanged()
    {
        UnitVmModel model = (UnitVmModel) getWindow();
        if (!model.getIsNew())
        {
            UserPortalItemModel selectedItem = (UserPortalItemModel) getSelectedItem();
            VM vm = (VM) selectedItem.getEntity();

            if (!StringHelper.isNullOrEmpty(vm.getTimeZone()))
            {
                model.getTimeZone().setSelectedItem(Linq.firstOrDefault(model.getTimeZone().getItems(),
                        new Linq.TimeZonePredicate(vm.getTimeZone())));
            }
        }
    }

    @Override
    public void eventRaised(Event ev, Object sender, EventArgs args)
    {
        super.eventRaised(ev, sender, args);

        UnitVmModel model = (UnitVmModel) getWindow();
        if (ev.matchesDefinition(ItemsChangedEventDefinition) && sender == model.getDataCenter())
        {
            vmModel_DataCenter_ItemsChanged();
        }
        else if (ev.matchesDefinition(ItemsChangedEventDefinition) && sender == model.getCluster())
        {
            vmModel_Cluster_ItemsChanged();
        }
        else if (ev.matchesDefinition(ItemsChangedEventDefinition) && sender == model.getDefaultHost())
        {
            vmModel_DefaultHost_ItemsChanged();
        }
        else if (ev.matchesDefinition(ItemsChangedEventDefinition) && sender == model.getDisplayProtocol())
        {
            vmModel_DisplayProtocol_ItemsChanged();
        }
        else if (ev.matchesDefinition(ItemsChangedEventDefinition) && sender == model.getPriority())
        {
            vmModel_Priority_ItemsChanged();
        }
        else if (ev.matchesDefinition(ItemsChangedEventDefinition) && sender == model.getTimeZone())
        {
            vmModel_TimeZone_ItemsChanged();
        }
    }

    @Override
    public void onVmAndPoolLoad()
    {
        if (getvms() != null && getpools() != null)
        {
            // Complete search.

            // Remove pools that has provided VMs.
            ArrayList<VmPool> filteredPools = new ArrayList<VmPool>();
            poolMap = new HashMap<Guid, VmPool>();

            for (VmPool pool : getpools())
            {
                // Add pool to map.
                poolMap.put(pool.getVmPoolId(), pool);

                int attachedVmsCount = 0;
                for (VM vm : getvms())
                {
                    if (vm.getVmPoolId() != null && vm.getVmPoolId().equals(pool.getVmPoolId()))
                    {
                        attachedVmsCount++;
                    }
                }

                if (attachedVmsCount < pool.getMaxAssignedVmsPerUser())
                {
                    filteredPools.add(pool);
                }
            }

            // Merge VMs and Pools, and create item models.
            List all = Linq.concat(getvms(), filteredPools);
            Linq.sort(all, new Linq.VmAndPoolByNameComparer());

            ArrayList<Model> items = new ArrayList<Model>();
            for (Object item : all)
            {
                UserPortalItemModel model = new UserPortalItemModel(this, this, ConsoleContext.UP_EXTENDED);
                model.setEntity(item);
                items.add(model);

                updateConsoleModel(model);
            }

            // In userportal 'Extended View': Set 'CanConnectAutomatically' to true if there's one and only one up VM.
            setCanConnectAutomatically(getUpVms(items).size() == 1);

            setItems(items);

            setvms(null);
            setpools(null);

            getSearchCompletedEvent().raise(this, EventArgs.Empty);
        }
    }

    @Override
    protected void updateConsoleModel(UserPortalItemModel item) {
        super.updateConsoleModel(item);
        if (item.getEntity() != null && item.getDefaultConsoleModel() != null) {
            // Adjust item's default console for userportal 'Extended View'
            item.getDefaultConsoleModel().setForceVmStatusUp(false);
        }
    }

    @Override
    protected String getListName() {
        return "UserPortalListModel"; //$NON-NLS-1$
    }

    @Override
    protected ConsoleContext getConsoleContext() {
        return ConsoleContext.UP_EXTENDED;
    }
}
