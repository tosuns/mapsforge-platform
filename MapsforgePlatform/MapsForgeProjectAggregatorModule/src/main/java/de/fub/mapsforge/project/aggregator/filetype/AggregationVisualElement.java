/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.filetype;

import de.fub.agg2graph.structs.DoubleRect;
import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.mapsforge.project.aggregator.factories.LayerNodeFactory;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.ui.component.StatisticsPanel;
import de.fub.mapsforge.project.utils.LayerTableCellRender;
import de.fub.utilsmodule.icons.IconRegister;
import geofiletypeapi.GeoUtil;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.DropDownButtonFactory;
import org.openide.awt.UndoRedo;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOpenAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

/**
 *
 * @author Serdar
 */
@MultiViewElement.Registration(
        displayName = "#LBL_AggregationBuilder_VISUAL",
        iconBase = "de/fub/mapsforge/project/aggregator/filetype/aggregationBuilderIcon.png",
        mimeType = {"text/aggregationbuilder+xml", "application/map"},
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "AggregationVisualElement",
        position = 1000)
@NbBundle.Messages("LBL_AggregationBuilder_VISUAL=Map")
public class AggregationVisualElement extends javax.swing.JPanel implements MultiViewElement, PropertyChangeListener, ExplorerManager.Provider {

    private static final long serialVersionUID = 1L;
    @StaticResource
    private static final String LAYER_ICON_PATH = "de/fub/mapsforge/project/aggregator/filetype/layersIcon.png";
    @StaticResource
    private static final String LAYERVIEW_ICON_PATH = "de/fub/mapsforge/project/aggregator/filetype/layerview.png";
    @StaticResource
    private static final String STATISTICS_BUTTON_ICON_PATH = "de/fub/mapsforge/project/aggregator/statisticsIcon.png";
    @StaticResource
    private static final String FIT_MAP_TO_SIZE_BUTTON_ICON_PATH = "de/fub/mapsforge/project/aggregator/filetype/zoomToMap.png";
    @StaticResource
    private static final String STATUS_BAR_VISIBLE_ICON_PATH = "de/fub/mapsforge/project/aggregator/filetype/statusbarIcon.png";
    private final JToolBar toolbar = new JToolBar();
    private final ExplorerManager explorerManager = new ExplorerManager();
    private transient final RequestProcessor requestProcessor = new RequestProcessor();
//    private transient final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;
    private transient final ViewUpdater viewUpdater = new ViewUpdater();
    private transient final LayerNodeFactory nodeFactory = new LayerNodeFactory();
    private transient final Lookup lookup;
    private transient MultiViewElementCallback callback;
    private transient Aggregator aggregator;
//    private transient Image defaulImage;
    private JToggleButton layerViewButton;
    private JButton layersButton;
    private JButton processRunButton;
    private JButton statisticsDataButton;
    private JButton fitToSizeButton;
    private JPopupMenu layersMenu;
    private JPopupMenu processMenu;
    private JComboBox<TileSource> tileSourceComboBox;
    private JToggleButton statusBarButton;

    /**
     * Creates new form AggregationVisualElement
     */
    public AggregationVisualElement(Lookup lkp) {

        DataObject dataObject = lkp.lookup(DataObject.class);
        if (dataObject != null) {
            aggregator = dataObject.getNodeDelegate().getLookup().lookup(Aggregator.class);
        }
        assert aggregator != null;
        lookup = ExplorerUtils.createLookup(explorerManager, getActionMap());
        explorerManager.setRootContext(new AbstractNode(Children.create(nodeFactory, true)));
        aggregator.addPropertyChangeListener(WeakListeners.propertyChange(AggregationVisualElement.this, aggregator));
        aggregator.create(viewUpdater);
        initGuiComponents();
    }

    private void initGuiComponents() {
        initComponents();
        toolbar.add(new JToolBar.Separator());
        setUpToolbar();
        outlineView1.getOutline().setRowHeight(300);
        outlineView1.getOutline().setRootVisible(false);
        outlineView1.getOutline().setDefaultRenderer(Object.class, new LayerTableCellRender());
        jSplitPane1.setDividerLocation(Integer.MAX_VALUE);
    }

