package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import lombok.ToString;

@ToString
public class ResourcePacksInfoPacket extends DataPacket {

    public boolean mustAccept = false;
    public boolean hasScripts = false;
    public ResourcePack[] behaviourPackEntries = new ResourcePack[0];
    public ResourcePack[] resourcePackEntries = new ResourcePack[0];

    @Override
    public byte pid() {
        return ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;
    }

    @Override
    public void decode() {
        this.mustAccept = this.getBoolean();
        this.hasScripts = this.getBoolean();
        for (int i = 0, count = this.getLShort(); i < count; i++) {
            this.getString();
            this.getString();
            this.getLLong();
            this.getString();
            this.getString();
            this.getString();
            this.getBoolean();
            //TODO
        }
        for (int i = 0, count = this.getLShort(); i < count; i++) {
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
        this.putLShort(this.behaviorPackEntries.length);
        for (ResourcePack resourcePack : this.behaviourPackEntries) {
            this.putString(resourcePack.getPackId().toString());
            this.putString(resourcePack.getPackVersion());
            this.putLLong(resourcePack.getPackSize());
            this.putString(""); //TODO: encryption key
            this.putString(""); //TODO: subpack name
            this.putString(""); //TODO: content identity
            this.putBoolean(false); //TODO: has scripts (?)
        }
        this.putLShort(this.resourcePackEntries.length);
        for (ResourcePack resourcePack : this.resourcePackEntries) {
            this.putString(resourcePack.getPackId().toString());
            this.putString(resourcePack.getPackVersion());
            this.putLLong(resourcePack.getPackSize());
            this.putString(""); //TODO: encryption key
            this.putString(""); //TODO: subpack name
            this.putString(""); //TODO: content identity
            this.putBoolean(false); //TODO: seems useless for resource packs
            this.putBoolean(false); //TODO: supports RTX
        }
    }
}
