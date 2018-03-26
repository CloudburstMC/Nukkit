package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.permission.CommandPermission;
import cn.nukkit.api.permission.PlayerPermission;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;

@Data
public class AdventureSettingsPacket implements MinecraftPacket {
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
