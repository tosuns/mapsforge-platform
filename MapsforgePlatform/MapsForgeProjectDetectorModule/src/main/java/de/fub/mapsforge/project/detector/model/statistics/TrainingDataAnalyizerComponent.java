/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.statistics;

import de.fub.mapsforge.project.detector.factories.nodes.DetectorNode;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.TrainingsDataProvider;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.inference.features.TrackLengthFeatureProcess;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author Serdar
 */
@MultiViewElement.Registration(
        displayName = "#LBL_Detector_TrainingData_Analyzer_VISUAL",
        mimeType = "text/detector+xml",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "DetectorAnalyzerVisual",
        position = 1200)
@NbBundle.Messages({
    "LBL_Detector_TrainingData_Analyzer_VISUAL=Data Analyzer"
})
public class TrainingDataAnalyizerComponent extends JPanel implements MultiViewElement {

    @StaticResource
    private static final String REFRESH_BUTTON_ICON_PATH = "de/fub/mapsforge/project/detector/model/statistics/refreshIcon.png";
    private static final Logger LOG = Logger.getLogger(TrainingDataAnalyizerComponent.class.getName());
    private static final long serialVersionUID = 1L;
    private JToolBar toolbar = null;
    private MultiViewElementCallback callback;
    private Lookup lookup;
    private Detector detector;
    private final Object UPDATE_MUTEX = new Object();
    private TrainingsDataProvider dataProvider;
    private JButton refreshButton;

