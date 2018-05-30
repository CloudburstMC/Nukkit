package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import com.nukkitx.server.network.bedrock.data.StructureEditorData;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeStructureEditorData;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeVector3i;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class StructureBlockUpdatePacket implements BedrockPacket {
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
