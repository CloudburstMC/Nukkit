package cn.nukkit.server.inventory.transaction;


import cn.nukkit.server.inventory.transaction.record.TransactionRecord;
import cn.nukkit.server.network.minecraft.session.PlayerSession;
import io.netty.buffer.ByteBuf;

import java.util.Collection;

/**
 * @author CreeperFace
 */
public interface InventoryTransaction {

    long getCreationTime();

    /*Set<InventoryAction> getActions();

    Set<Inventory> getInventories();

    void addAction(InventoryAction action);

    boolean canExecute();

    boolean execute();

    boolean hasExecuted();*/

    Collection<TransactionRecord> getRecords();

    void read(ByteBuf buffer);

    void write(ByteBuf buffer);

    Type getType();

    void handle(PlayerSession.PlayerNetworkPacketHandler session);

    enum Type {
        NORMAL,
        INVENTORY_MISMATCH,
        ITEM_USE,
        ITEM_USE_ON_ENTITY,
        ITEM_RELEASE
    }
}
