/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.processes;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.IAggregationStrategy;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.PointGhostPointPair;
import de.fub.agg2graph.agg.strategy.AbstractAggregationStrategy;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSSegment;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graphui.layers.AggContainerLayer;
import de.fub.agg2graphui.layers.MatchingLayer;
import de.fub.agg2graphui.layers.MergingLayer;
import de.fub.mapforgeproject.api.process.ProcessPipeline;
import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractXmlAggregationProcess;
import de.fub.mapsforge.project.models.Aggregator;
import java.awt.Image;
import java.util.ArrayList;
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
public class AggregationProcess extends AbstractXmlAggregationProcess<List<GPSSegment>, AggContainer> implements StatisticProvider {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private List<GPSSegment> inputList;
    private static final Logger LOG = Logger.getLogger(AggregationProcess.class.getName());
    private final MergingLayer mergeLayer;
    private final MatchingLayer matchingLayer;
    private final AggContainerLayer aggregationLayer;
    private int totalAggNodeCount = 0;
    private int totalGPSPointCount = 0;
    private int totalPointGhostPointPairs = 0;

    public AggregationProcess() {
        this(null);
    }

    public AggregationProcess(Aggregator aggregator) {
        super(aggregator);
        mergeLayer = new MergingLayer();
        matchingLayer = new MatchingLayer();
        aggregationLayer = new AggContainerLayer();

        getLayers().add(matchingLayer);
        getLayers().add(mergeLayer);
        getLayers().add(aggregationLayer);
    }

    @Override
    public void setInput(List<GPSSegment> input) {
        this.inputList = input;
    }

    @Override
    public AggContainer getResult() {
        synchronized (RUN_MUTEX) {
            return getAggregator().getAggContainer();
        }
    }

    @Override
    protected void start() {
        totalAggNodeCount = 0;
        totalGPSPointCount = 0;
        totalPointGhostPointPairs = 0;

        if (inputList != null
                && getAggregator() != null
                && getAggregator().getAggContainer() != null) {
            LOG.log(Level.FINE, "Segment size: {0}", inputList.size());
            ProgressHandle handle = ProgressHandleFactory.createHandle(getName());
            handle.start(inputList.size());
            try {

                aggregationLayer.clearRenderObjects();
                mergeLayer.clearRenderObjects();
                matchingLayer.clearRenderObjects();

                AggContainer aggContainer = getAggregator().getAggContainer();

                int counter = 0;

                LOG.log(Level.FINE, "clean segments: {0}", inputList.toString());

                for (GPSSegment inputSegment : inputList) {
                    if (canceled.get()) {
                        fireProcessCanceledEvent();
                        break;
                    }

                    counter++;
                    aggContainer.addSegment(inputSegment);

                    // update debug layers: matching, merging
                    IAggregationStrategy aggregationStrategy = aggContainer.getAggregationStrategy();

                    if (aggregationStrategy instanceof AbstractAggregationStrategy) {
                        AbstractAggregationStrategy abstractAggregationStrategy = (AbstractAggregationStrategy) aggregationStrategy;
                        IMergeHandler mergeHandler = abstractAggregationStrategy.getMergeHandler();

                        if (mergeHandler != null) {

                            List<AggNode> aggNodes = mergeHandler.getAggNodes();
                            if (aggNodes != null) {
                                totalAggNodeCount += aggNodes.size();
                                matchingLayer.add(aggNodes);
                            }

                            List<GPSPoint> gpsPoints = mergeHandler.getGpsPoints();
                            if (gpsPoints != null) {
                                totalGPSPointCount += gpsPoints.size();
                                matchingLayer.add(gpsPoints);
                            }

                            List<PointGhostPointPair> pointGhostPointPairs = mergeHandler.getPointGhostPointPairs();
                            for (PointGhostPointPair pgpp : pointGhostPointPairs) {
                                List<ILocation> line = new ArrayList<ILocation>(2);
                                line.add(new GPSPoint(pgpp.point));
                                line.add(new GPSPoint(pgpp.ghostPoint));
                                mergeLayer.add(line);
                                totalPointGhostPointPairs++;
                            }
                        }
                    }

                    fireProcessProgressEvent(new ProcessPipeline.ProcessEvent<AggregationProcess>(this, "Aggregation...", (int) ((100d / inputList.size()) * (counter))));
                    handle.progress(counter);
                    LOG.log(Level.FINE, "Segment number: {0}", (counter));
                }


                aggregationLayer.add(aggContainer);
            } finally {
                handle.finish();
            }

        }
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDisplayName();
        }
        return "Aggregation";
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDescription();
        }
        return "Aggregation process";
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

        StatisticSection section = new StatisticSection("Aggregation Statistics", "Displays all statistics data which are computed during the aggregation.");
        statisticSections.add(section);
        section.getStatisticsItemList().add(new StatisticItem("Total Aggregation Node Count", String.valueOf(totalAggNodeCount), "The total amount of aggreagtion nodes, which are created during this aggregation process."));
        section.getStatisticsItemList().add(new StatisticItem("Total GPS Point Count", String.valueOf(totalGPSPointCount), "The total amount of GPS Point that are added during the aggregation."));
        section.getStatisticsItemList().add(new StatisticItem("Total Count Point/GhostPoint Pairs ", String.valueOf(totalPointGhostPointPairs), "The total amount of paris of Point/Ghostpoint."));

        return statisticSections;
    }

    @Override
    public boolean cancel() {
        canceled.set(true);
        return canceled.get();
    }
}
