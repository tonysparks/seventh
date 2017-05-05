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
        Vector2f.Vector2fAdd(this.getPointA(), pointZ, this.getPointA());
        Vector2f.Vector2fAdd(this.getPointB(), pointZ, this.getPointB());
        Vector2f.Vector2fAdd(this.getPointC(), pointZ, this.getPointC());
    }
    
    /**
     * Translates the {@link Triangle} to a new location.
     * 
     * @param pointZ
     */
    public void translate(float pointZ) {
        Vector2f.Vector2fAdd(this.getPointA(), pointZ, this.getPointA());
        Vector2f.Vector2fAdd(this.getPointB(), pointZ, this.getPointB());
        Vector2f.Vector2fAdd(this.getPointC(), pointZ, this.getPointC());
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
     * @param a
     * @param dest
     */
    public static void TriangleTranslate(Triangle triangle, float a, Triangle dest) {
        Vector2f.Vector2fAdd(triangle.getPointA(), a, dest.getPointA());
        Vector2f.Vector2fAdd(triangle.getPointB(), a, dest.getPointB());
        Vector2f.Vector2fAdd(triangle.getPointC(), a, dest.getPointC());
    }
    
    /**
     * Determine if the supplied point is within (contains or intersects) the supplied {@link Triangle}.
     * 
     * @param pointZ
     * @param triangle
     * @return true if the point is in the {@link Triangle}
     */
    public static boolean pointIntersectsTriangle(Vector2f pointZ, Triangle triangle) {
        float x0 = triangle.getPointA().x; 
        float y0 = triangle.getPointA().y; 
        float x1 = triangle.getPointB().x; 
        float y1 = triangle.getPointB().y; 
        float x2 = triangle.getPointC().x; 
        float y2 = triangle.getPointC().y; 
        return pointIntersectsTriangle(pointZ.x, pointZ.y, x0, y0, x1, y1, x2, y2);
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
    public static boolean pointIntersectsTriangle(float pointZx, float pointZy, float pointAx, float pointAy, float pointBx, float pointBy, float pointCx, float pointCy) {
        
        float v0x = pointCx - pointAx;
        float v0y = pointCy - pointAy;
        
        float v1x = pointBx - pointAx;
        float v1y = pointBy - pointAy;
        
        float v2x = pointZx - pointAx;
        float v2y = pointZy - pointAy;
        
        // dot(v1.x * v2.x + v1.y * v2.y)
        
        // dot(v0,v0)
        float dot00 = v0x * v0x + v0y * v0y;
        
        // dot(v0,v1)
        float dot01 = v0x * v1x + v0y * v1y;
        
        // dot(v0,v2)
        float dot02 = v0x * v2x + v0y * v2y;
        
        // dot(v1, v1)
        float dot11 = v1x * v1x + v1y * v1y;
        
        // dot(v1, v2)
        float dot12 = v1x * v2x + v1y * v2y;
        
        float det = dot00 * dot11 - dot01 * dot01;
        if(det == 0) {
            return false;
        }
        
        float invDet = 1f / det;
        float u = (dot11 * dot02 - dot01 * dot12) * invDet;
        float v = (dot00 * dot12 - dot01 * dot02) * invDet;
        
        return (u >= 0) && (v >= 0) && (u + v < 1f);
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
