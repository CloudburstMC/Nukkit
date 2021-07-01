package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import lombok.ToString;

@ToString
public class ResourcePackStackPacket extends DataPacket {

    public boolean mustAccept;
    public ResourcePack[] behaviourPackStack = new ResourcePack[0];
    public ResourcePack[] resourcePackStack = new ResourcePack[0];
    public String gameVersion = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
    public Experiment[] experiments = new Experiment[0];
    public boolean hasPreviouslyUsedExperiments;

    @Override
    public byte pid() {
        return ProtocolInfo.RESOURCE_PACK_STACK_PACKET;
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
            Experiment experiment = new Experiment();
            experiment.experimentName = this.getString();
            experiment.enabled = this.getBoolean();
            this.experiments[i] = experiment;
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
            this.putString(""); //TODO: Subpack name
        }
        this.putUnsignedVarInt(this.resourcePackStack.length);
        for (ResourcePack resourcePack : this.resourcePackStack) {
            this.putString(resourcePack.getPackId().toString());
            this.putString(resourcePack.getPackVersion());
            this.putString(""); //TODO: Subpack name
        }
        this.putString(this.gameVersion);
        this.putLInt(this.experiments.length);
        for (Experiment experiment : this.experiments) {
            this.putString(experiment.experimentName);
            this.putBoolean(experiment.enabled);
        }
        this.putBoolean(hasPreviouslyUsedExperiments);
    }

    public static class Experiment {

        public String experimentName;
        public boolean enabled;
    }
}
