/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.aggregator.pipeline.processes;

import de.fub.agg2graph.graph.RamerDouglasPeuckerFilter;
import de.fub.agg2graph.input.CleaningOptions;
import de.fub.agg2graph.input.GPSCleaner;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.layers.GPSSegmentLayer;
import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.aggregator.xml.Properties;
import de.fub.maps.project.aggregator.xml.Property;
import de.fub.maps.project.aggregator.xml.PropertySection;
import de.fub.maps.project.aggregator.xml.PropertySet;
import de.fub.maps.project.api.process.ProcessPipeline.ProcessEvent;
import de.fub.maps.project.api.statistics.StatisticProvider;
import de.fub.maps.project.utils.AggregatorUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Process unit implementation, which handles the cleaning process of an
 * Aggregator.
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_CleanProcess_Name=Cleaner",
    "CLT_CleanProcess_Description=A simple GPS segment cleaner",
    "CLT_CLeanProcess_PropertySet_Setting_Name=Cleaning Settings",
    "CLT_CleanProcess_PropertySet_Setting_Description=No description available",
    "CLT_CleanProcess_PropertySet_RDPSetting_Name=Raimer Douglas Peucker Filter Settings",
    "CLT_CleanProcess_PropertySet_RDPSetting_Description=No description available"
})
@ServiceProvider(service = AbstractAggregationProcess.class)
public final class CleanProcess extends AbstractAggregationProcess<List<GPSSegment>, List<GPSSegment>> implements StatisticProvider {

    @StaticResource
    private static final String ICON_PATH = "de/fub/maps/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private static final String PROPERTY_SET_ID_CLEAN_SETTINGS = "clean.process.settings";
    private static final String PROPERTY_SET_ID_RAMER_DOUGLAS_PEUCKER_SETTINGS = "clean.process.rdp.settings";
    private ArrayList<GPSSegment> cleanSegmentList = new ArrayList<GPSSegment>();
    private List<GPSSegment> inputList = new ArrayList<GPSSegment>();
    private GPSCleaner gpsCleaner = new GPSCleaner();
    private GPSSegmentLayer gPSSegmentLayer;
    private int totalCleanSegmentCount = 0;
    private int totalCleanGPSPointCount = 0;
    private int totalSmoothedGPSPointCount = 0;

    public CleanProcess() {
        init();
    }

    private void init() {
        RenderingOptions renderingOptions = new RenderingOptions();
        renderingOptions.setColor(new Color(39, 172, 88)); // green
        renderingOptions.setRenderingType(RenderingOptions.RenderingType.ALL);
        renderingOptions.setzIndex(0);
        renderingOptions.setOpacity(1);
        gPSSegmentLayer = new GPSSegmentLayer(getName(), "Clean gps data", renderingOptions);
        getLayers().add(gPSSegmentLayer);
    }

    @Override
    public void setProcessDescriptor(ProcessDescriptor processDescriptor) {
        super.setProcessDescriptor(processDescriptor);
        if (processDescriptor != null) {
            PropertySet propertySet = getPropertySet(PROPERTY_SET_ID_CLEAN_SETTINGS);
            if (propertySet != null) {
                createCleaningOptions(propertySet.getProperties());
            }
        }
    }

    private void createCleaningOptions(List<Property> properties) {
        gpsCleaner.setCleaningOptions(AggregatorUtils.createValue(CleaningOptions.class, properties));
    }

    private PropertySet getPropertySet(String name) {
        if (getProcessDescriptor() != null) {
            Properties properties = getProcessDescriptor().getProperties();
            if (!properties.getSections().isEmpty()) {
                return getPropertySet(name, properties);
            }
        }
        return null;
    }

    private PropertySet getPropertySet(String name, Properties properties) {
        if (!properties.getSections().isEmpty()) {
            PropertySection propertySection = properties.getSections().iterator().next();
            for (int i = 0; i < propertySection.getPropertySet().size(); i++) {
                PropertySet propertySet = propertySection.getPropertySet().get(i);
                if (name.equals(propertySet.getName())) {
                    return propertySet;
                }
            }
        }
        return null;
    }

