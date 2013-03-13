/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.nodes;

import de.fub.gpxmodule.xml.gpx.Gpx.Rte;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class RteNode extends BeanNode<Rte> {

    public RteNode(Rte t) throws IntrospectionException {
        super(t);
    }

    public RteNode(Rte t, Children chldrn) throws IntrospectionException {
        super(t, chldrn);
    }

    public RteNode(Rte t, Children chldrn, Lookup lkp) throws IntrospectionException {
        super(t, chldrn, lkp);
    }
}
