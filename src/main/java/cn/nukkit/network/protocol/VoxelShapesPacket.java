package cn.nukkit.network.protocol;

import com.google.common.io.ByteStreams;
import lombok.ToString;

import java.io.IOException;
import java.util.Objects;
import java.util.zip.Deflater;

@ToString
public class VoxelShapesPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.__INTERNAL__VOXEL_SHAPES_PACKET;

    private static final BatchPacket CACHED_PACKET;

    private byte[] bin;

    static {
        VoxelShapesPacket pk = new VoxelShapesPacket();
        try {
            pk.bin = ByteStreams.toByteArray(Objects.requireNonNull(VoxelShapesPacket.class.getClassLoader().getResourceAsStream("voxel_shapes.bin")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        pk.tryEncode();
        CACHED_PACKET = pk.compress(Deflater.BEST_COMPRESSION);
    }

    public static BatchPacket getCachedPacket() {
        return CACHED_PACKET;
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();

        if (this.bin != null) {
            this.put(this.bin);
            return;
        }

        this.putUnsignedVarInt(0); // empty shapes array
        this.putUnsignedVarInt(0); // empty names map
        this.putLShort(0); // custom shapes count
    }
}
