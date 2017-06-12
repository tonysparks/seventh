/*
 *    leola-live 
 *  see license.txt
 */
package seventh.math;

import java.awt.geom.Line2D;



/**
 * @author Tony
 *
 */
public class Line {

    public Vector2f a;
    public Vector2f b;
    
    /**
     * Constructs a new Line.
     * 
     * @param a
     * @param b
     */
    public Line(Vector2f a, Vector2f b) {
        this.a = a; 
        this.b = b;
    }
    
    /**
     * Determine if a line intersects with another line.
     * 
     * @param l1
     * @param l2
     * @return
     */
    public static boolean lineIntersectLine( Line l1, Line l2 ) {
        return lineIntersectLine(l1.a, l1.b, l2.a, l2.b);
    }
    
    /**
     * Determine if a line intersects with another line
     * 
     * @param v1
     * @param v2
     * @param v3
     * @param v4
     * @return
     */
    public static boolean lineIntersectLine( Vector2f v1, Vector2f v2, Vector2f v3, Vector2f v4 )
    {
        return Line2D.linesIntersect(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y);
        
        
//        float denom = ((v4.y - v3.y) * (v2.x - v1.x)) - ((v4.x - v3.x) * (v2.y - v1.y));
//        float numerator = ((v4.x - v3.x) * (v1.y - v3.y)) - ((v4.y - v3.y) * (v1.x - v3.x));
//
//        float numerator2 = ((v2.x - v1.x) * (v1.y - v3.y)) - ((v2.y - v1.y) * (v1.x - v3.x));
//
//        if ( denom == 0.0f ) return false;
//
//        float ua = numerator / denom;
//        float ub = numerator2/ denom;
//
//        return (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f);

    }
    
    /**
     * Line Line intersection.  You've crossed the line, line! (maybe, we'll see).
     * 
     * @param v1x
     * @param v1y
     * @param v2x
     * @param v2y
     * @param v3x
     * @param v3y
     * @param v4x
     * @param v4y
     * @return true if the lines cross
     */
    public static boolean lineIntersectLine( float v1x, float v1y, float v2x, float v2y, float v3x, float v3y, float v4x, float v4y ) {
        return Line2D.linesIntersect(v1x, v1y, v2x, v2y, v3x, v3y, v4x, v4y);
    }
    
//    public static boolean lineIntersectsRect(float x0, float y0, float x1, float y1, Rectangle rect) {
//        
//        float l = rect.x;
//        
//        float d = x1 - x0;
//        if(d==0) return false;
//        
//        float m = (y1 - y0) / (x1 - x0);
//        float c = y0 - (m*x0);
//        
//        float topIntersection = 0f;
//        float bottomIntersection = 0f;
//        
//        if(m>0) {
//            topIntersection = (m*rect.x + c);
//        }
//        return false;
//    }
    
    /**
     * Determine if a line intersects with a {@link Rectangle}.
     * 
     * @param rect
     * @return
     */
    public static boolean lineIntersectsRectangle(Line l, Rectangle rect) {
        return lineIntersectsRectangle(l.a, l.b, rect);
    }
    
    /**
     * Determine if a line intersects with a {@link Rectangle}.
     * 
     * @param rect
     * @return
     */
    public static boolean lineIntersectsRectangle(Vector2f v1, Vector2f v2, Rectangle rect) {
        //Line2D line = new Line2D.Float(v1.x, v1.y, v2.x, v2.y);        
        //return line.intersects(new java.awt.Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
        
        return intersectsLine(rect, v1.x, v1.y, v2.x, v2.y);
        
//        
//        int x = rect.getX();
//        int y = rect.getY();
//        int width = rect.getWidth();
//        int height = rect.getHeight();
//        
//        Vector2f lowerLeft  = new Vector2f( x, y+height );
//        Vector2f upperRight = new Vector2f( x+width, y );
//        Vector2f upperLeft  = new Vector2f( x, y );
//        Vector2f lowerRight = new Vector2f( x+width, y+height);
//        
//        // check if it is inside
//        if (v1.x > lowerLeft.x && v1.x < upperRight.x && v1.y < lowerLeft.y && v1.y > upperRight.y &&
//            v2.x > lowerLeft.x && v2.x < upperRight.x && v2.y < lowerLeft.y && v2.y > upperRight.y ) {   
//            return true;
//        }
//        
//        // check each line for intersection
//        if (lineIntersectLine(v1,v2, upperLeft, lowerLeft ) ) return true;
//        if (lineIntersectLine(v1,v2, lowerLeft, lowerRight) ) return true;
//        if (lineIntersectLine(v1,v2, upperLeft, upperRight) ) return true;
//        if (lineIntersectLine(v1,v2, upperRight, lowerRight) ) return true;
//        
//        // no collision
//        return false;
    }
    
