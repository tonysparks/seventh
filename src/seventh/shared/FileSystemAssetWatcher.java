/*
 * see license.txt 
 */
package seventh.shared;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Watches a directory for any changes
 * 
 * @author Tony
 *
 */
public class FileSystemAssetWatcher implements AssetWatcher {

    class WatchedAssetImpl<T> implements WatchedAsset<T> {

        private AtomicReference<T> asset;
        private AssetLoader<T> loader;  
        private String filename;
        
        /**
         * @param filename
         * @param loader
         */
        public WatchedAssetImpl(String filename, AssetLoader<T> loader) throws IOException {
            this.asset = new AtomicReference<T>();
            this.loader = loader;
            this.filename = filename;
            
            onAssetChanged();
        }

        
        @Override
        public T getAsset() {
            return this.asset.get();
        }

        @Override
        public void release() {
            this.asset.set(null);
            removeWatchedAsset(this.filename);
        }
        
        
        @Override
        public void onAssetChanged() throws IOException {
            this.asset.set(loader.loadAsset(filename));
        }
        
        
    }
    
    private WatchService watchService;
    private Thread watchThread;
    private AtomicBoolean isActive;
    private Path pathToWatch;
    
    private Map<File, WatchedAsset<?>> watchedAssets;
    
    /**
     * @param dir
     * @throws IOException
     */
    public FileSystemAssetWatcher(File dir) throws IOException {
        FileSystem fileSystem = FileSystems.getDefault();
        this.isActive = new AtomicBoolean(false);
        
        this.watchedAssets = new ConcurrentHashMap<File, WatchedAsset<?>>();
        this.pathToWatch = dir.toPath();
        
        this.watchService = fileSystem.newWatchService();
        this.watchThread = new Thread(new Runnable() {
            
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                while(isActive.get()) {
                    try {
                        WatchKey key = watchService.take();
                        if(key.isValid()) {
                            List<WatchEvent<?>> events = key.pollEvents();
                            for(int i = 0; i < events.size(); i++) {
                                WatchEvent<?> event = events.get(i);
                                WatchEvent.Kind<?> kind = event.kind();
                                
                                /* ignore overflow events */
                                if(kind == StandardWatchEventKinds.OVERFLOW) {
                                    continue;
                                }
                                
                                /* we are only listening for 'changed' events */
                                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                                Path filename = ev.context();
                                                                
                                /* if we have a registered asset, lets go ahead and notify it */
                                WatchedAsset<?> watchedAsset = watchedAssets.get(new File(pathToWatch.toFile(), filename.toString()));
                                if(watchedAsset != null) {
                                    try {
                                        watchedAsset.onAssetChanged();
                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        
                        key.reset();
                    }
                    catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }, "watcher-thread");
        this.watchThread.setDaemon(true);
        
        this.pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    
    @Override
    public <T> WatchedAsset<T> loadAsset(String filename, AssetLoader<T> loader) throws IOException {        
        WatchedAsset<T> asset = new WatchedAssetImpl<T>(filename, loader);
        this.watchedAssets.put(new File(filename), asset);
        
        return asset;
    }
    
    
    @Override
    public void removeWatchedAsset(String filename) {
        this.watchedAssets.remove(filename);
    }
    
    
    @Override
    public void clearWatched() {
        this.watchedAssets.clear();
    }
    
    
    @Override
    public void startWatching() {
        this.isActive.set(true);
        this.watchThread.start();
    }
    
    
    @Override
    public void stopWatching() { 
        this.isActive.set(false);
        this.watchThread.interrupt();
    }        
}
