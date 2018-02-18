package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;

public class ResourcePacksInfoPacket extends DataPacket {

    public boolean mustAccept = false;
    public ResourcePack[] behaviourPackEntries = new ResourcePack[0];
    public ResourcePack[] resourcePackEntries = new ResourcePack[0];

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("RESOURCE_PACKS_INFO_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putBoolean(this.mustAccept);

        this.putLShort(this.behaviourPackEntries.length);
        for (ResourcePack entry : this.behaviourPackEntries) {
            this.putString(entry.getPackId());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
            this.putString(""); //unknown
        }

        this.putLShort(this.resourcePackEntries.length);
        for (ResourcePack entry : this.resourcePackEntries) {
            this.putString(entry.getPackId());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
            this.putString(""); //unknown
        }
    }

}
