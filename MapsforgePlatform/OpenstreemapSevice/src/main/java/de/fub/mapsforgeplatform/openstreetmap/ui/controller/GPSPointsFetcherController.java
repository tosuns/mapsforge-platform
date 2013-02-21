/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.ui.controller;

import de.fub.gpxmodule.xml.*;
import de.fub.mapsforgeplatform.openstreetmap.service.OpenstreetMapService;
import de.fub.mapsforgeplatform.openstreetmap.ui.GPSPointsFetcherTopComponent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.xml.bind.JAXBException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Serdar
 */
public class GPSPointsFetcherController {

    public static final String CACHE_FOLDER = "GPS.fetcher.cache.folder";
    public static final String CACHE_FOLDER_NAME = "gpxcache";
    private static final Logger LOG = Logger.getLogger(GPSPointsFetcherController.class.getName());
    private final GPXFactory factory = new GPXFactory();
    private final GPSPointsFetcherTopComponent view;
    private final Object MUTEX = new Object();
    private final Object PROGRSS_MUTEX = new Object();
    private boolean busy = false;
    private ProgressHandle handler;
    private int size = 0;
    private int counter = 0;
    private RequestProcessor requestProcessor = new RequestProcessor(getClass().getName(), Runtime.getRuntime().availableProcessors() * 4);
    private final Preferences preferences;
    private FileObject cacheDirectory;

    public GPSPointsFetcherController(GPSPointsFetcherTopComponent view) {
        this.view = view;
        this.view.getExplorerManager().setRootContext(new AbstractNode(Children.create(factory, true)));
        preferences = NbPreferences.forModule(getClass());
        String cacheFolder = preferences.get(CACHE_FOLDER, null);

        if (cacheFolder == null) {
            cacheFolder = MessageFormat.format("{0}{1}", System.getProperty("java.io.tmpdir"), CACHE_FOLDER_NAME);
        }
        if (cacheFolder != null) {
            File file = new File(cacheFolder);
            if (!file.exists()) {
                file.mkdirs();
            }
            cacheDirectory = FileUtil.toFileObject(file);
        }
    }

    @NbBundle.Messages({"Fetching_GPX_Data=Downloading GPX-Data-Batch {0} of {1}..."})
    public void handleFetch() throws JAXBException {
        synchronized (MUTEX) {
            if (!busy) {
                busy = true;
                view.lockInputFields(busy);
                RequestProcessor.getDefault().post(new Runnable() {
                    @Override
                    public void run() {
                        factory.clear();

                        int start = (Integer) view.getStartPage().getValue();
                        int end = (Integer) view.getEndPage().getValue();
                        handler = ProgressHandleFactory.createHandle(Bundle.Fetching_GPX_Data(start, end));
                        size = end - start + 1;
                        counter = 0;

                        try {
                            handler.start();
                            handler.switchToDeterminate(size);
                            for (; start <= end; start++) {
                                new FetchJob(
                                        view.getLeftLongitude().getText(),
                                        view.getBottomLatitude().getText(),
                                        view.getRightLongitude().getText(),
                                        view.getTopLatitude().getText(), String.valueOf(start)).run();
//                                requestProcessor.post(
//                                        );

                            }
//                            synchronized (PROGRSS_MUTEX) {
//                                while (counter < size) {
//                                    try {
//                                        LOG.info("start waiting");
//                                        PROGRSS_MUTEX.wait();
//                                    } catch (InterruptedException ex) {
//                                        LOG.log(Level.INFO, "awake from wait");
//                                    } finally {
//                                        LOG.info("awake from waiting");
//                                    }
//
//                                }
//                            }
                        } finally {
                            handler.finish();
                            busy = false;
                            view.lockInputFields(busy);
                        }
                    }
                });
            }
        }
    }

    private void updateList(Node gpxNode) {
        synchronized (PROGRSS_MUTEX) {
            if (gpxNode != null) {
                factory.add(gpxNode);
            }
            counter++;
            handler.progress(counter);
            handler.setDisplayName(Bundle.Fetching_GPX_Data(counter, size));
            PROGRSS_MUTEX.notifyAll();
        }
    }

    private class FetchJob implements Runnable {

        private final String leftLongitude;
        private final String bottomLatitude;
        private final String rightLongitude;
        private final String topLatitude;
        private final String page;

        public FetchJob(
                String leftLongitude,
                String bottomLatitude,
                String rightLongitude,
                String topLatitude,
                String page) {
            this.leftLongitude = leftLongitude;
            this.bottomLatitude = bottomLatitude;
            this.rightLongitude = rightLongitude;
            this.topLatitude = topLatitude;
            this.page = page;

        }

        @Override
        public void run() {
            OpenstreetMapService openstreetMapService = null;
            OutputStream outputStream = null;
            try {
                openstreetMapService = new OpenstreetMapService();
                Gpx gpx = openstreetMapService.gpsPoints(Gpx.class, leftLongitude, bottomLatitude, rightLongitude, topLatitude, page);
                if (gpx != null && gpx.getTrk() != null && !gpx.getTrk().isEmpty()) {
                    String bboxFolderName = MessageFormat.format("{0}_{1}_{2}_{3}", leftLongitude, bottomLatitude, rightLongitude, topLatitude);

                    if (cacheDirectory.getFileObject(bboxFolderName) == null) {
                        cacheDirectory.createFolder(bboxFolderName);
                    }
                    FileObject fileObject = cacheDirectory.createData(MessageFormat.format("{0}_{1}_{2}.gpx", "Gpx", String.valueOf(System.currentTimeMillis()), String.valueOf(page)));
                    outputStream = fileObject.getOutputStream();
                    javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
                    javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                    marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                    marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    marshaller.marshal(gpx, outputStream);
                    outputStream.close();
                    DataObject dataObject = DataObject.find(fileObject);
                    updateList(dataObject.getNodeDelegate());
                }
            } catch (IOException ex) {
                LOG.log(Level.INFO, ex.getMessage(), ex);
                updateList(null);
            } catch (JAXBException ex) {
                LOG.log(Level.INFO, ex.getMessage(), ex);
                updateList(null);
            } finally {
                if (openstreetMapService != null) {
                    openstreetMapService.close();
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }
}
