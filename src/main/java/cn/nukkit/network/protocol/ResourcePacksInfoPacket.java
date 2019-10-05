package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class ResourcePacksInfoPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean scripting;
    public ResourcePack[] behaviourPackEntries = new ResourcePack[0];
    public ResourcePack[] resourcePackEntries = new ResourcePack[0];

    private static void encodePacks(ByteBuf buffer, ResourcePack[] packs) {
        buffer.writeShortLE(packs.length);
        for (ResourcePack entry : packs) {
            Binary.writeString(buffer, entry.getPackId().toString());
            Binary.writeString(buffer, entry.getPackVersion());
            buffer.writeLongLE(entry.getPackSize());
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
