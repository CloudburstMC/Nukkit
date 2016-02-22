package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerChunkRequestEvent extends PlayerEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers()
	{
        return handlers;
    }
	
    private int chunkX,chunkZ;
	
    public PlayerChunkRequestEvent(Player player,int chunkX,int chunkZ)
	{
        this.player=player;
        this.chunkX=chunkX;
        this.chunkZ=chunkZ;
    }
	
    public int getChunkX()
	{
        return chunkX;
    }
	
    public int getChunkZ()
	{
        return chunkZ;
    }
}
