package com.Bubbles.Java3D;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.*;

import com.bubbles.api.BubblesCoords;
import com.bubbles.api.Pair;
//import com.sun.jna.ptr.FloatByReference;


/**
 * http://www3.ntu.edu.sg/home/ehchua/programming/java/J8a_GameIntro-BouncingBalls.html
 * 
 * The control logic and main display panel for game.
 * 
 * @author Hock-Chuan Chua
 * @version October 2010
 * @see Bubbles collision detection engine dropped into Hock-Chuans example. Based on version stated
 */
public class BallWorld extends JPanel { //implements BubblesCoords.CoordBridge {
   private static final int UPDATE_RATE = 30;    // Frames per second (fps)
   private static final float EPSILON_TIME = 1e-2f;  // Threshold for zero time
   
   // Balls
   private static final int MAX_BALLS = 225; // Max number allowed 
   private int currentNumBalls;             // Number currently active
   private Ball[] balls = new Ball[MAX_BALLS];
   private int speedPercent = 100;
   int getSpeedPercent() { return speedPercent; }

   private final BubblesCoords.IBridgeCoords bridge = new BubblesCoords.IBridgeCoords() 
   {	
		@Override
		public float getX(int id)
		{
			return balls[id].x;
		}

		@Override
		public float getY(int id)
		{
			return balls[id].y;
		}

		@Override
		public float getZ(int id)
		{
			return 0;
		}
   };
   private static BubblesCoords.IBridgeCoords staticBridge;

   // The obstacles
   private ContainerBox box;               // The container box
   private ObstacleLineSegment lanuchTube; // The launching tube for new balls
   private ObstacleLine cornerTopLeft;     // Line at the top-left corner 
   private ObstacleLine cornerTopRight;    // Line at the top-right corner
   private ObstacleLineSegment line;       // The line obstacle
   private ObstaclePolygon polygon1;       // A polygon obstacle
   private ObstaclePolygon polygon2;       // A polygon obstacle
   private ObstacleCircle circle;          // A circle obstacle
   
   private DrawCanvas canvas;    // The Custom canvas for drawing the box/ball
   private int canvasWidth;
   private int canvasHeight;

   private ControlPanel control; // The control panel of buttons and sliders.
   private boolean paused = false;  // Flag for pause/resume control
   // Broad phase collision detection engine
   private CollisionBubbles bubbles = new CollisionBubbles(); 
   
   // De-reference adding balls. 
   private boolean AddBall(int index, float x, float y, float radius, float speed, float angleInDegree, Color color) 
   {
	   float useRadius = radius/2.0f;
	   balls[index] = new Ball(index, x, y, useRadius, speed, speedPercent, angleInDegree, color);
	   
	   float bubbleRadius = balls[index].CalculateCollisionRadius();
	   return bubbles.AddBubble(index, bubbleRadius);
   }
   
