package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.util.Rotation;
import cn.nukkit.server.entity.EntityAttribute;
import cn.nukkit.server.entity.EntityLink;
import cn.nukkit.server.entity.data.EntityMetadata;
import cn.nukkit.server.nbt.util.VarInt;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class AddEntityPacket implements MinecraftPacket {
    @Getter
    private final List<EntityAttribute> entityAttributes = new ArrayList<>();
    @Getter
    private final EntityMetadata metadata = new EntityMetadata();
    @Getter
    private final List<EntityLink> links = new ArrayList<>();
    private long uniqueEntityId;
    private long runtimeEntityId;
    private int entityType;
    private Vector3f position;
    private Vector3f motion;
    private Rotation rotation;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        VarInt.writeUnsignedInt(buffer, entityType);
        writeVector3f(buffer, position);
        writeVector3f(buffer, motion);
        writeVector2f(buffer, rotation.getBodyRotation());
        writeEntityAttributes(buffer, entityAttributes);
        //metadata.writeTo(buffer);
        writeEntityLinks(buffer, links);
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