    /**
     * initializes and sets up the drop down menu of the Layer visiblity menu.
     */
    private void initPopupMenu() {
        nodeFactory.clear();
        aggComponent.clear();
        Collection<AbstractAggregationProcess<?, ?>> processes = aggregator.getPipeline().getProcesses();
        for (AbstractAggregationProcess<?, ?> process : processes) {
            for (final AbstractLayer<?> layer : process.getLayers()) {
                aggComponent.addLayer(layer);
                nodeFactory.add(layer);
                final JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(layer.getName());
                jCheckBoxMenuItem.setState(layer.isVisible());
                jCheckBoxMenuItem.addActionListener(new LayerActionListener(layer, jCheckBoxMenuItem));
                layersMenu.add(jCheckBoxMenuItem);
            }
        }
    }

    /**
     * initializes and sets up the drop down menu of the process run toolbar
     * button.
     */
    private void initProcessPopupMenu() {
        Collection<AbstractAggregationProcess<?, ?>> processes = aggregator.getPipeline().getProcesses();
        for (final AbstractAggregationProcess<?, ?> process : processes) {
            final JMenuItem menuItem = new JMenuItem(process.getName());
            menuItem.setToolTipText(process.getDescription());
            menuItem.addActionListener(new ActionListener() {
                @Override
                @SuppressWarnings("unchecked")
                public void actionPerformed(ActionEvent e) {
                    requestProcessor.post(new Runnable() {
                        @Override
                        public void run() {
                            aggregator.start((List<AbstractAggregationProcess<?, ?>>) Arrays.asList(process));
                        }
                    });
                }
            });
            processMenu.add(menuItem);
        }
    }

    @NbBundle.Messages({"CLT_Information_Message=Task still running!",
        "CLT_Process_Button_Tooltip=Run pipline",
        "CLT_Show_Hide_Layers=Shows/Hides Layers",
        "CLT_Show_Hide_Layer_View=Shows/Hides Layer View",
        "CLT_Statistics_Button_Tooltip=Displays all available Statistics of this Aggregator.",
        "CLT_Statistics_Window=Statistics",
        "CLT_Fit_Map_To_Size_Tooltip=Zoom the map to the size of the viewport.",
        "CLT_StatusBar_Button_Tooltip=Status bar enable/disable"
    })
    private void setUpToolbar() {

        // set up tilesource combobox
        if (tileSourceComboBox == null) {
            tileSourceComboBox = new JComboBox<TileSource>(
                    new TileSource[]{
                new OsmTileSource.Mapnik(),
                new OsmTileSource.CycleMap(),
                new BingAerialTileSource(),
                new MapQuestOsmTileSource(),
                new MapQuestOpenAerialTileSource()});
            tileSourceComboBox.setMaximumSize(new Dimension(100, 16));
            tileSourceComboBox.setSelectedIndex(0);
            tileSourceComboBox.addItemListener(new TileSourceComboBoxItemListenerImpl());
            toolbar.add(tileSourceComboBox);
            toolbar.add(new JToolBar.Separator());
        }

        // set up statistics button
        if (statisticsDataButton == null) {
            statisticsDataButton = new JButton(ImageUtilities.loadImageIcon(STATISTICS_BUTTON_ICON_PATH, true));
            statisticsDataButton.setToolTipText(Bundle.CLT_Statistics_Button_Tooltip());
            statisticsDataButton.addActionListener(new StatisticsDataButtonActionListenerImpl());
            toolbar.add(statisticsDataButton);
            toolbar.add(new JToolBar.Separator());
        }

        // set up layer show/hide drop down button
        if (layersMenu == null) {
            layersMenu = new JPopupMenu();
            initPopupMenu();
            layersButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon(LAYER_ICON_PATH, true), layersMenu);
            layersButton.setToolTipText(Bundle.CLT_Show_Hide_Layers());
            layersButton.addActionListener(new LayerButtonActionListenerImpl());
            toolbar.add(layersButton);
        } else {
            layersMenu.removeAll();
            initPopupMenu();
        }

