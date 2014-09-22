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

import java.util.ArrayList;
import java.util.Arrays;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

public class BubblesLibrary {
	public static class DistanceUnit extends Structure {
		public int axis; // 0=x, 1=y, 2=z
		public float abs_dist;
		public float radius; // width of this item
		public int id;

		@Override
		protected ArrayList<String> getFieldOrder() {
			return new ArrayList<String>(Arrays.asList(new String[] { "axis", "abs_dist", "radius", "id" }));
		}
	};

	public static class CollisionResult extends Structure {
		public int mCenterID;
		public DistanceUnit mDistanceUnit1;// = new DistanceUnit[3];
		public DistanceUnit mDistanceUnit2;
		public DistanceUnit mDistanceUnit3;

		@Override
		protected ArrayList<String> getFieldOrder() {
			return new ArrayList<String>(Arrays.asList(new String[] { "mCenterID", "mDistanceUnit1", "mDistanceUnit2", "mDistanceUnit3" }));
		}
	};

	public static class BubblesDllDirect {
		public static native boolean InitBubbles(boolean initSdl);

		// public static native boolean InitWithTrace(int traceMode, TraceFunc
		// func);
		public static native void UnInitBubbles();

		public static native int AddEngine();

		public static native void SetEngineTimerTrace(int engineId, TraceFunc func);

		public static native int GetEngineCount();

		public static native int AddEngineGroup(int engineId);

		public static native void AddEngineToGroup(int engineGroupId, int engineId);

		public static native int GetGroupCount();

		public static native boolean AddBubble(int engineid, int bubbleid, float radius, GetCoordsFunc func);

		public static native void RemoveBubble(int engineId, int bubbleId);

		public static native boolean GetEtheralness(int engineId, int bubbleId);

		public static native void SetEtheralness(int engineId, int bubbleId, boolean etheralness);

		public static native int GetRadius(int engineId, int bubbleId);

		public static native void SetRadius(int engineId, int bubbleId, float radius);

		public static native int GetBubbleCount(int engineid);

		public static native void StartEngine(int engineid, CollisionReportFunc func, int interval);

		public static native void PauseEngine(int engineId, boolean pause);

		public static native void PauseGroup(int groupId, boolean pause);

		static {
			Native.register("Bubbles.dll");
		}
	}

	public interface GetCoordsFunc extends StdCallCallback {
		void callback(int engineId, int id, FloatByReference x, FloatByReference y, FloatByReference z);
	}

	public interface CollisionReportFunc extends StdCallCallback {
		void callback(int groupId, int engineId, CollisionResult pbangs, int size) throws Exception;
	}

	public interface TraceFunc extends StdCallCallback {
		void callback(int engineIdUsually, int code);
	}
}