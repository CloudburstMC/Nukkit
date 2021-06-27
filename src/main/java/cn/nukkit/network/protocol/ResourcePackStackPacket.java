package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class ResourcePackStackPacket extends DataPacket {

    public boolean mustAccept = false;
    public ResourcePack[] behaviourPackStack = new ResourcePack[0];
    public ResourcePack[] resourcePackStack = new ResourcePack[0];
    public String gameVersion = ProtocolInfo.MINECRAFT_VERSION_NETWORK;
    public List<Experiment> experiments = new ArrayList<>();
    public boolean hasPreviouslyUsedExperiments;

    @Override
    public byte pid() {
        return ProtocolInfo.RESOURCE_PACK_STACK_PACKET;
    }

    @Override
    public void decode() {
        this.mustAccept = this.getBoolean();
        for (int i = 0, count = (int) this.getUnsignedVarInt(); i < count; i++) {
            this.getString();
            this.getString();
            this.getString();
            //TODO
        }
        for (int i = 0, count = (int) this.getUnsignedVarInt(); i < count; i++) {
            this.getString();
            this.getString();
            this.getString();
            //TODO
        }

        this.gameVersion = this.getString();
        for (int i = 0, count = this.getLInt(); i < count; i++) {
            Experiment experiment = new Experiment();
            experiment.experimentName = this.getString();
            experiment.enabled = this.getBoolean();
            this.experiments.add(experiment);
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
        this.putLInt(this.experiments.size());
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
