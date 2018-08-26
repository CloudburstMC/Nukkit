package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.*;

/**
 * @author CreeperFace
 */

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@ToString
public class Growable implements Metadata {

    @Getter
    final byte age;

    public static Growable of(int age) {
        return new Growable((byte) age);
    }

    public boolean isFullyGrown() {
        return age >= 7;
    }
}
