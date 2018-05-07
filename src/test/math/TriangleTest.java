package test.math;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.math.Rectangle;

import seventh.client.gfx.ReticleCursor;
import seventh.math.Line;
import seventh.math.Triangle;
import seventh.math.Vector2f;

public class TriangleTest {

    Triangle tri;

    /*
     * Purpose: Test Triangle Construcor Test. Input:
     * Triangle((1,2),(4,5),(10,11)) Expected: Triangle.a.x = 1 , Triangle.a.y =
     * 2, Triangle.b.x = 4 , Triangle.b.y = 5, Triagle.c.x = 10 , Triangle.c.y =
     * 11
     */

    @Test
    public void ConstructTest() {

        
        tri = new Triangle(new Vector2f(1, 2),new Vector2f(4, 5),new Vector2f(10, 11));

        assertTrue(tri.a.x == 1);
        assertTrue(tri.a.y == 2);
        assertTrue(tri.b.x == 4);
        assertTrue(tri.b.y == 5);
        assertTrue(tri.c.x == 10);
        assertTrue(tri.c.y == 11);

    }

    /*
     * Purpose: Test Triangle add all x + input.x , y + input.y. Input:
     * Vector2f(1,1) Expected: Triangle.a.x = 2 , Triangle.a.y = 3, Triangle.b.x
     * = 5 , Triangle.b.y = 6, Triangle.c.x = 11 , Triangle.c.y = 12
     * 
     */
    @Test
    public void TranslateAddTest() {

        tri = new Triangle(new Vector2f(1, 2),new Vector2f(4, 5),new Vector2f(10, 11));

        Vector2f tmp = new Vector2f(1, 1);
        tri.translate(tmp);

        assertTrue(tri.a.x == 2);
        assertTrue(tri.a.y == 3);
        assertTrue(tri.b.x == 5);
        assertTrue(tri.b.y == 6);
        assertTrue(tri.c.x == 11);
        assertTrue(tri.c.y == 12);
    }

    /*
     * Purpose: Test Triangle sub all x - input.x , y - input.y. Input:
     * Vector2f(-1,-1) Expected: Triangle.a.x = 0 , Triangle.a.y = 1,
     * Triangle.b.x = 3 , Triangle.b.y = 4, Triangle.c.x = 9 , Triangle.c.y = 10
     * 
     */
    @Test
    public void TranslatesubTest() {

        tri = new Triangle(new Vector2f(1, 2),new Vector2f(4, 5),new Vector2f(10, 11));

        Vector2f tmp = new Vector2f(-1, -1);
        tri.translate(tmp);

        assertTrue(tri.a.x == 0);
        assertTrue(tri.a.y == 1);
        assertTrue(tri.b.x == 3);
        assertTrue(tri.b.y == 4);
        assertTrue(tri.c.x == 9);
        assertTrue(tri.c.y == 10);
    }

    /*
     * Purpose: Test Triangle add all x + inputFloatValue , y + inputFloatValue
     * Input: Float 11 Expected: Triangle.a.x = 2 , Triangle.a.y = 3,
     * Triangle.b.x = 5 , Triangle.b.y = 6, Triangle.c.x = 11 , Triangle.c.y =
     * 12
     * 
     */
    @Test
    public void TranslateFloatAddTest() {

        tri = new Triangle(new Vector2f(1, 2),new Vector2f(4, 5),new Vector2f(10, 11));

        float tmp = 1;
        tri.translate(tmp);

        assertTrue(tri.a.x == 2);
        assertTrue(tri.a.y == 3);
        assertTrue(tri.b.x == 5);
        assertTrue(tri.b.y == 6);
        assertTrue(tri.c.x == 11);
        assertTrue(tri.c.y == 12);
    }

    /*
     * Purpose: Test Triangle sub all x - inputFloatValue , y - inputFloatValue.
     * Input: -1 
     * Expected: Triangle.a.x = 0 , Triangle.a.y = 1, Triangle.b.x = 3, Triangle.b.y = 4, Triangle.c.x = 9 , Triangle.c.y = 10
     * 
     */
    @Test
    public void TranslateFloatsubTest() {

        tri = new Triangle(new Vector2f(1, 2),new Vector2f(4, 5),new Vector2f(10, 11));

        // Construct Float value
        float tmp = -1;
        tri.translate(tmp);

        assertTrue(tri.a.x == 0);
        assertTrue(tri.a.y == 1);
        assertTrue(tri.b.x == 3);
        assertTrue(tri.b.y == 4);
        assertTrue(tri.c.x == 9);
        assertTrue(tri.c.y == 10);
    }