   /**
    * Constructor to create the UI components and init the game objects.
    * Set the drawing canvas to fill the screen (given its width and height).
    * 
    * @param width : screen width
    * @param height : screen height
    */
   public BallWorld(int width, int height) 
   {
	  staticBridge = bridge;
      final int controlHeight = 30;    
      canvasWidth = width;
      canvasHeight = height - controlHeight;  // Leave space for the control panel
      
      // Init the Container Box to fill the screen
      box = new ContainerBox(0, 0, canvasWidth, canvasHeight, Color.BLACK, Color.WHITE);
      
      // Init the obstacle blocks
      lanuchTube = new ObstacleLineSegment(32, canvasHeight - 160, 32, canvasHeight, Color.WHITE);
      cornerTopLeft  = new ObstacleLine(0, 50, 100, 0, Color.WHITE);
      cornerTopRight = new ObstacleLine(canvasWidth, 200, canvasWidth - 90, 0, Color.WHITE);
      line = new ObstacleLineSegment(36, 80, 100, 50, Color.WHITE);
      int[] polygon1Xs = {500, 630, 450, 380};
      int[] polygon1Ys = {280, 350, 420, 360};
      polygon1 = new ObstaclePolygon(polygon1Xs, polygon1Ys, Color.WHITE);
      int[] polygon2Xs = {150, 250, 350};
      int[] polygon2Ys = {550, 400, 550};
      polygon2 = new ObstaclePolygon(polygon2Xs, polygon2Ys, Color.WHITE);
      circle =  new ObstacleCircle(400, -30, 100, Color.WHITE); 
   
      currentNumBalls = 0;
      AddBall(currentNumBalls++, 100, 410, 25, 3, 34, Color.YELLOW);
      AddBall(currentNumBalls++, 80, 350, 25, 2, -114, Color.YELLOW);
   
      // Init the custom drawing panel for the box/ball
      canvas = new DrawCanvas();
      
      // Init the control panel
      control = new ControlPanel();
   
      // Layout the drawing panel and control panel
      this.setLayout(new BorderLayout());
      this.add(canvas, BorderLayout.CENTER);
      this.add(control, BorderLayout.SOUTH);
      
      // Handling window resize. Adjust container box to fill the screen.
      this.addComponentListener(new ComponentAdapter() {
         // Called back for first display and subsequent window resize.
         @Override
         public void componentResized(ComponentEvent e) {
            Component c = (Component)e.getSource();
            Dimension dim = c.getSize();
            canvasWidth = dim.width;
            canvasHeight = dim.height - controlHeight; // Leave space for control panel
            // Need to resize all components that is sensitive to the screen size.
            box.set(0, 0, canvasWidth, canvasHeight);
            lanuchTube.set(32, canvasHeight - 160, 32, canvasHeight);
            cornerTopRight.set(canvasWidth, 200, canvasWidth - 90, 0);
         }
      });
       
      // Start the ball bouncing
      gameStart();
      try {
		bubbles.Start(bridge);
	} catch (InterruptedException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
   }
   
   /** Start the ball bouncing. */
   public void gameStart() {
      // Run the game logic in its own thread.
      Thread gameThread = new Thread() {
         public void run() {
            while (true) {
               long beginTimeMillis, timeTakenMillis, timeLeftMillis;
               beginTimeMillis = System.currentTimeMillis();
               
               if (!paused) {
                  // Execute one game step
                  gameUpdate();
               }
               
               // Refresh the display
               repaint();
               
               // Provide the necessary delay to meet the target rate
               timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
               timeLeftMillis = 1000L / UPDATE_RATE - timeTakenMillis;
               if (timeLeftMillis < 5) timeLeftMillis = 5; // Set a minimum
               
               // Delay and give other thread a chance
               try {
                  Thread.sleep(timeLeftMillis);
               } catch (InterruptedException ex) {}
               
               Thread.yield();
            }
         }
      };
      gameThread.start();  // Invoke GaemThread.run()
   }
   
   private float processGameUpdate(int i, int j, float tMin)
   {
	   balls[i].intersect(balls[j], tMin);
	   if (balls[i].earliestCollisionResponse.t < tMin) {
		   tMin = balls[i].earliestCollisionResponse.t;
	   }
	   return tMin;
   }
   
   /** 
    * One game time-step. 
    * Update the game objects, with proper collision detection and response.
    */
   private Map<Pair<Integer> /*collision ball indexes*/, Long /*expire time*/> threatList = new LinkedHashMap<Pair<Integer>, Long>();
   private ArrayList<Pair<Integer>> deleteList = new ArrayList<Pair<Integer>>(50);
   
   public void gameUpdate() {
      float timeLeft = 1.0f;  // One time-step to begin with
      
      // Repeat until the one time-step is up 
      do {
         // Find the earliest collision up to timeLeft among all objects
         float tMin = timeLeft;
         long now = 0;
         now = System.currentTimeMillis();
         
         // Drain the hit queue
         bubbles.buildThreatList(balls, now, threatList);
 		 
         // Do narrow phase detection for threat pairs
         for (Pair<Integer> check : threatList.keySet()) 
        	 processGameUpdate(check.p1, check.p2, tMin);
         
   		 Thread.yield();        	 
         for (int i = 0; i < currentNumBalls; i++) 
         {
    		balls[i].intersect(box, tMin);
    		if (balls[i].earliestCollisionResponse.t < tMin) 
    		{
    			tMin = balls[i].earliestCollisionResponse.t;
    		}
        
            balls[i].intersect(cornerTopLeft, tMin);
            if (balls[i].earliestCollisionResponse.t < tMin) 
            {
               tMin = balls[i].earliestCollisionResponse.t;
            }
            
            balls[i].intersect(cornerTopRight, tMin);
            if (balls[i].earliestCollisionResponse.t < tMin) 
            {
               tMin = balls[i].earliestCollisionResponse.t;
            }
            
            balls[i].intersect(lanuchTube, tMin);
            if (balls[i].earliestCollisionResponse.t < tMin) 
            {
               tMin = balls[i].earliestCollisionResponse.t;
            }
            
            balls[i].intersect(polygon1, tMin);
            if (balls[i].earliestCollisionResponse.t < tMin) 
            {
               tMin = balls[i].earliestCollisionResponse.t;
            }
            
            balls[i].intersect(polygon2, tMin);
            if (balls[i].earliestCollisionResponse.t < tMin) 
            {
               tMin = balls[i].earliestCollisionResponse.t;
            }
            
            balls[i].intersect(line, tMin);
            if (balls[i].earliestCollisionResponse.t < tMin) 
            {
               tMin = balls[i].earliestCollisionResponse.t;
            }
            
            balls[i].intersect(circle, tMin);
            if (balls[i].earliestCollisionResponse.t < tMin) 
            {
               tMin = balls[i].earliestCollisionResponse.t;
            }
         }
         
         // Update all the balls up to the detected earliest collision time tMin,
         // or timeLeft if there is no collision.
         for (int i = 0; i < currentNumBalls; i++) 
         {
            if (balls[i].update(tMin)) {
            	float newRadius = balls[i].CalculateCollisionRadius();
            	bubbles.SetRadius(i, newRadius);
            }
         }
         
         Thread.yield();
         timeLeft -= tMin;                // Subtract the time consumed and repeat
      } while (timeLeft > EPSILON_TIME);  // Ignore remaining time less than threshold

	  long now = System.currentTimeMillis();
      for (Pair<Integer> check : threatList.keySet())
	  {
    	  if (threatList.get(check) < now)  // If the threat has expired
    		  deleteList.add(check);
	  }
      bubbles.purgeThreatList(balls, threatList, deleteList);    
   }
   
   /** The custom drawing panel for the bouncing ball (inner class). */
   class DrawCanvas extends JPanel 
   {
	  /** Custom drawing codes */
      @Override
      public void paintComponent(Graphics g) 
      {
         super.paintComponent(g);    // Paint background

         // Draw the box, obstacles and balls
         box.draw(g);
         cornerTopLeft.draw(g);
         cornerTopRight.draw(g);
         lanuchTube.draw(g);
         polygon1.draw(g);
         polygon2.draw(g);
         line.draw(g);
         circle.draw(g);
         
         int totalEnergy = 0;
    	 for (int i = 0; i < currentNumBalls; i++) 
    	 {
    		 totalEnergy += balls[i].getKineticEnergy();
    		 balls[i].draw(g);
    	 }
    	 
         // Display balls' information
         g.setColor(Color.BLUE);
         g.setFont(new Font("Courier New", Font.PLAIN, 12));
         int line = 0;
         g.drawString("Total Energy: " + (int)totalEnergy, 42, 20 + line*20);
      }

      /** Called back to get the preferred size of the component. */
      @Override
      public Dimension getPreferredSize() 
      {
         return (new Dimension(canvasWidth, canvasHeight));
      }
   }
   private final Color colors[] = { Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK }; 
   private int colorsIndex;
   private int lighterIndex;
   private Color getNewColor() {
	   if (colorsIndex >= colors.length) 
	   {
 		  colorsIndex = 0;
 		  lighterIndex++;
	   }
	   Color retVal = colors[colorsIndex++];
	   for (int i = 0; i <= lighterIndex; i++) 
	   {
		   retVal = retVal.brighter();
	   }
	   return retVal;
   }
   
   /** The control panel (inner class). */
   class ControlPanel extends JPanel {
	  private static final String buttonTitle = "Launch New Ball";
      /** Constructor to initialize UI components */
      public ControlPanel() {
         // A checkbox to toggle pause/resume movement
         JCheckBox pauseControl = new JCheckBox();
         this.add(new JLabel("Pause"));
         this.add(pauseControl);
         this.add(new JSeparator(SwingConstants.VERTICAL));         
         pauseControl.addItemListener(new ItemListener() 
         {
            @Override
            public void itemStateChanged(ItemEvent e) 
            {
               paused = !paused;  // Toggle pause/resume flag
               bubbles.Pause(paused);
               transferFocusUpCycle();  // To handle key events
            }
         });

         JCheckBox showBubblesControl = new JCheckBox();
         this.add(new JLabel("show bubbles"));
         this.add(showBubblesControl);
         this.add(new JSeparator(SwingConstants.VERTICAL)); 
         showBubblesControl.addItemListener(new ItemListener() 
         {
			@Override
            public void itemStateChanged(ItemEvent e) 
			{
               Ball.showBubbles = !Ball.showBubbles; 
               transferFocusUpCycle();  // To handle key events
            }
         });
         
         JCheckBox hollowWhenThreatened = new JCheckBox();
         this.add(new JLabel("hollow when threatened"));
         this.add(hollowWhenThreatened);
         this.add(new JSeparator(SwingConstants.VERTICAL));         
         hollowWhenThreatened.addItemListener(new ItemListener() 
         {
			@Override
            public void itemStateChanged(ItemEvent e) 
			{
               Ball.hollowWhenThreatened = !Ball.hollowWhenThreatened; 
               transferFocusUpCycle();  // To handle key events
            }
         });

         JCheckBox indicateThreats = new JCheckBox();
         this.add(new JLabel("indicate threats"));
         this.add(indicateThreats);
         this.add(new JSeparator(SwingConstants.VERTICAL));    
         indicateThreats.addItemListener(new ItemListener() 
         {
			@Override
            public void itemStateChanged(ItemEvent e) 
			{
               Ball.indicateThreats = !Ball.indicateThreats; 
               transferFocusUpCycle();  // To handle key events
            }
         });
                  
         final JTextField targetNumber = new JTextField();
         targetNumber.setText("0");
         final JButton targetPrev = new JButton("<-");
         final JButton targetNext = new JButton("->");
         this.add(new JLabel("target"));
         this.add(targetPrev);
         this.add(targetNumber);
         this.add(targetNext);
         this.add(new JSeparator(SwingConstants.VERTICAL));  
         targetNumber.addKeyListener(new KeyListener() //)(new CaretListener()//)(new InputMethodListener() 
         {
			@Override public void keyPressed(KeyEvent arg0) { }
			@Override public void keyReleased(KeyEvent arg0) { }			
			@Override public void keyTyped(KeyEvent arg0) { Ball.target = targetNumber.getText(); }
         });
         targetPrev.addActionListener(new ActionListener()
         {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					int target = Integer.parseInt(targetNumber.getText());
					targetNumber.setText(String.valueOf(target-1));
					Ball.target = targetNumber.getText();					
				}
				catch (NumberFormatException ex) { }
			}
         });
		 targetNext.addActionListener(new ActionListener()
         {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					int target = Integer.parseInt(targetNumber.getText());
					targetNumber.setText(String.valueOf(target+1));
					Ball.target = targetNumber.getText();
				}
				catch (NumberFormatException ex) { }
			}
         });         
         // A slider for adjusting the speed of all the balls by a factor
         int minFactor = 5;    // percent
         int maxFactor = 200;  // percent
         JSlider speedControl = new JSlider(JSlider.HORIZONTAL, minFactor, maxFactor, speedPercent);
         this.add(new JLabel("Speed"));
         this.add(speedControl);
         this.add(new JSeparator(SwingConstants.VERTICAL)); 
         speedControl.addChangeListener(new ChangeListener() 
         {
            @Override
            public void stateChanged(ChangeEvent e) 
            {
          	   	JSlider source = (JSlider)e.getSource();
          	   	if (!source.getValueIsAdjusting()) {
			      int percentage = (int)source.getValue();
			      
			      // take note of save ball speed here, in the ball save slot
			      final float[] ballSavedSpeedXs = new float[currentNumBalls];
			      final float[] ballSavedSpeedYs = new float[currentNumBalls];
			      for (int i = 0; i < currentNumBalls; i++) 
			      {
			    	ballSavedSpeedXs[i] = (balls[i].speedX / speedPercent) * 100.0f;
			    	ballSavedSpeedYs[i] = (balls[i].speedY / speedPercent) * 100.0f;
	          	  }
			      
				  for (int i = 0; i < currentNumBalls; i++) 
				  {
					balls[i].speedX = ballSavedSpeedXs[i] * percentage / 100.0f;
					balls[i].speedY = ballSavedSpeedYs[i] * percentage / 100.0f;
				  }
				  speedPercent = percentage;
          	   	}
          	   	transferFocusUpCycle();  // To handle key events
            }
         });

         // A button for launching the remaining balls
         final JButton launchControl = new JButton(buttonTitle);
         this.add(launchControl);
         launchControl.addActionListener(new ActionListener() 
         {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
               if (currentNumBalls < MAX_BALLS) 
               {
            	  float x, y, speedY;
            	  x = 16;
            	  y = canvasHeight - 20;
            	  speedY = 10f;
            	  if (AddBall(currentNumBalls, x, y, 10.0f, speedY, 90, getNewColor()) == false)
            	  {
            		  System.out.println("ERRRR");
            		  
            	  }
            	  currentNumBalls++;
            	 
                  if (currentNumBalls == MAX_BALLS) 
                  {
                     // Disable the button, as there is no more ball
                     launchControl.setEnabled(false);
                  }
                  launchControl.setText(buttonTitle + " " + currentNumBalls);
               }
               transferFocusUpCycle();  // To handle key events
            }
         });
      }
   }
}
