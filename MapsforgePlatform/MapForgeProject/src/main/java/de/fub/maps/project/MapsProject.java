/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project;

import de.fub.maps.project.actions.CopyImpl;
import de.fub.maps.project.actions.DeleteImpl;
import de.fub.maps.project.actions.MapsProjectActionProvider;
import de.fub.maps.project.actions.MoveImpl;
import de.fub.maps.project.actions.RenameImpl;
import de.fub.maps.project.xml.Maps;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer2;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public class MapsProject extends FileChangeAdapter implements Project, ModelSynchronizer2<Maps> {

    private final FileObject projectDir;
    private final ProjectState state;
    private Lookup lkp;
    private Maps mapsProjectData = null;
    private Set<ChangeListener> cs = new HashSet<ChangeListener>();
    private final Object MUTEX = new Object();

    MapsProject(FileObject projectDirectory, ProjectState state) {
        this.projectDir = projectDirectory;
        this.state = state;
        projectDirectory.addFileChangeListener(MapsProject.this);
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    public Maps getProjectData() throws JAXBException, IOException {
        if (mapsProjectData == null) {
            FileObject fileObject = getProjectDirectory().getFileObject(MapsProjectFactory.MAPS_PROJECT_FILE);
            if (fileObject == null) {
                throw new FileNotFoundException(
                        MessageFormat.format("Couldn't find {0} of project folder {1} ",
                        MapsProjectFactory.MAPS_PROJECT_FILE,
                        getProjectDirectory().getName()));
            }
            InputStream inputStream = null;
            try {
                inputStream = fileObject.getInputStream();
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Maps.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                mapsProjectData = (Maps) unmarshaller.unmarshal(inputStream); //NOI18N
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return mapsProjectData;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(
                    MapsProject.this,
                    state,
                    new DeleteImpl(this),
                    new RenameImpl(this),
                    new MoveImpl(this),
                    new CopyImpl(this),
                    new MapsProjectActionProvider(this),
                    //                    CommonProjectActions.closeProjectAction(),
                    new MapsProjectInfo(MapsProject.this),
                    new MapsLogicalView(MapsProject.this));
        }
        return lkp;
    }

    @Override
    public void fileChanged(FileEvent fe) {
        try {
            if (fe.getFile().getNameExt().equals(getProjectFile().getNameExt())) {
                mapsProjectData = null;
                fireChangeEvent();
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @NbBundle.Messages({"CLT_Project_Xml_File_Not_Found=Couldn't find mapsforge.xml file in project folder!"})
    private FileObject getProjectFile() throws FileNotFoundException {
        FileObject fileObject = getProjectDirectory().getFileObject(MapsProjectFactory.MAPS_PROJECT_FILE);
        if (fileObject == null) {
//            NotifyDescriptor.Message nm = new NotifyDescriptor.Message(Bundle.CLT_Project_Xml_File_Not_Found(), NotifyDescriptor.Message.ERROR_MESSAGE);
            throw new FileNotFoundException(Bundle.CLT_Project_Xml_File_Not_Found());
        }
        return fileObject;
    }

    @Override
    public void modelChanged(Object o, Maps mapsForge) {
        OutputStream outputStream = null;
        try {
            FileObject projectFile = getProjectFile();
            outputStream = projectFile.getOutputStream();
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Maps.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(mapsForge, outputStream);
            mapsProjectData = mapsForge;
            fireChangeEvent(o);
        } catch (FileAlreadyLockedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (javax.xml.bind.JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private void fireChangeEvent(Object o) {
        synchronized (MUTEX) {
            for (ChangeListener listener : cs) {
                if (listener != o) {
                    listener.stateChanged(new ChangeEvent(MapsProject.this));
                }
            }
        }
    }

    private void fireChangeEvent() {
        fireChangeEvent(MapsProject.this);
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
        synchronized (MUTEX) {
            cs.add(cl);
        }
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        synchronized (MUTEX) {
            cs.remove(cl);
        }
    }
}
