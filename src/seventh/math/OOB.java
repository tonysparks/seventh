/*
 * see license.txt 
 */
package seventh.math;

/**
 * Oriented Bounding Box.  Used for rotating objects.  In general, the collision checks are rather expensive (relative to
 * normal {@link Rectangle} collision checks).
 * 
 * @author Tony
 *
 */
public class OOB {

	public float width;
	public float height;
	public float orientation;
	
	public Vector2f center;
	
	public Vector2f topLeft, topRight, bottomLeft, bottomRight;
	
	/**
	 * Defaults to nothing
	 */
    public OOB() {
        this(0, new Vector2f(), 0, 0);
    }
	
    /**
     * Based off of the supplied {@link Rectangle}
     * 
     * @param r
     */
	public OOB(Rectangle r) {
	    this(0, new Vector2f(r.x + r.width/2, r.y + r.height/2), r.width, r.height);
	}
	
	/**
	 * Based off of the supplied {@link Rectangle} and orientation
	 * 
	 * @param orientation
	 * @param r
	 */
	public OOB(float orientation, Rectangle r) {
        this(orientation, new Vector2f(r.x + r.width/2, r.y + r.height/2), r.width, r.height);
    }
	
	/**
	 * Based off of the supplied {@link OOB}
	 * 
	 * @param oob
	 */
	public OOB(OOB oob) {
	    this(oob.orientation, new Vector2f(oob.center), oob.width, oob.height);
	}
	
	/**
	 * @param orientation
	 * @param cx
	 * @param cy
	 * @param width
	 * @param height
	 */
	public OOB(float orientation, float cx, float cy, float width, float height) {
	    this(orientation, new Vector2f(cx, cy), width, height);
	}
	
	/**
	 * @param orientation
	 * @param center
	 * @param width
	 * @param height
	 */
	public OOB(float orientation, Vector2f center, float width, float height) {
		this.orientation = orientation;
		this.center = center;
		this.width = width;
		this.height = height;
		
		this.topLeft = new Vector2f();
		this.topRight = new Vector2f();
		this.bottomLeft = new Vector2f();
		this.bottomRight = new Vector2f();
		
		setLocation(center);
	}
	
	/**
	 * Updates the {@link OOB}
	 * 
	 * @param newOrientation
	 * @param center
	 */
	public void update(float newOrientation, Vector2f center) {
		update(newOrientation, center.x, center.y);
	}
	
	/**
	 * Updates the OOB internal state
	 * 
	 * @param newOrientation
	 * @param px
	 * @param py
	 */
	public void update(float newOrientation, float px, float py) {
	    
	    // first translate to center coordinate space
        this.center.set(0,0);
        this.topLeft.set(center.x-width/2f, center.y+height/2f);
        this.topRight.set(center.x+width/2f, center.y+height/2f);
        this.bottomLeft.set(center.x-width/2f, center.y-height/2f);
        this.bottomRight.set(center.x+width/2f, center.y-height/2f);

        // rotate the rectangle
        this.orientation = newOrientation;
        
        Vector2f.Vector2fRotate(topLeft, orientation, topLeft);
        Vector2f.Vector2fRotate(topRight, orientation, topRight);
        Vector2f.Vector2fRotate(bottomLeft, orientation, bottomLeft);
        Vector2f.Vector2fRotate(bottomRight, orientation, bottomRight);
        
        // translate the rotated rectangle back to the supplied 
        // center position
        this.center.set(px, py);
        Vector2f.Vector2fAdd(topLeft, center, topLeft);
        Vector2f.Vector2fAdd(topRight, center, topRight);
        Vector2f.Vector2fAdd(bottomLeft, center, bottomLeft);
        Vector2f.Vector2fAdd(bottomRight, center, bottomRight);	    
	}

	/**
	 * set the rotation to the supplied orientation
	 * 
	 * @param newOrientation
	 */
	public void rotateTo(float newOrientation) {
	    update(orientation, center.x, center.y);
	}
	
	/**
	 * Rotate relative to the current orientation
	 * 
	 * @param adjustBy
	 */
	public void rotate(float adjustBy) {
	    rotateTo(orientation + adjustBy);
	}
	
	/**
	 * Move relative to the current center position
	 * 
	 * @param p
	 */
	public void translate(Vector2f p) {
	    translate(p.x, p.y);
	}
	
	
	/**
	 * Move relative to the current center position
	 * 
	 * @param px
	 * @param py
	 */
	public void translate(float px, float py) {
	    setLocation(center.x+px, center.y+py);
	}

