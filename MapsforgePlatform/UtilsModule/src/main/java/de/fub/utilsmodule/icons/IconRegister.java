/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.icons;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class IconRegister {

    private static final HashMap<String, Image> iconMap = new HashMap<String, Image>();
    private static final HashMap<String, Image> platformIconMap = new HashMap<String, Image>();
    private static Image SYSTEM_DRIVE_ICON;
    private static Image SYSTEM_FOLDER_ICON = null;
    private static final Object SYSTEM_ICON_MUTEX = new Object();
    private static final Object PLATFORM_ICON_MUTEX = new Object();
    public static final String ICON_REGISTER_PATH = "IconRegister";

    public static List<Image> getRegisteredIcons() {
        synchronized (PLATFORM_ICON_MUTEX) {
            if (platformIconMap.isEmpty()) {
                FileObject iconFolder = FileUtil.getConfigFile(ICON_REGISTER_PATH);
                for (FileObject icon : iconFolder.getChildren()) {
                    if (icon.isData()) {
                        Object urlPath = icon.getAttribute("url");
                        if (urlPath instanceof String) {
                            try {
                                URL url = new URL((String) urlPath);
                                platformIconMap.put(icon.getNameExt(), ImageIO.read(url));
                            } catch (MalformedURLException ex) {
                                Exceptions.printStackTrace(ex);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                }
            }
            return new ArrayList<Image>(platformIconMap.values());
        }
    }

    public static Image findRegisteredIcon(String nameWithExtension) {
        if (platformIconMap.isEmpty()) {
            FileObject iconFolder = FileUtil.getConfigFile(ICON_REGISTER_PATH);
            for (FileObject icon : iconFolder.getChildren()) {
                if (icon.isData()) {
                    Object urlPath = icon.getAttribute("iconBase");
                    if (urlPath instanceof String) {
                        try {
                            URL url = new URL((String) urlPath);
                            BufferedImage image = ImageIO.read(url);
                            platformIconMap.put(icon.getNameExt(), image);
                        } catch (MalformedURLException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        }
        return platformIconMap.get(nameWithExtension);
    }

    public Image findSystemIcon(File file) {
        synchronized (SYSTEM_ICON_MUTEX) {
            Image image = null;
            if (file != null) {
                String systemDisplayName = FileSystemView.getFileSystemView().getSystemDisplayName(file);
                if (!iconMap.containsKey(systemDisplayName)) {
                    iconMap.put(systemDisplayName, ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file)).getImage());
                }
                image = iconMap.get(FileSystemView.getFileSystemView().getSystemDisplayName(file));
            }
            return image;
        }
    }

    public Image findSystemIcon(FileObject fileObject) {
        return findSystemIcon(FileUtil.toFile(fileObject));
    }

    public synchronized Image getDriveIcon() {
        if (SYSTEM_DRIVE_ICON == null) {
            FileSystemView fileSystemView = FileSystemView.getFileSystemView();
            Icon systemIcon = fileSystemView.getSystemIcon(FileSystemView.getFileSystemView().getRoots()[0]);
            if (systemIcon instanceof ImageIcon) {
                SYSTEM_DRIVE_ICON = ((ImageIcon) systemIcon).getImage();
            }
        }
        return SYSTEM_DRIVE_ICON;
    }

    public synchronized Image getFolderIcon() {
        if (SYSTEM_FOLDER_ICON == null) {
            if (System.getProperty("user.dir") != null) {
                File file = new File(System.getProperty("user.dir"), "dummyFolder");
                if (file.mkdirs()) {
                    FileSystemView fileSystemView = FileSystemView.getFileSystemView();
                    Icon systemIcon = fileSystemView.getSystemIcon(file);
                    if (systemIcon instanceof ImageIcon) {
                        SYSTEM_FOLDER_ICON = ((ImageIcon) systemIcon).getImage();
                    }
                }
            }
        }
        return SYSTEM_FOLDER_ICON;
    }
}