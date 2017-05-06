/*
 *    leola-live 
 *  see license.txt
 */
package seventh.math;


/**
 * A triangle defined by 3 vectors of each corner.  The vectors are connected by:
 * <pre>
 *    B
 *   / \
 *  A---C
 * </pre>
 * 
 * @author Tony
 *
 */
public class Triangle {
    private Vector2f pointA;
    private Vector2f pointB;
    private Vector2f pointC;
    
    /**
     * @param pointA
     * @param pointB
     * @param pointC
     */
    public Triangle(Vector2f pointA, Vector2f pointB, Vector2f pointC) {
        setPointA(pointA); 
        setPointB(pointB); 
        setPointC(pointC);
    }
    
    /**
     * Translates the {@link Triangle} to a new location.
     * 
     * @param pointZ
     */
    public void translate(Vector2f pointZ) {
        TriangleTranslate(this,pointZ,this);
    }
    
    /**
     * Translates the {@link Triangle} to a new location.
     * 
     * @param scalar
     */
    public void translate(float scalar) {
        TriangleTranslate(this,scalar,this);
    }
    
    /**
     * Translates the {@link Triangle} to a new location.
     * 
     * @param triangle
     * @param pointZ
     * @param dest
     */
    public static void TriangleTranslate(Triangle triangle, Vector2f pointZ, Triangle dest) {
        Vector2f.Vector2fAdd(triangle.getPointA(), pointZ, dest.getPointA());
        Vector2f.Vector2fAdd(triangle.getPointB(), pointZ, dest.getPointB());
        Vector2f.Vector2fAdd(triangle.getPointC(), pointZ, dest.getPointC());
    }
    
    /**
     * Translates the {@link Triangle} to a new location.
     * 
     * @param triangle
     * @param scalar
     * @param dest
     */
    public static void TriangleTranslate(Triangle triangle, float scalar, Triangle dest) {
        Vector2f.Vector2fAdd(triangle.getPointA(), scalar, dest.getPointA());
        Vector2f.Vector2fAdd(triangle.getPointB(), scalar, dest.getPointB());
        Vector2f.Vector2fAdd(triangle.getPointC(), scalar, dest.getPointC());
    }
    
    /**
     * Determine if the supplied point is within (contains or intersects) the supplied {@link Triangle}.
     * 
     * @param pointZ
     * @param triangle
     * @return true if the point is in the {@link Triangle}
     */
    public static boolean pointIntersectsTriangle(Vector2f pointZ, Triangle triangle) {
        return barycentricTechnique(pointZ,new Vector2f(triangle.getPointA().x, triangle.getPointA().y), 
        		new Vector2f(triangle.getPointB().x, triangle.getPointB().y), new Vector2f(triangle.getPointC().x, triangle.getPointC().y));
    }

    /**
     * Determine if the supplied point is within (contains or intersects) the supplied {@link Triangle}.
     * 
     * @param pointZx
     * @param pointZy
     * @param pointAx
     * @param pointAy
     * @param pointBx
     * @param pointBy
     * @param pointCx
     * @param pointCy
     * @return true if the point is in the {@link Triangle}
     */
    public static boolean pointIntersectsTriangle(float pointZx, float pointZy, float pointAx, float pointAy, 
    		float pointBx, float pointBy, float pointCx, float pointCy) {
    	return barycentricTechnique(new Vector2f(pointZx, pointZy),new Vector2f(pointAx, pointAy), new Vector2f(pointBx, pointBy), new Vector2f(pointCx, pointCy));
    }

	private static boolean barycentricTechnique(Vector2f pointZ,Vector2f pointA,Vector2f pointB,Vector2f pointC) {
		Vector2f v0 = new Vector2f(pointC.x-pointA.x, pointC.y-pointA.y);
    	Vector2f v1 = new Vector2f(pointB.x-pointA.x, pointB.y-pointA.y);
    	Vector2f v2 = new Vector2f(pointZ.x-pointA.x, pointZ.y-pointA.y);
        
        float dot00 = dotProducts(v0,v0);
        float dot01 = dotProducts(v0,v1);
        float dot02 = dotProducts(v0,v2);
        float dot11 = dotProducts(v1,v1);
        float dot12 = dotProducts(v1,v2);
        
        float det = dot00 * dot11 - dot01 * dot01;
        if(det == 0) {
            return false;
        }
        
        float invDenom = 1f / det;
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;
        
        final boolean isOutOfAB = (u >= 0);
		final boolean isOutOfAC = (v >= 0);
		final boolean isOutofBC = (u + v < 1f);
		return isOutOfAB && isOutOfAC && isOutofBC;
	}
    
    private static float dotProducts(Vector2f v1,Vector2f v2){
    	return v1.x * v2.x + v2.y * v2.y;
    }
    
    /**
     * Determine if the supplied {@link Line} intersects with the supplied {@link Triangle}.
     * 
     * @param line
     * @param triangle
     * @return
     */
    public static boolean lineIntersectsTriangle(Line line, Triangle triangle) {
        return Line.lineIntersectLine(line.a, line.b, triangle.getPointA(), triangle.getPointB()) || 
               Line.lineIntersectLine(line.a, line.b, triangle.getPointB(), triangle.getPointC()) ||
               Line.lineIntersectLine(line.a, line.b, triangle.getPointC(), triangle.getPointA());
    }
    
