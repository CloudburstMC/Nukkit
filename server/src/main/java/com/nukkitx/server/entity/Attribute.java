package com.nukkitx.server.entity;

import lombok.NonNull;
import lombok.Value;

@Value
public final class Attribute {
    @NonNull
    private final String name;
    private final float value;
    private final float minimumValue;
    private final float maximumValue;
    private final float defaultValue;

    public Attribute(@NonNull String name, float value, float minimumValue, float maximumValue) {
        this.name = name;
        this.value = value;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.defaultValue = maximumValue;
    }

    public Attribute(@NonNull String name, float value, float minimumValue, float maximumValue, float defaultValue) {
        this.name = name;
        this.value = value;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.defaultValue = defaultValue;
    }
}