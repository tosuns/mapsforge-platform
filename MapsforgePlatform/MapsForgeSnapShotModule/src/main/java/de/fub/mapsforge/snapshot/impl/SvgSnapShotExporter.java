/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.snapshot.impl;

import de.fub.mapsforge.snapshot.api.AbstractComponentSnapShotExporter;
import de.fub.mapsforge.snapshot.api.ComponentSnapShotExporter;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

@NbBundle.Messages({
    "CLT_SvgSnapShotExporter_Description=Exports a swing component as svg file.",
    "CLT_SvgSnapShotExporter_Name=Svg"
})
@ServiceProvider(service = ComponentSnapShotExporter.class)
public final class SvgSnapShotExporter extends AbstractComponentSnapShotExporter {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/snapshot/impl/svgIcon.png";
    private static final long serialVersionUID = 1L;

    @Override
    public Image getIconImage() {
        return ImageUtilities.loadImage(ICON_PATH, false);
    }

    @Override
    public String getName() {
        return Bundle.CLT_SvgSnapShotExporter_Name();
    }

    @Override
    public String getShortDescription() {
        return Bundle.CLT_SvgSnapShotExporter_Description();
    }

    @Override
    public void export(Component component) {
        File selectedFile = showFileChoose("svg");
        if (selectedFile != null) {
            handleExport(selectedFile, component);
        }

    }

    private void handleExport(File file, Component component) {
        if (component != null) {
            Writer out = null;
            FileOutputStream fileOutputSream = null;
            SVGGraphics2D svgGenerator = null;
            try {
                DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

                // Create an instance of org.w3c.dom.Document.
                String svgNS = "http://www.w3.org/2000/svg";
                Document document = domImpl.createDocument(svgNS, "svg", null);

                // Create an instance of the SVG Generator.
                svgGenerator = new SVGGraphics2D(document);

                // Ask the test to render into the SVG Graphics2D implementation.
                component.paintAll(svgGenerator);

                // Finally, stream out SVG to the standard output using
                // UTF-8 encoding.
                boolean useCSS = true; // we want to use CSS style attributes
                fileOutputSream = new FileOutputStream(file);
                out = new OutputStreamWriter(fileOutputSream, "UTF-8");
                svgGenerator.stream(out, useCSS);
            } catch (SVGGraphics2DIOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                if (svgGenerator != null) {
                    svgGenerator.dispose();
                }
                try {
                    if (fileOutputSream != null) {
                        fileOutputSream.close();
                    }
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
