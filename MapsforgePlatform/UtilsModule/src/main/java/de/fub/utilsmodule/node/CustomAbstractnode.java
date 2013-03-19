/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.node;

import de.fub.utilsmodule.text.StringUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 * A custom node which only changes the short description method. The short
 * description will be line wrapped and transformd to a html test,
 *
 * With this changes the tooltip of a node that will be display will not have an
 * infinitive width with long short descriptions.
 *
 * @author Serdar
 */
public abstract class CustomAbstractnode extends AbstractNode {

    public CustomAbstractnode(Children children) {
        super(children);
    }

    public CustomAbstractnode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    @Override
    public void setShortDescription(String s) {
        super.setShortDescription(StringUtils.StringAsHtmlWrapString(s));
    }
}
