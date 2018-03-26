package cn.nukkit.server.entity.misc;

import cn.nukkit.api.entity.misc.Painting;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.packet.AddPaintingPacket;
import com.flowpowered.math.vector.Vector3f;

public class PaintingEntity extends BaseEntity implements Painting {

    public PaintingEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.PAINTING, position, level, server);
    }

    @Override
    public MinecraftPacket createAddEntityPacket() {
        AddPaintingPacket packet = new AddPaintingPacket();
        packet.setUniqueEntityId(getEntityId());
        packet.setRuntimeEntityId(getEntityId());
        packet.setBlockPosition(getPosition().toInt());
        packet.setTitle(Type.ALBAN.getName());
        return packet;
    }
}
