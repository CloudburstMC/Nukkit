package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.level.AdventureSettings;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;

@Data
public class AdventureSettingsPacket implements MinecraftPacket {
    private int worldPermissions;
    private AdventureSettings.CommandPermission commandPermission;
    private int actionPermissions;
    private AdventureSettings.PlayerPermission playerPermission;
    private int customStoredPermissions;
    private long uniqueEntityId;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, worldPermissions);
        writeUnsignedInt(buffer, commandPermission.ordinal());
        writeUnsignedInt(buffer, actionPermissions);
        writeUnsignedInt(buffer, playerPermission.ordinal());
        writeUnsignedInt(buffer, customStoredPermissions);
        buffer.writeLongLE(uniqueEntityId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        worldPermissions = readUnsignedInt(buffer);
        commandPermission = AdventureSettings.CommandPermission.values()[readUnsignedInt(buffer)];
        actionPermissions = readUnsignedInt(buffer);
        playerPermission = AdventureSettings.PlayerPermission.values()[readUnsignedInt(buffer)];
        customStoredPermissions = readUnsignedInt(buffer);
        uniqueEntityId = buffer.readLongLE();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
