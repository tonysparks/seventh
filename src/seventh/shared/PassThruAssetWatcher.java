/*
 * see license.txt 
 */
package seventh.shared;

import java.io.IOException;

/**
 * Simple pass-through implementation of {@link AssetWatcher}.  This takes 
 * very minimal resources as its just a small wrapper around the actual Asset.
 * 
 * @author Tony
 *
 */
public class PassThruAssetWatcher implements AssetWatcher {

    static class WatchedAssetImpl<T> implements WatchedAsset<T> {

        private final T asset;
                
        /**
         * @param asset
         */
        public WatchedAssetImpl(T asset) {
            this.asset = asset;
        }

        @Override
        public T getAsset() {
            return asset;
        }

        @Override
        public void release() {            
        }

        @Override
        public void onAssetChanged() throws IOException {            
        }
        
    }
    
    /**
     */
    public PassThruAssetWatcher() {
    }

    @Override
    public <T> WatchedAsset<T> loadAsset(String filename, AssetLoader<T> loader) throws IOException {
        return new WatchedAssetImpl<T>(loader.loadAsset(filename));
    }

    @Override
    public void removeWatchedAsset(String filename) {
    }

    @Override
    public void clearWatched() {
    }

    @Override
    public void startWatching() {
    }

    @Override
    public void stopWatching() {
    }

}