    /**
     * Creates new form TrainingDataAnalyizerComponent
     */
    public TrainingDataAnalyizerComponent() {
        initComponents();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int height = 0;
                for (Component component : contentPanel.getComponents()) {
                    height += component.getPreferredSize().height;
                }
                contentPanel.setPreferredSize(new Dimension(0, height));
            }
        });
    }

    public TrainingDataAnalyizerComponent(Lookup lookup) {
        this();
        this.lookup = new ProxyLookup(lookup, Lookups.singleton(contentPanel));
        Collection<? extends DetectorNode> allInstances = lookup.lookupResult(DetectorNode.class).allInstances();
        if (!allInstances.isEmpty()) {
            detector = allInstances.iterator().next().getLookup().lookup(Detector.class);
        }
    }

    private TrainingsDataProvider getDataProvider() {
        if (dataProvider == null) {
            dataProvider = lookup.lookup(TrainingsDataProvider.class);
            if (dataProvider == null) {
                // fall back
                dataProvider = detector.getLookup().lookup(TrainingsDataProvider.class);
            }
        }
        return dataProvider;
    }

    @Override
    public String getName() {
        return "DetectorAnalyzerVisual";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();
        barChartContainer1 = new TransportModeDataLengthChart();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        barChartContainer2 = new SegmentLengthHistogramChart();

        setLayout(new java.awt.BorderLayout());

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.PAGE_AXIS));

        barChartContainer1.setMaximumSize(new java.awt.Dimension(2147483647, 300));
        barChartContainer1.setMinimumSize(new java.awt.Dimension(102, 0));
        barChartContainer1.setPreferredSize(new java.awt.Dimension(682, 300));
        contentPanel.add(barChartContainer1);
        contentPanel.add(filler1);

        barChartContainer2.setMaximumSize(new java.awt.Dimension(2147483647, 300));
        barChartContainer2.setMinimumSize(new java.awt.Dimension(102, 0));
        barChartContainer2.setPreferredSize(new java.awt.Dimension(682, 300));
        contentPanel.add(barChartContainer2);

        jScrollPane1.setViewportView(contentPanel);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.fub.mapsforge.project.detector.model.statistics.BarChartContainer barChartContainer1;
    private de.fub.mapsforge.project.detector.model.statistics.BarChartContainer barChartContainer2;
    private javax.swing.JPanel contentPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        if (toolbar == null) {
            toolbar = new JToolBar();
            toolbar.addSeparator();
            toolbar.add(getRefreshButton());
        }
        return toolbar;
    }

    private JButton getRefreshButton() {
        if (refreshButton == null) {
            ImageIcon imageIcon = ImageUtilities.loadImageIcon(REFRESH_BUTTON_ICON_PATH, false);
            refreshButton = new JButton(imageIcon);
            refreshButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateViewAsync();
                }
            });
        }
        return refreshButton;
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
        updateViewAsync();
    }

    @Override
    public void componentClosed() {
    }

    @NbBundle.Messages({
        "CLT_Analyzer_DisplayName=Data Analyzer"
    })
    @Override
    public void componentShowing() {
        if (callback != null && detector != null) {
            String displayNString = MessageFormat.format("{0}[{1}]",
                    detector.getDataObject().getName(), Bundle.CLT_Analyzer_DisplayName());
            TopComponent topComponent = callback.getTopComponent();
            topComponent.setDisplayName(displayNString);
            topComponent.setHtmlDisplayName(displayNString);
        }
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

    private void updateViewAsync() {
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                getRefreshButton().setEnabled(false);
                ProgressHandle handle = ProgressHandleFactory.createHandle("Analysing data...");
                try {
                    handle.start();
                    updateView();
                } finally {
                    handle.finish();
                    getRefreshButton().setEnabled(true);
                }
            }
        });
    }

    private void updateView() {
        synchronized (UPDATE_MUTEX) {
            if (detector != null) {
                if (getDataProvider() != null) {
                    Map<String, List<TrackSegment>> data = getDataProvider().getData();
                    ArrayList<Double> histogramDatalist = new ArrayList<Double>();
                    ArrayList<String> keys = new ArrayList<String>(data.keySet());
                    Collections.sort(keys);

                    for (String key : keys) {
                        TrackLengthFeatureProcess feature = new TrackLengthFeatureProcess();
                        double length = 0;
                        for (TrackSegment trackSegment : data.get(key)) {
                            feature.setInput(trackSegment);
                            feature.run();
                            Double result = feature.getResult();
                            length += result;
                            histogramDatalist.add(result);
                        }

                        updateBarChart(length, key, data.values().size());
                    }
                    updateHistogramChart(histogramDatalist);
                }
            }
        }
    }

    private void updateHistogramChart(final ArrayList<Double> valueList) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final String _0_TO_50_METER = "0-50";
                    final String _50_TO_100_METER = "50-100";
                    final String _100_TO_200_METER = "100-200";
                    final String _200_TO_300_METER = "200-300";
                    final String _300_TO_400_METER = "300-400";
                    final String _400_TO_500_METER = "400-500";
                    final String _500_TO_600_METER = "500-600";
                    final String _600_TO_700_METER = "600-700";
                    final String _700_TO_800_METER = "700-800";
                    final String _800_TO_900_METER = "800-900";
                    final String _900_TO_1000_METER = "900-1000";
                    final String _1000_METER = "> 1000";
                    HashMap<String, Integer> bins = new HashMap<String, Integer>();
                    String binKey = null;
                    int totalCount = 0;
                    for (Double value : valueList) {
                        if (value > 0 && value < 50) {
                            binKey = _0_TO_50_METER;
                        } else if (value > 51 && value <= 100) {
                            binKey = _50_TO_100_METER;
                        } else if (value > 100 && value <= 200) {
                            binKey = _100_TO_200_METER;
                        } else if (value > 200 && value <= 300) {
                            binKey = _200_TO_300_METER;
                        } else if (value > 300 && value <= 400) {
                            binKey = _300_TO_400_METER;
                        } else if (value > 400 && value <= 500) {
                            binKey = _400_TO_500_METER;
                        } else if (value > 500 && value <= 600) {
                            binKey = _500_TO_600_METER;
                        } else if (value > 600 && value <= 700) {
                            binKey = _600_TO_700_METER;
                        } else if (value > 700 && value <= 800) {
                            binKey = _700_TO_800_METER;
                        } else if (value > 800 && value <= 900) {
                            binKey = _800_TO_900_METER;
                        } else if (value > 900 && value <= 1000) {
                            binKey = _900_TO_1000_METER;
                        } else if (value > 1000) {
                            binKey = _1000_METER;
                        }

                        if (binKey != null) {
                            if (!bins.containsKey(binKey)) {
                                bins.put(binKey, 0);
                            }
                            Integer count = bins.get(binKey);
                            count = count + 1;
                            bins.put(binKey, count);
                            totalCount++;
                        }
                    }
                    List<String> keys = Arrays.asList(_0_TO_50_METER, _50_TO_100_METER,
                            _100_TO_200_METER, _200_TO_300_METER,
                            _300_TO_400_METER, _400_TO_500_METER,
                            _500_TO_600_METER, _600_TO_700_METER,
                            _700_TO_800_METER, _800_TO_900_METER,
                            _900_TO_1000_METER, _1000_METER);

                    for (String key : keys) {
                        if (bins.containsKey(key)) {
                            barChartContainer2.getDataset().addValue(bins.get(key) / (double) totalCount * 100, "Segment length", key);
                        }
                    }

                } catch (Exception ex) {
                    LOG.severe(MessageFormat.format("Exception at value: {0}", valueList));
                }
            }
        });
    }

    private void updateBarChart(final double length, final String transportMode, final int size) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                barChartContainer1.getDataset().addValue(length / 1000, "Total Length", transportMode);
                barChartContainer1.getDataset().addValue(length / 1999 / size, "Avg. Segment Length", transportMode);
            }
        });

    }

    private static class TransportModeDataLengthChart extends BarChartContainer {

        private static final long serialVersionUID = 1L;

        public TransportModeDataLengthChart() {
            super();
            init();
        }

        private void init() {
            setTitle("Training Data Set");
            getPlot().getRenderer().setSeriesPaint(1, Color.red);
            getRangeAxis().setLabel(NbBundle.getMessage(TrainingDataAnalyizerComponent.class, "TransportModeDataLengthChart.value.axis.name"));
            getDomainAxis().setLabel(NbBundle.getMessage(TrainingDataAnalyizerComponent.class, "TransportModeDataLengthChart.domain.axis.name"));
            getRangeAxis().setUpperMargin(.1);
        }
    }

    private static class SegmentLengthHistogramChart extends BarChartContainer {

        private static final long serialVersionUID = 1L;

        public SegmentLengthHistogramChart() {
            super();
            init();
        }

        private void init() {
            setTitle("Segment length Histogramm");
            getRangeAxis().setLabel(NbBundle.getMessage(TrainingDataAnalyizerComponent.class, "SegmentLengthHistogramChart.value.axis.name"));
            getDomainAxis().setLabel(NbBundle.getMessage(TrainingDataAnalyizerComponent.class, "SegmentLengthHistogramChart.domain.axis.name"));
//            getRangeAxis().setRange(0, 100);
            getRangeAxis().setAutoRange(true);
            getRangeAxis().setUpperMargin(.1);
            getBarChart().removeLegend();
        }
    }
}
