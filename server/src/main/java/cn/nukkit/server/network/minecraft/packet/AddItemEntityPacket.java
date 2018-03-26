package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.util.MetadataDictionary;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class AddItemEntityPacket implements MinecraftPacket {
    private final MetadataDictionary metadata = new MetadataDictionary();
    private long uniqueEntityId;
    private long runtimeEntityId;
    private ItemInstance itemInstance;
    private Vector3f position;
    private Vector3f motion;
    private boolean fishing;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeItemInstance(buffer, itemInstance);
        writeVector3f(buffer, position);
        writeVector3f(buffer, motion);
        buffer.writeBoolean(fishing);
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