        // set up layer view button
        if (layerViewButton == null) {
            layerViewButton = new JToggleButton(ImageUtilities.loadImageIcon(LAYERVIEW_ICON_PATH, true));
            layerViewButton.setSelected(false);
            layerViewButton.setToolTipText(Bundle.CLT_Show_Hide_Layer_View());
            layerViewButton.addActionListener(new LayerViewButtonActionListenerImpl());
            toolbar.add(layerViewButton);

        }

        // set up fit map to size button
        if (fitToSizeButton == null) {
            fitToSizeButton = new JButton(ImageUtilities.loadImageIcon(FIT_MAP_TO_SIZE_BUTTON_ICON_PATH, true));
            fitToSizeButton.setToolTipText(Bundle.CLT_Fit_Map_To_Size_Tooltip());
            fitToSizeButton.addActionListener(new FitToSizeButtonActionListenerImpl());
            toolbar.add(fitToSizeButton);
            toolbar.add(new JToolBar.Separator());
        }

        // set up process drop down button
        if (processMenu == null) {
            processMenu = new JPopupMenu();
            initProcessPopupMenu();
            Image buttonImageIcon = IconRegister.findRegisteredIcon("toolbarProcessRunIcon.png");
            processRunButton = DropDownButtonFactory.createDropDownButton(new ImageIcon(buttonImageIcon), processMenu);
            processRunButton.setToolTipText(Bundle.CLT_Process_Button_Tooltip());
            processRunButton.addActionListener(new ProcessRunButtonActionListenerImpl());
            toolbar.add(processRunButton);
        } else {
            processMenu.removeAll();
            initProcessPopupMenu();
        }

