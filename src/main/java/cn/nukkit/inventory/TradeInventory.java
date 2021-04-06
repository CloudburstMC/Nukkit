package cn.nukkit.inventory;

import java.io.IOException;
import java.nio.ByteOrder;

import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.UpdateTradePacket;

public class TradeInventory extends BaseInventory {
	
	public static final int TRADE_INPUT_A = 4;
	public static final int TRADE_INPUT_B = 5;
	public static final int TRADE_OUTPUT = 51;
	
	// mojang, what the heck??
	public static final int FAKE_TRADE_INPUT = -30; // moves from fake ui (villager inventory) to player inventory
	public static final int FAKE_TRADE_OUTPUT = -31; // item is sent from player inventory to villager fake inv

	public TradeInventory(InventoryHolder holder) {
		super(holder, InventoryType.TRADING);
	}
	
	public void onOpen(Player who) {
		super.onOpen(who);
		
		UpdateTradePacket pk = new UpdateTradePacket();
		pk.windowId = (byte) who.getWindowId(this);
		pk.windowType = (byte) InventoryType.TRADING.getNetworkType();
		pk.isWilling = this.getHolder().isWilling();
		pk.screen2 = true;
		pk.trader = this.getHolder().getId();
		pk.tradeTier = this.getHolder().getTradeTier();
		pk.displayName = this.getHolder().getTraderName();
		pk.player = who.getId();
		pk.unknownVarInt1 = 0;
		try {
			pk.offers = NBTIO.write(this.getHolder().getOffers(),ByteOrder.LITTLE_ENDIAN, true);
		} catch(IOException ex) {}
		
		this.getHolder().setTradingWith(who.getId());
		who.isTrading = true;
		
		who.dataPacket(pk);
	}
	
	public void onClose(Player who) {
		for(int i = 0; i <= 1; i++) {
			Item item = getItem(i);
			if(who.getInventory().canAddItem(item)) {
				who.getInventory().addItem(item);
			} else {
				who.dropItem(item);
			}
			this.clear(i);
		}
		
		this.getHolder().cancelTradingWithPlayer();
		who.isTrading = false;
		
		super.onClose(who);
	}
	
	public EntityVillager getHolder() {
		return (EntityVillager) this.holder;
	}

}
