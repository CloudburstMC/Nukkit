package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import lombok.ToString;

@ToString
public class ResourcePacksInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean hasScripts;
    public boolean forceServerPacks;
    public ResourcePack[] behaviourPackEntries = new ResourcePack[0];
    public ResourcePack[] resourcePackEntries = new ResourcePack[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.mustAccept = this.getBoolean();
        this.hasScripts = this.getBoolean();
        this.forceServerPacks = this.getBoolean();
        int count = this.getLShort();
        this.behaviourPackEntries = new ResourcePack[count];
        for (int i = 0; i < count; i++) {
            this.getString();
            this.getString();
            this.getLLong();
            this.getString();
            this.getString();
            this.getString();
            this.getBoolean();
            //TODO
        }
        count = this.getLShort();
        this.resourcePackEntries = new ResourcePack[count];
        for (int i = 0; i < count; i++) {
            this.getString();
            this.getString();
            this.getLLong();
            this.getString();
            this.getString();
            this.getString();
            this.getBoolean();
            this.getBoolean();
            //TODO
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putBoolean(this.hasScripts);
        this.putBoolean(this.forceServerPacks);
        this.putLShort(this.behaviorPackEntries.length);
        for (ResourcePack resourcePack : this.behaviourPackEntries) {
            this.putString(resourcePack.getPackId().toString());
            this.putString(resourcePack.getPackVersion());
            this.putLLong(resourcePack.getPackSize());
            this.putString(""); //TODO: Encryption key
            this.putString(""); //TODO: Subpack name
            this.putString(""); //TODO: Content identity
            this.putBoolean(false); //TODO: Has scripts
        }
        this.putLShort(this.resourcePackEntries.length);
        for (ResourcePack resourcePack : this.resourcePackEntries) {
            this.putString(resourcePack.getPackId().toString());
            this.putString(resourcePack.getPackVersion());
            this.putLLong(resourcePack.getPackSize());
            this.putString(""); //TODO: Encryption key
            this.putString(""); //TODO: Subpack name
            this.putString(""); //TODO: Content identity
            this.putBoolean(false); //TODO: Seems useless for resource packs
            this.putBoolean(false); //TODO: Supports RTX
        }
    }
}
