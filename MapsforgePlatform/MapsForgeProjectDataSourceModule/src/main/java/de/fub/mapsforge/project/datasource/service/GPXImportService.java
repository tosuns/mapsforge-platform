/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.datasource.service;

import java.awt.event.ActionListener;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Serdar
 */
public interface GPXImportService extends ActionListener {

    public void setDestinationFolder(FileObject destinationFoldert);

    public String getName();
}
