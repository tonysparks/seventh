/*
 *	leola-live 
 *  see license.txt
 */
package seventh.math;

import leola.vm.Leola;
import leola.vm.lib.LeolaIgnore;
import leola.vm.lib.LeolaLibrary;
import leola.vm.types.LeoNamespace;

/**
 * @author Tony
 *
 */
public class MathLeolaLibrary implements LeolaLibrary {

	private Leola runtime;
	
	/* (non-Javadoc)
	 * @see leola.vm.lib.LeolaLibrary#init(leola.vm.Leola, leola.vm.types.LeoNamespace)
	 */
	@Override
	@LeolaIgnore
	public void init(Leola leola, LeoNamespace namespace) throws Exception {
		runtime = leola;
		
		LeoNamespace math = this.runtime.getOrCreateNamespace("math");
		math.store(this);

		namespace.put("math", math);
	}
	
	public Vector2f newVec2(Double x, Double y) {
		if(x!=null&&y!=null)
			return new Vector2f(x.floatValue(),y.floatValue());
		if(x!=null)
			return new Vector2f(x.floatValue(),0.0f);
		return new Vector2f();		
	}

	public Rectangle newRect(Integer x, Integer y, Integer w, Integer h) {
		if (x != null && y != null && w != null && h != null) {
			return new Rectangle(x, y, w, h);
		}
		
		if (x != null && y != null) {
			return new Rectangle(0, 0, x, y);
		}
		
		return new Rectangle();
	}
}
