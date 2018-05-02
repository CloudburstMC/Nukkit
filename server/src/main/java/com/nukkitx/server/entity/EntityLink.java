package com.nukkitx.server.entity;

import lombok.Value;

@Value
public class EntityLink {
    private final long fromUniqueEntityId;
    private final long toUniqueEntityId;
    private final byte type;
    private final boolean unknown;

    @Override
    public String toString() {
        return "PlayerAttribute{" +
                "fromUniqueEntityId=" + fromUniqueEntityId +
                ", toUniqueEntityId=" + toUniqueEntityId +
                ", type=" + type +
                ", unknown=" + unknown +
                '}';
    }
}
