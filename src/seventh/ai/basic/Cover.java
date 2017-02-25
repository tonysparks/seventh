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
        this.coverPos = coverPos;
        this.attackDir = attackDir;
    }
    
    /**
     * @param attackDir the attackDir to set
     */
    public void setAttackDir(Vector2f attackDir) {
        this.attackDir.set(attackDir);
    }
    
    /**
     * @param coverPos the coverPos to set
     */
    public void setCoverPos(Vector2f coverPos) {
        this.coverPos.set(coverPos);
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