    /**
     * The bitmask that indicates that a point lies to the left of
     * this <code>Rectangle2D</code>.
     * @since 1.2
     */
    private static final int OUT_LEFT = 1;

    /**
     * The bitmask that indicates that a point lies above
     * this <code>Rectangle2D</code>.
     * @since 1.2
     */
    private static final int OUT_TOP = 2;

    /**
     * The bitmask that indicates that a point lies to the right of
     * this <code>Rectangle2D</code>.
     * @since 1.2
     */
    private static final int OUT_RIGHT = 4;

    /**
     * The bitmask that indicates that a point lies below
     * this <code>Rectangle2D</code>.
     * @since 1.2
     */
    private static final int OUT_BOTTOM = 8;

    
    /**
     * {@inheritDoc}
     * @since 1.2
     */
    private static int outcode(Rectangle r, float x, float y) {
        /*
         * Note on casts to double below.  If the arithmetic of
         * x+w or y+h is done in int, then we may get integer
         * overflow. By converting to double before the addition
         * we force the addition to be carried out in double to
         * avoid overflow in the comparison.
         *
         * See bug 4320890 for problems that this can cause.
         */
        int out = 0;
        if (r.width <= 0) {
            out |= OUT_LEFT | OUT_RIGHT;
        } else if (x < r.x) {
            out |= OUT_LEFT;
        } else if (x > r.x + (float) r.width) {
            out |= OUT_RIGHT;
        }
        if (r.height <= 0) {
            out |= OUT_TOP | OUT_BOTTOM;
        } else if (y < r.y) {
            out |= OUT_TOP;
        } else if (y > r.y + (float) r.height) {
            out |= OUT_BOTTOM;
        }
        return out;
    }
    
    /**
     * Tests if the specified line segment intersects the interior of this
     * <code>Rectangle2D</code>.
     *
     * @param x1 the X coordinate of the start point of the specified
     *           line segment
     * @param y1 the Y coordinate of the start point of the specified
     *           line segment
     * @param x2 the X coordinate of the end point of the specified
     *           line segment
     * @param y2 the Y coordinate of the end point of the specified
     *           line segment
     * @return <code>true</code> if the specified line segment intersects
     * the interior of this <code>Rectangle2D</code>; <code>false</code>
     * otherwise.
     * @since 1.2
     */
    private static boolean intersectsLine(Rectangle r, float x1, float y1, float x2, float y2) {
        int out1, out2;
        if ((out2 = outcode(r, x2, y2)) == 0) {
            return true;
        }
        while ((out1 = outcode(r, x1, y1)) != 0) {
            if ((out1 & out2) != 0) {
                return false;
            }
            if ((out1 & (OUT_LEFT | OUT_RIGHT)) != 0) {
                float x = (float)r.x;
                if ((out1 & OUT_RIGHT) != 0) {
                    x += r.width;
                }
                y1 = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
                x1 = x;
            } else {
                float y = (float)r.y;
                if ((out1 & OUT_BOTTOM) != 0) {
                    y += r.height;
                }
                x1 = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
                y1 = y;
            }
        }
        return true;
    }
    
    /**
     * Returns true if the line intersects the rectangle, and stores the clipping points in the near and far vectors.
     * 
     * @param line
     * @param rect
     * @param near - the near collision
     * @param far - the far collision
     * @return if a collision occurred
     */
    public static boolean lineIntersectsRectangle(Line line, Rectangle rect, Vector2f near, Vector2f far) {
        return lineIntersectsRectangle(line.a, line.b, rect, near, far);
    }
    
    /**
     * Returns true if the line intersects the rectangle, and stores the clipping points in the near and far vectors.
     * 
     * @param v1
     * @param v2
     * @param rect
     * @param near - the near collision
     * @param far - the far collision
     * @return if a collision occurred
     */
    public static boolean lineIntersectsRectangle(Vector2f v1, Vector2f v2, Rectangle rect, Vector2f near, Vector2f far) {
        Vector2f O = v1;
        Vector2f D = new Vector2f();
        Vector2f.Vector2fNormalize(v2, D);
        
        Vector2f E = new Vector2f();
        E.x = rect.getWidth()  / 2;
        E.y = rect.getHeight() / 2;
        
        Vector2f C = new Vector2f();
        C.x = rect.getX() + E.x;
        C.y = rect.getY() + E.y;
        
        float t[] = {0,0};    /* parametric values corresponding to the points where the line intersects the AABB. */
        boolean result = intersectLineAABB(O, D, C, E, t, FloatUtil.epsilon);
        System.out.println(t);
        Vector2f tmp = new Vector2f();
                
        // near = O + t0 * D
        Vector2f.Vector2fMult(D, t[0], tmp);
        Vector2f.Vector2fAdd(O, tmp, near);
        
        // far = O + t1 * D
        Vector2f.Vector2fMult(D, t[1], tmp);
        Vector2f.Vector2fAdd(O, tmp, far);
                        
        return result && (rect.contains(far) 
                || rect.contains(near));
    }
    
