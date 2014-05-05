/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.math.Vector2f;

/**
 * Cover from an attack direction
 * 
 * @author Tony
 *
 */
public class Cover {

	private Vector2f coverPos;
	private Vector2f attackDir;
	/**
	 * @param coverPos
	 * @param attackDir
	 */
	public Cover(Vector2f coverPos, Vector2f attackDir) {
		super();
		this.coverPos = coverPos;
		this.attackDir = attackDir;
	}
	
	
	/**
	 * @return the attackDir
	 */
	public Vector2f getAttackDir() {
		return attackDir;
	}
	
	/**
	 * @return the coverPos
	 */
	public Vector2f getCoverPos() {
		return coverPos;
	}
}
