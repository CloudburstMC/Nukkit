package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.util.Skin;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class PlayerSkinPacket implements MinecraftPacket {
    private UUID uuid;
    private String newSkinName;
    private String oldSkinName;
    private Skin skin;

    @Override
    public void encode(ByteBuf buffer) {
        writeUuid(buffer, uuid);
        writeString(buffer, skin.getSkinId());
        writeString(buffer, newSkinName);
        writeString(buffer, oldSkinName);
        byte[] skinData = skin.getSkinData();
        byte[] capeData = skin.getCapeData();
        byte[] geometryData = skin.getGeometryData();
        writeUnsignedInt(buffer, skinData.length);
        buffer.writeBytes(skinData);
        writeUnsignedInt(buffer, capeData.length);
        buffer.writeBytes(capeData);
        writeString(buffer, skin.getGeometryName());
        writeUnsignedInt(buffer, geometryData.length);
        buffer.writeBytes(geometryData);
    }

    @Override
    public void decode(ByteBuf buffer) {
        uuid = readUuid(buffer);
        String skinId = readString(buffer);
        newSkinName = readString(buffer);
        oldSkinName = readString(buffer);
        byte[] skinData = new byte[readUnsignedInt(buffer)];
        buffer.readBytes(skinData);
        byte[] capeData = new byte[readUnsignedInt(buffer)];
        buffer.readBytes(capeData);
        String geometryName = readString(buffer);
        byte[] geometryData = new byte[readUnsignedInt(buffer)];
        buffer.readBytes(geometryData);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
