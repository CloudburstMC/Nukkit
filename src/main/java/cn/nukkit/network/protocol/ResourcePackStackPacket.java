package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import lombok.ToString;

@ToString
public class ResourcePackStackPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_STACK_PACKET;

    public boolean mustAccept = false;
    public ResourcePack[] behaviourPackStack = new ResourcePack[0];
    public ResourcePack[] resourcePackStack = new ResourcePack[0];
    public boolean isExperimental = false;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);

        this.putUnsignedVarInt(this.behaviourPackStack.length);
        for (ResourcePack entry : this.behaviourPackStack) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putString(""); //TODO: subpack name
        }

        this.putUnsignedVarInt(this.resourcePackStack.length);
        for (ResourcePack entry : this.resourcePackStack) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putString(""); //TODO: subpack name
        }

        this.putBoolean(isExperimental);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
