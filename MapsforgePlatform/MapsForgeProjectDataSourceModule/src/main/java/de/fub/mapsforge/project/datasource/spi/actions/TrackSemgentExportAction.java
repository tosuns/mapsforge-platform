/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.datasource.spi.actions;

import de.fub.gpxmodule.xml.Gpx;
import de.fub.gpxmodule.xml.ObjectFactory;
import de.fub.gpxmodule.xml.Trk;
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
import javax.xml.bind.Marshaller;
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
                exportDialog.getSaveButton(),
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

    /**
     * this method check whether the gpx extension if present and if not it
     * appends the extension.
     *
     * @param fileName
     * @return a String with '.gpx' extension
     */
    private String preprocessFileName(String fileName) {
        if (!fileName.endsWith(".gpx")) {
            fileName = MessageFormat.format("{0}.gpx", fileName);
        }
        return fileName;
    }

    private Gpx getExportGpx() {
        Children children = explorerManger.getRootContext().getChildren();

        final Gpx gpx = new Gpx();
        for (Node node : children.getNodes()) {
            if (node instanceof TrackSegmentBehaviour) {
                TrksegWrapper trkseg = node.getLookup().lookup(TrksegWrapper.class);
                TrackSegmentBehaviour trackNode = (TrackSegmentBehaviour) node;
                if (trackNode.isVisible() && trkseg != null) {

                    Trk trk = new Trk();
                    trk.setName(trkseg.getTrackName());
                    trk.setDesc(trkseg.getTrackDescription());
                    trk.getTrkseg().add(trkseg.getTrkseg());
                    gpx.getTrk().add(trk);
                }
            }
        }



        return gpx;
    }

    private void handler(ActionEvent event) {
        if (exportDialog != null && explorerManger != null) {
            if (event.getSource() == exportDialog.getOptions()[0]) {

                final boolean openAfterSave = exportDialog.getOpenAfterSave().isSelected();
                final Object selectedItem = exportDialog.getFolderName().getSelectedItem();
                String fileName = exportDialog.getFileName().getText();

                if (selectedItem instanceof String && fileName != null) {
                    final String folderName = (String) selectedItem;
                    fileName = preprocessFileName(fileName);

                    final Gpx gpx = getExportGpx();

                    GPXDatasourceNode datasourceNode = MapsForgeDatasourceNodeFactory.gpxDatasourceNode;

                    if (datasourceNode != null) {
                        final DataObject dataObject = datasourceNode.getLookup().lookup(DataObject.class);

                        if (dataObject != null) {
                            final String fileN = fileName;
                            FileUtil.runAtomicAction(new SaveImplemention(dataObject, folderName, fileN, gpx, openAfterSave));
                        }
                    }
                }
            }
        }
    }

    private static class SaveImplemention implements Runnable {

        private final DataObject dataObject;
        private final String folderName;
        private final String fileName;
        private final Gpx gpx;
        private final boolean openAfterSave;

        public SaveImplemention(DataObject dataObject, String folderName, String fileName, Gpx gpx, boolean openAfterSave) {
            this.dataObject = dataObject;
            this.folderName = folderName;
            this.fileName = fileName;
            this.gpx = gpx;
            this.openAfterSave = openAfterSave;
        }

        @Override
        public void run() {
            OutputStream outputStream = null;
            FileObject storedFileObject = null;

            try {

                storedFileObject = getOrCreateFileObject();
                outputStream = storedFileObject.getOutputStream();

                // write into the outputstream the gpx data
                Marshaller marshaller = createMarshaller();
                marshaller.marshal(new ObjectFactory().createGpx(gpx), outputStream);

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (JAXBException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                // close stream
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                checkAndOpen(storedFileObject);
            }
        }

        private FileObject getOrCreateFileObject() throws IOException {
            FileObject storedFileObject = null;
            // get parent folder
            FileObject datasourceFileObject = dataObject.getPrimaryFile();

            // get sub folder in which the gpx data will be stored
            FileObject folderObject = datasourceFileObject.getFileObject(folderName);

            // if the folder does not exist create the folder
            if (folderObject == null) {
                folderObject = datasourceFileObject.createFolder(folderName);
            }

            FileObject fileObject = folderObject.getFileObject(fileName);
            String alternativeFileName = fileName;

            // in case the previouse file name check-methods failed
            // we create a free file name if the given fileName already
            // exists in the current subfolder
            if (fileObject != null) {
                alternativeFileName = FileUtil.findFreeFileName(folderObject, fileName, "gpx");
            }

            storedFileObject = folderObject.createData(alternativeFileName);
            return storedFileObject;
        }

        private Marshaller createMarshaller() throws JAXBException {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            return marshaller;
        }

        private void checkAndOpen(FileObject fileObject) {
            // if the user selected to open the previously saved file
            // the respective editor will be opened.
            if (openAfterSave && fileObject != null) {
                try {
                    DataObject storedDataObject = DataObject.find(fileObject);
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
}
