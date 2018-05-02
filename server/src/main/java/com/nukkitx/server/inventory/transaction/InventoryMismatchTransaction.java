package com.nukkitx.server.inventory.transaction;

import com.nukkitx.server.network.minecraft.session.PlayerSession;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryMismatchTransaction extends SimpleTransaction {
    private static final Type type = Type.INVENTORY_MISMATCH;

    @Override
    public void execute(PlayerSession session) {

    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void handle(PlayerSession.PlayerNetworkPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "InventoryMismatchTransaction" + super.toString();
    }
}
