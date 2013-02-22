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
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.ProcessPipeline.ProcessEvent;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Properties;
import de.fub.mapsforge.project.aggregator.xml.Property;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.mapsforge.project.api.StatisticProvider;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.AggregateUtils;
import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = AbstractAggregationProcess.class)
public final class CleanProcess extends AbstractAggregationProcess<List<GPSSegment>, List<GPSSegment>> implements StatisticProvider {

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

        PropertySet propertySet = getPropertySet(CLEAN_SETTINGS);
        if (propertySet != null) {
            createCleaningOptions(propertySet.getProperties());
        }
        RenderingOptions renderingOptions = new RenderingOptions();
        renderingOptions.setColor(new Color(39, 172, 88)); // green
        renderingOptions.setRenderingType(RenderingOptions.RenderingType.ALL);
        renderingOptions.setzIndex(0);
        renderingOptions.setOpacity(1);
        gPSSegmentLayer = new GPSSegmentLayer(getName(), "Clean gps data", renderingOptions);
        layers.add(gPSSegmentLayer);
    }

    private void createCleaningOptions(List<Property> properties) {
        gpsCleaner.setCleaningOptions(AggregateUtils.createValue(CleaningOptions.class, properties));
    }

    private PropertySet getPropertySet(String name) {
        if (getDescriptor() != null) {
            Properties properties = getDescriptor().getProperties();
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
            filter = AggregateUtils.createValue(RamerDouglasPeuckerFilter.class, propertySet.getProperties());
        } else {
            filter = new RamerDouglasPeuckerFilter(5);
        }
        return filter;
    }

    @Override
    protected void start() {
        synchronized (MUTEX) {
            totalCleanGPSPointCount = 0;
            totalCleanSegmentCount = 0;
            totalSmoothedGPSPointCount = 0;
            if (inputList != null) {
                gPSSegmentLayer.clearRenderObjects();
                RamerDouglasPeuckerFilter rdpf = new RamerDouglasPeuckerFilter(5);
                int segCount = 0;
                LOG.log(Level.INFO, "segmentsize: {0}", inputList.size());
                int progess = 0;
                LOG.log(Level.INFO, "cleaning options: {0}", gpsCleaner.getCleaningOptions().toString());

                for (GPSSegment segment : inputList) {
                    List<GPSSegment> clean = gpsCleaner.clean(segment);
                    totalCleanSegmentCount += clean.size();
                    LOG.log(Level.INFO, "clean segmentsize: {0}", clean.size());
                    LOG.log(Level.INFO, "Cleaner segments: {0}", clean.toString());
                    for (GPSSegment cleanSegment : clean) {
                        totalCleanGPSPointCount += cleanSegment.size();
                        // run through Douglas-Peucker here (slightly modified
                        // perhaps to avoid too long edges)
                        GPSSegment smoothSegment = rdpf.simplify(cleanSegment);
                        LOG.log(Level.INFO, "rdpf segments: {0}", smoothSegment.toString());
                        totalSmoothedGPSPointCount += (cleanSegment.size() - smoothSegment.size());
                        cleanSegmentList.add(smoothSegment);
                        gPSSegmentLayer.add(smoothSegment);
                        LOG.log(Level.INFO, "clean segcount: {0}", (++segCount));
                    }
                    fireProcessEvent(new ProcessEvent(CleanProcess.this, "Cleaning...", (int) ((100d / inputList.size()) * (++progess))));
                }
            }
        }
    }

    @Override
    public List<GPSSegment> getResult() {
        synchronized (MUTEX) {
            return cleanSegmentList;
        }
    }

    @Override
    public void setInput(List<GPSSegment> input) {
        this.inputList = input;
    }

    @Override
    public String getName() {
        if (getDescriptor() != null) {
            return getDescriptor().getDisplayName();
        }
        return "Clean";
    }

    @Override
    public String getDescription() {
        if (getDescriptor() != null) {
            return getDescriptor().getDescription();
        }
        return "Clean Process";
    }

    @Override
    public void setDescriptor(ProcessDescriptor descriptor) {
        this.descriptor = descriptor;
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
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor desc = null;
        try {
            desc = AggregateUtils.getProcessDescriptor(getClass());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return desc;
    }

    @Override
    public List<StatisticSection> getStatisticData() throws StatisticNotAvailableException {
        List<StatisticSection> statisticSections = new ArrayList<StatisticProvider.StatisticSection>();
        statisticSections.add(getPerformanceData());

        StatisticSection section = new StatisticSection("Cleaning Statistics", "Statistical data of the cleaning process.");
        statisticSections.add(section);
        section.getStatisticsItemList().add(new StatisticItem("Clean GPS Point Count", String.valueOf(totalCleanGPSPointCount), "Total count of GPS points after cleaning."));
        section.getStatisticsItemList().add(new StatisticItem("Clean Segment Count", String.valueOf(totalCleanSegmentCount), "Total count of GPS segments after cleaning."));
        section.getStatisticsItemList().add(new StatisticItem("Clean GPS Point/Segment Ratio", String.valueOf(totalCleanGPSPointCount / (double) totalCleanSegmentCount), "The ratio of cleaned points to cleaned segements."));
        section.getStatisticsItemList().add(new StatisticItem("Smoothed GPS Points", String.valueOf(totalSmoothedGPSPointCount), "Total count of GPS points that filter by the RDP-Filter."));

        return statisticSections;
    }
}
