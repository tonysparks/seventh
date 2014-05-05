/*
 *	leola-live 
 *  see license.txt
 */
package seventh.math;

import static seventh.math.Vector2f.Vector2fDet;
import static seventh.math.Vector2f.Vector2fLengthSq;
import static seventh.math.Vector2f.Vector2fSubtract;
/**
 * @author Tony
 *
 */
public class Circle {

	public float radius;
	public Vector2f origin;

	/**
	 * Constructs a new {@link Circle}.
	 *
	 * @param origin
	 * @param radius
	 */
	public Circle(Vector2f origin, float radius) {
		this.origin = origin;
		this.radius = radius;
	}

	/**
	 * Check and see if the circle is contained within the supplied rectangle.
	 *
	 * @param circle
	 * @param rect
	 * @return
	 */
	public static boolean circleContainsRect(Circle circle, Rectangle rect) {
		int x = rect.getX();
		int y = rect.getY();
		int width = rect.getWidth();
		int height = rect.getHeight();

        Vector2f lowerLeft  = new Vector2f( x, y+height );
        Vector2f upperRight = new Vector2f( x+width, y );

        // check if it is inside
        return (circle.origin.x > lowerLeft.x && circle.origin.x < upperRight.x &&
        	    circle.origin.y < lowerLeft.y && circle.origin.y > upperRight.y );
	}

	/**
	 * Determine if the supplied {@link Circle} intersects the {@link Rectangle}.
	 *
	 * @param circle
	 * @param rect
	 * @return
	 */
	public static boolean circleIntersectsRect(Circle circle, Rectangle rect) {
		int x = rect.getX();
		int y = rect.getY();
		int width = rect.getWidth();
		int height = rect.getHeight();

        Vector2f lowerLeft  = new Vector2f( x, y+height );
        Vector2f upperRight = new Vector2f( x+width, y );
        Vector2f upperLeft  = new Vector2f( x, y );
        Vector2f lowerRight = new Vector2f( x+width, y+height);

        // check if it is inside
        if (circle.origin.x > lowerLeft.x && circle.origin.x < upperRight.x &&
        	circle.origin.y < lowerLeft.y && circle.origin.y > upperRight.y ) {
            return true;
        }

        // check each line for intersection
        if (circleIntersectsLine(circle, upperLeft, lowerLeft ) ) return true;
        if (circleIntersectsLine(circle, lowerLeft, lowerRight) ) return true;
        if (circleIntersectsLine(circle, upperLeft, upperRight) ) return true;
        if (circleIntersectsLine(circle, upperRight, lowerRight) ) return true;

        // no collision
        return false;
	}

	/**
	 * Determine if the Circle intersects the {@link Line}.
	 *
	 * @param circle
	 * @param line
	 * @return
	 */
	public static boolean circleIntersectsLine(Circle circle, Line line) {
		return circleIntersectsLine(circle, line.a, line.b);
	}

	/**
	 * Determine if the Circle intersects the line segment.
	 *
	 * @param circle
	 * @param line
	 * @return
	 */
	public static boolean circleIntersectsLine(Circle circle, Vector2f a, Vector2f b) {
		Vector2f ca = new Vector2f();
		Vector2f cb = new Vector2f();

		// translate to origin
		Vector2fSubtract(a, circle.origin, ca);
		Vector2fSubtract(b, circle.origin, cb);

		Vector2f d = new Vector2f();
		Vector2fSubtract(cb, ca, d);

		float dr = Vector2fLengthSq(d);
		float det = Vector2fDet(ca, cb);

		// only the discriminant matters:
		float delta = circle.radius * circle.radius * dr - (det*det);

		/*
		 * 	Delta<0	no intersection
		 *	Delta=0	tangent
		 *	Delta>0	intersection
		 *
		 */
		return delta >= 0;

	}
}
