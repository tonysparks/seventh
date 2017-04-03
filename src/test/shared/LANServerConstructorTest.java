package test.shared;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.shared.Config;
import seventh.shared.LANServerConfig;

public class LANServerConstructorTest {

	private Config config;
	private LANServerConfig lanconfig;
	
	@Test
	public void testLANServerConfig(){
		lanconfig = new LANServerConfig(config);
	}
	

}
