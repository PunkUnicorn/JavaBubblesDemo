package com.Bubbles.Java3D;

//import BallWorld;

import javax.swing.JFrame;

///*
// * $RCSfile: HelloUniverse.java,v $
// *
// * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions
// * are met:
// *
// * - Redistribution of source code must retain the above copyright
// *   notice, this list of conditions and the following disclaimer.
// *
// * - Redistribution in binary form must reproduce the above copyright
// *   notice, this list of conditions and the following disclaimer in
// *   the documentation and/or other materials provided with the
// *   distribution.
// *
// * Neither the name of Sun Microsystems, Inc. or the names of
// * contributors may be used to endorse or promote products derived
// * from this software without specific prior written permission.
// *
// * This software is provided "AS IS," without a warranty of any
// * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
// * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
// * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
// * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
// * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
// * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
// * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
// * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
// * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
// * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
// * POSSIBILITY OF SUCH DAMAGES.
// *
// * You acknowledge that this software is not designed, licensed or
// * intended for use in the design, construction, operation or
// * maintenance of any nuclear facility.
// *
// * $Revision: 1.2 $
// * $Date: 2007/02/09 17:21:42 $
// * $State: Exp $
// */
//
//package com.Bubbles.Java3D;
//
////https://www.cs.utexas.edu/~scottm/cs324e/handouts/setUpJava3dEclipse.htm
//
//import com.sun.j3d.utils.geometry.ColorCube;
//import com.sun.j3d.utils.universe.*;
////import com.sun.j3d.utils.geometry.ColorCube;
//import javax.media.j3d.*;
//import javax.vecmath.*;
//
//import java.awt.GraphicsConfiguration;
//import java.util.Timer;
//import java.util.TimerTask;
///**
// * Simple Java 3D example program to display a spinning cube.
// */
//
public class Java3D_Example { //extends javax.swing.JFrame {
//	private static final long serialVersionUID = 1L;
//	private SimpleUniverse univ = null;
//    private BranchGroup scene = null;
//    @SuppressWarnings("unused")
//	private CollisionDetection collisionDetection = new CollisionDetection();
//
//    public BranchGroup createSceneGraph() {
//		// Create the root of the branch graph
//		BranchGroup objRoot = new BranchGroup();
//	
//		// Create the TransformGroup node and initialise it to the
//		// identity. Enable the TRANSFORM_WRITE capability so that
//		// our behaviour code can modify it at run time. Add it to
//		// the root of the subgraph.
//		TransformGroup objTrans = new TransformGroup();
//		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//		objRoot.addChild(objTrans);
//	
//		// Create a simple Shape3D node; add it to the scene graph.
//		objTrans.addChild(new ColorCube(0.4));
//	
//		// Create a new Behaviour object that will perform the
//		// desired operation on the specified transform and add
//		// it into the scene graph.
//		Transform3D yAxis = new Transform3D();
//		Alpha rotationAlpha = new Alpha(-1, 4000);
//	
//		RotationInterpolator rotator =
//		    new RotationInterpolator(rotationAlpha, objTrans, yAxis,
//					     0.0f, (float) Math.PI*2.0f);
//		BoundingSphere bounds =
//		    new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
//		rotator.setSchedulingBounds(bounds);
//		objRoot.addChild(rotator);
//	
//	    // Have Java 3D perform optimisations on this scene graph.
//	    objRoot.compile();
//	
//		return objRoot;
//    }
//
//    private Canvas3D createUniverse() {
//		// Get the preferred graphics configuration for the default screen
//		GraphicsConfiguration config =
//		    SimpleUniverse.getPreferredConfiguration();
//	
//		// Create a Canvas3D using the preferred configuration
//		Canvas3D c = new Canvas3D(config);
//	
//		// Create simple universe with view branch
//		univ = new SimpleUniverse(c);
//	
//		// This will move the ViewPlatform back a bit so the
//		// objects in the scene can be viewed.
//		univ.getViewingPlatform().setNominalViewingTransform();
//	
//		// Ensure at least 5 msec per frame (i.e., < 200Hz)
//		univ.getViewer().getView().setMinimumFrameCycleTime(5);
//	
//		return c;
//    }
//
//    
//    class AlterVector extends TimerTask {
//
//		@Override
//		public void run() {
//			vector.add(new Vector3f(0.01f, 0.0f, 0.0f));
////			try {
////				Thread.sleep(10);
////			} catch (InterruptedException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//		}
//    	
//    }
//    
//    private Vector3f vector = new Vector3f( 1.f, 2.f, 3.f);
//    /**
//     * Creates new form HelloUniverse
//     */
//    public Java3D_Example() {
//		// Initialise the GUI components
//		initComponents();
//		// Create Canvas3D and SimpleUniverse; add canvas to drawing panel
//		Canvas3D c = createUniverse();
//		drawingPanel.add(c, java.awt.BorderLayout.CENTER);
//	
//		// Create the content branch and add it to the universes
//		scene = Possition.Move(vector);
//		Timer timer = new Timer(true);
//		timer.schedule(new AlterVector(), 1000);    	
//		
//		////////////////////////scene = createSceneGraph();
//		univ.addBranchGraph(scene);
//    }
//
//    // ----------------------------------------------------------------
//    
//    /** This method is called from within the constructor to
//     * Initialise the form.
//     * WARNING: Do NOT modify this code. The content of this method is
//     * always regenerated by the Form Editor.
//     */
//    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
//    private void initComponents() {
//        drawingPanel = new javax.swing.JPanel();
//
//        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
//        setTitle("HelloUniverse");
//        drawingPanel.setLayout(new java.awt.BorderLayout());
//
//        drawingPanel.setPreferredSize(new java.awt.Dimension(250, 250));
//        getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);
//
//        pack();
//    }// </editor-fold>//GEN-END:initComponents
//    
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//    	//Possition.Setup();
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new Java3D_Example().setVisible(true);
//            }
//        });
//    }
//    
//    // Variables declaration - do not modify//GEN-BEGIN:variables
//    private javax.swing.JPanel drawingPanel;
//    // End of variables declaration//GEN-END:variables
//    
	//import javax.swing.JFrame;
	/**
	 * Main Program for running the bouncing ball as a standalone application.
	 */
	//public class Main {
	   // Entry main program
	   public static void main(String[] args) {
	      // Run UI in the Event Dispatcher Thread (EDT), instead of Main thread
	      javax.swing.SwingUtilities.invokeLater(new Runnable() {
	         public void run() {
	            JFrame frame = new JFrame("A World of Balls");
	            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            frame.setContentPane(new BallWorld(800, 550)); // BallWorld is a JPanel
	            frame.pack();            // Preferred size of BallWorld
	            frame.setVisible(true);  // Show it
	         }
	      });
	   }
//}
}
