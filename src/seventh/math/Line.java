/*
 *	leola-live 
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
		Line2D line1 = new Line2D.Float(v1.x, v1.y, v2.x, v2.y);
		Line2D line2 = new Line2D.Float(v3.x, v3.y, v4.x, v4.y);
		return line1.intersectsLine(line2);
		
//	    float denom = ((v4.y - v3.y) * (v2.x - v1.x)) - ((v4.x - v3.x) * (v2.y - v1.y));
//	    float numerator = ((v4.x - v3.x) * (v1.y - v3.y)) - ((v4.y - v3.y) * (v1.x - v3.x));
//
//	    float numerator2 = ((v2.x - v1.x) * (v1.y - v3.y)) - ((v2.y - v1.y) * (v1.x - v3.x));
//
//	    if ( denom == 0.0f ) return false;
//
//	    float ua = numerator / denom;
//	    float ub = numerator2/ denom;
//
//	    return (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f);

	}
	
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
		Line2D line = new Line2D.Float(v1.x, v1.y, v2.x, v2.y);
		return line.intersects(new java.awt.Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
//		
//		int x = rect.getX();
//		int y = rect.getY();
//		int width = rect.getWidth();
//		int height = rect.getHeight();
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
		
		float t[] = {0,0};	/* parametric values corresponding to the points where the line intersects the AABB. */
		boolean result = intersectLineAABB(O, D, C, E, t, FloatUtil.epsilon);
		
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
	 * 	http://www.gamedev.net/community/forums/topic.asp?topic_id=433699
	 * 
	 * @param O
	 * @param D
	 * @param C
	 * @param e
	 * @param t
	 * @param epsilon
	 * @return
	 */
	private static boolean intersectLineAABB(
		    Vector2f O, // Line origin
		    Vector2f D, // Line direction (unit length)
		    Vector2f C, // AABB center
		    Vector2f e, // AABB extents
		    float t[],               // Parametric points of intersection on output
		    float epsilon)           // Threshold for epsilon test
	{
	    int parallel = 0;
	    boolean found = false;
	    Vector2f d = new Vector2f(); 
	    Vector2f.Vector2fSubtract(C,O, d);
	    
	    for (int i = 0; i < 2; ++i)
	    {
	        if (Math.abs(D.get(i)) < epsilon)
	            parallel |= 1 << i;
	        else
	        {
	            float es = (D.get(i) > 0.0f) ? e.get(i) : -e.get(i);
	            float invDi = 1.0f / D.get(i);

	            if (!found)
	            {
	                t[0] = (d.get(i) - es) * invDi;
	                t[1] = (d.get(i) + es) * invDi;
	                found = true;
	            }
	            else
	            {
	                float s = (d.get(i) - es) * invDi;
	                if (s > t[0])
	                    t[0] = s;
	                s = (d.get(i) + es) * invDi;
	                if (s < t[1])
	                    t[1] = s;
	                if (t[0] > t[1])
	                    return false;
	            }
	        }
	    }
	    
	    if (parallel != 0) {
	        for (int i = 0; i < 2; ++i) {
	            if ((parallel & (1 << i)) != 0 ) {
	                if (Math.abs(d.get(i) - t[0] * D.get(i)) > e.get(i) || Math.abs(d.get(i) - t[1] * D.get(i)) > e.get(i)) {
	                    return false;
	                }
	            }
	        }
	    }

	    return true;
	}
	
	public static void main(String [] args) {
		Line l = new Line(new Vector2f(-1,-1), new Vector2f(1,1));
//		Vector2f Pnear = new Vector2f();
//		Vector2f Pfar = new Vector2f();
		Vector2f C = new Vector2f(2,2);
		Vector2f E = new Vector2f(2,2);
//		Vector2f [] D = new Vector2f[2];
//		//for(int i = 0 ; i < 2; i++) {
//			D[0] = new Vector2f(0,0);
//			D[1] = new Vector2f(0,0);
//		//}
//		l.SegmentIntersectBox(l, C, R, D, Pnear, Pfar);
		
		float [] t = {0,0};
		intersectLineAABB(l.a, l.b, C, E, t, FloatUtil.epsilon);
		
	    Vector2f tmp = new Vector2f();
	    
	    Vector2f near = new Vector2f();
	    Vector2f far = new Vector2f();
	    	    
	    Vector2f.Vector2fMult(l.b, t[0], tmp);
		Vector2f.Vector2fAdd(l.a, tmp, near);

	    Vector2f.Vector2fMult(l.b, t[1], tmp);
		Vector2f.Vector2fAdd(l.a, tmp, far);
		
		System.out.println();
		
	}
	
}