    /*
     * Purpose: Test Translates the link Triangle to a new location. 
     * Input: Triangle, vector2f, dest Expected: TransFormed Dest -> Triangle
     */
    @Test
    public void TriangleTranslateFloatTest() {

        tri = new Triangle(new Vector2f(1, 2),new Vector2f(4, 5),new Vector2f(10, 11));
        Triangle dest = new Triangle(new Vector2f(3, 3), new Vector2f(7, 3), new Vector2f(5, 10));

        float value =1;
        tri.TriangleTranslate(tri, value, dest);
        assertTrue(dest.a.x == 2);
        assertTrue(dest.a.y == 3);
        assertTrue(dest.b.x == 5);
        assertTrue(dest.b.y == 6);
        assertTrue(dest.c.x == 11);
        assertTrue(dest.c.y == 12);
    }

    /*
     * Purpose: Test Translates the link Triangle to a new location. 
     * Input:Triangle, vector2f, dest 
     * Expected: TransFormed Dest's Value is same with inputTriangle
     */
    @Test
    public void TriangleTranslateVector2fTest() {
        
        tri = new Triangle(new Vector2f(1, 2),new Vector2f(4, 5),new Vector2f(10, 11));
        Triangle dest = new Triangle(new Vector2f(3, 3), new Vector2f(7, 3), new Vector2f(5, 10));


        dest.TriangleTranslate(tri, new Vector2f(1, 1), dest);
        assertTrue(dest.a.x == 2);
        assertTrue(dest.a.y == 3);
        assertTrue(dest.b.x == 5);
        assertTrue(dest.b.y == 6);
        assertTrue(dest.c.x == 11);
        assertTrue(dest.c.y == 12);
    }
    
