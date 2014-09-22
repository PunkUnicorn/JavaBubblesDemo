package com.Bubbles.Java3D;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Formatter;
//import collisionphysics.*;

/**
 * The bouncing ball.
 * 
 * @author Hock-Chuan Chua
 * @version v0.4 (31 October 2010)
 */
public class Ball  {
   int id;				 // Balls unique number
   float x, y;           // Ball's center x and y (package access)
   float speedX, speedY; // Ball's speed per step in x and y (package access)
   int threatCount;	 	 // count of how many times this occurs in the threat list
   float radius;         // Ball's radius (package access)
   private Color color;  // Ball's color
   private static final Color DEFAULT_COLOR = Color.BLUE;
   static boolean showBubbles = false;
   static boolean hollowWhenThreatened = false;
   static boolean indicateThreats = false;
   static String target = "";

   public float CalculateCollisionRadius() {
	   float speedFactor = (Math.max(Math.abs(speedX), Math.abs(speedY)) + 1) * 1.5f;
	   speedFactor *= 5f;
	   return (radius * 1.5f) + speedFactor; 
   }
   // For collision detection and response
   // Maintain the response of the earliest collision detected 
   //  by this ball instance. Only the first collision matters! (package access)
   CollisionResponse earliestCollisionResponse = new CollisionResponse();

   // For a broad phase collision detection
   
   /**
    * http://www3.ntu.edu.sg/home/ehchua/programming/java/J8a_GameIntro-BouncingBalls.html
    * 
    * Constructor: For user friendliness, user specifies velocity in speed and
    * moveAngle in usual Cartesian coordinates. Need to convert to speedX and
    * speedY in Java graphics coordinates for ease of operation.
    */
   public Ball(int id, float x, float y, float radius, float speed, int speedPercent, float angleInDegree, Color color) 
   {
	  this.id = id;
      this.x = x;
      this.y = y;
      
      // Convert (speed, angle) to (x, y), with y-axis inverted
      this.speedX = (float)(speed * Math.cos(Math.toRadians(angleInDegree)));      
      this.speedY = (float)(-speed * (float)Math.sin(Math.toRadians(angleInDegree)));
      
      this.speedX = (this.speedX / 100) * speedPercent;
      this.speedY = (this.speedY / 100) * speedPercent;
      
      this.radius = radius;
      this.color = color;
   }
   /** Constructor with the default color */
   public Ball(int id, float x, float y, float radius, float speed, int speedPercent, float angleInDegree) {
      this(id, x, y, radius, speed, speedPercent, angleInDegree, DEFAULT_COLOR);
   }

   // Working copy for computing response in intersect(), 
   // to avoid repeatedly allocating objects.
   private CollisionResponse tempResponse = new CollisionResponse(); 

   /**
    * Check if this ball collides with the container box in the coming time-step.
    * 
    * @param box: outer rectangular container.
    * @param timeLimit: upperbound of the time interval.
    */
   public void intersect(ContainerBox box, float timeLimit) {
      // Call movingPointIntersectsRectangleOuter, which returns the 
      // earliest collision to one of the 4 borders, if collision detected.
      CollisionPhysics.pointIntersectsRectangleOuter(
            this.x, this.y, this.speedX, this.speedY, this.radius,
            box.minX, box.minY, box.maxX, box.maxY,
            timeLimit, tempResponse);
      if (tempResponse.t < earliestCollisionResponse.t) {
         earliestCollisionResponse.copy(tempResponse);
      }
   }
   
   // Working copy for computing response in intersect(Ball, timeLimit), 
   // to avoid repeatedly allocating objects.
   private CollisionResponse thisResponse = new CollisionResponse(); 
   private CollisionResponse anotherResponse = new CollisionResponse();
   ArrayList<Ball> threatList = new ArrayList<Ball>(); 

   /**
    * Check if this ball collides with the given another ball in the interval 
    * (0, timeLimit].
    * 
    * @param another: another moving ball to be checked for collision.
    * @param timeLimit: upperbound of the time interval.
    */
   public void intersect(Ball another, float timeLimit) {
      // Call movingPointIntersectsMovingPoint() with timeLimit.
      // Use thisResponse and anotherResponse, as the working copies, to store the
      // responses of this ball and another ball, respectively.
      // Check if this collision is the earliest collision, and update the ball's
      // earliestCollisionResponse accordingly.
      CollisionPhysics.pointIntersectsMovingPoint(
            this.x, this.y, this.speedX, this.speedY, this.radius,
            another.x, another.y, another.speedX, another.speedY, another.radius,
            timeLimit, thisResponse, anotherResponse);
      
      if (anotherResponse.t < another.earliestCollisionResponse.t) {
         another.earliestCollisionResponse.copy(anotherResponse);
      }
      if (thisResponse.t < earliestCollisionResponse.t) {
         earliestCollisionResponse.copy(thisResponse);
      }
   }

   /**
    * Check if this ball collides with the given BlockPolygon in the interval 
    * (0, timeLimit].
    * 
    * @param polygon: the polygon-shape obstacle.
    * @param timeLimit: upperbound of the time interval.
    */
   public void intersect(ObstaclePolygon polygon, float timeLimit) {
      int numPoints = polygon.xPoints.length;
      CollisionPhysics.pointIntersectsPolygon(
            this.x, this.y, this.speedX, this.speedY, this.radius, 
            polygon.xPoints, polygon.yPoints, numPoints,
            timeLimit, tempResponse);
      if (tempResponse.t < earliestCollisionResponse.t) {
         earliestCollisionResponse.copy(tempResponse);
      }
   }

