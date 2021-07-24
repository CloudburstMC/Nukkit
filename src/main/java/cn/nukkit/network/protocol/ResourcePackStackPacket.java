package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import lombok.ToString;

@ToString
public class ResourcePackStackPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_STACK_PACKET;

    public boolean mustAccept;
    public ResourcePack[] behaviourPackStack = new ResourcePack[0];
    public ResourcePack[] resourcePackStack = new ResourcePack[0];
    public String gameVersion = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
    public Experiment[] experiments = new Experiment[0];
    public boolean hasPreviouslyUsedExperiments;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.mustAccept = this.getBoolean();
        int count = (int) this.getUnsignedVarInt();
        this.behaviourPackStack = new ResourcePack[count];
        for (int i = 0; i < count; i++) {
            this.getString();
            this.getString();
            this.getString();
            //TODO
        }
        count = (int) this.getUnsignedVarInt();
        this.resourcePackStack = new ResourcePack[count];
        for (int i = 0; i < count; i++) {
            this.getString();
            this.getString();
            this.getString();
            //TODO
        }
        this.gameVersion = this.getString();
        count = this.getLInt();
        this.experiments = new Experiment[count];
        for (int i = 0; i < count; i++) {
            this.experiments[i] = new Experiment(this.getString(), this.getBoolean());
        }
        this.hasPreviouslyUsedExperiments = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putUnsignedVarInt(this.behaviourPackStack.length);
        for (ResourcePack resourcePack : this.behaviourPackStack) {
            this.putString(resourcePack.getPackId().toString());
            this.putString(resourcePack.getPackVersion());
            this.putString(""); //TODO: subpack name
        }
        this.putUnsignedVarInt(this.resourcePackStack.length);
        for (ResourcePack resourcePack : this.resourcePackStack) {
            this.putString(resourcePack.getPackId().toString());
            this.putString(resourcePack.getPackVersion());
            this.putString(""); //TODO: subpack name
        }
        this.putString(this.gameVersion);
        this.putLInt(this.experiments.length);
        for (Experiment experiment : this.experiments) {
            this.putString(experiment.name);
            this.putBoolean(experiment.enabled);
        }
        this.putBoolean(hasPreviouslyUsedExperiments);
    }

    public static class Experiment {

        public final String name;
        public final boolean enabled;

        public Experiment(String name, boolean enabled) {
            this.name = name;
            this.enabled = enabled;
        }
    }
}
