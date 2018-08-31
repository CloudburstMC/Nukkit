package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.permission.CommandPermission;
import com.nukkitx.api.permission.PlayerPermission;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.server.entity.EntityLink;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import com.nukkitx.server.network.bedrock.util.MetadataDictionary;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;
import static com.nukkitx.server.network.util.VarInts.writeInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class AddPlayerPacket implements BedrockPacket {
    private UUID uuid;
    private String username;
    private String thirdPartyName;
    private int platformId;
    private long uniqueEntityId;
    private long runtimeEntityId;
    private String platformChatId;
    private Vector3f position;
    private Vector3f motion;
    private Rotation rotation;
    private ItemInstance hand;
    private final MetadataDictionary metadata = new MetadataDictionary();
    private int flags;
    private CommandPermission commandPermission;
    private int flags2;
    private PlayerPermission playerPermission;
    private int customFlags;
    //private final AdventureSettings adventureSettings = new AdventureSettings;
    private final List<EntityLink> entityLinks = new ArrayList<>();
    private String deviceId;

    @Override
    public void encode(ByteBuf buffer) {
        writeUuid(buffer, uuid);
        writeString(buffer, username);
        writeString(buffer, thirdPartyName);
        writeInt(buffer, platformId);
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeString(buffer, platformChatId);
        writeVector3f(buffer, position);
        writeVector3f(buffer, motion);
        writeRotation(buffer, rotation);
        writeItemInstance(buffer, hand);
        metadata.writeTo(buffer);
        writeUnsignedInt(buffer, flags);
        writeUnsignedInt(buffer, commandPermission.ordinal());
        writeUnsignedInt(buffer, flags2);
        writeUnsignedInt(buffer, playerPermission.ordinal());
        writeUnsignedInt(buffer, customFlags);
        buffer.writeLongLE(0);
        writeEntityLinks(buffer, entityLinks);
        writeString(buffer, deviceId.toString());
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
