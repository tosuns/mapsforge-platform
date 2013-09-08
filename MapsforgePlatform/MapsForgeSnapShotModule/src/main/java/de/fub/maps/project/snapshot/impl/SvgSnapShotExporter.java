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
package de.fub.maps.project.snapshot.impl;

import de.fub.maps.project.snapshot.api.AbstractComponentSnapShotExporter;
import de.fub.maps.project.snapshot.api.ComponentSnapShotExporter;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
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

/**
 * Implementation of the ComponentSnapShotExporter, which handles the
 * exportation/printing of a specified Component as SVG file.
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_SvgSnapShotExporter_Description=Exports a swing component as svg file.",
    "CLT_SvgSnapShotExporter_Name=Svg"
})
@ServiceProvider(service = ComponentSnapShotExporter.class)
public final class SvgSnapShotExporter extends AbstractComponentSnapShotExporter {

    @StaticResource
    private static final String ICON_PATH = "de/fub/maps/project/snapshot/impl/svgIcon.png";
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
                            Dimension dimension = preferredSize; //DimensionUtil.computeToA4(preferredSize);

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

                                // Ask the test to render into the SVG Graphics2D implementation.
//                                component.setPreferredSize(dimension);
//                                component.setSize(dimension);
//                                component.revalidate();
//                                component.repaint();
                                // paintAll must be called, a simple paint does
                                //not change the size of the component
                                component.paintAll(svgGenerator);
//                                component.setPreferredSize(preferredSize);
//                                component.setSize(preferredSize);
//                                component.revalidate();
//                                component.repaint();

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
}
