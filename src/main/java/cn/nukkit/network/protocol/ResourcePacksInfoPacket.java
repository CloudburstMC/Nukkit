package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePackInfoEntry;

public class ResourcePacksInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept = false;
    public ResourcePackInfoEntry[] behaviourPackEntries = new ResourcePackInfoEntry[0];
    public ResourcePackInfoEntry[] resourcePackEntries = new ResourcePackInfoEntry[0];

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putShort(this.behaviourPackEntries.length);
        for (ResourcePackInfoEntry entry : this.behaviourPackEntries) {
            this.putString(entry.getPackId());
            this.putString(entry.getVersion());
            this.putLLong(entry.getPackSize());
        }
        this.putShort(this.resourcePackEntries.length);
        for (ResourcePackInfoEntry entry : this.resourcePackEntries) {
            this.putString(entry.getPackId());
            this.putString(entry.getVersion());
            this.putLLong(entry.getPackSize());
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
