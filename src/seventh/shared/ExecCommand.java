/*
 * see license.txt 
 */
package seventh.shared;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Executes a batch script
 * 
 * @author Tony
 *
 */
public class ExecCommand extends Command {

    /**
     * @param name
     */
    public ExecCommand() {
        super("exec");
    }

    /* (non-Javadoc)
     * @see palisma.shared.Command#execute(palisma.shared.Console, java.lang.String[])
     */
    @Override
    public void execute(Console console, String... args) {
        String filePath = mergeArgsDelim(" ", args);
        File file = new File(filePath);
        if(!file.exists()) {
            console.println("The file '" + filePath + "' does not exist.");
        }
        else {
            RandomAccessFile raf = null; 
            try {
                raf = new RandomAccessFile(file, "r");
                String line = null;
                while( (line=raf.readLine()) != null) {
                    if(!line.trim().startsWith("#")) {
                        console.execute(line);
                    }
                }
            } catch (IOException e) {
                console.println("*** Error parsing the file: " + e);
            }
            finally {
                if(raf!=null) {
                    try {
                        raf.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
    }

}
