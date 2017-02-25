/*
 *    leola-live 
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
     * @return true if the point is in the {@link Triangle}
     */
    public static boolean pointIntersectsTriangle(Vector2f a, Triangle triangle) {
        float x0 = triangle.a.x; 
        float y0 = triangle.a.y; 
        float x1 = triangle.b.x; 
        float y1 = triangle.b.y; 
        float x2 = triangle.c.x; 
        float y2 = triangle.c.y; 
        return pointIntersectsTriangle(a.x, a.y, x0, y0, x1, y1, x2, y2);
    }

    /**
     * Determine if the supplied point is within (contains or intersects) the supplied {@link Triangle}.
     * 
     * @param px
     * @param py
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return true if the point is in the {@link Triangle}
     */
    public static boolean pointIntersectsTriangle(float px, float py, float x0, float y0, float x1, float y1, float x2, float y2) {
        
        float v0x = x2 - x0;
        float v0y = y2 - y0;
        
        float v1x = x1 - x0;
        float v1y = y1 - y0;
        
        float v2x = px - x0;
        float v2y = py - y0;
        
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
     * @return true if the rectangle intersects the triangle
     */
    public static boolean rectangleIntersectsTriangle(Rectangle rectangle, Triangle triangle) {          
        float x0 = triangle.a.x; 
        float y0 = triangle.a.y; 
        float x1 = triangle.b.x; 
        float y1 = triangle.b.y; 
        float x2 = triangle.c.x; 
        float y2 = triangle.c.y; 
        
        return rectangleIntersectsTriangle(rectangle, x0, y0, x1, y1, x2, y2);
    }
    
    /**
     * Determine if the supplied {@link Rectangle} and {@link Triangle} intersect.
     * 
     * <p>
     * The algorithm used is from <a>http://www.sebleedelisle.com/2009/05/super-fast-trianglerectangle-intersection-test/</a>
     * 
     * @param rectangle
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return true if the rectangle intersects the triangle
     */
    public static boolean rectangleIntersectsTriangle(Rectangle rectangle, float x0, float y0, float x1, float y1, float x2, float y2) {          
        int l = rectangle.x; 
        int r = rectangle.x + rectangle.width; 
        int t = rectangle.y; 
        int b = rectangle.y + rectangle.height; 
        
        
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
