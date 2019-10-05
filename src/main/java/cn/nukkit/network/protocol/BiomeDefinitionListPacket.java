package cn.nukkit.network.protocol;

import cn.nukkit.Nukkit;
import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.io.InputStream;

@ToString(exclude = "tag")
public class BiomeDefinitionListPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.BIOME_DEFINITION_LIST_PACKET;

    private static final byte[] TAG;

    static {
        try {
            InputStream inputStream = Nukkit.class.getClassLoader().getResourceAsStream("biome_definitions.dat");
            if (inputStream == null) {
                throw new AssertionError("Could not find biome_definitions.dat");
            }
            //noinspection UnstableApiUsage
            TAG = ByteStreams.toByteArray(inputStream);
        } catch (Exception e) {
            throw new AssertionError("Error whilst loading biome_definitions.dat", e);
        }
    }

    public byte[] tag = TAG;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeBytes(tag);
    }
}