    /**
     * Determine if the supplied {@link Line} intersects with the supplied {@link Triangle}.
     * 
     * @param pointA
     * @param pointB
     * @param triangle
     * @return
     */
    public static boolean lineIntersectsTriangle(Vector2f pointA, Vector2f pointB, Triangle triangle) {
        return Line.lineIntersectLine(pointA, pointB, triangle.getPointA(), triangle.getPointB()) || 
               Line.lineIntersectLine(pointA, pointB, triangle.getPointB(), triangle.getPointC()) ||
               Line.lineIntersectLine(pointA, pointB, triangle.getPointC(), triangle.getPointA());
    }

    /**
     * Determine if the supplied {@link Rectangle} and {@link Triangle} intersect.
     * 
     * <p>
     * The algorithm used is from <a>http://www.sebleedelisle.com/2009/05/super-fast-trianglerectangle-intersection-test/</a>
     * 
     * @param rectangle
     * @param t
     * @return true if the rectangle intersects the triangle
     */
    public static boolean rectangleIntersectsTriangle(Rectangle rectangle, Triangle triangle) {          
        return rectangleIntersectsTriangle(rectangle, triangle.getPointA().x, triangle.getPointA().y, 
        		triangle.getPointB().x, triangle.getPointB().y, triangle.getPointC().x, triangle.getPointC().y);
    }
    
    /**
     * Determine if the supplied {@link Rectangle} and {@link Triangle} intersect.
     * 
     * <p>
     * The algorithm used is from <a>http://www.sebleedelisle.com/2009/05/super-fast-trianglerectangle-intersection-test/</a>
     * 
     * @param rectangle
     * @param pointAx
     * @param pointAy
     * @param pointBx
     * @param pointBy
     * @param pointCx
     * @param pointCy
     * @return true if the rectangle intersects the triangle
     */
    public static boolean rectangleIntersectsTriangle(Rectangle rectangle, float pointAx, float pointAy, float pointBx, float pointBy, float pointCx, float pointCy) {
    	final int isInRectangle = 3;
        int locationA = pointLocationWithRectangle(new Vector2f(pointAx,pointAy), rectangle);
        if ( locationA == isInRectangle ) return true;
        
        int locationB = pointLocationWithRectangle(new Vector2f(pointBx,pointBy), rectangle);
        if ( locationB == isInRectangle ) return true;
        
        int locationC = pointLocationWithRectangle(new Vector2f(pointCx,pointCy), rectangle);
        if ( locationC == isInRectangle ) return true;

        if(checkLineIntersection(new Vector2f(pointAx, pointAy), new Vector2f(pointBx, pointBy), rectangle, locationA^locationB)){
        	return true;
        }
        if(checkLineIntersection(new Vector2f(pointBx, pointBy),new Vector2f( pointCx, pointCy), rectangle, locationB^locationC)){
        	return true;
        }
        if(checkLineIntersection(new Vector2f(pointAx, pointAy),new Vector2f( pointCx, pointCy), rectangle, locationA^locationC)){
        	return true;
        }
        return false;
    }

	private static boolean checkLineIntersection(Vector2f pointA, Vector2f pointB, Rectangle rectangle, int relationTwoPoints) {
		if(relationTwoPoints == 0)	return false;
		int recStartX = rectangle.x; 
        int recEndX = rectangle.x + rectangle.width; 
        int recStartY = rectangle.y; 
        int recEndY = rectangle.y + rectangle.height;
        
        float gradient = (pointB.y-pointA.y) / (pointB.x-pointA.x); 
        float yIntercept = pointA.y -(gradient * pointA.x);
        if(relationTwoPoints > 1 && isCoordIntersect(gradient * recStartX + yIntercept,recStartY,recEndY)) return true;
        if(relationTwoPoints > 2 && isCoordIntersect((recStartY - yIntercept) / gradient,recStartX,recEndX)) return true;
        if(relationTwoPoints > 4 && isCoordIntersect(gradient * recEndX + yIntercept,recStartY,recEndY)) return true;
        if(relationTwoPoints > 8 && isCoordIntersect((recEndY - yIntercept) / gradient,recStartX,recEndX)) return true;
        return false;
	}
	
	private static boolean isCoordIntersect(float checkCoord, int recStartCoord, int recEndCoord){
		if(recStartCoord < checkCoord && checkCoord < recEndCoord) return true;
		return false;
	}

	private static int pointLocationWithRectangle(Vector2f point, Rectangle rectangle) {
        int recStartX = rectangle.x; 
        int recEndX = rectangle.x + rectangle.width; 
        int recStartY = rectangle.y; 
        int recEndY = rectangle.y + rectangle.height; 
		int locationValue = 0;
        if ( point.x > recStartX ) locationValue = 1;
        if ( point.y > recStartY ) locationValue |= (locationValue<<1);
        if ( point.x > recEndX ) locationValue |= (locationValue<<2);
        if ( point.y > recEndY ) locationValue |= (locationValue<<3);
		return locationValue;
	}

	public Vector2f getPointA() {
		return pointA;
	}

	public void setPointA(Vector2f a) {
		this.pointA = a;
	}

	public Vector2f getPointB() {
		return pointB;
	}

	public void setPointB(Vector2f b) {
		this.pointB = b;
	}

	public Vector2f getPointC() {
		return pointC;
	}

	public void setPointC(Vector2f c) {
		this.pointC = c;
	}
    
}
