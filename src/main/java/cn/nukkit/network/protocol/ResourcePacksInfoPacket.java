package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import lombok.ToString;

@ToString
public class ResourcePacksInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean unknownBool;
    public ResourcePack[] behaviourPackEntries = new ResourcePack[0];
    public ResourcePack[] resourcePackEntries = new ResourcePack[0];

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putBoolean(this.unknownBool);

        encodePacks(this.resourcePackEntries);
        encodePacks(this.behaviourPackEntries);
    }

    private void encodePacks(ResourcePack[] packs) {
        this.putLShort(packs.length);
        for (ResourcePack entry : packs) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
            this.putString(""); // encryption key
            this.putString(""); // sub-pack name
            this.putString(""); // content identity
            this.putBoolean(false); // ???
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
