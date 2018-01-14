package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemStack;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cn.nukkit.server.nbt.util.VarInt.readSignedInt;
import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readItemStack;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readUuid;

@Data
public class CraftingEventPacket implements MinecraftPacket {
    private final List<ItemStack> input = new ArrayList<>();
    private final List<ItemStack> output = new ArrayList<>();
    private byte windowId;
    private Type type;
    private UUID uuid;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        type = Type.values()[readSignedInt(buffer)];
        uuid = readUuid(buffer);

        int inputCount = readUnsignedInt(buffer);
        for (int i = 0; i < inputCount; i++) {
            input.add(readItemStack(buffer));
        }

        int outputCount = readUnsignedInt(buffer);
        for (int i = 0; i < outputCount; i++) {
            output.add(readItemStack(buffer));
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    public enum Type {
        SHAPELESS,
        SHAPED,
        FURNACE,
        FURNACE_DATA,
        MULTI,
        SHULKER_BOX
    }
}
