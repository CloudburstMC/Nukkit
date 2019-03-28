package cn.nukkit.network.protocol;

import cn.nukkit.Nukkit;
import com.google.common.io.ByteStreams;
import lombok.ToString;

import java.io.IOException;

@ToString(exclude = {"tag"})
public class AvailableEntityIdentifiersPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.AVAILABLE_ENTITY_IDENTIFIERS_PACKET;

    private static final byte[] TAG;

    static {
        try {
            //noinspection UnstableApiUsage
            TAG = ByteStreams.toByteArray(Nukkit.class.getClassLoader().getResourceAsStream("entity_identifiers.dat"));
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load entity_identifiers_net.dat");
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