    private RamerDouglasPeuckerFilter getFilterInstance() {
        RamerDouglasPeuckerFilter filter = null;
        PropertySet propertySet = getPropertySet(PROPERTY_SET_ID_RAMER_DOUGLAS_PEUCKER_SETTINGS);
        if (propertySet != null) {
            filter = AggregatorUtils.createValue(RamerDouglasPeuckerFilter.class, propertySet.getProperties());
        } else {
            filter = new RamerDouglasPeuckerFilter(5);
        }
        return filter;
    }

    @Override
    protected void start() {
        totalCleanGPSPointCount = 0;
        totalCleanSegmentCount = 0;
        totalSmoothedGPSPointCount = 0;
        ProgressHandle handle = ProgressHandleFactory.createHandle(getName());

        if (inputList != null) {
            handle.start(inputList.size());
            try {
                gPSSegmentLayer.clearRenderObjects();
                RamerDouglasPeuckerFilter rdpf = getFilterInstance();
                int progess = 0;
                for (GPSSegment segment : inputList) {

                    if (canceled.get()) {
                        fireProcessCanceledEvent();
                        break;
                    }

                    List<GPSSegment> clean = gpsCleaner.clean(segment);
                    totalCleanSegmentCount += clean.size();
                    for (GPSSegment cleanSegment : clean) {

                        if (canceled.get()) {
                            fireProcessCanceledEvent();
                            break;
                        }

                        totalCleanGPSPointCount += cleanSegment.size();
                        // run through Douglas-Peucker here (slightly modified
                        // perhaps to avoid too long edges)
                        GPSSegment smoothSegment = rdpf.simplify(cleanSegment);
                        totalSmoothedGPSPointCount += (cleanSegment.size() - smoothSegment.size());
                        cleanSegmentList.add(smoothSegment);
                        gPSSegmentLayer.add(smoothSegment);
                    }
                    fireProcessProgressEvent(new ProcessEvent<CleanProcess>(CleanProcess.this, "Cleaning...", (int) ((100d / inputList.size()) * (++progess))));
                    handle.progress(progess);
                }
            } finally {
                handle.finish();
            }
        }
    }

    @Override
    public List<GPSSegment> getResult() {
        synchronized (RUN_MUTEX) {
            return cleanSegmentList;
        }
    }

