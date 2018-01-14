package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeRuntimeEntityId;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class NpcRequestPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private Type requestType;
    private String command;
    private byte actionType;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        buffer.writeByte(requestType.ordinal());
        writeString(buffer, command);
        buffer.writeByte(actionType);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }

    public enum Type {
        // TODO: Didn't really look too far into this.
        SET_ACTION,
        EXECUTE_COMMAND_ACTION,
        EXECUTE_CLOSING_COMMANDS
    }
}
