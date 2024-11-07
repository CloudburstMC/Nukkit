package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntityVillagerV1;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.network.protocol.UpdateTradePacket;

import java.io.IOException;
import java.nio.ByteOrder;

public class TradeInventory extends BaseInventory {

    public TradeInventory(InventoryHolder holder) {
        super(holder, InventoryType.TRADING);
    }

    public void onOpen(Player who) {
        super.onOpen(who);

        UpdateTradePacket pk = new UpdateTradePacket();
        pk.windowId = (byte) who.getWindowId(this);
        pk.windowType = (byte) InventoryType.TRADING.getNetworkType();
        pk.isWilling = this.getHolder().isWilling();
        pk.screen2 = false; // use old trade screen
        pk.trader = this.getHolder().getId();
        pk.tradeTier = this.getHolder().getTradeTier();
        pk.player = who.getId();
        try {
            pk.offers = NBTIO.write(this.getHolder().getOffers(),ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException ignored) {}

        who.dataPacket(pk);
    }

    public void onClose(Player who) {
        for (int i = 0; i <= 1; i++) {
            Item item = getItem(i);
            this.clear(i);
            if (who.getInventory().canAddItem(item)) {
                who.getInventory().addItem(item);
            } else {
                who.level.dropItem(who, item);
            }
        }

        super.onClose(who);
    }

    public EntityVillagerV1 getHolder() {
        return (EntityVillagerV1) this.holder;
    }
}
