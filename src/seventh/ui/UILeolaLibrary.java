/*
 * see license.txt 
 */
package seventh.ui;

import leola.vm.Leola;
import leola.vm.exceptions.LeolaRuntimeException;
import leola.vm.lib.LeolaLibrary;
import leola.vm.types.LeoNamespace;

/**
 * @author Tony
 *
 */
public class UILeolaLibrary implements LeolaLibrary {

    class LeolaWidget {
        Widget widget;
    }
    
    class LeolaButton extends LeolaWidget {
        Button btn;
        
    }
    
    /**
     * 
     */
    public UILeolaLibrary() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init(Leola leola, LeoNamespace namespace) throws LeolaRuntimeException {
        leola.putIntoNamespace(this, namespace);
    }

}
