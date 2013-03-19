/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject;

import de.fub.mapforgeproject.actions.CopyImpl;
import de.fub.mapforgeproject.actions.DeleteImpl;
import de.fub.mapforgeproject.actions.MapsForgeProjectActionProvider;
import de.fub.mapforgeproject.actions.MoveImpl;
import de.fub.mapforgeproject.actions.RenameImpl;
import de.fub.mapforgeproject.xml.MapsForge;
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
public class MapsForgeProject extends FileChangeAdapter implements Project, ModelSynchronizer2<MapsForge> {

    private final FileObject projectDir;
    private final ProjectState state;
    private Lookup lkp;
    private MapsForge mapsForgeProjectData = null;
    private Set<ChangeListener> cs = new HashSet<ChangeListener>();
    private final Object MUTEX = new Object();

    MapsForgeProject(FileObject projectDirectory, ProjectState state) {
        this.projectDir = projectDirectory;
        this.state = state;
        projectDirectory.addFileChangeListener(MapsForgeProject.this);
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    public MapsForge getProjectData() throws JAXBException, IOException {
        if (mapsForgeProjectData == null) {
            FileObject fileObject = getProjectDirectory().getFileObject(MapsForgeProjectFactory.MAPS_FORGE_PROJECT_FILE);
            if (fileObject == null) {
                throw new FileNotFoundException(
                        MessageFormat.format("Couldn't find {0} of project folder {1} ",
                        MapsForgeProjectFactory.MAPS_FORGE_PROJECT_FILE,
                        getProjectDirectory().getName()));
            }
            InputStream inputStream = null;
            try {
                inputStream = fileObject.getInputStream();
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(MapsForge.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                mapsForgeProjectData = (MapsForge) unmarshaller.unmarshal(inputStream); //NOI18N
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return mapsForgeProjectData;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(
                    MapsForgeProject.this,
                    state,
                    new DeleteImpl(this),
                    new RenameImpl(this),
                    new MoveImpl(this),
                    new CopyImpl(this),
                    new MapsForgeProjectActionProvider(this),
                    //                    CommonProjectActions.closeProjectAction(),
                    new MapsForgeProjectInfo(MapsForgeProject.this),
                    new MapsForgeLogicalView(MapsForgeProject.this));
        }
        return lkp;
    }

    @Override
    public void fileChanged(FileEvent fe) {
        try {
            if (fe.getFile().getNameExt().equals(getProjectFile().getNameExt())) {
                mapsForgeProjectData = null;
                fireChangeEvent();
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @NbBundle.Messages({"CLT_Project_Xml_File_Not_Found=Couldn't find mapsforge.xml file in project folder!"})
    private FileObject getProjectFile() throws FileNotFoundException {
        FileObject fileObject = getProjectDirectory().getFileObject(MapsForgeProjectFactory.MAPS_FORGE_PROJECT_FILE);
        if (fileObject == null) {
//            NotifyDescriptor.Message nm = new NotifyDescriptor.Message(Bundle.CLT_Project_Xml_File_Not_Found(), NotifyDescriptor.Message.ERROR_MESSAGE);
            throw new FileNotFoundException(Bundle.CLT_Project_Xml_File_Not_Found());
        }
        return fileObject;
    }

    @Override
    public void modelChanged(Object o, MapsForge mapsForge) {
        OutputStream outputStream = null;
        try {
            FileObject projectFile = getProjectFile();
            outputStream = projectFile.getOutputStream();
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(MapsForge.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(mapsForge, outputStream);
            mapsForgeProjectData = mapsForge;
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
                    listener.stateChanged(new ChangeEvent(MapsForgeProject.this));
                }
            }
        }
    }

    private void fireChangeEvent() {
        fireChangeEvent(MapsForgeProject.this);
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