	/**
	 * Move the center position to the supplied position
	 * 
	 * @param px
	 * @param py
	 */
	public void setLocation(float px, float py) {
	    update(orientation, px, py);
    }
	
	
	/**
	 * Move the center position to the supplied position
	 * 
	 * @param pos
	 */
	public void setLocation(Vector2f pos) {
	    setLocation(pos.x, pos.y);
	}
	
	/**
	 * Re-adjust the width/height properties
	 * 
	 * @param width
	 * @param height
	 */
	public void setBounds(float width, float height) {
	    this.width = width;
	    this.height = height;
	    setLocation(center);
	}
	
	/**
     * @return the center
     */
    public Vector2f getCenter() {
        return center;
    }
    
    /**
     * @return the orientation
     */
    public float getOrientation() {
        return orientation;
    }
    
    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }
    
    /**
     * @return the height
     */
    public float getHeight() {
        return height;
    }
	
	/**
	 * Determines if the supplied point is inside the {@link OOB}
	 * 
	 * @param p
	 * @return true if the point is inside the OOB
	 */
	public boolean contains(Vector2f p) {
	    return contains(p.x, p.y);
	}
	
	/**
     * Determines if the supplied point is inside the {@link OOB}
     * 
     * @param px
     * @param py
     * @return true if the point is inside the OOB
     */
    public boolean contains(float px, float py) {
        
        // just do simple contains if there is no rotation
        if(orientation == 0) {
            return Math.abs(center.x-px) < width/2 && Math.abs(center.y-py) < height/2;
        }

        double tx = Math.cos(orientation)*px - Math.sin(orientation)*py;
        double ty = Math.cos(orientation)*py + Math.sin(orientation)*px;

        double cx = Math.cos(orientation)*center.x - Math.sin(orientation)*center.y;
        double cy = Math.cos(orientation)*center.y + Math.sin(orientation)*center.x;

        return Math.abs(cx-tx) < width/2 && Math.abs(cy-ty) < height/2;
    }
	
    /**
     * Determines if the {@link Rectangle} interests with this {@link OOB}
     * @param b
     * @return true if the {@link Rectangle} intersects with this {@link OOB}
     */
    public boolean intersects(Rectangle b) {
    	return // check to see if the OOB corners are within the rectangle
    		   b.contains(topLeft) ||
               b.contains(topRight) ||
               b.contains(bottomLeft) ||
               b.contains(bottomRight) ||
    
               
               // now check the lines
               checkLineAgainstOOB(topLeft, topRight, b) ||
    		   checkLineAgainstOOB(topRight, bottomRight, b) ||
    		   checkLineAgainstOOB(bottomRight, bottomLeft, b) ||
    		   checkLineAgainstOOB(bottomLeft, topLeft, b) ||               
               
               // now check if the Rectangle is in this OOB
               contains(b.x        , b.y) ||
               contains(b.x+b.width, b.y) ||
               contains(b.x+b.width, b.y+b.height) ||
               contains(b.x        , b.y-b.height);

    }
    
    /**
     * Determines if the supplied {@link OOB} intersects this {@link OOB}
     * 
     * @param other
     * @return true if the two {@link OOB}'s overlap
     */
	public boolean intersects(OOB other) {
		return // check the for line intersections
			   checkLineAgainstOOB(topLeft, topRight, other) ||
			   checkLineAgainstOOB(topRight, bottomRight, other) ||
			   checkLineAgainstOOB(bottomRight, bottomLeft, other) ||
			   checkLineAgainstOOB(bottomLeft, topLeft, other) ||
			   
			   // now check to see if they are inside eachother
			   hasCornersInside(other) || 
			   other.hasCornersInside(this);
		
	}
		
	/**
	 * Probably a more efficient way of doing this, but in general the algorithm
	 * determines if any of the four corners of either {@link OOB} is contained in the
	 * other {@link OOB} (and checks the reverse)
	 * 
	 * @param other
	 * @return true if any of the supplied {@link OOB} corners are contained in this {@link OOB}
	 */
	private boolean hasCornersInside(OOB other) {
	    return contains(other.topLeft) ||
	           contains(other.topRight) ||
	           contains(other.bottomLeft) ||
	           contains(other.bottomRight);
	}

	/**
	 * Not very efficient means of testing, but tests the supplied line with all of the other lines
	 * that make up the other {@link OOB}
	 * 
	 * @param a
	 * @param b
	 * @param other
	 * @return true if the supplied line intersects with one of the lines that make up the {@link OOB}
	 */
	private boolean checkLineAgainstOOB(Vector2f a, Vector2f b, OOB other) {
		return Line.lineIntersectLine(a, b, other.topLeft, other.topRight) ||
			   Line.lineIntersectLine(a, b, other.topRight, other.bottomRight) ||
			   Line.lineIntersectLine(a, b, other.bottomRight, other.bottomLeft) ||
               Line.lineIntersectLine(a, b, other.bottomLeft, other.topLeft);
	}
	
	private boolean checkLineAgainstOOB(Vector2f a, Vector2f b, Rectangle other) {
		return Line.lineIntersectLine(a.x, a.y, b.x, b.y, other.x            , other.y             , other.x+other.width, other.y) ||
			   Line.lineIntersectLine(a.x, a.y, b.x, b.y, other.x+other.width, other.y             , other.x+other.width, other.y+other.height) ||
			   Line.lineIntersectLine(a.x, a.y, b.x, b.y, other.x+other.width, other.y+other.height, other.x            , other.y+other.height) ||
               Line.lineIntersectLine(a.x, a.y, b.x, b.y, other.x            , other.y+other.height, other.x            , other.y);
	}
	
	
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{  \n  width: ").append(width)
                .append(", \n  height: ").append(height)
                .append(", \n  orientation: ").append(orientation)
                .append(", \n  center: ").append(center)
                .append(", \n  topLeft: ").append(topLeft)
                .append(", \n  topRight: ").append(topRight)
                .append(", \n  bottomLeft: ").append(bottomLeft)
                .append(", \n  bottomRight: ").append(bottomRight)
                .append(" \n}");
        
        return builder.toString();
    }

    public static void main(String[] args) {
	    OOB a = new OOB( (float)Math.PI/4f, new Vector2f(0,0), 50, 50);
	    
	    
	    System.out.println(a.contains(1, 1));
	    System.out.println(a.contains(-1, -1));
	    
	    System.out.println(a.contains(0, 25));
	    System.out.println(a.contains(0, -25));
	    System.out.println(a.contains(25, 0));
	    System.out.println(a.contains(-25, 0));
	    
	    float d = 36;
	    System.out.println(a.contains(0, d));
        System.out.println(a.contains(0, -d));
        System.out.println(a.contains(d, 0));
        System.out.println(a.contains(-d, 0));
        
        Vector2f v = new Vector2f(25, 0);
        Vector2f.Vector2fRotate(v, 3*Math.PI/4, v);
        System.out.println("Point is in a: " + a.contains(v));
	    
	   // a.setLocation(76, 76);
	    
        printOOB(a);
        
	    
	    OOB b = new OOB( a.orientation, new Vector2f(35.355f,35.355f), 50, 50);
	    b.translate(1, 1);
	    b.translate(-1, -1);
	    printOOB(b);
	    
	    System.out.println("A intersects B: " + a.intersects(b));
	}
	
	private static void printOOB(OOB a) {
	    System.out.printf("C : (%3.1f, %3.1f)  D: %3.1f \n",a.center.x,a.center.y,Vector2f.Vector2fDistance(Vector2f.ZERO_VECTOR, a.center));
	    System.out.printf("TL: (%3.1f, %3.1f)  D: %3.1f \n",a.topLeft.x,a.topLeft.y,Vector2f.Vector2fDistance(Vector2f.ZERO_VECTOR, a.topLeft));
        System.out.printf("TR: (%3.1f, %3.1f)  D: %3.1f \n",a.topRight.x,a.topRight.y,Vector2f.Vector2fDistance(Vector2f.ZERO_VECTOR, a.topRight));
        System.out.printf("BL: (%3.1f, %3.1f)  D: %3.1f \n",a.bottomLeft.x,a.bottomLeft.y,Vector2f.Vector2fDistance(Vector2f.ZERO_VECTOR, a.bottomLeft));
        System.out.printf("BR: (%3.1f, %3.1f)  D: %3.1f \n",a.bottomRight.x,a.bottomRight.y,Vector2f.Vector2fDistance(Vector2f.ZERO_VECTOR, a.bottomRight));
	}
}
