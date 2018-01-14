package cn.nukkit.server.inventory.transaction;

import cn.nukkit.api.item.ItemStack;
import cn.nukkit.server.inventory.transaction.record.TransactionRecord;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.nukkit.server.nbt.util.VarInt.readSignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
@EqualsAndHashCode
public abstract class ComplexTransaction implements InventoryTransaction {
    private final long creationTime;
    private final List<TransactionRecord> records = new ArrayList<>();
    private int slot;
    private ItemStack item;
    private Vector3f fromPosition;

    public ComplexTransaction() {
        creationTime = System.currentTimeMillis();
    }

    public void read(ByteBuf buffer) {
        slot = readSignedInt(buffer);
        item = readItemStack(buffer);
        fromPosition = readVector3f(buffer);
    }

    public void write(ByteBuf buffer) {
        writeSignedInt(buffer, slot);
        writeItemStack(buffer, item);
        writeVector3f(buffer, fromPosition);
    }

    @Override
    public String toString() {
        return "(" +
                "type=" + getType() +
                ", records=" + Arrays.toString(getRecords().toArray()) +
                ", creationTime=" + creationTime +
                ", slot=" + slot +
                ", item=" + item +
                ", fromPosition=" + fromPosition;
    }
}
