/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.processes;

import de.fub.agg2graph.graph.RamerDouglasPeuckerFilter;
import de.fub.agg2graph.input.CleaningOptions;
import de.fub.agg2graph.input.GPSCleaner;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.layers.GPSSegmentLayer;
import de.fub.mapforgeproject.api.process.ProcessPipeline.ProcessEvent;
import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractXmlAggregationProcess;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Properties;
import de.fub.mapsforge.project.aggregator.xml.Property;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.AggregatorUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = AbstractAggregationProcess.class)
public final class CleanProcess extends AbstractXmlAggregationProcess<List<GPSSegment>, List<GPSSegment>> implements StatisticProvider {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private static final String CLEAN_SETTINGS = "Cleaning Settings";
    private static final String RAMER_DOUGLAS_PEUCKER_SETTINGS = "Raimer Douglas Peucker Filter Settings";
    private static final Logger LOG = Logger.getLogger(CleanProcess.class.getName());
    private ArrayList<GPSSegment> cleanSegmentList = new ArrayList<GPSSegment>();
    private List<GPSSegment> inputList = new ArrayList<GPSSegment>();
    private GPSCleaner gpsCleaner = new GPSCleaner();
    private final Object MUTEX = new Object();
    private final GPSSegmentLayer gPSSegmentLayer;
    private int totalCleanSegmentCount = 0;
    private int totalCleanGPSPointCount = 0;
    private int totalSmoothedGPSPointCount = 0;

    public CleanProcess() {
        this(null);
    }

    public CleanProcess(Aggregator aggregator) {
        super(aggregator);


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
            PropertySet propertySet = getPropertySet(CLEAN_SETTINGS);
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
        PropertySet propertySet = getPropertySet(RAMER_DOUGLAS_PEUCKER_SETTINGS);
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
                RamerDouglasPeuckerFilter rdpf = new RamerDouglasPeuckerFilter(5);
                int segCount = 0;
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
}
