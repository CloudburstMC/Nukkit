package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.data.StructureEditorData;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeStructureEditorData;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class StructureBlockUpdatePacket implements MinecraftPacket {
    private Vector3i blockPosition;
    private int structureType;
    private StructureEditorData structureEditorData;
    private boolean boundingBoxVisible;
    private boolean powered;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPosition);
        writeUnsignedInt(buffer, structureType);
        writeStructureEditorData(buffer, structureEditorData);
        buffer.writeBoolean(boundingBoxVisible);
        buffer.writeBoolean(powered);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
    }

    public enum Type {
        NONE,
        SAVE,
        LOAD,
    }
}
