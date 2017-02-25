/*
 *    leola-live 
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

    public static boolean circleContainsPoint(Circle circle, Vector2f a) {
        return circleContainsPoint(circle.origin.x, circle.origin.y, circle.radius, a.x, a.y);
    }
    
    /**
     * Determines if the point is in the circle
     * 
     * @param cx
     * @param cy
     * @param radius
     * @param x
     * @param y
     * @return
     */
    public static boolean circleContainsPoint(float cx, float cy, float radius, float x, float y) {
        float distSq = (cx - x) * 2 + (cy - y) * 2;
        return distSq <= radius*radius;
    }
    
    /**
     * Check and see if the circle is contained within the supplied rectangle.
     *
     * @param circle
     * @param rect
     * @return
     */
    public static boolean circleContainsRect(Circle circle, Rectangle rect) {
        return circleContainsRect(circle.origin.x, circle.origin.y, circle.radius, rect);
    }

    /**
     * Check and see if the circle is contained within the supplied rectangle.
     *
     * @param circle
     * @param rect
     * @return
     */
    public static boolean circleContainsRect(float cx, float cy, float radius, Rectangle rect) {
        int x = rect.getX();
        int y = rect.getY();
        int width = rect.getWidth();
        int height = rect.getHeight();
        
        // check if it is inside
        return (cx > x && cx < x+width &&
                cy < y+height && cy > y );
    }
    

    /**
     * Determine if the supplied {@link Circle} intersects the {@link Rectangle}.
     *
     * @param circle
     * @param rect
     * @return
     */
    public static boolean circleIntersectsRect(Circle circle, Rectangle rect) {
        return circleIntersectsRect(circle.origin.x, circle.origin.y, circle.radius, rect);
    }

    /**
     * Determine if the supplied {@link Circle} intersects the {@link Rectangle}.
     *
     * @param circle
     * @param rect
     * @return
     */
    public static boolean circleIntersectsRect(float cx, float cy, float radius, Rectangle rect) {
        float circleDistanceX = Math.abs(cx - rect.x);
        float circleDistanceY = Math.abs(cy - rect.y);

        if (circleDistanceX > (rect.width/2 + radius)) { return false; }
        if (circleDistanceY > (rect.height/2 + radius)) { return false; }

        if (circleDistanceX <= (rect.width/2)) { return true; } 
        if (circleDistanceY <= (rect.height/2)) { return true; }

        float cornerDistanceSq = (circleDistanceX - rect.width/2) * (circleDistanceX - rect.width/2) +
                                 (circleDistanceY - rect.height/2) * (circleDistanceY - rect.height/2);

        return (cornerDistanceSq <= (radius * radius));
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
         *     Delta<0    no intersection
         *    Delta=0    tangent
         *    Delta>0    intersection
         *
         */
        return delta >= 0;

    }
}
