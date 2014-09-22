package com.bubbles.api;

import java.util.HashMap;

import com.sun.jna.ptr.FloatByReference;

/**
 * The coordinate callbacks
 * 
 */
public class BubblesCoords {
	/**
	 * For the Bubbles engine to enquire on x, y, and z coordinates of collision
	 * items
	 */
	public interface IBridgeCoords {
		/**
		 * Gets the items x coordinate
		 * 
		 * @param id
		 *            Bubble id of the x coordinate to get
		 * @return The x coordinate of id
		 */
		float getX(int id);

		/**
		 * Gets the items y coordinate
		 * 
		 * @param id
		 *            Bubble id of the y coordinate to get
		 * @return The y coordinate of id
		 */
		float getY(int id);

		/**
		 * Gets the items z coordinate
		 * 
		 * @param id
		 *            Bubble id of the z coordinate to get
		 * @return The z coordinate of id
		 */
		float getZ(int id);
	}

	/**
	 * Maps bubble id to an IBridgeCoords implementation
	 */
	static class CoordBridgeMap extends HashMap<Integer, IBridgeCoords> {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Instance of the internal Bubble id to IBridgeCoords map
	 * 
	 * @see getCoordMap()
	 */
	private static CoordBridgeMap coordBridgeMap = new CoordBridgeMap();

	/**
	 * Gets the instance of the internal IBridgeCoords map
	 * 
	 * @return
	 */
	static CoordBridgeMap getCoordMap() {
		return coordBridgeMap;
	}

	/**
	 * Instance of the GetCoordsFunc that is passed to the engine
	 */
	static BubblesLibrary.GetCoordsFunc getCoordsFunc = new BubblesLibrary.GetCoordsFunc() {
		public void callback(final int engineId, final int bubbleId, final FloatByReference x, final FloatByReference y, final FloatByReference z) {
			IBridgeCoords bridge = BubblesCoords.getCoordMap().get(engineId);
			x.setValue(bridge.getX(bubbleId));
			y.setValue(bridge.getY(bubbleId));
			z.setValue(bridge.getZ(bubbleId));
			bridge = null;
		}
	};
}
