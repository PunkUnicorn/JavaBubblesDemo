/*
 * Copyright (c) BubPactLib 2013 Matthew Cocks
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
 * * Neither the name of 'BubPactLib' nor the names of
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
package com.bubbles.api;

import java.util.Observer;
import com.bubbles.api.BubblesCoords.IBridgeCoords;
import com.bubbles.api.BubblesLibrary;
import com.bubbles.api.BubblesLibrary.TraceFunc;

public class BubblesClass {
	/**
	 * Trace callback because tracing is platform dependent. Used for debugging
	 * the engine really
	 */
	public static BubblesLibrary.TraceFunc getTraceCallback = new BubblesLibrary.TraceFunc() {
		public void callback(final int engineIdUsually, final int trace) {
			System.out.println(engineIdUsually + ", " + trace);
		}
	};

	/**
	 * Starts the engine. Bubbles can be added before or after, or both.
	 * SDL_Init() is called in this function. It can be bypassed but you have to
	 * add a parameter
	 * 
	 * @see UnInitBubbles()
	 * @return true if successful or false if not
	 */
	public static boolean InitBubbles() {
		return BubblesLibrary.BubblesDllDirect.InitBubbles(true);
	}

	/**
	 * This is the accompanying called to InitBubbles
	 */
	public static void UnInitBubbles() {
		BubblesLibrary.BubblesDllDirect.UnInitBubbles();
	}

	/**
	 * This adds an instance of a new engine
	 * 
	 * @return The new engines id
	 */
	public static int AddEngine() {
		return BubblesLibrary.BubblesDllDirect.AddEngine();
	}

	/**
	 * Sets a callback function to receive the time it took the engine to
	 * complete a cycle
	 * 
	 * @param engineId
	 *            The engine to trace
	 * @param func
	 *            Callback function whose second parameter is the time took in
	 *            milliseconds
	 */
	public static void SetEngineTimerTrace(final int engineId, final TraceFunc func) {
		BubblesLibrary.BubblesDllDirect.SetEngineTimerTrace(engineId, func);
	}

	/**
	 * Gets the number of engines added
	 * 
	 * @return Number of engines added
	 */
	public static int GetEngineCount() {
		return BubblesLibrary.BubblesDllDirect.GetEngineCount();
	}

	/**
	 * Associates an engine with a new group. This means multiple engines can
	 * share the workload. Engines in the same group share the same list of ALL
	 * bubbles in the group. But each engine only reports on the results for the
	 * bubbles that have been added to it. So instead of
	 * 
	 * 200 x 200 DONE
	 * 
	 * its
	 * 
	 * 100 x 200 then 100 x 200 DONE
	 * 
	 * stops the spike see? Works with even more engines too if it's worth it
	 * 
	 * 
	 * Wait. I have to tell you it only works if you add bubbles <i>after</i>
	 * assigning groups. Groups first, then bubbles.
	 * 
	 * @param engineId
	 *            Engine to start the group off with
	 * @return new group ID
	 */
	public static int AddEngineGroup(final int engineId) {
		return BubblesLibrary.BubblesDllDirect.AddEngineGroup(engineId);
	}

	/**
	 * Associates an engine with an existing group
	 * 
	 * @see AddEngineGroup()
	 * @param groupId
	 *            Group ID of a group made with <i>AddEngineGroup()</i>
	 * @param engineId
	 */
	public static void AddEngineToGroup(final int groupId, final int engineId) {
		BubblesLibrary.BubblesDllDirect.AddEngineToGroup(groupId, engineId);
	}

	/**
	 * Returns the number of groups
	 * 
	 * @return Number of groups
	 */
	public static int GetGroupCount() {
		return BubblesLibrary.BubblesDllDirect.GetGroupCount();
	}

	/**
	 * Adds a new bounding bubble (cube bubble or cubble)
	 * 
	 * @param engineId
	 *            Engine Id of the engine to add
	 * @param bubbleId
	 *            Your unique id for this bubble
	 * @param radius
	 *            Width of the cube. The radius is a lie
	 * @return
	 */
	public static boolean AddBubble(final int engineId, final int bubbleId, final float radius) {
		return BubblesLibrary.BubblesDllDirect.AddBubble(engineId, bubbleId, radius, BubblesCoords.getCoordsFunc);
	}

	/**
	 * Deletes a bubble from the engine.
	 * 
	 * @param engineId
	 *            Engine which had the bubble added
	 * @param bubbleId
	 *            Bubble Id to delete
	 */
	public static void RemoveBubble(final int engineId, final int bubbleId) {
		BubblesLibrary.BubblesDllDirect.RemoveBubble(engineId, bubbleId);
	}

	/**
	 * Returns the etheral state of a bubble
	 * 
	 * @see SetEtheralness()
	 * @param engineId
	 *            Engine the bubble was added to
	 * @param bubbleId
	 *            Bubble Id
	 * @return
	 */
	public static boolean GetEtheralness(final int engineId, final int bubbleId) {
		return BubblesLibrary.BubblesDllDirect.GetEtheralness(engineId, bubbleId);
	}

	/**
	 * All bubbles are solid by default. Etheral bubbles have hits reported to
	 * them, but do not report as hits to other things. Like they go through
	 * things
	 * 
	 * @param engineId
	 *            Engine the bubble was added to
	 * @param bubbleId
	 *            Bubble Id
	 * @param etheralness
	 *            The new etheral state
	 */
	public static void SetEtheralness(final int engineId, final int bubbleId, final boolean etheralness) {
		BubblesLibrary.BubblesDllDirect.SetEtheralness(engineId, bubbleId, etheralness);
	}

	/**
	 * It's not a radius it's the width of the bubble cube. This function
	 * returns it.
	 * 
	 * @param engineId
	 *            Engine the bubble was added to
	 * @param bubbleId
	 *            Bubble Id
	 * @return Width of the bubble cube. The radius is a lie
	 */
	public static float GetRadius(final int engineId, final int bubbleId) {
		return BubblesLibrary.BubblesDllDirect.GetRadius(engineId, bubbleId);
	}

	/**
	 * It's not a radius it's the width of the bubble cube. This function sets
	 * it.
	 * 
	 * @param engineId
	 *            Engine the bubble was added to
	 * @param bubbleId
	 *            Bubble Id
	 * @param radius
	 *            Width of the bubble cube. The radius is a lie
	 */
	public static void SetRadius(final int engineId, final int bubbleId, final float radius) {
		BubblesLibrary.BubblesDllDirect.SetRadius(engineId, bubbleId, radius);
	}

	/**
	 * Gets the number of bubbles for a given engine
	 * 
	 * @param engineid
	 *            Engine Id to get the number of bubbles from
	 * @return Number of bubbles for that engine
	 */
	public static int GetBubbleCount(final int engineid) {
		return BubblesLibrary.BubblesDllDirect.GetBubbleCount(engineid);
	}

	/**
	 * Starts the engine timer.
	 * 
	 * @param engineid
	 *            Engine Id to start
	 * @param observer
	 *            Observer instance to receive collision results
	 * @param bridge
	 *            Bridge so the collision engine can furiously invoke this to
	 *            get bubbles x, y and z coordinate
	 * @param interval
	 *            The interval frequency the engine searches for collisions
	 */
	public static void StartEngine(final int engineid, final Observer observer, final IBridgeCoords bridge, final int interval) {
		BubblesCollisions.CollisionObservers.put(engineid, observer);
		BubblesCoords.getCoordMap().put(engineid, bridge);
		BubblesLibrary.BubblesDllDirect.StartEngine(engineid, BubblesCollisions.getCollisionReportCallback, interval);
	}

	/**
	 * Pauses the engine
	 * 
	 * @param engineid
	 *            Engine Id to pause
	 * @param pause
	 *            Pause state: true to pause, false to not be paused
	 */
	public static void PauseEngine(final int engineid, final boolean pause) {
		BubblesLibrary.BubblesDllDirect.PauseEngine(engineid, pause);
	}

	/**
	 * Pauses all engines in the group
	 * 
	 * @param engineid
	 *            Group Id of all engines to pause
	 * @param pause
	 *            Pause state: true to pause, false to not be paused
	 */
	public static void PauseGroup(final int groupid, final boolean pause) {
		BubblesLibrary.BubblesDllDirect.PauseGroup(groupid, pause);
	}
}
