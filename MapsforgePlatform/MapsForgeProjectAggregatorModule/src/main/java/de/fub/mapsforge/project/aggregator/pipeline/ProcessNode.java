/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class ProcessNode extends AbstractNode implements PropertyChangeListener, ProcessPipeline.ProcessListener {

    @StaticResource
    private static final String PROCESS_ICON_NORMAL = "de/fub/mapsforge/project/aggregator/processIconNormal.png";
    @StaticResource
    private static final String PROCESS_ICON_RUN = "de/fub/mapsforge/project/aggregator/processIconRun.png";
    @StaticResource
    private static final String PROCESS_ICON_ERROR = "de/fub/mapsforge/project/aggregator/processIconError.png";

    public ProcessNode(AbstractAggregationProcess<?, ?> process) {
        super(Children.LEAF, Lookups.singleton(process));
        setDisplayName(process.getName());
        setShortDescription(process.getDescription());
        process.addPropertyChangeListener(WeakListeners.propertyChange(ProcessNode.this, process));
        process.addProcessListener(ProcessNode.this);
    }

    @Override
    public Image getIcon(int type) {
        AbstractAggregationProcess process = getLookup().lookup(AbstractAggregationProcess.class);
        if (process != null) {
            switch (process.getProcessState()) {
                case OK:
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

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireIconChange();
    }

    @Override
    public void changed(ProcessPipeline.ProcessEvent event) {
        fireIconChange();
    }
}
