/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.ui;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.DataSet;
import de.fub.mapsforge.project.detector.model.xmls.TrainingSet;
import de.fub.mapsforge.project.detector.model.xmls.TransportMode;
import de.fub.utilsmodule.Collections.ObservableArrayList;
import de.fub.utilsmodule.Collections.ObservableList;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class TrainingDatasetComponent extends javax.swing.JPanel implements ExplorerManager.Provider, Lookup.Provider {

    private static final long serialVersionUID = 1L;
    private Lookup context;
    private Detector detector;
    private final ExplorerManager explorerManager = new ExplorerManager();
    private Lookup lookup = ExplorerUtils.createLookup(explorerManager, getActionMap());

    /**
     * Creates new form TrainingDatasetComponent
     */
    public TrainingDatasetComponent() {
        initComponents();
        beanTreeView1.setRootVisible(true);
    }

    public TrainingDatasetComponent(Lookup context) {
        this();
        this.context = context;
        detector = context.lookup(Detector.class);
        RootNode rootNode = new RootNode(new TrainingDatasetWrapper(detector));
        explorerManager.setRootContext(rootNode);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        beanTreeView1 = new org.openide.explorer.view.BeanTreeView();

        setLayout(new java.awt.BorderLayout());

        beanTreeView1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        add(beanTreeView1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView beanTreeView1;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    public static class RootNode extends AbstractNode {

        public static final String ACTION_PATH = "mapsforge/Detector/Wizard/Traningsset/Actions";
        private final TrainingDatasetWrapper dataset;

        private RootNode(TrainingDatasetWrapper dataset) {
            super(Children.create(new TransportNodeFactory(dataset), true), Lookups.fixed(dataset));
            this.dataset = dataset;
            setDisplayName(NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RootNode_Name"));
        }

        @Override
        public Image getIcon(int type) {
            Image image = IconRegister.getFolderIcon();
            if (image == null) {
                super.getIcon(type);
            }
            return image;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{new AddTransportModeAction(dataset)};
        }
    }

    public static class TransportNodeFactory extends ChildFactory<TransportMode> implements ChangeListener {

        private final TrainingDatasetWrapper dataset;

        private TransportNodeFactory(TrainingDatasetWrapper dataset) {
            this.dataset = dataset;
            dataset.addChangeListener(TransportNodeFactory.this);
        }

        @Override
        protected boolean createKeys(List<TransportMode> list) {
            ArrayList<TransportMode> arrayList = new ArrayList<TransportMode>(dataset.transportModes);
            Collections.sort(arrayList, new Comparator<TransportMode>() {
                @Override
                public int compare(TransportMode o1, TransportMode o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            list.addAll(arrayList);
            return true;
        }

        @Override
        protected Node createNodeForKey(TransportMode transportMode) {
            return new TransportModeFilterNode(transportMode, dataset);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            refresh(true);
        }
    }

    private static class DataSetNodeFactory extends ChildFactory<DataSet> implements ChangeListener {

        private final TransportMode transportMode;
        private final TrainingDatasetWrapper dataset;

        private DataSetNodeFactory(TransportMode transportMode, TrainingDatasetWrapper dataset) {
            this.transportMode = transportMode;
            this.dataset = dataset;
            this.dataset.addChangeListener(WeakListeners.change(DataSetNodeFactory.this, this.dataset));
        }

        @Override
        protected boolean createKeys(List<DataSet> toPopulate) {
            toPopulate.addAll(transportMode.getDataset());
            return true;
        }

        @Override
        protected Node createNodeForKey(final DataSet data) {
            return new AbstractNodeImpl(data);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            refresh(true);
        }

        private class AbstractNodeImpl extends AbstractNode {

            private final DataSet data;
            private DataObject dataObject = null;

            public AbstractNodeImpl(DataSet data) {
                super(Children.LEAF);
                this.data = data;
                File file = new File(data.getUrl());
                if (file.exists()) {
                    FileObject fileObject = FileUtil.toFileObject(FileUtil.normalizeFile(file));
                    try {
                        dataObject = DataObject.find(fileObject);
                        setDisplayName(dataObject.getName());
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } else {
                    setName(data.getUrl());
                }
            }

            @Override
            public Action[] getActions(boolean context) {
                return new Action[]{new RemoveDatasetAction(data, transportMode, dataset)};
            }

            @Override
            public Image getIcon(int type) {
                return dataObject != null ? dataObject.getNodeDelegate().getIcon(type) : super.getIcon(type);
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }
        }
    }

    static class TransportModeFilterNode extends AbstractNode implements ChangeListener {

        private static final String ACTION_PATH = "mapsforge/Detector/Wizard/Traningsset/Transportmode/Actions";
        private final TransportMode tranportMode;
        private final TrainingDatasetWrapper dataset;

        TransportModeFilterNode(TransportMode transportMode, TrainingDatasetWrapper dataset) {
            super(Children.create(new DataSetNodeFactory(transportMode, dataset), true), Lookups.fixed(dataset, transportMode));
            this.tranportMode = transportMode;
            this.dataset = dataset;
            setDisplayName(transportMode.getName());
            dataset.addChangeListener(WeakListeners.change(TransportModeFilterNode.this, dataset));
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{new AddDatasetAction(tranportMode, dataset),
                null,
                new RemoveAllDatasetItemsAction(tranportMode, dataset),
                null,
                new RemoveTransportModeAction(tranportMode, dataset),
                new RenameTransportModeAction(tranportMode, dataset)};
        }

        @Override
        public Image getIcon(int type) {
            Image image = IconRegister.getFolderIcon();
            if (image == null) {
                image = super.getIcon(type);
            }
            return image;
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            setDisplayName(tranportMode.getName());
            fireIconChange();
        }
    }

    private static class TrainingDatasetWrapper implements ChangeListener {

        private final ChangeSupport changeSupport = new ChangeSupport(this);
        private ObservableList<TransportMode> transportModes = new ObservableArrayList<TransportMode>();
        private final Detector detector;
        private final TrainingSet trainingSet;

        public TrainingDatasetWrapper(Detector detector) {
            this.detector = detector;
            trainingSet = detector.getDetectorDescriptor().getDatasets().getTrainingSet();
            transportModes.addAll(detector.getDetectorDescriptor().getDatasets().getTrainingSet().getTransportModeList());
            transportModes.addChangeListener(WeakListeners.change(TrainingDatasetWrapper.this, transportModes));
        }

        public ObservableList<TransportMode> getTransportModes() {
            return transportModes;
        }

        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }

        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }

        public void fireChange() {
            changeSupport.fireChange();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            trainingSet.getTransportModeList().clear();
            trainingSet.getTransportModeList().addAll(transportModes);
            fireChange();
        }
    }

    static class AddTransportModeAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final TrainingDatasetWrapper dataset;

        public AddTransportModeAction(TrainingDatasetWrapper dataset) {
            super(NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_AddTransportModeAction_Name"));
            this.dataset = dataset;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(
                    NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_AddTransportModeAction_InputLine_Text"),
                    NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_AddTransportModeAction_InputLine_Title"));
            Object notify = DialogDisplayer.getDefault().notify(nd);
            if (NotifyDescriptor.InputLine.OK_OPTION == notify) {
                String inputText = nd.getInputText();
                TransportMode transportMode = new TransportMode(inputText);
                dataset.getTransportModes().add(transportMode);
            }
        }
    }

    static class RemoveTransportModeAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final TrainingDatasetWrapper dataset;
        private final TransportMode transportMode;

        RemoveTransportModeAction(TransportMode transportMode, TrainingDatasetWrapper dataset) {
            super(NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RemoveTransportModeAction_Name"));
            this.transportMode = transportMode;
            this.dataset = dataset;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RemoveTransportModeAction_Remove_Text"),
                    NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RemoveTransportModeAction_Remove_Title"),
                    NotifyDescriptor.YES_NO_OPTION);
            Object notify = DialogDisplayer.getDefault().notify(nd);
            if (NotifyDescriptor.YES_OPTION == notify) {
                dataset.getTransportModes().remove(transportMode);
                dataset.fireChange();
            }
        }
    }

    static class RenameTransportModeAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final TrainingDatasetWrapper dataset;
        private final TransportMode transportMode;

        public RenameTransportModeAction(TransportMode transportMode, TrainingDatasetWrapper dataset) {
            super(NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RenameTransportModeAction_Name"));
            this.transportMode = transportMode;
            this.dataset = dataset;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(
                    NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RenameTransportModeAction_InputText"),
                    NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RenameTransportModeAction_InputTitle"));
            Object notify = DialogDisplayer.getDefault().notify(nd);
            if (NotifyDescriptor.OK_OPTION == notify) {
                transportMode.setName(nd.getInputText());
                dataset.fireChange();
            }
        }
    }

    static class AddDatasetAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private FolderExplorer folderExplorer;
        private final TrainingDatasetWrapper dataset;
        private final TransportMode transportMode;

        public AddDatasetAction(TransportMode transportMode, TrainingDatasetWrapper dataset) {
            super(NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_AddDatasetAction_Name"));
            this.transportMode = transportMode;
            this.dataset = dataset;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            folderExplorer = new FolderExplorer();
            DialogDescriptor dd = new DialogDescriptor(folderExplorer, "Datasources");
            DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
            if (DialogDescriptor.OK_OPTION.equals(dd.getValue())) {
                Node[] selectedNodes = folderExplorer.getSelectedFiles();

                for (Node node : selectedNodes) {
                    DataObject dataObject = node.getLookup().lookup(DataObject.class);
                    transportMode.getDataset().addAll(collectData(dataObject));
                }
                int indexOf = dataset.getTransportModes().indexOf(transportMode);
                dataset.getTransportModes().remove(transportMode);
                dataset.getTransportModes().add(indexOf == -1 ? 0 : indexOf, transportMode);
            }
        }

        private List<DataSet> collectData(DataObject dataObject) {
            List<DataSet> result = new LinkedList<DataSet>();
            if (dataObject.getPrimaryFile().isFolder()) {
                for (FileObject fileObject : dataObject.getPrimaryFile().getChildren()) {
                    try {
                        result.addAll(collectData(DataObject.find(fileObject)));
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            } else if (dataObject.getPrimaryFile().isData()) {
                result.add(new DataSet(dataObject.getPrimaryFile().getPath()));
            }
            return result;
        }
    }

    private static class RemoveAllDatasetItemsAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final TransportMode transportMode;
        private final TrainingDatasetWrapper dataset;

        public RemoveAllDatasetItemsAction(TransportMode transportMode, TrainingDatasetWrapper dataset) {
            super(NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RemoveAllDatasetItemsAction_Name"));
            this.transportMode = transportMode;
            this.dataset = dataset;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            transportMode.getDataset().clear();
            int indexOf = dataset.getTransportModes().indexOf(transportMode);
            dataset.getTransportModes().remove(transportMode);
            dataset.getTransportModes().add(indexOf == -1 ? 0 : indexOf, transportMode);
        }
    }

    private static class RemoveDatasetAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final TrainingDatasetWrapper dataset;
        private final TransportMode transportMode;
        private final DataSet data;

        public RemoveDatasetAction(DataSet data, TransportMode transportMode, TrainingDatasetWrapper dataset) {
            super(NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RemoveDatasetAction_Name"));
            this.data = data;
            this.transportMode = transportMode;
            this.dataset = dataset;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RemoveDatasetAction_Text"),
                    NbBundle.getMessage(TrainingDatasetComponent.class, "TrainingDatasetComponent.CLT_RemoveDatasetAction_Title"),
                    NotifyDescriptor.Confirmation.YES_NO_OPTION);
            Object notify = DialogDisplayer.getDefault().notify(nd);
            if (NotifyDescriptor.YES_OPTION == notify) {
                transportMode.getDataset().remove(data);
                dataset.fireChange();
            }
        }
    }
}
