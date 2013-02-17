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
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.ProcessPipeline;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.models.Aggregator;
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
public class AggregationProcess extends AbstractAggregationProcess<List<GPSSegment>, AggContainer> {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private List<GPSSegment> inputList;
    private static final Logger LOG = Logger.getLogger(AggregationProcess.class.getName());
    private final MergingLayer mergeLayer;
    private final MatchingLayer matchingLayer;
    private final AggContainerLayer aggregationLayer;

    public AggregationProcess() {
        this(null);
    }

    public AggregationProcess(Aggregator aggregator) {
        super(aggregator);
        mergeLayer = new MergingLayer();
        matchingLayer = new MatchingLayer();
        aggregationLayer = new AggContainerLayer();

        layers.add(matchingLayer);
        layers.add(mergeLayer);
        layers.add(aggregationLayer);
    }

    @Override
    public void setInput(List<GPSSegment> input) {
        this.inputList = input;
    }

    @Override
    public AggContainer getResult() {
        return aggregator.getAggContainer();
    }

    @Override
    protected void start() {
        if (inputList != null
                && aggregator != null
                && aggregator.getAggContainer() != null) {
            LOG.log(Level.INFO, "Segment size: {0}", inputList.size());

            aggregationLayer.clearRenderObjects();
            mergeLayer.clearRenderObjects();
            matchingLayer.clearRenderObjects();
            AggContainer aggContainer = aggregator.getAggContainer();

            int counter = 0;

            for (GPSSegment inputSegment : inputList) {
                counter++;
                aggregator.getAggContainer().addSegment(inputSegment);

                // update debug layers: matching, merging
                IAggregationStrategy aggregationStrategy = aggContainer.getAggregationStrategy();
                if (aggregationStrategy instanceof AbstractAggregationStrategy) {
                    AbstractAggregationStrategy abstractAggregationStrategy = (AbstractAggregationStrategy) aggregationStrategy;
                    IMergeHandler mergeHandler = abstractAggregationStrategy.getMergeHandler();
                    if (mergeHandler != null) {
                        List<AggNode> aggNodes = mergeHandler.getAggNodes();
                        if (aggNodes != null) {
                            matchingLayer.add(aggNodes);
                        }

                        List<GPSPoint> gpsPoints = mergeHandler.getGpsPoints();
                        if (gpsPoints != null) {
                            matchingLayer.add(gpsPoints);
                        }

                        List<PointGhostPointPair> pointGhostPointPairs = mergeHandler.getPointGhostPointPairs();
                        for (PointGhostPointPair pgpp : pointGhostPointPairs) {
                            List<ILocation> line = new ArrayList<ILocation>(2);
                            line.add(new GPSPoint(pgpp.point));
                            line.add(new GPSPoint(pgpp.ghostPoint));
                            mergeLayer.add(line);
                        }
                    }
                }

                fireProcessEvent(new ProcessPipeline.ProcessEvent(this, "Aggregation...", (int) ((100d / inputList.size()) * (counter))));
                LOG.log(Level.INFO, "Segment number: {0}", (counter));
            }


            aggregationLayer.add(aggContainer);

        }
    }

    @Override
    public String getName() {
        return "Aggregation";
    }

    @Override
    public String getDescription() {
        return "Aggregation process";
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