    /**
     * Calculates the far collision point of the {@link Line} through the {@link Rectangle}.
     * 
     * <p>
     * It is recommended that the {@link Line} is guaranteed to intersect the supplied {@link Rectangle}. 
     * 
     * @param L
     * @param rect
     * @return
     */
    public static Vector2f farCollisionPoint(Line L, Rectangle rect) {
        Vector2f near = new Vector2f();
        Vector2f far = new Vector2f();
        
        Line.lineIntersectsRectangle(L, rect, near, far);
        
        return far;
    }
    
    /**
     * Calculates the near collision point of the {@link Line} through the {@link Rectangle}.
     * 
     * <p>
     * It is recommended that the {@link Line} is guaranteed to intersect the supplied {@link Rectangle}. 
     * 
     * @param L
     * @param rect
     * @return
     */
    public static Vector2f nearCollisionPoint(Line L, Rectangle rect) {
        Vector2f near = new Vector2f();
        Vector2f far = new Vector2f();
        
        Line.lineIntersectsRectangle(L, rect, near, far);
        
        return near;
    }
    

    /**
     * Great thanks to jvk on this article:
     *     http://www.gamedev.net/community/forums/topic.asp?topic_id=433699
     * 
     * @param origin
     * @param direction
     * @param center
     * @param extent
     * @param slab[]
     * @param epsilon
     * @return boolean
     */
    private static boolean intersectLineAABB(
            Vector2f origin, // Line origin
            Vector2f direction, // Line direction (unit length)
            Vector2f center, // AABB center
            Vector2f extent, // AABB extents
            float slab[],               // Parametric points of intersection on output
            float epsilon)           // Threshold for epsilon test
    {
        int parallelStatement = 0; // 01 : parallel with x axis, 10 : parallel with y axis.
        boolean storeSlab = false;
        Vector2f dest = new Vector2f(); 
        Vector2f.Vector2fSubtract(center,origin, dest);

        for (int i = 0; i < 2; ++i)
        {
            boolean isDirParallel = Math.abs(direction.get(i)) < epsilon;
			if (isDirParallel)
                parallelStatement |= 1 << i;
            else
            {
                boolean isDirPositive = direction.get(i) > 0.0f;
				float extentSlab = isDirPositive ? extent.get(i) : -extent.get(i); 
                float invDir = 1.0f / direction.get(i); 

                if (!storeSlab)
                {
                    slab[0] = (dest.get(i) - extentSlab) * invDir;
                    slab[1] = (dest.get(i) + extentSlab) * invDir;
                    storeSlab = true;
                }
                else
                {
                    float yAxisSlab = (dest.get(i) - extentSlab) * invDir;
                    if (yAxisSlab > slab[0]){
                        slab[0] = yAxisSlab;
                    }
                    yAxisSlab = (dest.get(i) + extentSlab) * invDir; 
                    if (yAxisSlab < slab[1]){
                        slab[1] = yAxisSlab;
                    }
                    if (slab[0] > slab[1])
                        return false;
                }
            }
        }
        
        if (parallelStatement != 0) {
            for (int i = 0; i < 2; ++i) {
                if ((parallelStatement & (1 << i)) != 0 ) {
                    boolean isIntersectedWithMinSlab = Math.abs(dest.get(i) - slab[0] * direction.get(i)) > extent.get(i);
					boolean isIntersectWithMaxSlab = Math.abs(dest.get(i) - slab[1] * direction.get(i)) > extent.get(i);
					if (isIntersectedWithMinSlab || isIntersectWithMaxSlab) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
    
    public static void main(String [] args) {
        Line l = new Line(new Vector2f(-1,-1), new Vector2f(1,1));
//        Vector2f Pnear = new Vector2f();
//        Vector2f Pfar = new Vector2f();
        Vector2f C = new Vector2f(2,2);
        Vector2f E = new Vector2f(2,2);
//        Vector2f [] D = new Vector2f[2];
//        //for(int i = 0 ; i < 2; i++) {
//            D[0] = new Vector2f(0,0);
//            D[1] = new Vector2f(0,0);
//        //}
//        l.SegmentIntersectBox(l, C, R, D, Pnear, Pfar);
        
        float [] t = {0,0};
        boolean a = intersectLineAABB(l.a, l.b, C, E, t, FloatUtil.epsilon);
        System.out.println(a);
        Vector2f tmp = new Vector2f();
        
        Vector2f near = new Vector2f();
        Vector2f far = new Vector2f();
       
        Vector2f.Vector2fMult(l.b, t[0], tmp);
        Vector2f.Vector2fAdd(l.a, tmp, near);

        Vector2f.Vector2fMult(l.b, t[1], tmp);
        Vector2f.Vector2fAdd(l.a, tmp, far);
        
    }
    
}
