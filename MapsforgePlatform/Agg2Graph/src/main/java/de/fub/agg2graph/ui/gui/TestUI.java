/**
 * *****************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the GNU
 * Affero Public License v3.0 which accompanies this distribution, and is
 * available at http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Contributors: Johannes Mitlmeier - initial API and implementation
 * ****************************************************************************
 */
package de.fub.agg2graph.ui.gui;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.AggregationStrategyFactory;
import de.fub.agg2graph.agg.IAggregationStrategy;
import de.fub.agg2graph.agg.tiling.CachingStrategyFactory;
import de.fub.agg2graph.agg.tiling.DefaultCachingStrategy;
import de.fub.agg2graph.agg.tiling.ICachingStrategy;
import de.fub.agg2graph.agg.tiling.TileCache;
import de.fub.agg2graph.agg.tiling.TileManager;
import de.fub.agg2graph.input.FileHandler;
import de.fub.agg2graph.input.Globals;
import de.fub.agg2graph.management.MiniProfiler;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.DoubleRect;
import de.fub.agg2graph.structs.ILocation;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class TestUI {

    public JFrame frmTestui;
    private boolean showLayers = true;
    private MainRenderingPanel mainPanel;
    private LayerManager layerManager;
    private JPanel panel_1;
    private String[] stepNames = CalcThread.stepNames;
    private JScrollPane[] settingsPanels = new JScrollPane[stepNames.length];
    private JPanel[] settingsInnerPanels = new JPanel[stepNames.length];
    private JButton[] processButtons = new JButton[stepNames.length];
    public List<List<VisualObjectEditor>> objectEditors;
    private JScrollPane scrollPane;
    public UIStepStorage uiStepStorage;
    public ObjectSelectionComboBox sourceFolderCombo;
    private boolean loading = false;
    public DoubleRect dataBoundingBox = null;
    public JTabbedPane tabbedPane;
    private TileManager tm;
    private TileCache tc;
    private CalcThread ct;
    private JPanel panel_11;
    private JScrollPane scrollPane_2;
    private JPanel panel_10;
    private JSplitPane splitPane;
    private boolean painting;
    private JPanel trackSelectionPanel;
    private final static int MAX_SELECTABLE_TRACES = 50;
    public Set<File> deselectedTraceFiles;

    public static void main(String[] args) {
        MiniProfiler.print();

        TestUI ui = new TestUI();
        Globals.put("ui", ui);

        ui.show(true);
    }

    /**
     * show the window
     */
    public void show(final boolean showControls) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager
                            .getSystemLookAndFeelClassName());
                    UIManager
                            .setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(frmTestui);
                    frmTestui.setVisible(true);

                    // load properties from last time
                    Properties uiProperties = new Properties();
                    int left = 50;
                    int top = 40;
                    int width = 600;
                    int height = 400;
                    try {
                        uiProperties.load(new FileInputStream("ui.properties"));
                        left = Integer.parseInt(uiProperties.getProperty(
                                "left", "50"));
                        top = Integer.parseInt(uiProperties.getProperty("top",
                                "50"));
                        width = Integer.parseInt(uiProperties.getProperty(
                                "width", "600"));
                        height = Integer.parseInt(uiProperties.getProperty(
                                "height", "400"));
                        int sel = Integer.parseInt(uiProperties.getProperty(
                                "input-selection", "0"));
                        sourceFolderCombo.setSelectedIndex(Math.min(sel,
                                sourceFolderCombo.getItemCount() - 1));
                        updateTrackSelectionPanel();
                        frmTestui.setSize(width, height);
                        frmTestui.setLocation(left, top);
                        if (!showControls) {
                            removeControls();
                        }
                        frmTestui.validate();
                    } catch (IOException e) {
                    }

                    MiniProfiler.print("UI ready");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public TestUI() {
        initialize();

        uiStepStorage = new UIStepStorage(this);
        uiStepStorage.setOpenOsmExportFile(true);

        addSettingsPanels();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        final TestUI outerThis = this;
        layerManager = new LayerManager(this);

        frmTestui = new JFrame();
        frmTestui.setTitle("TestUI");
        frmTestui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmTestui.addWindowListener(new CloseAdapter());

        splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setPreferredSize(new Dimension(350, 350));
        splitPane.setResizeWeight(0.75);
        frmTestui.getContentPane().add(splitPane, BorderLayout.CENTER);

        scrollPane = new JScrollPane();
        scrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        splitPane.setRightComponent(scrollPane);

        panel_1 = new JPanel();
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int height = scrollPane.getViewport().getHeight() - 16;
                double factor = (double) mainPanel.getWidth()
                        / (double) mainPanel.getHeight();
                int width = (int) (height * factor);
                Dimension size = new Dimension(width, height);
                Component[] subComponents = panel_1.getComponents();
                for (Component component : subComponents) {
                    if (component instanceof IRenderingPanel) {
                        JPanel rp = (JPanel) component;
                        rp.setMaximumSize(size);
                        rp.setMinimumSize(size);
                        rp.setPreferredSize(size);
                        rp.updateUI();
                        rp.repaint();
                    }
                }
                mainPanel.repaint();
            }
        });
        scrollPane.setViewportView(panel_1);
        panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JPanel panel_8 = new JPanel();
        splitPane.setLeftComponent(panel_8);
        panel_8.setLayout(new BorderLayout(0, 0));
        mainPanel = new MainRenderingPanel(this);
        mainPanel.setPreferredSize(new Dimension(500, 350));
        layerManager.setMainPanel(mainPanel);
        panel_8.add(mainPanel, BorderLayout.CENTER);

        JPanel panel_9 = new JPanel();
        frmTestui.getContentPane().add(panel_9, BorderLayout.SOUTH);
        panel_9.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel_9.add(panel);
        panel.setLayout(new GridLayout(0, 1, 0, 0));

        tabbedPane = new JTabbedPane(SwingConstants.TOP);
        tabbedPane.setPreferredSize(new Dimension(200, 150));
        panel.add(tabbedPane);

        // generate tabs
        for (int i = 0; i < stepNames.length; i++) {
            JPanel inner = new JPanel();
            inner.setLayout(new GridBagLayout());
            JScrollPane outer = new JScrollPane(inner,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            outer.getVerticalScrollBar().setUnitIncrement(16);
            tabbedPane.addTab(stepNames[i], outer);

            settingsInnerPanels[i] = inner;
            settingsPanels[i] = outer;
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        sourceFolderCombo = new ObjectSelectionComboBox();
        sourceFolderCombo.setMinimumSize(new Dimension(150, 20));
        sourceFolderCombo.setPreferredSize(new Dimension(150, 20));
        sourceFolderCombo.setSize(new Dimension(150, 20));
        File inputFolder = FileHandler.getFile("test/input");
        if (inputFolder != null) {
            System.out.println(inputFolder.getAbsolutePath());
            File[] folders = inputFolder.listFiles();
            if (folders != null) {
                Arrays.sort(folders);
                if (folders != null) {
                    for (File folder : folders) {
                        sourceFolderCombo.addItem(folder, folder.getName());
                    }
                }
            }
        }
        sourceFolderCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTrackSelectionPanel();
            }
        });
        settingsInnerPanels[0].add(new JLabeledComponentPanel(new JLabel(
                "Source folder: "), sourceFolderCombo), gbc);
        // track selection
        trackSelectionPanel = new JPanel();
        trackSelectionPanel.setLayout(new BoxLayout(trackSelectionPanel,
                BoxLayout.Y_AXIS));
        gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.gridy = 1;
        settingsInnerPanels[0].add(trackSelectionPanel, gbc);
        updateTrackSelectionPanel();

        JButton btnSaveagg = new JButton("save");
        btnSaveagg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // save agg
                uiStepStorage.getAggContainer().getCachingStrategy().save();
            }
        });
        btnSaveagg.setMnemonic('s');
        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        settingsInnerPanels[2].add(btnSaveagg, gbc);

        JButton btnLoadagg = new JButton("load");
        btnLoadagg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // load agg
                makeAgg(((File) sourceFolderCombo.getSelectedItem()).getName());
                if (tc.isInMemory()) {
                    JOptionPane.showMessageDialog(frmTestui,
                            "No aggregation found.");
                    return;
                }
                try {
                    tc.loadTile(tm.getRoot(), true);
                } catch (ParserConfigurationException e1) {
                    e1.printStackTrace();
                } catch (SAXException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (dataBoundingBox.isFresh()) {
                    Set<AggNode> nodes = tm.getRoot().getInnerNodes();
                    List<AggNode> nodeList = new ArrayList<AggNode>(nodes
                            .size());
                    nodeList.addAll(nodes);
                    parseDim(nodeList);
                }
                layerManager.repaintAllLayers();
            }
        });
        btnLoadagg.setMnemonic('l');
        gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.gridx = 1;
        gbc.gridy = 0;
        settingsInnerPanels[2].add(btnLoadagg, gbc);

        panel_10 = new JPanel();
        panel_9.add(panel_10, BorderLayout.SOUTH);
        panel_10.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        scrollPane_2 = new JScrollPane();
        panel_10.add(scrollPane_2);

        panel_11 = new JPanel();
        scrollPane_2.setBorder(BorderFactory.createEmptyBorder());
        scrollPane_2.setViewportView(panel_11);

        // generate buttons
        for (int i = 0; i < stepNames.length; i++) {
            JButton btn = new JButton(stepNames[i]);
            panel_11.add(btn);
            processButtons[i] = btn;
        }

        processButtons[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeAgg(((File) sourceFolderCombo.getSelectedItem()).getName());
                ct = new CalcThread(outerThis);
                ct.setTask("input");
                ct.start();
            }
        });

        processButtons[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ct = new CalcThread(outerThis);
                ct.setTask("clean");
                ct.start();
            }
        });

        processButtons[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeAgg(((File) sourceFolderCombo.getSelectedItem()).getName());
                ct = new CalcThread(outerThis);
                ct.setTask("agg");
                ct.start();
            }
        });

        processButtons[3].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ct = new CalcThread(outerThis);
                ct.setTask("road");
                ct.start();
            }
        });

        processButtons[4].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ct = new CalcThread(outerThis);
                ct.setTask("osm");
                ct.start();
            }
        });
    }

    private void updateTrackSelectionPanel() {
        trackSelectionPanel.removeAll();
        File file = (File) sourceFolderCombo.getSelectedItem();
        if (file != null) {
            File[] files = file.listFiles(FileHandler.gpxFilter);
            if (files.length < MAX_SELECTABLE_TRACES) {
                Arrays.sort(files);
                for (final File track : files) {
                    final JCheckBox box = new JCheckBox(track.getName());
                    box.setSelected(true);
                    box.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (box.isSelected()) {
                                deselectedTraceFiles.remove(track);
                            } else {
                                deselectedTraceFiles.add(track);
                            }
                        }
                    });
                    trackSelectionPanel.add(box);
                }
            }
            deselectedTraceFiles = new HashSet<File>();
            trackSelectionPanel.revalidate();
            trackSelectionPanel.repaint();
        }
    }

    public void addSettingsPanels() {
        File file = (File) sourceFolderCombo.getSelectedItem();
        if (file != null) {
            makeAgg(file.getName());
        }
        // enable configuration options in UI
        objectEditors = new ArrayList<List<VisualObjectEditor>>(stepNames.length);
        // for every step
        List<ClassObjectEditor> objectEditorList;
        GridBagConstraints gbc;
        ClassObjectEditor coe;
        for (int i = 0; i < CalcThread.levels.size(); i++) {
            objectEditorList = uiStepStorage.getObjectEditorsForLevel(i);
            objectEditors.add(new ArrayList<VisualObjectEditor>(
                    objectEditorList.size()));
            for (int j = 0; j < objectEditorList.size(); j++) {
                coe = objectEditorList.get(j);
                gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weightx = 1;
                gbc.gridwidth = 4;
                gbc.gridx = 0;
                gbc.gridy = j + 10;
                VisualObjectEditor oe = new VisualObjectEditor(coe);
                settingsInnerPanels[i].add(oe, gbc);
            }
        }
    }

    private void makeAgg(String name) {
        AggContainer agg = uiStepStorage.getAggContainer();
        if (agg != null && agg.getDataSource() != null
                && agg.getDataSource().getAbsolutePath().endsWith("/" + name)) {
            return;
        }
        if (uiStepStorage.levelReached < 2) {
            uiStepStorage.clear(2);
        }
        // make a new AggContainer
        IAggregationStrategy aggStrat;
        ICachingStrategy cacheStrat;
        if (agg != null) {
            aggStrat = agg.getAggregationStrategy();
            cacheStrat = agg.getCachingStrategy();
            agg = AggContainer.createContainer(new File("test/agg/" + name),
                    aggStrat, cacheStrat);
        } else {
            aggStrat = AggregationStrategyFactory.getObject();
            cacheStrat = CachingStrategyFactory.getObject();
            agg = AggContainer.createContainer(new File("test/agg/" + name),
                    aggStrat, cacheStrat);
            cacheStrat.clear();
        }
        if (cacheStrat instanceof DefaultCachingStrategy) {
            tc = ((DefaultCachingStrategy) cacheStrat).getTc();
            tm = ((DefaultCachingStrategy) cacheStrat).getTm();
        }
        tm.maxElementsPerTile = 2000;
        uiStepStorage.setAggContainer(agg);
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public MainRenderingPanel getMainPanel() {
        return mainPanel;
    }

    public LayerManager getLayerManager() {
        return layerManager;
    }

    public void addSidePanel(JPanel panel) {
        if (panel instanceof MainRenderingPanel) {
            return;
        }
        panel_1.add(panel);
    }

    public UIStepStorage getUIStepStorage() {
        return uiStepStorage;
    }

    public void parseDim(List<? extends ILocation> locations) {
        if (dataBoundingBox == null && locations.size() > 0) {
            ILocation loc = locations.get(0);
            dataBoundingBox = new DoubleRect(loc.getLat(), loc.getLon(), 0, 0);
        }
        for (ILocation location : locations) {
            double minX = Math
                    .min(dataBoundingBox.getMinX(), location.getLat());
            double minY = Math
                    .min(dataBoundingBox.getMinY(), location.getLon());
            double maxX = Math
                    .max(dataBoundingBox.getMaxX(), location.getLat());
            double maxY = Math
                    .max(dataBoundingBox.getMaxY(), location.getLon());
            dataBoundingBox.setRect(minX, minY, maxX - minX, maxY - minY);
        }
    }

    public class CloseAdapter extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            // save data
            Properties prop = new Properties();
            try {
                prop.setProperty("left",
                        String.valueOf(frmTestui.getLocation().x));
                prop.setProperty("top",
                        String.valueOf(frmTestui.getLocation().y));
                prop.setProperty("width",
                        String.valueOf(frmTestui.getSize().width));
                prop.setProperty("height",
                        String.valueOf(frmTestui.getSize().height));
                prop.setProperty("input-selection",
                        String.valueOf(sourceFolderCombo.getSelectedIndex()));
                prop.store(new FileOutputStream("ui.properties"), null);
            } catch (IOException ex) {
            }
        }
    }

    public void removeControls() {
        frmTestui.getContentPane().removeAll();
        frmTestui.getContentPane().setLayout(new BorderLayout());
        frmTestui.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frmTestui.pack();
    }

    public void setPainting(boolean painting) {
        this.painting = painting;
    }

    public boolean isPainting() {
        return painting;
    }

    public boolean isShowLayers() {
        return showLayers;
    }

    public void setShowLayers(boolean showLayers) {
        this.showLayers = showLayers;
    }
}
