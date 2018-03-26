package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeSignedBlockPosition;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeVector3f;

@Data
public class ExplodePacket implements MinecraftPacket {
    private final List<Vector3i> records = new ArrayList<>();
    private Vector3f position;
    private float radius;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3f(buffer, position);
        buffer.writeFloatLE(radius);
        writeUnsignedInt(buffer, records.size());
        records.forEach(blockPos -> writeSignedBlockPosition(buffer, blockPos));
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
