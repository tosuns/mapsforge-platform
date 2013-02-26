/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import de.fub.mapforgeproject.api.process.ProcessNode;
import java.awt.Image;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public class AggregationProcessNode extends ProcessNode {

    @StaticResource
    private static final String PROCESS_ICON_NORMAL = "de/fub/mapsforge/project/aggregator/processIconNormal.png";
    @StaticResource
    private static final String PROCESS_ICON_RUN = "de/fub/mapsforge/project/aggregator/processIconRun.png";
    @StaticResource
    private static final String PROCESS_ICON_ERROR = "de/fub/mapsforge/project/aggregator/processIconError.png";

    public AggregationProcessNode(AbstractAggregationProcess<?, ?> process) {
        super(process);
        process.addPropertyChangeListener(WeakListeners.propertyChange(AggregationProcessNode.this, process));
    }

    @Override
    public Image getIcon(int type) {
        AbstractAggregationProcess process = getLookup().lookup(AbstractAggregationProcess.class);
        if (process != null) {
            switch (process.getProcessState()) {
                case INACTIVE:
                    return ImageUtilities.loadImage(PROCESS_ICON_NORMAL);
                case RUNNING:
                    return ImageUtilities.loadImage(PROCESS_ICON_RUN);
                case ERROR:
                    return ImageUtilities.loadImage(PROCESS_ICON_ERROR);
                default:
                    break;
            }
        }
        return super.getIcon(type); //fall back
    }
}
