package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.util.Rotation;
import cn.nukkit.server.entity.data.EntityMetadata;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class AddPlayerPacket implements MinecraftPacket {
    @Getter
    private final EntityMetadata metadata = new EntityMetadata();
    private UUID uuid;
    private String username;
    private long uniqueEntityId;
    private long runtimeEntityId;
    private Vector3f position;
    private Vector3f motion;
    private Rotation rotation;
    private ItemStack hand;
    //@Getter
    //private final AdventureSettings adventureSettings = new AdventureSettings;

    @Override
    public void encode(ByteBuf buffer) {
        writeUuid(buffer, uuid);
        writeString(buffer, username);
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeVector3f(buffer, position);
        writeVector3f(buffer, motion);
        writeRotation(buffer, rotation);
        writeItemStack(buffer, hand);
        //writeMetadata(buffer, metadata);
        //writeAdventureSettings(buffer, adventureSettings);
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
