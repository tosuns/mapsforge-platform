/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.nodes;

import de.fub.gpxmodule.xml.gpx.Trkseg;
import java.beans.IntrospectionException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class TrkSegNode extends BeanNode<Trkseg> {

    public TrkSegNode(Trkseg t) throws IntrospectionException {
        this(t, Children.LEAF);
    }

    public TrkSegNode(Trkseg t, Children chldrn) throws IntrospectionException {
        this(t, chldrn, Lookups.singleton(t));
    }

    public TrkSegNode(Trkseg t, Children chldrn, Lookup lkp) throws IntrospectionException {
        super(t, chldrn, lkp);
    }
}
