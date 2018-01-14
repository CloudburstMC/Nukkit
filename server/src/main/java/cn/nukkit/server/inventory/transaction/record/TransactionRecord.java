package cn.nukkit.server.inventory.transaction.record;

import cn.nukkit.api.item.ItemStack;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.Getter;

import javax.annotation.Nonnull;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readItemStack;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeItemStack;

@Data
public abstract class TransactionRecord {
    private int slot;
    private ItemStack oldItem;
    private ItemStack newItem;

    public void write(ByteBuf buffer){
        writeUnsignedInt(buffer, slot);
        writeItemStack(buffer, oldItem);
        writeItemStack(buffer, newItem);
    }

    public void read(ByteBuf buffer){
        slot = readUnsignedInt(buffer);
        oldItem = readItemStack(buffer);
        newItem = readItemStack(buffer);
    }

    public abstract Type getType();

    public abstract void execute(PlayerSession session);

    public enum Type {
        CONTAINER,
        GLOBAL,
        WORLD_INTERACTION,
        CREATIVE,
        CRAFT(99999);

        private static final TIntObjectMap<Type> BY_ID = new TIntObjectHashMap<>(5);
        @Getter
        private final int id;

        Type() {
            id = ordinal();
            add();
        }

        Type(int id) {
            this.id = id;
            add();
        }

        @Nonnull
        public static Type byId(int id) {
            Type type = BY_ID.get(id);
            if (type == null) {
                throw new IllegalArgumentException(id + " is not a valid inventory source type!");
            }
            return type;
        }

        private void add() {
            BY_ID.put(id, this);
        }
    }
}