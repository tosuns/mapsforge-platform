/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.datasource.spi.actions;

import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.gpxmodule.xml.gpx.ObjectFactory;
import de.fub.gpxmodule.xml.gpx.Trk;
import de.fub.mapsforge.project.datasource.GPXDatasourceNode;
import de.fub.mapsforge.project.datasource.MapsForgeDatasourceNodeFactory;
import de.fub.mapsforge.project.datasource.spi.TrackSegmentBehaviour;
import de.fub.mapsforge.project.datasource.spi.TrksegWrapper;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 *
 * @author Serdar
 */
public class TrackSemgentExportAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private ExplorerManager explorerManger;
    private ExportDialog exportDialog;

    public TrackSemgentExportAction() {
        super();
        Image image = IconRegister.findRegisteredIcon("exportIcon.png");
        if (image != null) {
            putValue(Action.SMALL_ICON, new ImageIcon(image));
        }
    }

    public TrackSemgentExportAction(ExplorerManager explorerManager) {
        this();
        this.explorerManger = explorerManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        exportDialog = new ExportDialog();
        Object[] options = exportDialog.getOptions();
        DialogDescriptor dd = new DialogDescriptor(
                exportDialog,
                "Save Dialog",
                true,
                options,
                options[1],
                DialogDescriptor.BOTTOM_ALIGN,
                HelpCtx.DEFAULT_HELP,
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler(e);
            }
        });
        dd.setClosingOptions(options);
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);

    }

    private void handler(ActionEvent event) {
        if (exportDialog != null && explorerManger != null) {
            if (event.getSource() == exportDialog.getOptions()[0]) {

                final boolean openAfterSave = exportDialog.getOpenAfterSave().isSelected();
                final Object selectedItem = exportDialog.getFolderName().getSelectedItem();
                String fileName = exportDialog.getFileName().getText();

                if (!fileName.endsWith(".gpx")) {
                    fileName = MessageFormat.format("{0}.gpx", fileName);
                }

                if (selectedItem instanceof String && fileName != null) {
                    final String folderName = (String) selectedItem;
                    Children children = explorerManger.getRootContext().getChildren();
                    Trk trk = new Trk();

                    for (Node node : children.getNodes()) {
                        if (node instanceof TrackSegmentBehaviour) {
                            TrksegWrapper trkseg = node.getLookup().lookup(TrksegWrapper.class);
                            TrackSegmentBehaviour trackNode = (TrackSegmentBehaviour) node;
                            if (trackNode.isVisible() && trkseg != null) {
                                trk.getTrkseg().add(trkseg.getTrkseg());
                            }
                        }
                    }

                    final Gpx gpx = new Gpx();
                    gpx.getTrk().add(trk);

                    GPXDatasourceNode datasourceNode = MapsForgeDatasourceNodeFactory.gpxDatasourceNode;

                    if (datasourceNode != null) {
                        final DataObject dataObject = datasourceNode.getLookup().lookup(DataObject.class);

                        if (dataObject != null) {
                            final String fileN = fileName;
                            FileUtil.runAtomicAction(new Runnable() {
                                @Override
                                public void run() {
                                    OutputStream outputStream = null;
                                    FileObject storedFileObject = null;

                                    try {

                                        FileObject datasourceFileObject = dataObject.getPrimaryFile();
                                        FileObject folderObject = datasourceFileObject.getFileObject(folderName);
                                        if (folderObject == null) {
                                            folderObject = datasourceFileObject.createFolder(folderName);
                                        }

                                        FileObject fileObject = folderObject.getFileObject(fileN);
                                        String newFileName = fileN;
                                        if (fileObject != null) {
                                            newFileName = FileUtil.findFreeFileName(folderObject, fileN, "gpx");
                                        }

                                        storedFileObject = folderObject.createData(newFileName);
                                        outputStream = storedFileObject.getOutputStream();

                                        javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
                                        javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                                        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                                        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                                        marshaller.marshal(new ObjectFactory().createGpx(gpx), outputStream);


                                    } catch (IOException ex) {
                                        Exceptions.printStackTrace(ex);
                                    } catch (JAXBException ex) {
                                        Exceptions.printStackTrace(ex);
                                    } finally {
                                        if (outputStream != null) {
                                            try {
                                                outputStream.close();
                                            } catch (IOException ex) {
                                                Exceptions.printStackTrace(ex);
                                            }
                                        }
                                        if (openAfterSave && storedFileObject != null) {
                                            try {
                                                DataObject storedDataObject = DataObject.find(storedFileObject);
                                                OpenCookie openCookie = storedDataObject.getLookup().lookup(OpenCookie.class);
                                                if (openCookie != null) {
                                                    openCookie.open();
                                                }
                                            } catch (DataObjectNotFoundException ex) {
                                                Exceptions.printStackTrace(ex);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
