package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.nbt.NBTEncodingType;
import cn.nukkit.server.nbt.stream.NBTWriter;
import cn.nukkit.server.nbt.tag.Tag;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.util.LittleEndianByteBufOutputStream;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.io.IOException;

import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeUniqueEntityId;

@Data
public class UpdateTradePacket implements MinecraftPacket {
    private byte windowId;
    private byte windowType;
    private int unknown0; // Couldn't find anything on this one.
    private int unknown1; // Something to do with AI and randomness?
    private boolean willing;
    private long traderUniqueEntityId;
    private long playerUniqueEntityId;
    private String displayName;
    private Tag<?> offers;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeByte(windowType);
        writeSignedInt(buffer, unknown0);
        writeSignedInt(buffer, unknown1);
        buffer.writeBoolean(willing);
        writeUniqueEntityId(buffer, traderUniqueEntityId);
        writeUniqueEntityId(buffer, playerUniqueEntityId);
        writeString(buffer, displayName);
        try (NBTWriter writer = new NBTWriter(new LittleEndianByteBufOutputStream(buffer), NBTEncodingType.MCPE)) {
            writer.write(offers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }
}
