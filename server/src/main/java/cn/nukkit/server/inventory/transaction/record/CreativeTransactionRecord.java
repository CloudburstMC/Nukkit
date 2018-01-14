package cn.nukkit.server.inventory.transaction.record;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreativeTransactionRecord extends TransactionRecord {
    private static final Type type = Type.CREATIVE;
    private int inventoryId;

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void execute(PlayerSession session) {
        // TODO
    }
}
