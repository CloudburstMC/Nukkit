package com.nukkitx.server.math;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathHelper {

    public short clamp(short value, short min, short max) {
        return value < min ? min : value > max ? max : value;
    }
}