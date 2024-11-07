package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.ExperimentData;
import cn.nukkit.resourcepacks.ResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;

import java.util.List;

@ToString
public class ResourcePackStackPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_STACK_PACKET;

    public boolean mustAccept;
    public String gameVersion = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
    public ResourcePack[] behaviourPackStack = ResourcePack.EMPTY_ARRAY;
    public ResourcePack[] resourcePackStack = ResourcePack.EMPTY_ARRAY;
    public final List<ExperimentData> experiments = new ObjectArrayList<>(1);

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putUnsignedVarInt(this.behaviourPackStack.length);
        for (ResourcePack entry : this.behaviourPackStack) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putString("");
        }
        this.putUnsignedVarInt(this.resourcePackStack.length);
        for (ResourcePack entry : this.resourcePackStack) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putString("");
        }
        this.putString(this.gameVersion);
        this.putExperiments(this.experiments);
        this.putBoolean(false); // Has editor packs
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
