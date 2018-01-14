package cn.nukkit.server.inventory.transaction;

import cn.nukkit.server.network.NetworkPacketHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InventoryMismatchTransaction extends SimpleTransaction {
    private static final Type type = Type.INVENTORY_MISMATCH;

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "InventoryMismatchTransaction" + super.toString();
    }
}
