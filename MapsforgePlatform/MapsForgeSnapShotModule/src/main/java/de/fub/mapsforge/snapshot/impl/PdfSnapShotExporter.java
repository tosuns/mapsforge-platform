/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.snapshot.impl;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import de.fub.mapsforge.snapshot.api.AbstractComponentSnapShotExporter;
import de.fub.mapsforge.snapshot.api.ComponentSnapShotExporter;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_PdfSnapShotExporter_Description=Exports a component as Pdf File.",
    "CLT_PdfSnapShotExporter_Name=Pdf"
})
@ServiceProvider(service = ComponentSnapShotExporter.class)
public class PdfSnapShotExporter extends AbstractComponentSnapShotExporter {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/snapshot/impl/pdfIcon.png";

    @Override
    public Image getIconImage() {
        return ImageUtilities.loadImage(ICON_PATH, false);
    }

    @Override
    public String getName() {
        return Bundle.CLT_PdfSnapShotExporter_Name();
    }

    @Override
    public String getShortDescription() {
        return Bundle.CLT_PdfSnapShotExporter_Description();
    }

    @Override
    public void export(Component component) {
        File showFileChoose = showFileChoose("pdf");
        if (showFileChoose != null) {
            handleExport(showFileChoose, component);
        }
    }

    private void handleExport(final File selectedFile, final Component component) {
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        if (component != null) {
                            PdfGraphics2D pdfGraphics2D = null;
                            try {
                                Dimension preferredSize = component.getSize();
                                Dimension dimension = preferredSize; //DimensionUtil.computeToA4Pdf(preferredSize);
                                // step 1
                                Document document = new Document(new Rectangle(dimension.width, dimension.height));
                                // step 2
                                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(selectedFile));
                                // step 3
                                document.open();
//                                document.newPage();
                                // step 4
                                PdfContentByte cb = writer.getDirectContent();
//                                PdfTemplate map = cb.createTemplate(dimension.width * 10, dimension.height * 10);
                                pdfGraphics2D = new PdfGraphics2D(cb, dimension.width, dimension.height);
//                                component.setPreferredSize(dimension);
//                                component.setSize(dimension);
//                                component.revalidate();
//                                component.repaint();
                                // paintAll must be called, a simple paint does
                                //not change the size of the component
                                component.printAll(pdfGraphics2D);
//                                component.setPreferredSize(preferredSize);
//                                component.setSize(preferredSize);
//                                component.revalidate();
//                                component.repaint();

                                pdfGraphics2D.dispose();
//                                cb.addTemplate(map, 0, 0);
                                // step 5
                                document.close();
                            } catch (FileNotFoundException ex) {
                                Exceptions.printStackTrace(ex);
                            } catch (DocumentException ex) {
                                Exceptions.printStackTrace(ex);
                            } finally {
                                if (pdfGraphics2D != null) {
                                    pdfGraphics2D.dispose();
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}
