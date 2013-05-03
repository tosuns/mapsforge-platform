/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapviewer.ui.caches;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

/**
 *
 * @author Serdar
 */
public class PersistentTileCache implements TileCache {

    private static final Logger LOG = Logger.getLogger(PersistentTileCache.class.getName());
    private File cacheFolder;
    private final static String TEMP_FOLDER = "java.io.tmpdir";
    private final static String CACHE_FOLDER_NAME = "OsmTileCache";
    private static final TileCache memTileCache = new MemoryTileCache();
    private final static String TILE_NAME = "tile.png";
    private final static String PATH_PATTERN = "{0, number, integer}_{1, number, integer}_{2, number, integer}";
    private Set<String> tiles = Collections.synchronizedSet(new HashSet<String>());
    private RequestProcessor requestProcessor = new RequestProcessor(getClass().getName(), Runtime.getRuntime().availableProcessors());
    private static File tmpDir = null;

    private synchronized static File getCacheDir() {
        if (tmpDir == null) {
            String property = System.getProperty(TEMP_FOLDER);
            tmpDir = property != null ? new File(property) : new File(".");
        }
        return tmpDir;
    }

    public PersistentTileCache() {
        super();
        if (getCacheDir() != null && getCacheDir().isDirectory()) {
            for (File file : getCacheDir().listFiles()) {
                if (CACHE_FOLDER_NAME.equals(file.getName())) {
                    cacheFolder = file;
                    break;
                }
            }
            if (cacheFolder == null) {
                cacheFolder = new File(getCacheDir(), CACHE_FOLDER_NAME);
                if (!cacheFolder.exists()) {
                    cacheFolder.mkdir();
                }
            }
        } else {
            throw new IllegalStateException();
        }
        assert cacheFolder != null;
        for (File children : cacheFolder.listFiles()) {
            tiles.add(children.getName());
        }
    }

    private File getFileOfParent(File parent, String fileName) {
        File result = null;
        if (fileName != null && parent != null) {
            for (File child : parent.listFiles()) {
                if (fileName.equals(child.getName())) {
                    result = child;
                    break;
                }
            }
        }
        return result;
    }

    private File getFileFromCacheDir(String fileName) {
        return getFileOfParent(cacheFolder, fileName);
    }

    private File getTile(File folder) {
        File tileFile = null;
        for (File child : folder.listFiles()) {
            if (TILE_NAME.equals(child.getName())) {
                tileFile = child;
                break;
            }
        }
        return tileFile;
    }

    @Override
    public Tile getTile(TileSource ts, final int x, final int y, final int z) {
        Tile tile = memTileCache.getTile(ts, x, y, z);
        if (tile == null) {
            InputStream inputStream = null;
            try {
                String fileName = MessageFormat.format("{0}", MessageFormat.format(PATH_PATTERN, x, y, z));
                if (tiles.contains(fileName)) {
                    File tileFolder = getFileFromCacheDir(fileName);
                    if (tileFolder != null) {
                        File tileObject = getTile(tileFolder);
                        if (tileObject != null) {
                            long lastChacked = System.currentTimeMillis() - tileObject.lastModified();
                            if (lastChacked <= (7 * 24 * 60 * 60 * 1000)) {
                                inputStream = new FileInputStream(tileObject);
                                BufferedImage read = ImageIO.read(inputStream);
                                tile = new Tile(ts, x, y, z, read);
                                tile.setLoaded(true);
                                memTileCache.addTile(tile);
                            } else {
                                requestProcessor.post(new PersistJob(x, y, z, tile));
                            }
                        }
                    }
                } else {
                    LOG.log(Level.FINEST, "cache miss for tile: {0}", fileName);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        } else if (tile.isLoaded() && !tile.hasError() && !tiles.contains(MessageFormat.format(PATH_PATTERN, x, y, z))) {
            requestProcessor.post(new PersistJob(x, y, z, tile));
        }
        return tile;
    }

    private class PersistJob implements Runnable {

        private final int x;
        private final int y;
        private final int z;
        private final Tile tile;

        public PersistJob(final int x, final int y, final int z, Tile tile) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.tile = tile;
        }

        @Override
        public void run() {
            OutputStream outputStream = null;
            try {
                File tileFileObject = createTileFile(x, y, z);
                if (tileFileObject != null) {
                    outputStream = new FileOutputStream(tileFileObject);
                    ImageIO.write(tile.getImage(), "png", outputStream);
                    tiles.add(MessageFormat.format(PATH_PATTERN, x, y, z));
                    LOG.log(Level.FINEST, "tile persist job finished for file {0}", tileFileObject.getAbsolutePath());
                } else {
                    throw new IOException();
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
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

        private synchronized File createTileFile(int x, int y, int z) throws IOException {
            File folder = getFolder(x, y, z);
            File tileFileObject = getFileOfParent(folder, TILE_NAME);
            if (tileFileObject == null) {
                tileFileObject = new File(folder, TILE_NAME);
                try {
                    if (!tileFileObject.exists()) {
                        if (tileFileObject.createNewFile()) {
                            //  we have to wait a little bit
                            // to let the filesystem release the lock to the
                            // file
                            wait(100);
                        }
                    }
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            return tileFileObject;
        }

        private synchronized File getFolder(int x, int y, int z) throws IOException {
            File fileObject = getFileFromCacheDir(MessageFormat.format(PATH_PATTERN, x, y, z));
            if (fileObject == null || !fileObject.isDirectory()) {
                fileObject = new File(cacheFolder, MessageFormat.format(PATH_PATTERN, x, y, z));
                if (!fileObject.exists()) {
                    fileObject.mkdir();
                }
            }
            return fileObject;
        }
    }

    @Override
    public void addTile(final Tile tile) {
        if (tile.isLoaded()
                && !tile.hasError()
                && !tiles.contains(MessageFormat.format(PATH_PATTERN, tile.getXtile(), tile.getYtile(), tile.getZoom()))) {
            requestProcessor.post(new PersistJob(tile.getXtile(), tile.getYtile(), tile.getZoom(), tile));
        }
        memTileCache.addTile(tile);
    }

    @Override
    public int getTileCount() {
        return memTileCache.getTileCount();
    }
}
