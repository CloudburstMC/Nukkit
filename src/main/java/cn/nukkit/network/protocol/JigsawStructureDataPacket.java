package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import lombok.ToString;

import java.io.IOException;
import java.util.zip.Deflater;

@ToString
public class JigsawStructureDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.__INTERNAL__JIGSAW_STRUCTURE_DATA_PACKET;

    private static final BatchPacket CACHED_PACKET;

    private byte[] tag;

    static {
        JigsawStructureDataPacket pk = new JigsawStructureDataPacket();

        try {
            pk.tag = NBTIO.writeNetwork(new CompoundTag("")
                    .putList(new ListTag<>("jigsaws"))
                    .putList(new ListTag<>("processors"))
                    .putList(new ListTag<>("structure_sets"))
                    .putList(new ListTag<>("template_pools")));
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
        this.put(tag);
    }
}
