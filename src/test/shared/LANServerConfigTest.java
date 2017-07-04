package test.shared;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.shared.Config;
import seventh.shared.LANServerConfig;

public class LANServerConfigTest {


    private LANServerConfig lanconfig;
    private Config config;
    
    @Before
    public void setUp() throws Exception{
        config = EasyMock.createMock(Config.class);
        lanconfig = new LANServerConfig(config);
    }
    
    @After
    public void tearDown() throws Exception{
        config = null;
        lanconfig = null;
    }
    
    /*
     * Purpose: check the broadcast address by checking getBroadcastAddress()
     * Input: defaultValue = "224.0.0.44"
     *           keys = "lan"
     *        key2 = "broadcast_address"
     * Expected: 
     *             "224.0.0.44"
     *             
     */
    
    @Test
    public void testBroadcastAddress() {
        config.getStr("224.0.0.44", "lan", "broadcast_address");
        EasyMock.expect(config.getStr("224.0.0.44", "lan", "broadcast_address")).andReturn("224.0.0.44");
        EasyMock.replay(config);

        LANServerConfig lanconfig = new LANServerConfig(config);
        String expected = "224.0.0.44"; 
        String actual = lanconfig.getBroadcastAddress();
        assertEquals(expected, actual);
        EasyMock.verify(config);
        
    }



}
