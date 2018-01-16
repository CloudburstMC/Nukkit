package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class UpdateBlockPacket implements MinecraftPacket {
    public static final Set<Flag> FLAG_ALL = Sets.newHashSet(Flag.NEIGBORS, Flag.NETWORK);
    public static final Set<Flag> FLAG_ALL_PRIORITY = Sets.newHashSet(Flag.NEIGBORS, Flag.NETWORK, Flag.PRIORITY);
    private final Set<Flag> flags = new HashSet<>();
    private Vector3i blockPosition;
    private int blockId;
    private int metadata;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPosition);
        writeUnsignedInt(buffer, blockId);
        int flagValue = 0;
        for (Flag flag: flags) {
            flagValue |= flag.id;
        }
        writeUnsignedInt(buffer, (flagValue << 4) | metadata);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }

    public enum Flag {
        NONE(0b0000),
        NEIGBORS(0b0001),
        NETWORK(0b0010),
        NOGRAPHIC(0b0100),
        PRIORITY(0b1000);

        private final int id;

        Flag(int id) {
            this.id = id;
        }
    }
}
