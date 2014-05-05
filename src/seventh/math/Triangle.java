/*
 *	leola-live 
 *  see license.txt
 */
package seventh.math;


/**
 * A triangle defined by 3 vectors of each corner.  The vectors are connected by:
 * <pre>
 *    b
 *   / \
 *  a---c
 * </pre>
 * 
 * @author Tony
 *
 */
public class Triangle {
	public Vector2f a;
	public Vector2f b;
	public Vector2f c;
	
	/**
	 * @param a
	 * @param b
	 * @param c
	 */
	public Triangle(Vector2f a, Vector2f b, Vector2f c) {
		this.a = a; this.b = b; this.c = c;
	}
	
	/**
	 * Translates the {@link Triangle} to a new location.
	 * 
	 * @param a
	 */
	public void translate(Vector2f a) {
		Vector2f.Vector2fAdd(this.a, a, this.a);
		Vector2f.Vector2fAdd(this.b, a, this.b);
		Vector2f.Vector2fAdd(this.c, a, this.c);
	}
	
	/**
	 * Translates the {@link Triangle} to a new location.
	 * 
	 * @param a
	 */
	public void translate(float a) {
		Vector2f.Vector2fAdd(this.a, a, this.a);
		Vector2f.Vector2fAdd(this.b, a, this.b);
		Vector2f.Vector2fAdd(this.c, a, this.c);
	}
	
	/**
	 * Translates the {@link Triangle} to a new location.
	 * 
	 * @param triangle
	 * @param a
	 * @param dest
	 */
	public static void TriangleTranslate(Triangle triangle, Vector2f a, Triangle dest) {
		Vector2f.Vector2fAdd(triangle.a, a, dest.a);
		Vector2f.Vector2fAdd(triangle.b, a, dest.b);
		Vector2f.Vector2fAdd(triangle.c, a, dest.c);
	}
	
	/**
	 * Translates the {@link Triangle} to a new location.
	 * 
	 * @param triangle
	 * @param a
	 * @param dest
	 */
	public static void TriangleTranslate(Triangle triangle, float a, Triangle dest) {
		Vector2f.Vector2fAdd(triangle.a, a, dest.a);
		Vector2f.Vector2fAdd(triangle.b, a, dest.b);
		Vector2f.Vector2fAdd(triangle.c, a, dest.c);
	}
	
	/**
	 * Determine if the supplied point is within (contains or intersects) the supplied {@link Triangle}.
	 * 
	 * @param a
	 * @param triangle
	 * @return
	 */
	public static boolean pointIntersectsTriangle(Vector2f a, Triangle triangle) {
		throw new UnsupportedOperationException("Method not implement!");
	}
	
	/**
	 * Determine if the supplied {@link Line} intersects with the supplied {@link Triangle}.
	 * 
	 * @param L
	 * @param triangle
	 * @return
	 */
	public static boolean lineIntersectsTriangle(Line L, Triangle triangle) {
		return Line.lineIntersectLine(L.a, L.b, triangle.a, triangle.b) || 
			   Line.lineIntersectLine(L.a, L.b, triangle.b, triangle.c) ||
			   Line.lineIntersectLine(L.a, L.b, triangle.c, triangle.a);
	}
	
	/**
	 * Determine if the supplied {@link Line} intersects with the supplied {@link Triangle}.
	 * 
	 * @param a
	 * @param b
	 * @param triangle
	 * @return
	 */
	public static boolean lineIntersectsTriangle(Vector2f a, Vector2f b, Triangle triangle) {
		return Line.lineIntersectLine(a, b, triangle.a, triangle.b) || 
			   Line.lineIntersectLine(a, b, triangle.b, triangle.c) ||
			   Line.lineIntersectLine(a, b, triangle.c, triangle.a);
	}
	
	/**
	 * Determine if the supplied {@link Rectangle} and {@link Triangle} intersect.
	 * 
	 * <p>
	 * The algorithm used is from <a>http://www.sebleedelisle.com/2009/05/super-fast-trianglerectangle-intersection-test/</a>
	 * 
	 * @param rectangle
	 * @param t
	 * @return
	 */
	public static boolean rectangleIntersectsTriangle(Rectangle rectangle, Triangle triangle) {		  
		int l = rectangle.getX(); 
        int r = rectangle.getY(); 
        int t = rectangle.getWidth(); 
        int b = rectangle.getHeight(); 
 
        float x0 = triangle.a.x; 
        float y0 = triangle.a.y; 
        float x1 = triangle.b.x; 
        float y1 = triangle.b.y; 
        float x2 = triangle.c.x; 
        float y2 = triangle.c.y; 
         
        int b0 = 0;
        if ( x0 > l ) b0=1;
        if ( y0 > t ) b0 |= (b0<<1);
        if ( x0 > r ) b0 |= (b0<<2);
        if ( y0 > b ) b0 |= (b0<<3);
         
        if ( b0 == 3 ) return true;
         
        int b1 = 0;
        if ( x1 > l ) b1=1;
        if ( y1 > t ) b1 |= (b1<<1);
        if ( x1 > r ) b1 |= (b1<<2);
        if ( y1 > b ) b1 |= (b1<<3);
         
        if ( b1 == 3 ) return true;
         
        int b2 = 0;
        if ( x2 > l ) b2=1;
        if ( y2 > t ) b2 |= (b2<<1);
        if ( x2 > r ) b2 |= (b2<<2);
        if ( y2 > b ) b2 |= (b2<<3);
         
        if ( b2 == 3 ) return true;

        int i0 = b0 ^ b1;
        if (i0 != 0)
        {
            float m = (y1-y0) / (x1-x0); 
            float c = y0 -(m * x0);
            if ( (i0 & 1) > 0 ) { float s = m * l + c; if ( s > t && s < b) return true; }
            if ( (i0 & 2) > 0 ) { float s = (t - c) / m; if ( s > l && s < r) return true; }
            if ( (i0 & 4) > 0 ) { float s = m * r + c; if ( s > t && s < b) return true; }
            if ( (i0 & 8) > 0 ) { float s = (b - c) / m; if ( s > l && s < r) return true; }
        }
        
        int i1 = b1 ^ b2;
        if (i1 != 0)
        {
        	float m = (y2-y1) / (x2-x1); 
        	float c = y1 -(m * x1);
            if ( (i1 & 1) > 0 ) { float s = m * l + c; if ( s > t && s < b) return true; }
            if ( (i1 & 2) > 0 ) { float s = (t - c) / m; if ( s > l && s < r) return true; }
            if ( (i1 & 4) > 0 ) { float s = m * r + c; if ( s > t && s < b) return true; }
            if ( (i1 & 8) > 0 ) { float s = (b - c) / m; if ( s > l && s < r) return true; }
        }
        
        int i2 = b0 ^ b2;
        if (i2 != 0)
        {
        	float m = (y2-y0) / (x2-x0); 
        	float c = y0 -(m * x0);
            if ( (i2 & 1) > 0 ) { float s = m * l + c; if ( s > t && s < b) return true; }
            if ( (i2 & 2) > 0 ) { float s = (t - c) / m; if ( s > l && s < r) return true; }
            if ( (i2 & 4) > 0 ) { float s = m * r + c; if ( s > t && s < b) return true; }
            if ( (i2 & 8) > 0 ) { float s = (b - c) / m; if ( s > l && s < r) return true; }
        }
        
        return false;
	}
}
