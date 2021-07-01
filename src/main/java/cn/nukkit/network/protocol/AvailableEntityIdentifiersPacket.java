package cn.nukkit.network.protocol;

import cn.nukkit.Nukkit;
import com.google.common.io.ByteStreams;
import lombok.ToString;

import java.io.InputStream;

@ToString(exclude = {"tag"})
public class AvailableEntityIdentifiersPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.AVAILABLE_ENTITY_IDENTIFIERS_PACKET;

    private static final byte[] TAG;

    static {
        try {
            InputStream inputStream = Nukkit.class.getClassLoader().getResourceAsStream("entity_identifiers.dat");
            if (inputStream == null) {
                throw new AssertionError("Could not find entity_identifiers.dat");
            }
            //noinspection UnstableApiUsage
            TAG = ByteStreams.toByteArray(inputStream);
        } catch (Exception e) {
            throw new AssertionError("Error whilst loading entity_identifiers.dat", e);
        }
    }

    public byte[] tag = TAG;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.tag = this.get();
    }

    @Override
    public void encode() {
        this.reset();
        this.put(this.tag);
    }
}
