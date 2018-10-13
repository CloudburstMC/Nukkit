package com.nukkitx.server.entity;

import lombok.Value;

@Value
public class EntityLink {
    private final long fromUniqueEntityId;
    private final long toUniqueEntityId;
    private final byte type;
    private final boolean unknown;
}
