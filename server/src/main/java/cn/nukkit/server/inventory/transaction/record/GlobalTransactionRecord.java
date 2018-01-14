package cn.nukkit.server.inventory.transaction.record;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GlobalTransactionRecord extends TransactionRecord {
    private static final Type type = Type.GLOBAL;

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void execute(PlayerSession session) {
        // TODO
    }
}
