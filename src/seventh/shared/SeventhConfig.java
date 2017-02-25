/*
 * see license.txt 
 */
package seventh.shared;

import java.io.IOException;

import harenet.NetConfig;

/**
 * The Seventh configuration
 * 
 * @author Tony
 *
 */
public class SeventhConfig {

    protected Config config; 
    private MasterServerConfig masterServerConfig;
    
    /**
     * @param configurationPath the path to the configuration file
     * @param configurationRootNode the configuration root entry
     * @throws Exception
     */
    public SeventhConfig(String configurationPath, String configurationRootNode) throws Exception {
        this(new Config(configurationPath, configurationRootNode));
    }
    
    /**
     * @param config
     */
    public SeventhConfig(Config config) {
        this.config = config;
        this.masterServerConfig = new MasterServerConfig(config);
    }
    
    /**
     * @return the config
     */
    public Config getConfig() {
        return config;
    }
    
    /**
     * @return the masterServerConfig
     */
    public MasterServerConfig getMasterServerConfig() {
        return masterServerConfig;
    }
    
    /**
     * @return the {@link LANServerConfig}
     */
    public LANServerConfig getLANServerConfig() {
        return new LANServerConfig(this.config);
    }

    /**
     * @return the networking configuration
     */
    public NetConfig getNetConfig() {
        return this.config.getNetConfig();
    }
    
    /**
     * Saves off the configuration file
     * 
     * @throws IOException
     */
    public void save() throws IOException {
        this.config.save();
    }
    
    /**
     * Saves off the configuration file
     * 
     * @param filename
     * @throws IOException
     */
    public void save(String filename) throws IOException {
        this.config.save(filename);
    }
    

}