   /**
    * Check if this ball collides with the given BlockLine in the interval 
    * (0, timeLimit]. A line has no end points (infinite length).
    * 
    * @param line: the line-shape obstacle.
    * @param timeLimit: upperbound of the time interval.
    */
   public void intersect(ObstacleLine line, float timeLimit) {
      CollisionPhysics.pointIntersectsLine(
            this.x, this.y, this.speedX, this.speedY, this.radius, 
            line.x1, line.y1, line.x2, line.y2,
            timeLimit, tempResponse);
      if (tempResponse.t < earliestCollisionResponse.t) {
         earliestCollisionResponse.copy(tempResponse);
      }
   }
   
   /**
    * Check if this ball collides with the given BlockLineSegment in the interval 
    * (0, timeLimit]. A line segment has two definite end-points.
    * 
    * @param lineSegment: the line-shape obstacle.
    * @param timeLimit: upperbound of the time interval.
    */
   public void intersect(ObstacleLineSegment lineSegment, float timeLimit) {
      // Check the line segment for probable collision.
      CollisionPhysics.pointIntersectsLineSegment(
            this.x, this.y, this.speedX, this.speedY, this.radius, 
            lineSegment.x1, lineSegment.y1, lineSegment.x2, lineSegment.y2,
            timeLimit, tempResponse);
      if (tempResponse.t < earliestCollisionResponse.t) {
            earliestCollisionResponse.copy(tempResponse);
      }
   }
   
   /**
    * Check if this ball collides with the given Circle in the interval 
    * (0, timeLimit].
    * 
    * @param circle: the circle-shape obstacle.
    * @param timeLimit: upperbound of the time interval.
    */
   public void intersect(ObstacleCircle circle, float timeLimit) {
      CollisionPhysics.pointIntersectsPoint(
            this.x, this.y, this.speedX, this.speedY, this.radius,
            circle.centerX, circle.centerY, circle.radius,
            timeLimit, tempResponse);
      if (tempResponse.t < earliestCollisionResponse.t) {
         earliestCollisionResponse.copy(tempResponse);
      }
   }
   
   /** 
    * Update the states of this ball for the given time, where time <= 1.
    * 
    * @param time: the earliest collision time detected in the system.
    *    If this ball's earliestCollisionResponse.time equals to time, this
    *    ball is the one that collided; otherwise, there is a collision elsewhere.
    */
   public boolean update(float time) {
	  boolean involvedInCollision = false;
      // Check if this ball is responsible for the first collision?
      if (earliestCollisionResponse.t <= time) { // FIXME: threshold?
         // This ball collided, get the new position and speed
         this.x = earliestCollisionResponse.getNewX(this.x, this.speedX);
         this.y = earliestCollisionResponse.getNewY(this.y, this.speedY);
         this.speedX = earliestCollisionResponse.newSpeedX;
         this.speedY = earliestCollisionResponse.newSpeedY;
         involvedInCollision = true;
      } else {
         // This ball does not involve in a collision. Move straight.
         this.x += this.speedX * time;         
         this.y += this.speedY * time;         
      }
      // Clear for the next collision detection
      earliestCollisionResponse.reset();
      return involvedInCollision;
   }

   /** Draw itself using the given graphics context. */
   public void draw(Graphics g) 
   {
	  boolean targetsApply = (Ball.target.toString().isEmpty() == false);
	  boolean isTargetted = false;
	  if (targetsApply)
	  {
		  try {
			  int target = Integer.parseInt(Ball.target.toString());
			  isTargetted = (target == id);
		  } catch (NumberFormatException e) {
		      //Will Throw exception!
		      //do something! anything to handle the exception.
		  }
	  }
	  
	  g.setColor(color);
      
      if ((targetsApply == false || isTargetted) && (hollowWhenThreatened && threatCount > 0))
      {
    	  g.drawOval((int)(x - radius), (int)(y - radius), (int)(2 * radius),
    			  (int)(2 * radius));
      }
      else
      {
    	  g.fillOval((int)(x - radius), (int)(y - radius), (int)(2 * radius),
    			  (int)(2 * radius));
      }

	  if ((targetsApply == false || isTargetted) && (indicateThreats))
	  {
    	  for(int i=0; i < threatCount; i++)
    	  {
    		  Ball threat = threatList.get(i);
    		  g.drawLine((int)x, (int)y, (int)threat.x, (int)threat.y);
    	  }
	  }
      
      if ((targetsApply == false || isTargetted) && (showBubbles)) {
    	  g.setColor(color);
    	  float ballRadius = CalculateCollisionRadius();
    	  g.drawRect((int) (x - ballRadius), (int) (y - ballRadius), (int) (ballRadius * 2), (int) (ballRadius * 2));
      }
   }
   
   /** Return the magnitude of speed. */
   public float getSpeed() {
      return (float)Math.sqrt(speedX * speedX + speedY * speedY);
   }
   
   /** Return the direction of movement in degrees (counter-clockwise). */
   public float getMoveAngle() {
      return (float)Math.toDegrees(Math.atan2(-speedY, speedX));
   }
   
   /** Return mass */
   public float getMass() {
      return radius * radius * radius / 1000f;
   }
   
   /** Return the kinetic energy (0.5mv^2) */
   public float getKineticEnergy() {
      return 0.5f * getMass() * (speedX * speedX + speedY * speedY);
   }

   /** Describe itself. */
   public String toString() {
      sb.delete(0, sb.length());
      formatter.format("@(%3.0f,%3.0f) r=%3.0f V=(%3.0f,%3.0f) " +
            "S=%4.1f \u0398=%4.0f KE=%3.0f", 
            x, y, radius, speedX, speedY, getSpeed(), getMoveAngle(),
            getKineticEnergy());  // \u0398 is theta
      return sb.toString();
   }
   private StringBuilder sb = new StringBuilder();
   private Formatter formatter = new Formatter(sb);
}
