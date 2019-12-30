package cn.nukkit.network.protocol;

import cn.nukkit.pack.Pack;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class ResourcePacksInfoPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean scripting;
    public final List<Pack> behaviourPackEntries = new ArrayList<>();
    public final List<Pack> resourcePackEntries = new ArrayList<>();

    private static void encodePacks(ByteBuf buffer, List<Pack> packs) {
        buffer.writeShortLE(packs.size());
        for (Pack entry : packs) {
            Binary.writeString(buffer, entry.getId().toString());
            Binary.writeString(buffer, entry.getVersion().toString());
            buffer.writeLongLE(entry.getSize());
            Binary.writeString(buffer, ""); // encryption key
            Binary.writeString(buffer, ""); // sub-pack name
            Binary.writeString(buffer, ""); // content identity
            buffer.writeBoolean(false); // scripting
        }
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeBoolean(this.mustAccept);
        buffer.writeBoolean(this.scripting);

        encodePacks(buffer, this.resourcePackEntries);
        encodePacks(buffer, this.behaviourPackEntries);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
