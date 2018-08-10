package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author CreeperFace
 */
@AllArgsConstructor
@Getter
public abstract class Slab implements Metadata {

    final boolean upper;
    final boolean doubleSlab;
}
