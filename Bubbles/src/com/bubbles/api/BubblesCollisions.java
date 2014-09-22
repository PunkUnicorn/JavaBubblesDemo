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

import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import sun.misc.Queue;

import com.bubbles.api.BubblesLibrary;

/**
 *	The collision results
 * 
 */
public final class BubblesCollisions {
	/**
	 * 	Data object to hold a collision report
	 */
	public static class BangModel extends Observable {
		/**
		 * Constructs a new <i>BangModel</i> from raw collision results
		 * @param p_bangs	Collision results from JNA adapted native memory
		 * @param p_size	The number of collision results 
		 */
		BangModel(final BubblesLibrary.CollisionResult[] p_bangs, final int p_size) {
			bangs = p_bangs;
			size = p_size;
		}

		/**
		 * Collision result array
		 * @see	 getBangs()
		 */
		private final BubblesLibrary.CollisionResult[] bangs;
		/**
		 * The number of collision results in the array <i>bangs</i>		 * 
		 */
		private final int size;

		/**
		 * Gets the psudo-native collision results array
		 * @return	Collision result array
		 */
		public BubblesLibrary.CollisionResult[] getBangs() {
			return bangs;
		}

		/**
		 * True if empty
		 * @return	True if empty
		 */
		public boolean isEmpty() {
			return size == 0;
		}
	}

	/**
	 * Map from engineId to observer instance which receives collision results
	 */
	public static class ObserverMap extends TreeMap<Integer /* engineId */, Observer> {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Engine id to collision result observer map
	 */
	public final static ObserverMap CollisionObservers = new ObserverMap();

	/**
	 * The collision function passed to the native Bubbles. Feeds CollisionObservers with BangModels
	 */
	public static BubblesLibrary.CollisionReportFunc getCollisionReportCallback = new BubblesLibrary.CollisionReportFunc() {
		public void callback(final int groupid, final int engineId, final BubblesLibrary.CollisionResult pbangs, final int size) throws Exception {
			if (size == 0) {
				CollisionObservers.get(engineId).update(new BangModel(null, size), size);
			} else {
				pbangs.setAutoWrite(false);
				final BubblesLibrary.CollisionResult[] bangs = (BubblesLibrary.CollisionResult[]) pbangs.toArray(size);
				CollisionObservers.get(engineId).update(new BangModel(bangs, size), size);
			}
			Thread.yield(); //VM hint. Results might come through at high frequency with a large volume of data
		}
	};

	/**
	 * Strongly types an Observer interface with supplied types for the Observable and argh0
	 * @param <T1>	The type to be Observable
	 * @param <T2>	The arg parameter type
	 */
	public static abstract class TypedObserver<T1 /*extends Observable ?*/, T2> implements Observer {
		protected abstract void update(T1 o, T2 argh0);

		@SuppressWarnings("unchecked") /*throws exception if takes exception*/
		@Override
		public void update(final Observable o, final Object argh0) throws ClassCastException {
			update((T1) o, (T2) argh0);
		}
	}

	/**
	 * 'interface' of observer class to pass to the collision service
	 *
	 */
	public static abstract class ObserveHit extends TypedObserver<BangModel, Integer> {
	}

	/**
	 * Given an observer feeds application with collision results
	 *
	 */
	public static class CollisionService implements Runnable {
		/**
		 * local queue to p
		 */
		private final BangModelProducer bangAdapter;

		public Observer getObserver() {
			return bangAdapter;
		}

		public ObserveHit observer;

		public CollisionService(final int p_engineId, final ObserveHit p_observer) {
			bangAdapter = new BangModelProducer();
			observer = p_observer;
		}

		public void run() {
			while (Thread.currentThread().isInterrupted() == false) {
				for (BangModel payload = bangAdapter.accept(); payload != null && Thread.currentThread().isInterrupted() == false; payload = bangAdapter
						.accept()) {
					observer.update(payload, null);
				}

				Thread.yield();
			}
		}
	}

	/**
	 *	Acquires collision results from the native library and stores them till calls to <i>accept()</i>  
	 */
	private static class BangModelProducer extends ObserveHit {
		private final Queue bangQueue = new Queue();

		/**
		 * Gets the next queued up collision result batch
		 * @return	The next set of collision result batch on the queue
		 */
		public BangModel accept() {
			if (bangQueue.isEmpty())
				return null;
			try {
				return (BangModel) bangQueue.dequeue();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			Thread.yield(); //VM hint
			return null;
		}

		/**
		 * Callback to receive collision results still warm from the native Bubbles
		 * It just puts them on the queue though
		 */
		@Override
		protected void update(final BangModel bangModelOrig, final Integer argh) {
			bangQueue.enqueue(bangModelOrig);
		}
	}
}
