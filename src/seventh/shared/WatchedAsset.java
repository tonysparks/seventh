/*
 * see license.txt 
 */
package seventh.shared;

import java.io.IOException;


/**
 * The asset that is being watched
 * 
 * @author Tony
 *
 */
public interface WatchedAsset<T> {

    /**
     * Retrieve the Asset
     * 
     * @return the Asset in question
     */
    public T getAsset();
    
    
    /**
     * Release this asset
     */
    public void release();
   
    
    /**
     * The asset has changed on the file system
     * 
     * @throws IOException
     */
    public void onAssetChanged() throws IOException;
}
