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
import de.fub.mapsforge.project.aggregator.xml.AggregatorDescriptor;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptorList;
import de.fub.mapsforge.project.aggregator.xml.Properties;
import de.fub.mapsforge.project.aggregator.xml.Property;
import de.fub.mapsforge.project.aggregator.xml.PropertySection;
import de.fub.mapsforge.project.aggregator.xml.PropertySet;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.AggregateUtils;
import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = AbstractAggregationProcess.class)
public class CleanProcess extends AbstractAggregationProcess<List<GPSSegment>, List<GPSSegment>> {

    private static final Logger LOG = Logger.getLogger(CleanProcess.class.getName());
    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private ArrayList<GPSSegment> cleanSegmentList = new ArrayList<GPSSegment>();
    private List<GPSSegment> inputList = new ArrayList<GPSSegment>();
    private GPSCleaner gpsCleaner = new GPSCleaner();
    private final Object MUTEX = new Object();
    private final GPSSegmentLayer gPSSegmentLayer;

    public CleanProcess() {
        this(null);
    }

    private void createCleaningOptions(List<Property> properties) {
        gpsCleaner.setCleaningOptions(AggregateUtils.createValue(CleaningOptions.class, properties));

    }

    public CleanProcess(Aggregator aggregator) {
        super(aggregator);
        if (aggregator != null) {
            AggregatorDescriptor aggregatorDescriptor = aggregator.getDescriptor();
            if (aggregatorDescriptor != null) {
                ProcessDescriptorList pipeline = aggregatorDescriptor.getPipeline();
                for (ProcessDescriptor processDescriptor : pipeline.getList()) {
                    if (getClass().getName().equals(processDescriptor.getJavatype())) {
                        Properties properties = processDescriptor.getProperties();
                        if (!properties.getSections().isEmpty()) {
                            PropertySection propertySection = properties.getSections().iterator().next();
                            for (int i = 0; i < propertySection.getPropertySet().size(); i++) {
                                PropertySet propertySet = propertySection.getPropertySet().get(i);
                                if ("Cleaning Settings".equals(propertySet.getName())) {
                                    createCleaningOptions(propertySet.getProperties());
                                }

                            }
                        }
                    }
                }
            }
        }

//        CleaningOptions cleaningOptions = gpsCleaner.getCleaningOptions();
//        cleaningOptions.filterBySegmentLength = true;
//        cleaningOptions.minSegmentLength = 1;
//        cleaningOptions.maxSegmentLength = 100;
//        cleaningOptions.filterByEdgeLength = true;
//        cleaningOptions.minEdgeLength = 0.3;
//        cleaningOptions.maxEdgeLength = 750;
//        cleaningOptions.filterZigzag = true;
//        cleaningOptions.maxZigzagAngle = 30;
//        cleaningOptions.filterFakeCircle = true;
//        cleaningOptions.maxFakeCircleAngle = 50;
//        cleaningOptions.filterOutliers = false;
//        cleaningOptions.maxNumOutliers = 2;

        RenderingOptions renderingOptions = new RenderingOptions();
        renderingOptions.setColor(new Color(39, 172, 88)); // green
        renderingOptions.setRenderingType(RenderingOptions.RenderingType.ALL);
        renderingOptions.setzIndex(0);
        renderingOptions.setOpacity(1);
        gPSSegmentLayer = new GPSSegmentLayer("clean", "Clean gps data", renderingOptions);
        layers.add(gPSSegmentLayer);
    }

    @Override
    protected void start() {
        synchronized (MUTEX) {
            if (inputList != null) {
                gPSSegmentLayer.clearRenderObjects();
                RamerDouglasPeuckerFilter rdpf = new RamerDouglasPeuckerFilter(5);
                int segCount = 0;
                LOG.log(Level.INFO, "segmentsize: {0}", inputList.size());
                int progess = 0;
                for (GPSSegment segment : inputList) {
                    List<GPSSegment> clean = gpsCleaner.clean(segment);
                    LOG.log(Level.INFO, "clean segmentsize: {0}", clean.size());
                    for (GPSSegment cleanSegment : clean) {
                        // run through Douglas-Peucker here (slightly modified
                        // perhaps to avoid too long edges)
                        cleanSegment = rdpf.simplify(cleanSegment);
                        cleanSegmentList.add(cleanSegment);
                        gPSSegmentLayer.add(cleanSegment);
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
        return "Clean";
    }

    @Override
    public String getDescription() {
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
}
