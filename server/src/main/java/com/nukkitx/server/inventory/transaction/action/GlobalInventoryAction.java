package com.nukkitx.server.inventory.transaction.action;

import com.nukkitx.server.network.bedrock.session.PlayerSession;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalInventoryAction extends InventoryAction {
    private static final Type type = Type.GLOBAL;

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void execute(PlayerSession session) {

    }
}
