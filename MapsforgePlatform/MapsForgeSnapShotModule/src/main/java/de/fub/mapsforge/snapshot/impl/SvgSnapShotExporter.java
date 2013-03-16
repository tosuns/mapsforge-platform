/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.snapshot.impl;

import de.fub.mapsforge.snapshot.api.ComponentSnapShotExporter;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.swing.filechooser.FileFilter;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

@NbBundle.Messages({
    "CLT_SvgSnapShotExporter_Description=Exports a swing component as svg file."
})
public final class SvgSnapShotExporter implements ComponentSnapShotExporter {

    private static final long serialVersionUID = 1L;

    @Override
    public Image getIconImage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getShortDescription() {
        return Bundle.CLT_SvgSnapShotExporter_Description();
    }

    @Override
    public void export(Component component) {
        FileChooserBuilder fileChooserBuilder = new FileChooserBuilder(ComponentSnapShotExporter.class).addFileFilter(new SvgFileFilter());
        handleExport(null, component);
    }

    private void handleExport(File file, Component component) {
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

    private static class SvgFileFilter extends FileFilter {

        public SvgFileFilter() {
        }

        @Override
        public boolean accept(File file) {
            FileObject fileObject = FileUtil.toFileObject(file);
            if (fileObject != null) {
                return fileObject.isFolder() || fileObject.isData() && "svg".equalsIgnoreCase(fileObject.getExt());
            } else {
                return file.isDirectory() || "svg".equalsIgnoreCase(file.getName().substring(file.getName().lastIndexOf(".") + 1));
            }
        }

        @Override
        public String getDescription() {
            return "Scalable Vector Graphic (*.svg)";
        }
    }
}
