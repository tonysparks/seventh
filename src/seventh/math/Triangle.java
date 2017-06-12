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
        this.pointA = pointA; 
        this.pointB = pointB; 
        this.pointC = pointC;
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
        Vector2f.Vector2fAdd(triangle.pointA, pointZ, dest.pointA);
        Vector2f.Vector2fAdd(triangle.pointB, pointZ, dest.pointB);
        Vector2f.Vector2fAdd(triangle.pointC, pointZ, dest.pointC);
    }
    
    /**
     * Translates the {@link Triangle} to a new location.
     * 
     * @param triangle
     * @param scalar
     * @param dest
     */
    public static void TriangleTranslate(Triangle triangle, float scalar, Triangle dest) {
        Vector2f.Vector2fAdd(triangle.pointA, scalar, dest.pointA);
        Vector2f.Vector2fAdd(triangle.pointB, scalar, dest.pointB);
        Vector2f.Vector2fAdd(triangle.pointC, scalar, dest.pointC);
    }
    
    /**
     * Determine if the supplied point is within (contains or intersects) the supplied {@link Triangle}.
     * 
     * @param pointZ
     * @param triangle
     * @return true if the point is in the {@link Triangle}
     */
    public static boolean pointIntersectsTriangle(Vector2f pointZ, Triangle triangle) {
        return barycentricTechnique(pointZ,triangle);
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
    	return barycentricTechnique(new Vector2f(pointZx, pointZy),
    			new Triangle(new Vector2f(pointAx, pointAy), new Vector2f(pointBx, pointBy), new Vector2f(pointCx, pointCy)));
    }

	private static boolean barycentricTechnique(Vector2f pointZ,Triangle triangle) {
		Vector2f v0 = new Vector2f(triangle.pointC.x-triangle.pointA.x, triangle.pointC.y-triangle.pointA.y);
    	Vector2f v1 = new Vector2f(triangle.pointB.x-triangle.pointA.x, triangle.pointB.y-triangle.pointA.y);
    	Vector2f v2 = new Vector2f(pointZ.x-triangle.pointA.x, pointZ.y-triangle.pointA.y);
        
        float det = dotProducts(v0,v0) * dotProducts(v1,v1) - dotProducts(v0,v1) * dotProducts(v0,v1);
        if(det == 0) {
            return false;
        }
        
        float invDenom = 1f / det;
        float u = (dotProducts(v1,v1) * dotProducts(v0,v2) - dotProducts(v0,v1) * dotProducts(v1,v2)) * invDenom;
        float v = (dotProducts(v0,v0) * dotProducts(v1,v2) - dotProducts(v0,v1) * dotProducts(v0,v2)) * invDenom;
        
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
        return Line.lineIntersectLine(line.a, line.b, triangle.pointA, triangle.pointB) || 
               Line.lineIntersectLine(line.a, line.b, triangle.pointB, triangle.pointC) ||
               Line.lineIntersectLine(line.a, line.b, triangle.pointC, triangle.pointA);
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
        return Line.lineIntersectLine(pointA, pointB, triangle.pointA, triangle.pointB) || 
               Line.lineIntersectLine(pointA, pointB, triangle.pointB, triangle.pointC) ||
               Line.lineIntersectLine(pointA, pointB, triangle.pointC, triangle.pointA);
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
        return rectangleIntersectsTriangle(rectangle, triangle.pointA.x, triangle.pointA.y, 
        		triangle.pointB.x, triangle.pointB.y, triangle.pointC.x, triangle.pointC.y);
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
    
}
