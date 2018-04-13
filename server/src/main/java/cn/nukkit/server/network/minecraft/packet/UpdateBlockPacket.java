package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
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
    private int runtimeId;
    private DataLayer dataLayer;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPosition);
        writeUnsignedInt(buffer, runtimeId);
        int flagValue = 0;
        for (Flag flag: flags) {
            flagValue |= flag.id;
        }
        writeUnsignedInt(buffer, flagValue);
        writeUnsignedInt(buffer, dataLayer.ordinal());
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }

    public enum DataLayer {
        NORMAL,
        LIQUID
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
