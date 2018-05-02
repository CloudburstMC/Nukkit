package com.nukkitx.server.inventory.transaction.action;

import com.nukkitx.server.network.minecraft.session.PlayerSession;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreativeInventoryAction extends InventoryAction {
    private static final Type type = Type.CREATIVE;
    private int inventoryId;

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void execute(PlayerSession session) {

    }
}
