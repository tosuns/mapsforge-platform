/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.processes;

import de.fub.agg2graph.input.GPXReader;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.ui.gui.RenderingOptions;
import de.fub.agg2graphui.controller.AbstractLayer;
import de.fub.agg2graphui.layers.GPSSegmentLayer;
import de.fub.mapforgeproject.api.process.ProcessPipeline;
import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractXmlAggregationProcess;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.models.Aggregator;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
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
public class DatasourceProcess extends AbstractXmlAggregationProcess<Void, List<GPSSegment>> implements StatisticProvider {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private static final Logger LOG = Logger.getLogger(DatasourceProcess.class.getName());
    private final List<GPSSegment> segments = new ArrayList<GPSSegment>();
    private GPSSegmentLayer gPSSegmentLayer;
    private int totalSegmentCount = 0;
    private int totalGPSPoints = 0;
    private int totalGPXFiles = 0;

    public DatasourceProcess() {
        this(null);
    }

    public DatasourceProcess(Aggregator container) {
        super(container);
        init();
    }

    private void init() {
        RenderingOptions renderingOptions = new RenderingOptions();
        renderingOptions.setColor(new Color(97, 123, 228)); // blue
        renderingOptions.setRenderingType(RenderingOptions.RenderingType.ALL);
        renderingOptions.setzIndex(-1);
        renderingOptions.setOpacity(1);
        gPSSegmentLayer = new GPSSegmentLayer("Raw-Layer", "Displays the raw gps data.", renderingOptions);
        super.getLayers().add(gPSSegmentLayer);
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDisplayName();
        }
        return "Gpx Datasource";
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDescription();
        }
        return "Gpx segment provider";
    }

    @Override
    public void setInput(Void input) {
        // do nothing we get the input from aggContainer
    }

    @Override
    public List<GPSSegment> getResult() {
        synchronized (RUN_MUTEX) {
            return Collections.unmodifiableList(this.segments);
        }
    }

    @Override
    public List<AbstractLayer<?>> getLayers() {
        return Collections.unmodifiableList(super.getLayers());
    }

    @Override
    protected void start() {
        totalGPSPoints = 0;
        totalGPXFiles = 0;
        totalSegmentCount = 0;
        segments.clear();
        gPSSegmentLayer.clearRenderObjects();
        List<Source> sourceList = getAggregator().getSourceList();
        int progress = 0;
        ProgressHandle handle = ProgressHandleFactory.createHandle(getName());
        handle.start(sourceList.size());
        try {
            for (Source source : sourceList) {

                if (canceled.get()) {
                    fireProcessCanceledEvent();
                    break;
                }

                File file = new File(source.getUrl());
                if (file.exists()) {
                    this.segments.addAll(GPXReader.getSegments(file));
                    totalSegmentCount += segments.size();
                    int segmentId = 0;
                    for (GPSSegment segment : segments) {

                        if (canceled.get()) {
                            fireProcessCanceledEvent();
                            break;
                        }

                        segment.addIDs("I" + (segmentId++));
                        gPSSegmentLayer.add(segment);
                        totalGPSPoints += segment.size();
                    }
                }
                totalGPXFiles++;
                handle.progress(totalGPXFiles);
                fireProcessProgressEvent(new ProcessPipeline.ProcessEvent<DatasourceProcess>(this, "import", (int) ((100d / sourceList.size()) * (++progress))));
            }
        } finally {
            handle.finish();
        }
        LOG.log(Level.INFO, "segments: {0}", segments.toString());
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

        StatisticSection section = new StatisticSection("GPS Datasource Statistics", "Displays the statistical data of the raw gps data.");
        statisticSections.add(section);
        section.getStatisticsItemList().add(new StatisticItem("Total GPS Points", String.valueOf(totalGPSPoints), "Represents the total count of GPS points that the data set contains."));
        section.getStatisticsItemList().add(new StatisticItem("Total GPS Segemtns", String.valueOf(totalSegmentCount), "Represents the total count of GPS segments that the data set contains."));
        section.getStatisticsItemList().add(new StatisticItem("GPS points/segment ratio", String.valueOf(totalGPSPoints / (double) totalSegmentCount), "Displays the ratio of gps points to segments"));
        section.getStatisticsItemList().add(new StatisticItem("Total GPX File", String.valueOf(getAggregator().getSourceList().size()), "Represents the total amount of GPX files as data set"));

        return statisticSections;
    }

    @Override
    public boolean cancel() {
        canceled.set(true);
        return canceled.get();
    }
}
