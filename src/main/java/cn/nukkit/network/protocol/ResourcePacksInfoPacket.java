package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import lombok.ToString;

@ToString
public class ResourcePacksInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean scripting;
    public boolean forceServerPacks;
    public ResourcePack[] behaviourPackEntries = new ResourcePack[0];
    public ResourcePack[] resourcePackEntries = new ResourcePack[0];

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putBoolean(this.scripting);
        this.putBoolean(this.forceServerPacks);
        this.encodeBehaviourPacks(this.behaviourPackEntries);
        this.encodeResourcePacks(this.resourcePackEntries);
    }

    private void encodeBehaviourPacks(ResourcePack[] packs) {
        this.putLShort(packs.length);
        for (ResourcePack entry : packs) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
            this.putString(""); // encryption key
            this.putString(""); // sub-pack name
            this.putString(""); // content identity
            this.putBoolean(false); // scripting
        }
    }

    private void encodeResourcePacks(ResourcePack[] packs) {
        this.putLShort(packs.length);
        for (ResourcePack entry : packs) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
            this.putString(entry.getEncryptionKey()); // encryption key
            this.putString(""); // sub-pack name
            this.putString(!entry.getEncryptionKey().equals("") ? entry.getPackId().toString() : ""); // content identity
            this.putBoolean(false); // scripting
            this.putBoolean(false); // raytracing capable
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
