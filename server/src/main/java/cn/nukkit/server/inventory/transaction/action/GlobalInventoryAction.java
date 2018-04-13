package cn.nukkit.server.inventory.transaction.action;

import cn.nukkit.server.network.minecraft.session.PlayerSession;
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
