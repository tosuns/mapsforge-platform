/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors.filters;

import de.fub.agg2graph.gpseval.data.filter.MinTimeDiffWaypointFilter;
import de.fub.agg2graph.structs.GPSTrack;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.pipeline.preprocessors.FilterProcess;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MinTimeDiffWaypointFilterProcess_Name=Minimum Time Difference Filter",
    "CLT_MinTimeDiffWaypointFilterProcess_Description=This filter is responsible "
    + "to check whether the time difference between each pair of gps points "
    + "contained by a gps track does not exceed a given threshold. if there "
    + "is a pair of gps point where the time difference exceed the specified "
    + "the track get seperated into two segement and the filter continues "
    + "the filtering process with the second segment until the end of the "
    + "gps track is reached."
})
@ServiceProvider(service = FilterProcess.class)
public class MinTimeDiffWaypointFilterProcess extends FilterProcess<GPSTrack, GPSTrack> {

    private static final String PROPERTY_TIME_DIFF = "timeDiff";
    private GPSTrack gpsTrack = null;
    private MinTimeDiffWaypointFilter filter = new MinTimeDiffWaypointFilter();

    public MinTimeDiffWaypointFilterProcess() {
        this(null);
    }

    public MinTimeDiffWaypointFilterProcess(Detector detector) {
        super(detector);
        init();
    }

    private void init() {
        ProcessDescriptor processDescriptor = getProcessDescriptor();
        if (processDescriptor != null) {
            for (Property property : processDescriptor.getProperties().getPropertyList()) {
                filter.setParam(PROPERTY_TIME_DIFF, property.getValue());
            }
        }
    }

    @Override
    protected void start() {
        filter.reset();
    }

    @Override
    public String getName() {
        return Bundle.CLT_MinTimeDiffWaypointFilterProcess_Name();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_MinTimeDiffWaypointFilterProcess_Description();
    }

    @Override
    public void setInput(GPSTrack gpsTrack) {
        this.gpsTrack = gpsTrack;
    }

    @Override
    public GPSTrack getResult() {
        return this.gpsTrack;
    }

    @Override
    public boolean cancel() {
        return false;
    }
}
