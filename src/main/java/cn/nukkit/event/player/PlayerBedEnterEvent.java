package cn.nukkit.event.player;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.Player;

public class PlayerBedEnterEvent extends PlayerEvent implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	
	private Block bed;
	
	public PlayerBedEnterEvent(Player player, Block bed){
		this.player = player;
		this.bed = bed;
	}
	
	public Block getBed(){
		return bed;
	}
}
