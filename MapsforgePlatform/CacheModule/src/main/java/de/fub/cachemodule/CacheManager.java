/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.cachemodule;

import de.fub.cachemodule.cache.Cache;

/**
 *
 * @author Serdar
 */
public class CacheManager {
        
    private CacheManager() {
    }

    
    public synchronized Cache getCache(String id) {
        throw new UnsupportedOperationException();
    }
    
    public static CacheManager getInstance() {
        return CacheManagerHolder.INSTANCE;
    }

    private static class CacheManagerHolder {

        private static final CacheManager INSTANCE = new CacheManager();
    }
}
