/*
 * see license.txt 
 */
package seventh.shared;

import java.io.IOException;

/**
 * @author Tony
 *
 */
public interface AssetWatcher {

    /**
     * Loads the Asset and marks it for being 'watched'
     * 
     * @param filename
     * @param loader
     * @return the {@link WatchedAsset}
     * @throws IOException
     */
    public <T> WatchedAsset<T> loadAsset(String filename, AssetLoader<T> loader) throws IOException;

    /**
     * Removes the asset from being watched
     * 
     * @param filename
     */
    public void removeWatchedAsset(String filename);

    /**
     * Clear all watched assets
     */
    public void clearWatched();

    /**
     * Starts watching
     */
    public void startWatching();

    /**
     * Stops watching
     */
    public void stopWatching();

}