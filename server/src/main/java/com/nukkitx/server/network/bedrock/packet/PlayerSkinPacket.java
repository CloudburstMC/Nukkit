package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.util.Skin;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;
import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class PlayerSkinPacket implements BedrockPacket {
    private UUID uuid;
    private String newSkinName;
    private String oldSkinName;
    private Skin skin;
    private boolean premium;

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
        buffer.writeBoolean(premium);
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
        premium = buffer.readBoolean();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