    @Override
    public void setInput(List<GPSSegment> input) {
        this.inputList = input;
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDisplayName();
        }
        return "Clean";
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDescription();
        }
        return "Clean Process";
    }

    @Override
    public Image getIcon() {
        return IMAGE;
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    @Override
    public List<StatisticSection> getStatisticData() throws StatisticNotAvailableException {
        List<StatisticSection> statisticSections = new ArrayList<StatisticProvider.StatisticSection>();
        statisticSections.add(getPerformanceData());

        StatisticSection section = new StatisticSection("Cleaning Statistics", "Statistical data of the cleaning process."); //NO18N
        statisticSections.add(section);

        section.getStatisticsItemList().add(
                new StatisticItem("Clean GPS Point Count",
                        String.valueOf(totalCleanGPSPointCount), "Total count of GPS points after cleaning.")); //NO18N

        section.getStatisticsItemList().add(
                new StatisticItem("Clean Segment Count",
                        String.valueOf(totalCleanSegmentCount), "Total count of GPS segments after cleaning.")); //NO18N

        section.getStatisticsItemList().add(
                new StatisticItem("Clean GPS Point/Segment Ratio",
                        String.valueOf(totalCleanGPSPointCount / (double) totalCleanSegmentCount), "The ratio of cleaned points to cleaned segements.")); //NO18N

        section.getStatisticsItemList().add(
                new StatisticItem("Smoothed GPS Points",
                        String.valueOf(totalSmoothedGPSPointCount), "Total count of GPS points that filter by the RDP-Filter.")); //NO18N

        return statisticSections;
    }

    @Override
    public boolean cancel() {
        canceled.set(true);
        return canceled.get();
    }

    @Override
    public Component getVisualRepresentation() {
        return null;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(CleanProcess.class.getName());
        descriptor.setDisplayName(Bundle.CLT_CleanProcess_Name());
        descriptor.setDescription(Bundle.CLT_CleanProcess_Description());

        PropertySection propertySection = new PropertySection();
        propertySection.setId(CleanProcess.class.getName());
        propertySection.setName(Bundle.CLT_CleanProcess_Name());
        propertySection.setDescription(Bundle.CLT_CleanProcess_Description());

        descriptor.getProperties().getSections().add(propertySection);

        PropertySet propertySet = new PropertySet();
        propertySet.setId(PROPERTY_SET_ID_CLEAN_SETTINGS);
        propertySet.setName(Bundle.CLT_CLeanProcess_PropertySet_Setting_Name());
        propertySet.setDescription(Bundle.CLT_CleanProcess_PropertySet_Setting_Description());

        Property property = new Property();
        property.setId("filterBySegmentLength");
        property.setName("minSegmentLength");
        property.setDescription("No description available");
        property.setJavaType(Boolean.class.getName());
        property.setValue(Boolean.TRUE.toString());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("minSegmentLength");
        property.setName("minSegmentLength");
        property.setDescription("No description available");
        property.setJavaType(Long.class.getName());
        property.setValue(String.valueOf(1));
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("maxSegmentLength");
        property.setName("maxSegmentLength");
        property.setDescription("No description available");
        property.setJavaType(Long.class.getName());
        property.setValue(String.valueOf(100));
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("filterByEdgeLength");
        property.setName("filterByEdgeLength");
        property.setDescription("No description available");
        property.setJavaType(Boolean.class.getName());
        property.setValue(Boolean.TRUE.toString());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("minEdgeLength");
        property.setName("minEdgeLength");
        property.setDescription("No description available");
        property.setJavaType(Double.class.getName());
        property.setValue("" + 0.3);
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("maxEdgeLength");
        property.setName("maxEdgeLength");
        property.setDescription("No description available");
        property.setJavaType(Double.class.getName());
        property.setValue("" + 750);
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("filterByEdgeLengthIncrease");
        property.setName("filterByEdgeLengthIncrease");
        property.setDescription("No description available");
        property.setJavaType(Boolean.class.getName());
        property.setValue(Boolean.TRUE.toString());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("minEdgeLengthIncreaseFactor");
        property.setName("minEdgeLengthIncreaseFactor");
        property.setDescription("No description available");
        property.setJavaType(Double.class.getName());
        property.setValue("10");
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("minEdgeLengthAfterIncrease");
        property.setName("minEdgeLengthAfterIncrease");
        property.setDescription("No description available");
        property.setJavaType(Double.class.getName());
        property.setValue("30");
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("filterZigzag");
        property.setName("filterZigzag");
        property.setDescription("No description available");
        property.setJavaType(Boolean.class.getName());
        property.setValue(Boolean.TRUE.toString());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("maxZigzagAngle");
        property.setName("maxZigzagAngle");
        property.setDescription("No description available");
        property.setJavaType(Double.class.getName());
        property.setValue("30");
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("filterFakeCircle");
        property.setName("filterFakeCircle");
        property.setDescription("No description available");
        property.setJavaType(Boolean.class.getName());
        property.setValue(Boolean.TRUE.toString());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("maxFakeCircleAngle");
        property.setName("maxFakeCircleAngle");
        property.setDescription("No description available");
        property.setJavaType(Double.class.getName());
        property.setValue("50");
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("filterOutliers");
        property.setName("filterOutliers");
        property.setDescription("No description available");
        property.setJavaType(Boolean.class.getName());
        property.setValue(Boolean.FALSE.toString());
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("maxNumOutliers");
        property.setName("maxNumOutliers");
        property.setDescription("No description available");
        property.setJavaType(Integer.class.getName());
        property.setValue("2");
        propertySet.getProperties().add(property);

        propertySection.getPropertySet().add(propertySet);

        propertySet = new PropertySet();
        propertySet.setId(PROPERTY_SET_ID_RAMER_DOUGLAS_PEUCKER_SETTINGS);
        propertySet.setName(Bundle.CLT_CleanProcess_PropertySet_RDPSetting_Name());
        propertySet.setDescription(Bundle.CLT_CleanProcess_PropertySet_RDPSetting_Description());

        property = new Property();
        property.setId("epsilon");
        property.setName("epsilon");
        property.setDescription("No description available");
        property.setJavaType(Double.class.getName());
        property.setValue("5");
        propertySet.getProperties().add(property);

        property = new Property();
        property.setId("maxSegmentLength");
        property.setName("maxSegmentLength");
        property.setDescription("No description available");
        property.setJavaType(Double.class.getName());
        property.setValue("100");
        propertySet.getProperties().add(property);

        propertySection.getPropertySet().add(propertySet);
        return descriptor;
    }
}
