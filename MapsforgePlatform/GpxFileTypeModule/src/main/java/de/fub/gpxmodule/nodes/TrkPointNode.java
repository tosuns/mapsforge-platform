/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.gpxmodule.nodes;

import de.fub.gpxmodule.xml.Wpt;
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
                trackPoint.getLat().doubleValue(),
                trackPoint.getLon().doubleValue(),
                trackPoint.getTime() != null
                ? formatter.format(trackPoint.getTime())
                : Bundle.CLT_time_not_available()));
        setShortDescription(Bundle.CTL_descritpion());
    }
}
