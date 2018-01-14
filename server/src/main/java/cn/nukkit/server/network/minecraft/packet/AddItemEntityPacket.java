package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemStack;
import cn.nukkit.server.entity.data.EntityMetadata;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.Getter;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class AddItemEntityPacket implements MinecraftPacket {
    @Getter
    private final EntityMetadata metadata = new EntityMetadata();
    private long uniqueEntityId;
    private long runtimeEntityId;
    private ItemStack itemStack;
    private Vector3f position;
    private Vector3f motion;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeItemStack(buffer, itemStack);
        writeVector3f(buffer, position);
        writeVector3f(buffer, motion);
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
