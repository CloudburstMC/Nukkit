package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class CommandBlockUpdatePacket implements MinecraftPacket {
    private boolean block;
    private Vector3i blockPosition;
    private int commandBlockMode;
    private boolean redstoneMode;
    private boolean conditional;
    private long minecartRuntimeEntityId;
    private String command;
    private String lastOutput;
    private String name;
    private boolean outputTracked;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        block = buffer.readBoolean();
        if (block) {
            blockPosition = readVector3i(buffer);
            commandBlockMode = readUnsignedInt(buffer);
            redstoneMode = buffer.readBoolean();
            conditional = buffer.readBoolean();
        } else {
            minecartRuntimeEntityId = readRuntimeEntityId(buffer);
        }

        command = readString(buffer);
        lastOutput = readString(buffer);
        name = readString(buffer);
        outputTracked = buffer.readBoolean();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
