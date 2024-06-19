package cn.nukkit.entity.custom;

public interface CustomEntity {

    EntityDefinition getEntityDefinition();

    default String getIdentifier() {
        return this.getEntityDefinition().getIdentifier();
    }

    default int getNetworkId() {
        return this.getEntityDefinition().getRuntimeId();
    }
}
