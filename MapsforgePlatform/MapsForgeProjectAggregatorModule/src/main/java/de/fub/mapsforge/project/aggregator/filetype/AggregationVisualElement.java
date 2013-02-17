/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.filetype;

import de.fub.agg2graph.structs.DoubleRect;
import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.mapsforge.project.aggregator.factories.nodes.AggregatorNode;
import de.fub.mapsforge.project.aggregator.factories.LayerNodeFactory;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.LayerTableCellRender;
import geofiletypeapi.GeoUtil;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.DropDownButtonFactory;
import org.openide.awt.UndoRedo;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;

/**
 *
 * @author Serdar
 */
@MultiViewElement.Registration(
        displayName = "#LBL_AggregationBuilder_VISUAL",
        iconBase = "de/fub/mapsforge/project/aggregator/filetype/aggregationBuilderIcon.png",
        mimeType = "text/aggregationbuilder+xml",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "AggregationVisualElement",
        position = 3000)
@NbBundle.Messages("LBL_AggregationBuilder_VISUAL=Map")
public class AggregationVisualElement extends javax.swing.JPanel implements MultiViewElement, PropertyChangeListener, ExplorerManager.Provider {

    @StaticResource
    private static final String LAYER_ICON_PATH = "de/fub/mapsforge/project/aggregator/filetype/layersIcon.png";
    @StaticResource
    private static final String LAYERVIEW_ICON_PATH = "de/fub/mapsforge/project/aggregator/filetype/layerview.png";
    @StaticResource
    private static final String PROCESS_BUTTON_ICON_PATH = "de/fub/mapsforge/project/aggregator/toolbarProcessRunIcon.png";
    private static final long serialVersionUID = 1L;
    private Aggregator aggregator;
    private JToolBar toolbar = new JToolBar();
    private transient MultiViewElementCallback callback;
    private JToggleButton layerViewButton;
    private ExplorerManager explorerManager = new ExplorerManager();
    private Lookup lookup;
    private final LayerNodeFactory nodeFactory = new LayerNodeFactory();
    private JButton layersButton;
    private JButton processButton;
    private JPopupMenu layersMenu;
    private JPopupMenu processMenu;
    private Image defaulImage;
    private final RequestProcessor requestProcessor = new RequestProcessor();

    /**
     * Creates new form AggregationVisualElement
     */
    public AggregationVisualElement(Lookup lkp) {
        AggregatorNode node = lkp.lookup(AggregatorNode.class);
        if (node != null) {
            aggregator = node.getLookup().lookup(Aggregator.class);
        }
        assert aggregator != null;
        lookup = ExplorerUtils.createLookup(explorerManager, getActionMap());
        explorerManager.setRootContext(new AbstractNode(Children.create(nodeFactory, true)));
        aggregator.addPropertyChangeListener(WeakListeners.propertyChange(AggregationVisualElement.this, aggregator));
        initGuiComponents();
    }

    private void initGuiComponents() {
        initComponents();
        toolbar.add(new JToolBar.Separator());
        setUpToolbar();
        outlineView1.getOutline().setRowHeight(300);
        outlineView1.getOutline().setRootVisible(false);
        outlineView1.getOutline().getColumnModel().getColumn(0).setCellRenderer(new LayerTableCellRender());
        jSplitPane1.setDividerLocation(Integer.MAX_VALUE);
    }

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
                jCheckBoxMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        layer.setVisible(jCheckBoxMenuItem.getState());
                    }
                });
                layersMenu.add(jCheckBoxMenuItem);
            }
        }
    }

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

    @NbBundle.Messages({"CLT_Information_Message=Task still running!", "CLT_Process_Button_Tooltip=Run pipline"})
    private void setUpToolbar() {

        if (processMenu == null) {
            processMenu = new JPopupMenu();
            initProcessPopupMenu();
            processButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon(PROCESS_BUTTON_ICON_PATH, true), processMenu);
            processButton.setToolTipText(Bundle.CLT_Process_Button_Tooltip());
            processButton.addActionListener(new ActionListener() {
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
            });
            toolbar.add(processButton);
        } else {
            processMenu.removeAll();
            initProcessPopupMenu();
        }

        if (layersMenu == null) {
            layersMenu = new JPopupMenu();
            initPopupMenu();
            layersButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon(LAYER_ICON_PATH, true), layersMenu);
            layersButton.addActionListener(new ActionListener() {
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
            });
            toolbar.add(layersButton);
        } else {
            layersMenu.removeAll();
            initPopupMenu();
        }

        if (layerViewButton == null) {

            layerViewButton = new JToggleButton(ImageUtilities.loadImageIcon(LAYERVIEW_ICON_PATH, true));
            layerViewButton.setSelected(false);
            layerViewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateLayerView();
                }
            });
            toolbar.add(layerViewButton);
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

        outlineView1.setMinimumSize(new java.awt.Dimension(0, 25));
        outlineView1.setPreferredSize(new java.awt.Dimension(0, 400));
        jSplitPane1.setRightComponent(outlineView1);
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
        return new Action[0];
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                List<Source> sourceList = aggregator.getSourceList();
                if (sourceList != null) {
                    for (Source source : sourceList) {
                        String url = source.getUrl();
                        if (url != null) {
                            File file = new File(url);
                            Rectangle2D boundingBox = GeoUtil.getBoundingBox(file);
                            if (boundingBox != null) {
                                aggComponent.showArea(new DoubleRect(boundingBox.getX(), boundingBox.getY(), boundingBox.getWidth(), boundingBox.getHeight()));
                            }

                        }

                    }
                }
            }
        });
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
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Aggregator.PROP_NAME_AGGREGATOR_STATE.equals(evt.getPropertyName()) && callback != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TopComponent topComponent = callback.getTopComponent();
                    switch (aggregator.getAggregatorState()) {
                        case ERROR:
                        case INACTIVE:
                            if (defaulImage != null) {
                                topComponent.setIcon(defaulImage);
                            }
                            if (processButton != null) {
                                processButton.setEnabled(true);
                            }
                            break;
                        case RUNNING:
                            defaulImage = topComponent.getIcon();
                            topComponent.setIcon(aggregator.getAggregatorState().getImage());
                            if (processButton != null) {
                                processButton.setEnabled(false);
                            }
                            break;
                    }
                }
            });
        } else if (Aggregator.PROP_NAME_DATAOBJECT.equals(evt.getPropertyName())) {
            setUpToolbar();
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
}
