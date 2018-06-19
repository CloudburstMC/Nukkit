package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.permission.CommandPermission;
import com.nukkitx.api.permission.PlayerPermission;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class AdventureSettingsPacket implements BedrockPacket {
    private int flags;
    private CommandPermission commandPermission;
    private int flags2;
    private PlayerPermission playerPermission;
    private int customFlags;
    private long uniqueEntityId;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, flags);
        writeUnsignedInt(buffer, commandPermission.ordinal());
        writeUnsignedInt(buffer, flags2);
        writeUnsignedInt(buffer, playerPermission.ordinal());
        writeUnsignedInt(buffer, customFlags);
        buffer.writeLongLE(uniqueEntityId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        flags = readUnsignedInt(buffer);
        commandPermission = CommandPermission.values()[readUnsignedInt(buffer)];
        flags2 = readUnsignedInt(buffer);
        playerPermission = PlayerPermission.values()[readUnsignedInt(buffer)];
        customFlags = readUnsignedInt(buffer);
        uniqueEntityId = buffer.readLongLE();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
