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
        return barycentricTechnique(pointZ.x, pointZ.y, triangle.getPointA().x, triangle.getPointA().y, 
        		triangle.getPointB().x, triangle.getPointB().y, triangle.getPointC().x, triangle.getPointC().y);
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
    	return barycentricTechnique(pointZx, pointZy, pointAx, pointAy, pointBx, pointBy, pointCx, pointCy);
    }

	private static boolean barycentricTechnique(float pointZx, float pointZy, float pointAx, float pointAy,
			float pointBx, float pointBy, float pointCx, float pointCy) {
		Vector2f v0 = new Vector2f(pointCx-pointAx, pointCy-pointAy);
    	Vector2f v1 = new Vector2f(pointBx-pointAx, pointBy-pointAy);
    	Vector2f v2 = new Vector2f(pointZx-pointAx, pointZy-pointAy);
        
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
        float x0 = triangle.getPointA().x; 
        float y0 = triangle.getPointA().y; 
        float x1 = triangle.getPointB().x; 
        float y1 = triangle.getPointB().y; 
        float x2 = triangle.getPointC().x; 
        float y2 = triangle.getPointC().y; 
        
        return rectangleIntersectsTriangle(rectangle, x0, y0, x1, y1, x2, y2);
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
        int l = rectangle.x; 
        int r = rectangle.x + rectangle.width; 
        int t = rectangle.y; 
        int b = rectangle.y + rectangle.height; 
        
        
        int b0 = 0;
        if ( pointAx > l ) b0=1;
        if ( pointAy > t ) b0 |= (b0<<1);
        if ( pointAx > r ) b0 |= (b0<<2);
        if ( pointAy > b ) b0 |= (b0<<3);
         
        if ( b0 == 3 ) return true;
         
        int b1 = 0;
        if ( pointBx > l ) b1=1;
        if ( pointBy > t ) b1 |= (b1<<1);
        if ( pointBx > r ) b1 |= (b1<<2);
        if ( pointBy > b ) b1 |= (b1<<3);
         
        if ( b1 == 3 ) return true;
         
        int b2 = 0;
        if ( pointCx > l ) b2=1;
        if ( pointCy > t ) b2 |= (b2<<1);
        if ( pointCx > r ) b2 |= (b2<<2);
        if ( pointCy > b ) b2 |= (b2<<3);
         
        if ( b2 == 3 ) return true;

        int i0 = b0 ^ b1;
        if (i0 != 0)
        {
            float m = (pointBy-pointAy) / (pointBx-pointAx); 
            float c = pointAy -(m * pointAx);
            if ( (i0 & 1) > 0 ) { float s = m * l + c; if ( s > t && s < b) return true; }
            if ( (i0 & 2) > 0 ) { float s = (t - c) / m; if ( s > l && s < r) return true; }
            if ( (i0 & 4) > 0 ) { float s = m * r + c; if ( s > t && s < b) return true; }
            if ( (i0 & 8) > 0 ) { float s = (b - c) / m; if ( s > l && s < r) return true; }
        }
        
        int i1 = b1 ^ b2;
        if (i1 != 0)
        {
            float m = (pointCy-pointBy) / (pointCx-pointBx); 
            float c = pointBy -(m * pointBx);
            if ( (i1 & 1) > 0 ) { float s = m * l + c; if ( s > t && s < b) return true; }
            if ( (i1 & 2) > 0 ) { float s = (t - c) / m; if ( s > l && s < r) return true; }
            if ( (i1 & 4) > 0 ) { float s = m * r + c; if ( s > t && s < b) return true; }
            if ( (i1 & 8) > 0 ) { float s = (b - c) / m; if ( s > l && s < r) return true; }
        }
        
        int i2 = b0 ^ b2;
        if (i2 != 0)
        {
            float m = (pointCy-pointAy) / (pointCx-pointAx); 
            float c = pointAy -(m * pointAx);
            if ( (i2 & 1) > 0 ) { float s = m * l + c; if ( s > t && s < b) return true; }
            if ( (i2 & 2) > 0 ) { float s = (t - c) / m; if ( s > l && s < r) return true; }
            if ( (i2 & 4) > 0 ) { float s = m * r + c; if ( s > t && s < b) return true; }
            if ( (i2 & 8) > 0 ) { float s = (b - c) / m; if ( s > l && s < r) return true; }
        }
        
        return false;
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
