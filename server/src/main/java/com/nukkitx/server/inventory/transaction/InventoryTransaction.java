package com.nukkitx.server.inventory.transaction;


import com.nukkitx.server.inventory.transaction.action.InventoryAction;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import io.netty.buffer.ByteBuf;

import java.util.Collection;

public interface InventoryTransaction {

    long getCreationTime();

    /*Set<InventoryAction> getActions();

    Set<Inventory> getInventories();

    void addAction(InventoryAction action);

    boolean canExecute();

    boolean execute();

    boolean hasExecuted();*/

    void execute(PlayerSession session);

    Collection<InventoryAction> getActions();

    void read(ByteBuf buffer);

    void write(ByteBuf buffer);

    Type getType();

    enum Type {
        NORMAL,
        INVENTORY_MISMATCH,
        ITEM_USE,
        ITEM_USE_ON_ENTITY,
        ITEM_RELEASE
    }
}
