package com.Bubbles.Java3D;

import java.awt.Color;
import java.awt.Graphics;

/**
 * http://www3.ntu.edu.sg/home/ehchua/programming/java/J8a_GameIntro-BouncingBalls.html
 * 
 * An Polygon Obstacle.
 * 
 * @author Hock-Chuan Chua
 * @version October 2010
 */
public class ObstaclePolygon {
   int[] xPoints;  // x-coordinate of the points 
   int[] yPoints;  // y-coordinate of the points
   Color color;    // Polygon's filled color
   
   /** Constructors */
   public ObstaclePolygon(int[] xPoints, int[] yPoints, Color color) {
      this.xPoints = xPoints;
      this.yPoints = yPoints;
      this.color = color;
   }
   
   /** Constructor with the default color */
   public ObstaclePolygon(int[] xPoints, int[] yPoints, int numPoints) {
      this(xPoints, yPoints, Color.YELLOW);
   }

   /** Draw itself using the given graphic context. */
   public void draw(Graphics g) {
      g.setColor(color);
      g.fillPolygon(xPoints, yPoints, xPoints.length);
   }
}
