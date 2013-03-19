/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.snapshot.impl;

import de.fub.mapsforge.snapshot.api.AbstractComponentSnapShotExporter;
import de.fub.mapsforge.snapshot.api.ComponentSnapShotExporter;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.swing.SwingUtilities;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
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

    private void handleExport(final File file, final Component component) {
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {

                if (component != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Dimension preferredSize = component.getPreferredSize();
                            Dimension dimension = computeDestDimension(preferredSize);

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
                                svgGenerator.setSVGCanvasSize(dimension);
//                                svgGenerator.scale(dimension.getWidth() / preferredSize.getWidth(), dimension.getHeight() / preferredSize.getHeight());
                                // Ask the test to render into the SVG Graphics2D implementation.
                                component.setPreferredSize(dimension);
                                component.setSize(dimension);
                                component.revalidate();
                                component.repaint();
                                component.paintAll(svgGenerator);
                                component.setPreferredSize(preferredSize);
                                component.setSize(preferredSize);
                                component.revalidate();
                                component.repaint();

                                // Finally, stream out SVG to the standard output using
                                // UTF-8 encoding.
                                boolean useCSS = false; // we want to use CSS style attributes
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
                    });
                }
            }
        });
    }

    /**
     * this method computes the A4 dimension in pixel unites depending on the
     * monitor screen resolution.
     *
     * @param sourceDimension
     * @return
     */
    private Dimension computeDestDimension(Dimension sourceDimension) {
        Dimension destDimension = sourceDimension;

        // metric factor inch in mm
        final double metricFactor = 2.54 * 10d;

        // compute ppi value through screnn resolution and a factor. the factor is currently not
        // resolution independent.
        double ppi = Toolkit.getDefaultToolkit().getScreenResolution() * 0.9379;

        // get current sourceDimension as mm units
        double widthInMm = sourceDimension.getWidth() / ppi * metricFactor;
        double heightInMm = sourceDimension.getHeight() / ppi * metricFactor;

        // compute the factors in relation to a A4 form
        double widthFactor = widthInMm < 210 ? widthInMm / 210 : 210 / widthInMm;
        double heightFactor = heightInMm < 297 ? heightInMm / 297 : 297 / heightInMm;

        // compute the destination dimension
        int destWidth = (int) (sourceDimension.getWidth() * widthFactor);
        int destHeight = (int) (sourceDimension.getHeight() * heightFactor);
        destDimension = new Dimension(destWidth, destHeight);

        return destDimension;
    }
}
