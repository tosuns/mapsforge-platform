/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.ui.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

@Messages("CTL_SaveAsSvgAction=Save As Svg")
public final class SaveAsSvgAction extends AbstractAction {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/detector/model/inference/ui/actions/svgIcon.png";
    private static final long serialVersionUID = 1L;
    private JComponent component;

    public SaveAsSvgAction() {
        super(null, ImageUtilities.loadImageIcon(ICON_PATH, false));
        putValue(Action.SHORT_DESCRIPTION, Bundle.CTL_SaveAsSvgAction());
    }

    public SaveAsSvgAction(JComponent component) {
        this();
        this.component = component;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (component != null) {
            Writer out = null;
            try {
                DOMImplementation domImpl =
                        GenericDOMImplementation.getDOMImplementation();
                // Create an instance of org.w3c.dom.Document.
                String svgNS = "http://www.w3.org/2000/svg";
                Document document = domImpl.createDocument(svgNS, "svg", null);
                // Create an instance of the SVG Generator.
                SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
                // Ask the test to render into the SVG Graphics2D implementation.
                component.paintAll(svgGenerator);
                // Finally, stream out SVG to the standard output using
                // UTF-8 encoding.
                boolean useCSS = true; // we want to use CSS style attributes
                out = new StringWriter();
//                        new OutputStreamWriter(System.out, "UTF-8");
                svgGenerator.stream(out, useCSS);
            } catch (SVGGraphics2DIOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        }
    }
}
