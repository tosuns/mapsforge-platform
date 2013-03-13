/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.nodes;

import de.fub.gpxmodule.xml.gpx.Wpt;
import java.beans.IntrospectionException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import org.openide.nodes.BeanNode;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class TrkPointNode extends BeanNode<Wpt> {

    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @NbBundle.Messages({"CTL_descritpion=Lat/Lon and time stamp of the track point.", "CLT_time_not_available=not available"})
    public TrkPointNode(Wpt trackPoint) throws IntrospectionException {
        super(trackPoint);
        setDisplayName(MessageFormat.format("Track Point: {0}/{1} [{2}]",
                trackPoint.getLat().getValue().doubleValue(),
                trackPoint.getLon().getValue().doubleValue(),
                trackPoint.getTime() != null
                ? formatter.format(trackPoint.getTime())
                : Bundle.CLT_time_not_available()));
        setShortDescription(Bundle.CTL_descritpion());
    }
}