        if (this.statusBarButton == null) {
            statusBarButton = new JToggleButton(ImageUtilities.loadImageIcon(STATUS_BAR_VISIBLE_ICON_PATH, true));
            statusBarButton.setToolTipText(Bundle.CLT_StatusBar_Button_Tooltip());
            statusBarButton.addActionListener(new StatusBarButtonActionListenerImpl());
            // default setting: status bar is visible
            statusBarButton.doClick();
            toolbar.addSeparator();
            toolbar.add(statusBarButton);
            toolbar.addSeparator();
        }
    }

    private void updateLayerView() {
        if (layerViewButton.isSelected()) {
            jSplitPane1.setEnabled(true);
            jSplitPane1.setDividerLocation(.8);
        } else {
            jSplitPane1.setEnabled(false);
            jSplitPane1.setDividerLocation(1d);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        outlineView1 = new org.openide.explorer.view.OutlineView("Layers");
        aggComponent = new de.fub.agg2graphui.AggTopComponent();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(1000000);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setEnabled(false);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(400, 276));

        outlineView1.setMinimumSize(new java.awt.Dimension(0, 25));
        outlineView1.setPreferredSize(new java.awt.Dimension(0, 400));
        jSplitPane1.setRightComponent(outlineView1);

        aggComponent.setMinimumSize(new java.awt.Dimension(400, 274));
        jSplitPane1.setLeftComponent(aggComponent);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.agg2graphui.AggTopComponent aggComponent;
    private javax.swing.JSplitPane jSplitPane1;
    private org.openide.explorer.view.OutlineView outlineView1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        Action[] retValue;
        // the multiviewObserver was passed to the element in setMultiViewCallback() method.
        if (callback != null) {
            retValue = callback.createDefaultActions();
            // add you own custom actions here..
        } else {
            // fallback..
            retValue = new Action[0];
        }
        return retValue;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
        updateMap();
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
        if (this.callback != null && aggregator != null) {
            this.callback.getTopComponent().setDisplayName(aggregator.getAggregatorDescriptor().getName());
        }
    }

    @Override
    public CloseOperationState canCloseElement() {
        return aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING ? CloseOperationState.STATE_OK : null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Aggregator.PROP_NAME_AGGREGATOR_STATE.equals(evt.getPropertyName()) && callback != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TopComponent topComponent = callback.getTopComponent();
                    if (callback.isSelectedElement()) {
                        topComponent.setIcon(aggregator.getDataObject().getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16));
                    }
                }
            });
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    private void updateMap() {
        List<Source> sourceList = aggregator.getSourceList();
        if (sourceList != null) {
            Area totalBoundingBox = new Area();

            for (Source source : sourceList) {
                String url = source.getUrl();
                if (url != null) {
                    File file = new File(url);
                    Rectangle2D boundingBox = GeoUtil.getBoundingBox(file);
                    if (boundingBox != null) {
                        totalBoundingBox.add(new Area(boundingBox));
                    }
                }
            }
            Rectangle2D bounds = totalBoundingBox.getBounds2D();
            aggComponent.showArea(
                    new DoubleRect(
                    bounds.getX(),
                    bounds.getY(),
                    bounds.getWidth(),
                    bounds.getHeight()));
        }
    }

    private class ViewUpdater implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setUpToolbar();
                }
            });
        }
    }

    private static class LayerActionListener implements ActionListener {

        private final AbstractLayer<?> layer;
        private final JCheckBoxMenuItem jCheckBoxMenuItem;

        public LayerActionListener(AbstractLayer<?> layer, JCheckBoxMenuItem jCheckBoxMenuItem) {
            this.layer = layer;
            this.jCheckBoxMenuItem = jCheckBoxMenuItem;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            layer.setVisible(jCheckBoxMenuItem.getState());
        }
    }

    private class TileSourceComboBoxItemListenerImpl implements ItemListener {

        public TileSourceComboBoxItemListenerImpl() {
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (tileSourceComboBox.getSelectedItem() instanceof TileSource) {
                aggComponent.setTileSource((TileSource) tileSourceComboBox.getSelectedItem());
            }
        }
    }

    private class StatisticsDataButtonActionListenerImpl implements ActionListener {

        public StatisticsDataButtonActionListenerImpl() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (aggregator != null) {
                StatisticsPanel statisticsPanel = new StatisticsPanel(aggregator.getStatistics());
                DialogDescriptor dd = new DialogDescriptor(statisticsPanel, Bundle.CLT_Statistics_Window());
                dd.setOptionType(DialogDescriptor.DEFAULT_OPTION);
                DialogDisplayer.getDefault().notifyLater(dd);
            }
        }
    }

    private class LayerButtonActionListenerImpl implements ActionListener {

        public LayerButtonActionListenerImpl() {
        }
        private boolean allVisible = true;

        @Override
        public void actionPerformed(ActionEvent e) {
            allVisible = !allVisible;
            MenuElement[] subElements = layersMenu.getSubElements();
            for (MenuElement element : subElements) {
                if (element instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem item = (JCheckBoxMenuItem) element;
                    item.doClick();
                }
            }
        }
    }

    private class LayerViewButtonActionListenerImpl implements ActionListener {

        public LayerViewButtonActionListenerImpl() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            updateLayerView();
        }
    }

    private class FitToSizeButtonActionListenerImpl implements ActionListener {

        public FitToSizeButtonActionListenerImpl() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            updateMap();
        }
    }

    private class ProcessRunButtonActionListenerImpl implements ActionListener {

        public ProcessRunButtonActionListenerImpl() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (aggregator.getAggregatorState() != Aggregator.AggregatorState.RUNNING) {
                requestProcessor.post(new Runnable() {
                    @Override
                    public void run() {
                        aggregator.start(new ArrayList<AbstractAggregationProcess<?, ?>>(aggregator.getPipeline().getProcesses()));
                    }
                });
            } else {
                NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.CLT_Information_Message(), NotifyDescriptor.ERROR_MESSAGE);

                DialogDisplayer.getDefault().notifyLater(nd);
            }
        }
    }

    private class StatusBarButtonActionListenerImpl implements ActionListener {

        public StatusBarButtonActionListenerImpl() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            aggComponent.setStatusBarVisible(statusBarButton.isSelected());
        }
    }
}
