package com.nukkitx.server.inventory.transaction.action;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.Getter;

import javax.annotation.Nonnull;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readItemInstance;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeItemInstance;
import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public abstract class InventoryAction {
    private int slot;
    private ItemInstance oldItem;
    private ItemInstance newItem;

    public void write(ByteBuf buffer){
        writeUnsignedInt(buffer, slot);
        writeItemInstance(buffer, oldItem);
        writeItemInstance(buffer, newItem);
    }

    public void read(ByteBuf buffer){
        slot = readUnsignedInt(buffer);
        oldItem = readItemInstance(buffer);
        newItem = readItemInstance(buffer);
    }

    public abstract Type getType();

    public enum Type {
        CONTAINER,
        GLOBAL,
        WORLD_INTERACTION,
        CREATIVE,
        CRAFT(99999);

        private static final TIntObjectMap<Type> BY_ID = new TIntObjectHashMap<>(5);

        static {
            for (Type type : values()) {
                BY_ID.put(type.id, type);
            }
        }

        @Getter
        private final int id;

        Type() {
            id = ordinal();
        }

        Type(int id) {
            this.id = id;
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

    public abstract void execute(PlayerSession session);

    public boolean invalidHand(ItemInstance serverItem) {
        return !serverItem.equals(oldItem);
    }
}