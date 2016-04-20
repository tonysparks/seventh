/*
 *	leola-live 
 *  see license.txt
 */
package seventh.math;

/**
 * A {@link Rectangle} which is very similar to Java's and LWJGL's implementation, however with Android
 * development a developer may not want to include LWJGL on their classpath.
 * 
 * <p>
 * Most of the code is taken from the LWJGL Rectangle implementation.
 * 
 * @author Tony
 *
 */
public class Rectangle {

	/**
	 * X Component
	 */
	public int x;
	
	/**
	 * Y Component
	 */
	public int y;
	
	/**
	 * Width
	 */
	public int width;
	
	/**
	 * Height
	 */
	public int height;

	/**
	 * @param b
	 * @param width
	 * @param height
	 */
	public Rectangle(Vector2f b, int width, int height) {		
		this.x = (int)b.x;
		this.y = (int)b.y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Rectangle(int x, int y, int width, int height) {		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	

	/**
	 * @param width
	 * @param height
	 */
	public Rectangle(int width, int height) {
		this(0,0,width, height);		
	}

	/**
	 * @param rect
	 */
	public Rectangle(Rectangle rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}
	
	/**
	 */
	public Rectangle() {
		this(0,0,0,0);
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
		
	
	/**
	 * Sets the components.  Equivalent to setBounds
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void set(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Sets the components. Equivalent to set
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	
	/**
	 * Sets the components.  Equivalent to setBounds
	 * 
	 * @param b
	 * @param width
	 * @param height
	 */
	public void set(Vector2f b, int width, int height) {
		this.x = (int)b.x;
		this.y = (int)b.y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Sets the components. Equivalent to set
	 * 
	 * @param b
	 * @param width
	 * @param height
	 */
	public void setBounds(Vector2f b, int width, int height) {
		this.x = (int)b.x;
		this.y = (int)b.y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Sets the components, same as set
	 * 
	 * @param b
	 */
	public void setBounds(Rectangle b) {
		this.x = b.x;
		this.y = b.y;
		this.width = b.width;
		this.height = b.height;
	}
	
	/**
	 * Sets the components
	 * 
	 * @param b
	 */
	public void set(Rectangle b) {
		this.x = b.x;
		this.y = b.y;
		this.width = b.width;
		this.height = b.height;
	}
	
	/**
	 * Zero outs all components.
	 */
	public void zeroOut() {
		this.x = this.y = this.width = this.height = 0;
	}
	
	/**
	 * Sets the x and y components
	 * @param x
	 * @param y
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets the x and y components
	 * @param v
	 */
	public void setLocation(Vector2f v) {
		this.x = (int)v.x;
		this.y = (int)v.y;
	}
	
	/**
	 * Sets the x and y to match the supplied rectangle.
	 * @param r
	 */
	public void setLocation(Rectangle r) {
		this.x = r.x;
		this.y = r.y;
	}
	
	/**
	 * Sets the width and height
	 * @param w
	 * @param h
	 */
	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
	}
	
	/**
	 * Sets the width and height to match the supplied rectangle.
	 * @param r
	 */
	public void setSize(Rectangle r) {
		this.width = r.width;
		this.height = r.height;
	}
	
	
	/**
	 * Centers around the position
	 * @param pos
	 */
	public void centerAround(Vector2f pos) {
		this.x = (int)pos.x - (this.width/2);
		this.y = (int)pos.y - (this.height/2);
	}
	
	/**
	 * Centers around the position
	 * @param x
	 * @param y
	 */
	public void centerAround(int x, int y) {
		this.x = x - (this.width/2);
		this.y = y - (this.height/2);
	}
	
	/**
	 * @see Rectangle#RectangleIntersection(Rectangle, Rectangle, Rectangle)
	 * 
	 * @param b
	 * @return the intersection rectangle
	 */
	public Rectangle intersection(Rectangle b) {
		Rectangle dest = new Rectangle();
		RectangleIntersection(this, b, dest);
		return dest;
	}
	
	/**
	 * Tests for intersection
	 * 
	 * @param b
	 * @return true if this rectangle intersects b
	 */
	public boolean intersects(Rectangle b) {
		return RectangleIntersects(this, b);
	}
	
	/**
	 * Tests to see if this contains b
	 * @param b
	 * @return true if this contains b
	 */
	public boolean contains(Rectangle b) {
		return RectangleContains(this, b);
	}
	
	/**
	 * Tests to see if this contains b
	 * @param b
	 * @return true if this contains b
	 */
	public boolean contains(Vector2f b) {
		return RectangleContains(this, b); 
	}
	
	/**
	 * Tests to see if this contains b
	 * @param x
	 * @param y
	 * @return true if this contains the x & y
	 */
	public boolean contains(int x, int y) {
		return RectangleContains(this, x, y); 
	}
	
	/**
	 * If this {@link Rectangle} contains the {@link OBB}
	 * @param oob
	 * @return true if it contains it
	 */
	public boolean contains(OBB oob) {
		return RectangleContains(this, oob.topLeft) &&
			   RectangleContains(this, oob.topRight) &&
			   RectangleContains(this, oob.bottomRight) &&
			   RectangleContains(this, oob.bottomLeft);
	}
	
	/**
	 * Adds b to this.
	 * @param b
	 */
	public void add(Vector2f b) {
		RectangleAdd(this, b, this);
	}
	
	/**
	 * Adds x and y to this
	 * @param x
	 * @param y
	 */
	public void add(int x, int y) {
		RectangleAdd(this, x, y, this);
	}
	
	/**
	 * Adds the x and y components to this rectangle
	 * 
	 * <pre>
	 * RectangleAdd(this, b, this);
	 * </pre>
	 * 
	 * @see Rectangle#RectangleAdd(Rectangle, Rectangle, Rectangle)
	 * 
	 * @param b
	 */
	public void add(Rectangle b) {
		RectangleAdd(this, b, this);
	}
	
	/*
	 * The below is from the LWJGL implementation.
	 */
	
	/* 
	 * Copyright (c) 2002-2008 LWJGL Project
	 * All rights reserved.
	 * 
	 * Redistribution and use in source and binary forms, with or without
	 * modification, are permitted provided that the following conditions are 
	 * met:
	 * 
	 * * Redistributions of source code must retain the above copyright 
	 *   notice, this list of conditions and the following disclaimer.
	 *
	 * * Redistributions in binary form must reproduce the above copyright
	 *   notice, this list of conditions and the following disclaimer in the
	 *   documentation and/or other materials provided with the distribution.
	 *
	 * * Neither the name of 'LWJGL' nor the names of 
	 *   its contributors may be used to endorse or promote products derived 
	 *   from this software without specific prior written permission.
	 * 
	 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
	 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
	 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
	 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
	 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
	 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
	 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
	 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
	 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
	 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 */	
	
	/**
	 * Checks whether two rectangles are equal.
	 * <p>
	 * The result is <code>true</code> if and only if the argument is not 
	 * <code>null</code> and is a <code>Rectangle</code> object that has the 
	 * same top-left corner, width, and height as this <code>Rectangle</code>. 
	 * @param obj the <code>Object</code> to compare with
	 *                this <code>Rectangle</code>
	 * @return    <code>true</code> if the objects are equal; 
	 *            <code>false</code> otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Rectangle) {
			Rectangle r = (Rectangle) obj;
			return ((x == r.x) && (y == r.y) && (width == r.width) && (height == r.height));
		}
		return super.equals(obj);
	}

	/**
	 * Debugging
	 * @return a String
	 */
	public String toString() {
		return "{ \"x\" : " + x + ", \"y\" : " + y + ", \"width\" : " + width + ", \"height\" : " + height + " }";
	}
	
	/**
	 * Computes the intersection of this <code>Rectangle</code> with the 
	 * specified <code>Rectangle</code>. Returns a new <code>Rectangle</code> 
	 * that represents the intersection of the two rectangles.
	 * If the two rectangles do not intersect, the result will be
	 * an empty rectangle.
	 *
	 * @param     r   the specified <code>Rectangle</code>
	 * @param    dest the largest <code>Rectangle</code> contained in both the 
	 *            specified <code>Rectangle</code> and in 
	 *		  this <code>Rectangle</code>; or if the rectangles
	 *            do not intersect, an empty rectangle.
	 */
	public static void RectangleIntersection(Rectangle a, Rectangle b, Rectangle dest) {
		int tx1 = a.x;
		int ty1 = a.y;
		int rx1 = b.x;
		int ry1 = b.y;
		long tx2 = tx1;
		tx2 += a.width;
		long ty2 = ty1;
		ty2 += a.height;
		long rx2 = rx1;
		rx2 += b.width;
		long ry2 = ry1;
		ry2 += b.height;
		if (tx1 < rx1)
			tx1 = rx1;
		if (ty1 < ry1)
			ty1 = ry1;
		if (tx2 > rx2)
			tx2 = rx2;
		if (ty2 > ry2)
			ty2 = ry2;
		tx2 -= tx1;
		ty2 -= ty1;
		// tx2,ty2 will never overflow (they will never be
		// larger than the smallest of the two source w,h)
		// they might underflow, though...
		if (tx2 < Integer.MIN_VALUE)
			tx2 = Integer.MIN_VALUE;
		if (ty2 < Integer.MIN_VALUE)
			ty2 = Integer.MIN_VALUE;
		
		dest.set(tx1, ty1, (int) tx2, (int) ty2);		
	}
	
	/**
	 * Determines if two {@link Rectangle} intersect.
	 * @param a
	 * @param b
	 * @return true if a intersects b
	 */
	public static boolean RectangleIntersects(Rectangle a, Rectangle b) {
		int tw = a.width;
		int th = a.height;
		int rw = b.width;
		int rh = b.height;
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		int tx = a.x;
		int ty = a.y;
		int rx = b.x;
		int ry = b.y;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		//      overflow || intersect
		return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
	}		
	/**
	 * Determines if a contains b
	 * 
	 * @param a
	 * @param b
	 * @return true if a contains b
	 */
	public static boolean RectangleContains(Rectangle a, Rectangle b) {		
		int X = b.x; 
		int Y = b.y;
		int W = b.width;
		int H = b.height;
		
		int w = a.width;
		int h = a.height;
		if ((w | h | W | H) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if any dimension is zero, tests below must return false...
		int x = a.x;
		int y = a.y;
		if (b.x < x || b.y < y) {
			return false;
		}
		w += x;
		W += X;
		if (W <= X) {
			// X+W overflowed or W was zero, return false if...
			// either original w or W was zero or
			// x+w did not overflow or
			// the overflowed x+w is smaller than the overflowed X+W
			if (w >= x || W > w)
				return false;
		} else {
			// X+W did not overflow and W was not zero, return false if...
			// original w was zero or
			// x+w did not overflow and x+w is smaller than X+W
			if (w >= x && W > w)
				return false;
		}
		h += y;
		H += Y;
		if (H <= Y) {
			if (h >= y || H > h)
				return false;
		} else {
			if (h >= y && H > h)
				return false;
		}
		return true;
	}
	
	/**
	 * If the supplied rectangle contains a point
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean RectangleContains(Rectangle a, int bx, int by) {
		int w = a.width;
		int h = a.height;
		if ((w | h) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if either dimension is zero, tests below must return false...
		int x = a.x;
		int y = a.y;
		if (bx < x || by < y) {
			return false;
		}
		w += x;
		h += y;
		//    overflow || intersect
		return ((w < x || w > bx) && (h < y || h > by));
	}
	
	/**
	 * If the supplied rectangle contains a point
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean RectangleContains(Rectangle a, Vector2f b) {
		int w = a.width;
		int h = a.height;
		if ((w | h) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if either dimension is zero, tests below must return false...
		int x = a.x;
		int y = a.y;
		if (b.x < x || b.y < y) {
			return false;
		}
		w += x;
		h += y;
		//    overflow || intersect
		return ((w < x || w > b.x) && (h < y || h > b.y));
	}
	
	/**
	 * Adds to the Rectangle
	 * 
	 * @param a
	 * @param b
	 * @param dest - result
	 */
	public static void RectangleAdd(Rectangle a, Vector2f b, Rectangle dest) {
		dest.x = a.x + (int)b.x;
		dest.y = a.y + (int)b.y;
	}
	
	/**
	 * Adds to the rectangle
	 * @param a
	 * @param x
	 * @param y
	 * @param dest - result
	 */
	public static void RectangleAdd(Rectangle a, int x, int y, Rectangle dest) {
		dest.x = a.x + x;
		dest.y = a.y + y;
	}
	
	
	/**
	 * Adds the x & y component of Rectangle b to Rectangle a and stores it in dest.
	 * @param a
	 * @param b
	 * @param dest - the result
	 */
	public static void RectangleAdd(Rectangle a, Rectangle b, Rectangle dest) {
		dest.x = a.x + b.x;
		dest.y = a.y + b.y;
	}
}
