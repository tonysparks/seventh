package testOracle;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.shared.Config;
import seventh.shared.MasterServerConfig;

public class MasterServerConfigTest {
	
	private MasterServerConfig msconfig;
	private Config config;

	@Before
	public void setUp() throws Exception {
		config = EasyMock.createMock(Config.class);
		msconfig = new MasterServerConfig(config);
	}

	@After
	public void tearDown() throws Exception {
		config = null;
		msconfig = null;
	}

	
	/*
	 * Purpose: Test URL 
	 * Input: "master_server", "url" 
	 *        
	 * Expected: "master_server + url"
	 */

	
	@Test
	public void getUrlTest() {
		EasyMock.expect(config.getString("mater_server", "url")).andReturn("master_server url");
		EasyMock.replay(config);
		String expected = "master_server url";
		String actual = msconfig.getUrl();
		assertEquals(expected, actual);
		
		EasyMock.verify(config);
	}


}
