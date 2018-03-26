package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.util.Rotation;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.util.MetadataDictionary;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class AddPlayerPacket implements MinecraftPacket {
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
    //private final AdventureSettings adventureSettings = new AdventureSettings;
    //private final List<EntityLink> entityLinks = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        writeUuid(buffer, uuid);
        writeString(buffer, username);
        writeString(buffer, thirdPartyName);
        writeSignedInt(buffer, platformId);
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeString(buffer, platformChatId);
        writeVector3f(buffer, position);
        writeVector3f(buffer, motion);
        writeRotation(buffer, rotation);
        writeItemInstance(buffer, hand);
        metadata.writeTo(buffer);
        //writeAdventureSettings(buffer, adventureSettings);
        //writeEntityLinks(buffer, entityLinks);
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