    /*
     * Purpose: Test to determine if the supplied Point intersects with the supplied Triangle
     * Input: 3 3, triangle(2,2 ,2 15, 10,2)
     * Expected: true
     */
    @Test
    public void pointIntersectsTriangleTrueTest() {
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));

        Vector2f point= new Vector2f(3,3);
        assertTrue(true == tri.pointIntersectsTriangle(point, tri));
    
    }

    /*
     * Purpose: Test to determine if the supplied Point intersects with the supplied Triangle}
     * Input: -1 -1, triangle(2,2 ,2 15, 10,2)
     * Expected: flase
     */
    @Test
    public void pointIntersectsTriangleFalseTest() {

        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        Vector2f point= new Vector2f(-1,-1);

        //Out Of triangle
        assertTrue(false == tri.pointIntersectsTriangle(point, tri));
    
    }
    
    
    /*
     * PurPose: Test Determine if the supplied link Line intersects with the supplied link Triangle.
     * Input: Line((1,1),(3,3)),Line((3,3),(1,2)),Line((3,3),(2,1)),Line((3,3),(10,10)),Line((3,3),(10,1)),Line((3,3),(2,15))
     * Expected: true
     * 
     */
    @Test
    public void lineIntersectsTriangleLineTrueTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        Line line1 = new Line(new Vector2f(3,3),new Vector2f(1,2));
        Line line2 = new Line(new Vector2f(3,3),new Vector2f(2,1));
        Line line3 = new Line(new Vector2f(3,3),new Vector2f(10,10));

        
        assertTrue(true == tri.lineIntersectsTriangle(line1, tri));
        assertTrue(true == tri.lineIntersectsTriangle(line2, tri));
        assertTrue(true == tri.lineIntersectsTriangle(line3, tri));

    }
    

    /*
     * PurPose: Test Determine if the supplied link Line intersects with the supplied link Triangle.
     * Input: Line((0,0),(1,1))
     * Expected: false
     * 
     */
    @Test
    public void lineIntersectsTriangleLineFalseTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        Line line1 = new Line(new Vector2f(0,0),new Vector2f(1,1));        
        assertTrue(false == tri.lineIntersectsTriangle(line1, tri));
    }
    
    
    /*
     * PurPose: Test Determine if the supplied link Line intersects with the supplied link Triangle
     * Input: Point((1,1) and (3,3)),Point((3,3) and (1,2)),Point((3,3) and(2,1)),Point((3,3) and (10,10)),Point ((3,3)and (10,1)),point((3,3) and (2,15))
     * Expected: true
     * 
     */
    @Test
    public void lineIntersectsTrianglePointTrueTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));

        assertTrue(true == tri.lineIntersectsTriangle(new Vector2f(3,3),new Vector2f(1,2), tri));
        assertTrue(true == tri.lineIntersectsTriangle(new Vector2f(3,3),new Vector2f(2,1), tri));
        assertTrue(true == tri.lineIntersectsTriangle(new Vector2f(3,3),new Vector2f(10,10), tri));
    }
    
    /*
     * PurPose: Test Determine if the supplied link Line intersects with the supplied link Triangle
     * Input: Point((0,0) and (1,1))
     * Expected: false
     * 
     */
    @Test
    public void lineIntersectsTrianglePointFalseTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        assertTrue(false == tri.lineIntersectsTriangle(new Vector2f(1,1),new Vector2f(0,0), tri));
    }
    
    
    
    
    /*
     * PurPose: Test Determine if the supplied link Rectangle and link Triangle intersect. -> Not Intersect
     * Input: Rectangle((2,2),8,3), Triangle((2,2),(2,15),(10,2)) //inside
     * Input: Rectangle((0,2),0,3), Triangle((2,2),(2,15),(10,2)) //width = 0
     * Input: Rectangle((1,0),3,0), Triangle((2,2),(2,15),(10,2)) //height =0
     * Input: Rectangle((0,0),20,20), Triangle((2,2),(2,15),(10,2)) //outside
     * Expected: false
     * 
     */
    @Test  //Inside
    public void rectangleIntersectsTriangleFalseTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(2, 2),(int)8,(int)13);
        
        assertTrue(false == tri.rectangleIntersectsTriangle(rectangle,tri));
    }
    @Test  //width =0
    public void rectangleIntersectsTriangleFalseHeighZeroTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(0, 2),(int)0,(int)13);
        
        assertTrue(false == tri.rectangleIntersectsTriangle(rectangle,tri));
    }
    @Test  //height =0
    public void rectangleIntersectsTriangleFalseWidthZeroTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(1, 0),(int)3,(int)0);
        
        assertTrue(false == tri.rectangleIntersectsTriangle(rectangle,tri));
    }    
    @Test  //OutSide
    public void rectangleIntersectsTriangleFalseOutsideUnderTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(2, 0),(int)8,(int)0);
        
        assertTrue(false == tri.rectangleIntersectsTriangle(rectangle,tri));
    }
    
    
    
    
    /*
     * PurPose: Test Determine if the supplied link Rectangle and link Triangle intersect. -> Intersect
     * Input: Rectangle((1,1),10,6), Triangle((2,2),(2,15),(10,2)) - All direction Line intersect
     * Input: Rectangle((3,1),2,3), Triangle((2,2),(2,15),(10,2))  -Under Line intersect
     * Input: Rectangle((1,3),4,4), Triangle((2,2),(2,15),(10,2)) -Height Line intersect
     * Input: Rectangle((3,3),15,15), Triangle((2,2),(2,15),(10,2)) -Hypotenuse Line intersect
     * Input: Rectangle((1,3),15,0), Triangle((2,2),(2,15),(10,2)) -Rectangle Height = 0
     * Input: Rectangle((3,1),0,15), Triangle((2,2),(2,15),(10,2)) -Rectangle width = 0
     * Input: Rectangle((-5,-5),50,50), Triangle((2,2),(2,15),(10,2)) - Triangle Located In Rectangle 

     * Expected: True
     * 
     */
    @Test //All Direction Intersect
    public void rectangleIntersectsTriangleTrueAllLineTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(1, 1),(int)10,(int)6);
        
        assertTrue(true == tri.rectangleIntersectsTriangle(rectangle,tri));
    }
    @Test //Under Line Intersect 
    public void rectangleIntersectsTriangleTrueUnderLineTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(3, 1),(int)2,(int)3);
        assertTrue(true == tri.rectangleIntersectsTriangle(rectangle,tri));
    }    
    
    @Test //Height Line Intersect
    public void rectangleIntersectsTriangleTrueHeightLineTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(1, 3),6,4);
        assertTrue(true == tri.rectangleIntersectsTriangle(rectangle,tri));
    }
    @Test //Hypotenuse Line InterSect
    public void rectangleIntersectsTriangleTrueHypotenuseLineTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(3, 3),15,15);
        assertTrue(true == tri.rectangleIntersectsTriangle(rectangle,tri));
    }
    
    @Test //Width Zero Rectangle InterSect
    public void rectangleIntersectsTriangleTrueWidthZeroTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(1, 3),15,0);
        assertTrue(true == tri.rectangleIntersectsTriangle(rectangle,tri));
    }    
    @Test //Height Zero Rectangle InterSect
    public void rectangleIntersectsTriangleTrueHeigthZeroTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(3, 1),0,15);
        assertTrue(true == tri.rectangleIntersectsTriangle(rectangle,tri));
    }
    
    @Test //Height Zero Rectangle InterSect
    public void rectangleIntersectsTriangleTrueRectangleInsideTest(){
        
        tri = new Triangle(new Vector2f(2, 2), new Vector2f(2, 15), new Vector2f(10, 2));
        seventh.math.Rectangle rectangle = new seventh.math.Rectangle(new Vector2f(-5, -5),50,50);
        assertTrue(true == tri.rectangleIntersectsTriangle(rectangle,tri));
    }

}
