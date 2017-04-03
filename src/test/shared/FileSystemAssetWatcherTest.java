package test.shared;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.shared.AssetLoader;
import seventh.shared.Cons;
import seventh.shared.FileSystemAssetWatcher;

public class FileSystemAssetWatcherTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	/**
	 * test the FileSystemAssetWatcher
	 * @throws IOException
	 */
	@Test
	public void testFileSystemAssetWatcher() throws IOException {
		File file = new File("/Users/hyeongjukim/Desktop/");
		FileSystemAssetWatcher FSA = new FileSystemAssetWatcher(file);
		FSA.startWatching();
		
		FSA.loadAsset("new.txt", new AssetLoader<File>(){
			@Override
            public File loadAsset(String filename) throws IOException {
                try {
                    Cons.println("Evaluating: " + filename);
                    
                    Cons.println("Successfully evaluated: " + filename);
                } 
                catch (Exception e) {
                    Cons.println("*** Error evaluating: " + filename);
                    Cons.println("*** " + e);
                }
                return null;
            }
		});
		
		FSA.stopWatching();
		FSA.clearWatched();
		FSA.removeWatchedAsset("new.txt");
		
		
	}
}
