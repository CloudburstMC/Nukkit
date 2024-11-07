package cn.nukkit.network.protocol;

import cn.nukkit.Nukkit;
import com.google.common.io.ByteStreams;
import lombok.ToString;

import java.util.zip.Deflater;

@ToString(exclude = "tag")
public class BiomeDefinitionListPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.BIOME_DEFINITION_LIST_PACKET;

    private static final BatchPacket CACHED_PACKET;

    private byte[] tag;

    static {
        try {
            BiomeDefinitionListPacket pk = new BiomeDefinitionListPacket();
            pk.tag = ByteStreams.toByteArray(Nukkit.class.getClassLoader().getResourceAsStream("biome_definitions.dat"));
            pk.tryEncode();
            CACHED_PACKET = pk.compress(Deflater.BEST_COMPRESSION);
        } catch (Exception e) {
            throw new AssertionError("Error whilst loading biome definitions", e);
        }
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
        if (this.tag == null) {
            throw new RuntimeException("tag == null, use getCachedPacket!");
        }

        this.reset();
        this.put(this.tag);
    }
}
