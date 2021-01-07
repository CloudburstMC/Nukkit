package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import lombok.ToString;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
public class PositionTrackingDBServerBroadcastPacket extends DataPacket {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final byte NETWORK_ID = ProtocolInfo.POS_TRACKING_SERVER_BROADCAST_PACKET;
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private static final Action[] ACTIONS = Action.values();

    private Action action;
    private int trackingId;
    private CompoundTag tag;
    
    private CompoundTag requireTag() {
        if (tag == null) {
            tag = new CompoundTag()
                    .putByte("version", 1)
                    .putString("id", String.format("0x%08x", trackingId));
        }
        return tag;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setAction(Action action) {
        this.action = action;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setTrackingId(int trackingId) {
        this.trackingId = trackingId;
        if (tag != null) {
            tag.putString("id", String.format("0x%08x", trackingId));
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public BlockVector3 getPosition() {
        if (tag == null) {
            return null;
        }
        ListTag<IntTag> pos = tag.getList("pos", IntTag.class);
        if (pos == null || pos.size() != 3) {
            return null;
        }
        return new BlockVector3(pos.get(0).data, pos.get(1).data, pos.get(2).data);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setPosition(BlockVector3 position) {
        setPosition(position.x, position.y, position.z);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setPosition(Vector3 position) {
        setPosition(position.getFloorX(), position.getFloorY(), position.getFloorZ());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setPosition(int x, int y, int z) {
        requireTag().putList(new ListTag<>("pos")
                .add(new IntTag("", x))
                .add(new IntTag("", y))
                .add(new IntTag("", z))
        );
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getStatus() {
        if (tag == null) {
            return 0;
        }
        return tag.getByte("status");
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setStatus(int status) {
        requireTag().putByte("status", status);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getVersion() {
        if (tag == null) {
            return 0;
        }
        return tag.getByte("version");
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setVersion(int status) {
        requireTag().putByte("version", status);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getDimension() {
        if (tag == null) {
            return 0;
        }
        return tag.getByte("dim");
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setDimension(int dimension) {
        requireTag().putInt("dim", dimension);
    }

    @Override
    public void encode() {
        reset();
        putByte((byte) action.ordinal());
        putVarInt(trackingId);
        try {
            put(NBTIO.writeNetwork(tag != null? tag : new CompoundTag()));
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public void decode() {
        action = ACTIONS[getByte()];
        trackingId = getVarInt();
        try(FastByteArrayInputStream inputStream = new FastByteArrayInputStream(get())) {
            tag = NBTIO.readNetworkCompressed(inputStream);
        } catch (IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public PositionTrackingDBServerBroadcastPacket clone() {
        return (PositionTrackingDBServerBroadcastPacket) super.clone();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public enum Action {
        UPDATE,
        DESTROY,
        NOT_FOUND
    }
}
