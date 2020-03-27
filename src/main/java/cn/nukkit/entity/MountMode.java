package cn.nukkit.entity;

import com.nukkitx.protocol.bedrock.data.EntityLink;

public enum MountMode {
    RIDER(EntityLink.Type.RIDER),
    PASSENGER(EntityLink.Type.PASSENGER);

    private final EntityLink.Type type;

    MountMode(EntityLink.Type type) {
        this.type = type;
    }

    public EntityLink.Type getType() {
        return type;
    }
}
