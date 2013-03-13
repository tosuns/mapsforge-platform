/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.nodes;

import de.fub.gpxmodule.xml.gpx.Trk;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages("CLT_no_name_available=No name available")
public class TrkNode extends BeanNode<Trk> {

    public TrkNode(Trk t) throws IntrospectionException {
        this(t, Children.LEAF);
    }

    public TrkNode(Trk t, Children chldrn) throws IntrospectionException {
        this(t, chldrn, Lookups.singleton(t));
    }

    public TrkNode(Trk t, Children chldrn, Lookup lkp) throws IntrospectionException {
        super(t, chldrn, lkp);

        if (t.getName() == null) {
            setDisplayName(Bundle.CLT_no_name_available());
        }
    }
}
