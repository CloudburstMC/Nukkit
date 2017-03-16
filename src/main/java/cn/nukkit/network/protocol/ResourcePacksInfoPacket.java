package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;

public class ResourcePacksInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept = false;
    public ResourcePack[] behaviourPackEntries = new ResourcePack[0];
    public ResourcePack[] resourcePackEntries = new ResourcePack[0];

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);

        this.putLShort(this.behaviourPackEntries.length);
        for (ResourcePack entry : this.behaviourPackEntries) {
            this.putString(entry.getPackId());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
        }

        this.putLShort(this.resourcePackEntries.length);
        for (ResourcePack entry : this.resourcePackEntries) {
            this.putString(entry.getPackId());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
