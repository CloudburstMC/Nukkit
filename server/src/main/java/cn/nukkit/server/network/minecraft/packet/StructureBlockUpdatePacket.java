package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeBlockPosition;

@Data
public class StructureBlockUpdatePacket implements MinecraftPacket {
    private Vector3i blockPosition;
    private int structureType;
    //private StructureEditorData structureEditorData;
    /*
    StructureEditorData:
    xStructureOffset
    yStructureOffset
    zStructureOffset
    xStructureSize
    yStructureSize
    zStructureSize
    ...
    StructureSettings
    TODO: Delve deeper into this mess.
     */
    private boolean boundingBoxVisible;
    private boolean powered;

    @Override
    public void encode(ByteBuf buffer) {
        writeBlockPosition(buffer, blockPosition);
        writeUnsignedInt(buffer, structureType);
        //writeStructureEditorData(buffer, structureEditorData);
        buffer.writeBoolean(boundingBoxVisible);
        buffer.writeBoolean(powered);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }

    public enum Type {
        NONE,
        SAVE,
        LOAD,
    }
}
