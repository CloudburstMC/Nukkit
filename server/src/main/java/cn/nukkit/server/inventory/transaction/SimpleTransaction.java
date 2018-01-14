package cn.nukkit.server.inventory.transaction;

import cn.nukkit.server.inventory.transaction.record.TransactionRecord;
import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@EqualsAndHashCode
public abstract class SimpleTransaction implements InventoryTransaction {
    private final long creationTime;
    private final List<TransactionRecord> records = new ArrayList<>();

    public SimpleTransaction() {
        creationTime = System.currentTimeMillis();
    }

    @Override
    public void read(ByteBuf buffer) {
    }

    @Override
    public void write(ByteBuf buffer) {
    }

    @Override
    public String toString() {
        return "(" +
                "type=" + getType() +
                ", records=" + Arrays.toString(getRecords().toArray()) +
                ", creationTime=" + creationTime +
                ')';
    }
}
