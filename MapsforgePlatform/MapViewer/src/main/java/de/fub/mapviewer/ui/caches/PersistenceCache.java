/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.mapviewer.ui.caches;

import de.fub.mapviewer.ui.MapViewerTileFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Serdar
 */
public class PersistenceCache extends TileCache {

    private static final Logger LOG = Logger.getLogger(PersistenceCache.class.getName());

    private final String TEMP_FOLDER = "java.io.tmpdir";
    private final int WEEK_IN_MILLISECONDS = 7 * 24 * 60 * 60 * 1000;
    private final Map<URI, BufferedImage> TILES = Collections.synchronizedMap(new HashMap<URI, BufferedImage>());
    private final String TILE_NAME = "tile.png";
    private final MapViewerTileFactory TILEFACTORYINFO;
    private final Dispatcher DISPATCHER = new Dispatcher();
    private FileObject cacheFolder;

    public PersistenceCache(MapViewerTileFactory tileFactory) {
        assert tileFactory != null;
        this.TILEFACTORYINFO = tileFactory;
    }

    @Override
    public void needMoreMemory() {
        TILES.clear();
    }

    @Override
    public BufferedImage get(URI uri) throws IOException {
        BufferedImage img = null;
        if (TILES.containsKey(uri)) {
            img = TILES.get(uri);
        } else {
            img = getCached(uri);
        }
        return img;
    }

    @Override
    public void put(URI uri, byte[] bimg, BufferedImage img) {
        TILES.put(uri, img);
        BufferedImage cached = getCached(uri);
        if (cached == null) {
            DISPATCHER.persist(uri, img);
        }
    }

    private BufferedImage getCached(URI uri) {
        BufferedImage img = null;
        FileObject cache = getCacheFolder();
        if (cache != null && uri.getPath() != null) {
            try {
                String path = URLEncoder.encode(uri.getPath().replaceAll("/", "-"), "UTF-8");
                FileObject fileObject = cache.getFileObject(path);
                if (fileObject != null && fileObject.isFolder()) {
                    fileObject = fileObject.getFileObject(TILE_NAME);
                    if (fileObject != null) {
                        img = ImageIO.read(FileUtil.toFile(fileObject));
                    }
                }
            } catch (UnsupportedEncodingException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return img;
    }

    private FileObject getCacheFolder() {
        synchronized (TILE_NAME) {
            if (cacheFolder == null) {
                final String TEMP_DIR = System.getProperty(TEMP_FOLDER);
                if (TEMP_DIR != null) {
                    String normalizePath = FileUtil.normalizePath(TEMP_DIR);
                    if (normalizePath != null) {
                        FileObject fileObject = FileUtil.toFileObject(new File(normalizePath));
                        if (fileObject != null) {
                            cacheFolder = fileObject.getFileObject(TILEFACTORYINFO.getName());
                            if (cacheFolder == null) {
                                try {
                                    cacheFolder = fileObject.createFolder(TILEFACTORYINFO.getName());
                                } catch (IOException ex) {
                                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                                }
                            }
                        }
                    }
                }
            }
        }
        return cacheFolder;
    }

    private class Dispatcher {

        private final RequestProcessor REQUESTPROCESSOR = new RequestProcessor(Dispatcher.class.getName(), Runtime.getRuntime().availableProcessors() * 4);

        private void persist(URI uri, BufferedImage img) {
            REQUESTPROCESSOR.post(new TileJob(uri, img));
        }
    }

    private class TileJob implements Runnable {

        private final URI uri;
        private final BufferedImage img;

        private TileJob(URI uri, BufferedImage img) {
            this.uri = uri;
            this.img = img;
        }

        @Override
        public void run() {
            FileObject cache = getCacheFolder();
            if (cache != null && uri.getPath() != null) {
                try {
                    String path = URLEncoder.encode(uri.getPath().replaceAll("/", "-"), "UTF-8");
                    FileObject fileObject = cache.getFileObject(path);
                    if (fileObject == null) {
                        FileObject folder = cache.createFolder(path);
                        FileObject tile = folder.createData(TILE_NAME);
                        ImageIO.write(img, "png", FileUtil.toFile(tile));
                    } else {
                        if (fileObject.isFolder()) {
                            FileObject tile = fileObject.getFileObject(TILE_NAME);
                            if (tile == null) {
                                tile = fileObject.createData(TILE_NAME);
                                ImageIO.write(img, "png", FileUtil.toFile(tile));
                            } else {
                                long timeDiff = System.currentTimeMillis() - tile.lastModified().getTime();
                                if (timeDiff > WEEK_IN_MILLISECONDS) {
                                    ImageIO.write(img, "png", FileUtil.toFile(tile));
                                }
                            }
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }
}
