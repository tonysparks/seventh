package Test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.shared.MasterServerClient;
import seventh.shared.MasterServerConfig;

public class MasterServerClientTest {
	
	private MasterServerClient msclient;
	private MasterServerConfig config;
	/*
	 * Purpose: Test MasterServerClient constructor
	 * Input: MasterServerConfig config
	 */
	
	@Test
	public void MasterServerClientTest(){
		msclient = new MasterServerClient(config);
	}

}
