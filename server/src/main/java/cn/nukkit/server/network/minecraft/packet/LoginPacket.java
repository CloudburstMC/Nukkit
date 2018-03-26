package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.MinecraftPacketRegistry;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readLEAsciiString;

@Data
@EqualsAndHashCode(exclude = {"skinData"})
@ToString(exclude = {"chainData", "skinData"})
public class LoginPacket implements MinecraftPacket {
    private int protocolVersion;
    // Base64 strings so we only need Ascii characters.
    private AsciiString chainData;
    private AsciiString skinData;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        buffer.markReaderIndex();
        protocolVersion = buffer.readInt();
        if (protocolVersion > 65535) {
            buffer.resetReaderIndex();
            protocolVersion = buffer.readUnsignedShort();
        }
        if (!MinecraftPacketRegistry.isProtocolCompatible(protocolVersion)) {
            return; // No point in reading anymore data.
        }

        ByteBuf jwt = buffer.readSlice(readUnsignedInt(buffer)); // Get the JWT.
        chainData = readLEAsciiString(jwt);
        skinData = readLEAsciiString(jwt);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
