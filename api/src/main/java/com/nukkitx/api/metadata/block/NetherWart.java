package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.Value;

/**
 * @author CreeperFace
 */
@Value
public class NetherWart implements Metadata {

    final byte age;

    public static NetherWart of(int age) {
        return new NetherWart((byte) age);
    }

    public boolean isFullyGrown() {
        return this.age >= 3;
    }
}
