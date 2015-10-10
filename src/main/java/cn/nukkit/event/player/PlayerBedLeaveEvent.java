package cn.nukkit.event.player;

import cn.nukkit.block.Block;
import cn.nukkit.Player;

public class PlayerBedLeaveEvent extends PlayerEvent{
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlers(){
    return handlers;
  }
	
	private $bed;
	
	public PlayerBedLeaveEvent(Player player, Block bed){
		this.player = player;
		this.bed = bed;
	}
	
	public Block getBed(){
		return bed;
	}
}
