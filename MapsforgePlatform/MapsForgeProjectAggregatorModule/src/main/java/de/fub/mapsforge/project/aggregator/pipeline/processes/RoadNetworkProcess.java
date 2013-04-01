/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.processes;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.management.Statistics;
import de.fub.agg2graph.roadgen.Intersection;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.agg2graphui.layers.IntersectionLayer;
import de.fub.agg2graphui.layers.RoadNetworkLayer;
import de.fub.mapforgeproject.api.process.ProcessPipeline;
import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractXmlAggregationProcess;
import de.fub.mapsforge.project.models.Aggregator;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = AbstractAggregationProcess.class)
public class RoadNetworkProcess extends AbstractXmlAggregationProcess<AggContainer, RoadNetwork> implements StatisticProvider {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private RoadNetwork roadNetwork = null;
    private IntersectionLayer intersectionLayer = new IntersectionLayer();
    private RoadNetworkLayer roadNetworkLayer = new RoadNetworkLayer();

    public RoadNetworkProcess() {
        this(null);
    }

    public RoadNetworkProcess(Aggregator container) {
        super(container);
        getLayers().add(intersectionLayer);
        getLayers().add(roadNetworkLayer);
    }

    @Override
    public void setInput(AggContainer input) {
    }

    @Override
    protected void start() {

        if (getAggregator() != null) {
            ProgressHandle handle = ProgressHandleFactory.createHandle(getName());
            try {
                handle.start();
                roadNetworkLayer.clearRenderObjects();
                intersectionLayer.clearRenderObjects();

                roadNetwork = new RoadNetwork();
                roadNetwork.parse(getAggregator().getAggContainer(), null);
                handle.switchToDeterminate(roadNetwork.intersections.size());
                int counter = 0;
                for (Intersection intersection : roadNetwork.intersections) {

                    if (canceled.get()) {
                        fireProcessCanceledEvent();
                        return;
                    }

                    intersectionLayer.add(intersection);
                    handle.progress(++counter);
                }

                roadNetworkLayer.add(roadNetwork);

                fireProcessProgressEvent(new ProcessPipeline.ProcessEvent<RoadNetworkProcess>(this, "Creating Roadnetwork...", 100));
            } finally {
                handle.finish();
            }
        }
    }

    @Override
    public RoadNetwork getResult() {
        synchronized (RUN_MUTEX) {
            return roadNetwork;
        }
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDisplayName();
        }
        return "Road Generator";
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDescription();
        }
        return "Default Road Generator";
    }

    @Override
    public Image getIcon() {
        return IMAGE;
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    @NbBundle.Messages({"CLT_No_Statistics_Available=No road network was computed to provide its statistics. Please run the read generator process!",
        "CLT_Description_Not_Available=Description not available."})
    @Override
    public List<StatisticSection> getStatisticData() throws StatisticNotAvailableException {
        List<StatisticSection> statisticData = new ArrayList<StatisticSection>();

        // create process performance statistics
        StatisticSection section = getPerformanceData();
        statisticData.add(section);

        if (getResult() != null) {
            // create road network statistics
            section = new StatisticSection("Road Network Statistics", "Displays statistical data of the generated road network");
            statisticData.add(section);
            Map<String, Double> data = Statistics.getData(getResult());
            for (Entry<String, Double> entry : data.entrySet()) {
                section.getStatisticsItemList().add(new StatisticItem(entry.getKey(), String.valueOf(entry.getValue()), Bundle.CLT_Description_Not_Available()));
            }
        }
        return statisticData;
    }

    @Override
    public boolean cancel() {
        canceled.set(true);
        return canceled.get();
    }
}
