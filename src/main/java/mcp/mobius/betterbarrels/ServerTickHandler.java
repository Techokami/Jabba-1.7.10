package mcp.mobius.betterbarrels;

import java.util.WeakHashMap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;

public enum ServerTickHandler {
	INSTANCE;

	class Timer{
		private long interval;
		private long lastTick = System.nanoTime();
		
		public Timer(long interval){
			this.interval = interval * 1000L * 1000L; //Interval is passed in millisecond but stored in nanosecond.
		}
		
		public boolean isDone(){
			long    time  = System.nanoTime();
			long    delta = (time - this.lastTick) - this.interval;
			boolean done  = delta >= 0;
			if (!done) return false;
			
			this.lastTick = time - delta;
			return true;
		}
	}
	
	// Hash map of dirty barrels for automatic cleanup
	// The boolean is never used and is just there to be able to have a WeakHashMap with automatic key handling
	private WeakHashMap<TileEntityBarrel, Boolean> dirtyBarrels = new WeakHashMap<TileEntityBarrel, Boolean>();
	public Timer timer = new Timer(BetterBarrels.limiterDelay);
	
	@SubscribeEvent
	public void tickServer(TickEvent.ServerTickEvent event) {
		if (timer.isDone()){
			for (TileEntityBarrel barrel : dirtyBarrels.keySet()){
				barrel.markDirtyExec();
			}
			dirtyBarrels.clear();
		}
	}

	public void markDirty(TileEntityBarrel barrel){
		this.markDirty(barrel, true);
	}
	public void markDirty(TileEntityBarrel barrel, boolean bspace){
		this.dirtyBarrels.put(barrel, true);
		if (bspace)
			if (barrel.coreUpgrades.hasEnder && !barrel.getWorldObj().isRemote) BSpaceStorageHandler.instance().markAllDirty(barrel.id);		
	}	
}
