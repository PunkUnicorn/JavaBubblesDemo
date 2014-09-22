package com.Bubbles.Java3D;

import java.util.List;
import java.util.Map;

import com.bubbles.api.Bank;
import com.bubbles.api.BubblesClass;
import com.bubbles.api.BubblesCollisions;
import com.bubbles.api.BubblesCoords.IBridgeCoords;
import com.bubbles.api.BubblesLibrary;
import com.bubbles.api.LockedQueue;
import com.bubbles.api.Pair;
import com.bubbles.api.PairMaker;

public class CollisionBubbles 
{
	private final int engineInterval = 200;
	private int engineId1, engineId2, groupId;
	private int anEngineId = -1; //to alternate between engines when adding bubbles
	private Thread bubbleProducer = null, bubbleProducerToo = null;
	private Bank<Pair<Integer>> rawHitBank = new Bank<Pair<Integer>>(10000, new PairMaker<Integer>());
	private Bank<Pair<Integer>> currentThreatBank = new Bank<Pair<Integer>>(5000, new PairMaker<Integer>());
	public CollisionObserver collisionObserver = new CollisionObserver();
	public CollisionBubbles() 
	{
		// Set the JNA library path by hand till I sort this out	
		final String path = "C:\\Users\\Matthew\\workspace\\Publish";
        System.setProperty("jna.library.path", path);
        // http://www.devx.com/tips/Tip/22124
        
        boolean init = BubblesClass.InitBubbles();
        if (init == false)
        	return; 
        
        anEngineId = 
        	engineId1 = BubblesClass.AddEngine();
        
        groupId = BubblesClass.AddEngineGroup(engineId1);
        engineId2 = BubblesClass.AddEngine();
        BubblesClass.AddEngineToGroup(groupId, engineId2);
	}
		
	public void Start(IBridgeCoords bridge) throws InterruptedException 
	{
        BubblesCollisions.CollisionService oh = new BubblesCollisions.CollisionService(engineId1, collisionObserver);
        BubblesCollisions.CollisionService ohToo = new BubblesCollisions.CollisionService(engineId2, collisionObserver);
        bubbleProducer = new Thread(oh);
        bubbleProducer.start();
        BubblesClass.StartEngine(engineId1, oh.getObserver(), bridge, engineInterval);

        Thread.sleep(100);
        bubbleProducerToo = new Thread(ohToo);
        bubbleProducerToo.start();
        BubblesClass.StartEngine(engineId2, ohToo.getObserver(), bridge, engineInterval);
	}

	public boolean AddBubble(int id, float radius) 
	{
		if (anEngineId == -1) return false;
		int mylastEngineId =  anEngineId = (anEngineId == engineId1 ? engineId2 : engineId1);
		
        return BubblesClass.AddBubble(mylastEngineId, id, radius);
	}
	
	public void SetRadius(int bubbleId, float radius) {
		BubblesLibrary.BubblesDllDirect.SetRadius(engineId1, bubbleId, radius);
		BubblesLibrary.BubblesDllDirect.SetRadius(engineId2, bubbleId, radius);
	}
	
	public void Pause(boolean pause) 
	{
		BubblesClass.PauseEngine(engineId1, pause);
		BubblesClass.PauseEngine(engineId2, pause);
	}
	
	public LockedQueue<Pair<Integer> > getFeederQueue() { return feederQueue; } 
	private LockedQueue<Pair<Integer> > feederQueue = new LockedQueue<Pair<Integer> >();
	private class CollisionObserver extends BubblesCollisions.ObserveHit 
	{
		@Override
		protected void update(BubblesCollisions.BangModel o, Integer arg) 
		{
			if (o.isEmpty()) return;
			
			BubblesLibrary.CollisionResult[] bangs = o.getBangs();
			for (int i = bangs.length - 1; 0 <= i; i--) 
			{
				BubblesLibrary.CollisionResult item = bangs[i];
				int min = Math.min(item.mCenterID, item.mDistanceUnit1.id);
				int max = Math.max(item.mCenterID, item.mDistanceUnit1.id);
				updateFeederQueue(min, max);
			}
			
			Thread.yield();
		}

		private void updateFeederQueue(int min, int max)
		{
			Pair<Integer> key;
			try
			{
				key = rawHitBank.get();
				key.p1 = min;
				key.p2 = max;				
				getFeederQueue().add(key);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			key = null;
		}
	}
	
	void buildThreatList(Ball[] balls, long now, Map<Pair<Integer>, Long> threatList) 
	{
		final long cacheDurationMs = (long) (engineInterval*1.05f);
		Pair<Integer> each = null;
		Long expireTimeMs = now + cacheDurationMs;
		while ((each = getFeederQueue().poll()) != null) {
			Pair<Integer> clone = new Pair<Integer>(each.p1, each.p2);
			try
			{
				rawHitBank.forget(each);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updateThreatList(balls, threatList, expireTimeMs, clone);
			clone = null;
		}
	}

	private void updateThreatList(Ball[] balls, Map<Pair<Integer>, Long> threatList, Long expireTimeMs, Pair<Integer> clone)
	{
		if (threatList.containsKey(clone)) 
		{
			threatList.put(clone, expireTimeMs);
			return;
		}

		Pair<Integer> threat;
		try
		{
			threat = currentThreatBank.get();
			threat.p1 = clone.p1;
			threat.p2 = clone.p2;
			incrementThreat(balls, threatList, expireTimeMs, clone, threat);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		threat = null;
	}

	void purgeThreatList(Ball[] balls, Map<Pair<Integer>, Long> threatList, List<Pair<Integer>> deleteList)
	{
		for (Pair<Integer> check : deleteList)
			decrementThreat(balls, threatList, check);

		deleteList.clear();
	}

	private void incrementThreat(Ball[] balls, Map<Pair<Integer>, Long> threatList, Long expireTimeMs,
			Pair<Integer> clone, Pair<Integer> threat)
	{
		threatList.put(threat, expireTimeMs);
		balls[clone.p1].threatCount++;
		balls[clone.p2].threatCount++;
		balls[clone.p1].threatList.add(balls[clone.p2]);
		balls[clone.p2].threatList.add(balls[clone.p1]);
	}

	private void decrementThreat(Ball[] balls, Map<Pair<Integer>, Long> threatList, Pair<Integer> check)
	{
		threatList.remove(check);
		balls[check.p1].threatCount--;
		balls[check.p2].threatCount--;
		balls[check.p1].threatList.remove(balls[check.p2]);
		balls[check.p2].threatList.remove(balls[check.p1]);
		try
		{
			currentThreatBank.forget(check);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
