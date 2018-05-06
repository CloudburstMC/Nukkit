package com.nukkitx.server.inventory.transaction;

import com.nukkitx.server.inventory.transaction.action.InventoryAction;
import com.nukkitx.server.network.minecraft.session.PlayerSession;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NormalTransaction extends SimpleTransaction {
    public static final int ACTION_PUT_SLOT = -2;
    public static final int ACTION_GET_SLOT = -3;
    public static final int ACTION_GET_RESULT = -4;
    public static final int ACTION_CRAFT_USE = -5;
    public static final int ACTION_ENCHANT_ITEM = 29;
    public static final int ACTION_ENCHANT_LAPIS = 31;
    public static final int ACTION_ENCHANT_RESULT = 33;
    public static final int ACTION_DROP = 199;
    private static final Type type = Type.NORMAL;

    @Override
    public void execute(PlayerSession session) {
        for (InventoryAction action : getActions()) {
            action.execute(session);
        }
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
        return "NormalTransaction" + super.toString();
    }
}
