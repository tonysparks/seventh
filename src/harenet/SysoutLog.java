/*
 * see license.txt 
 */
package harenet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple <code>System.out</code> implementation of the {@link Log} interface.
 * 
 * @author Tony
 *
 */
public class SysoutLog implements Log {

    private boolean enabled;
    private static final DateFormat format = new SimpleDateFormat("HH:mm:ss SSSS");
    /**
     * 
     */
    public SysoutLog() {
        this.enabled = false;
    }

    /* (non-Javadoc)
     * @see netspark.Log#enabled()
     */
    @Override
    public boolean enabled() {
        return this.enabled;
    }

    /* (non-Javadoc)
     * @see netspark.Log#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    /* (non-Javadoc)
     * @see netspark.Log#debug(java.lang.String)
     */
    @Override
    public void debug(String msg) {
        if(this.enabled||true) {
            System.out.println(format.format(new Date()) + ":" + msg);
        }

    }

    /* (non-Javadoc)
     * @see netspark.Log#error(java.lang.String)
     */
    @Override
    public void error(String msg) {
        if(this.enabled) {
            System.out.println(format.format(new Date()) + ": *** " + msg);
        }
    }

    /* (non-Javadoc)
     * @see netspark.Log#info(java.lang.String)
     */
    @Override
    public void info(String msg) {
        if(this.enabled) {
            System.out.println(format.format(new Date()) + ":" +msg);
        }
    }

}
