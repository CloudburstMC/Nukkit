package com.nukkitx.api.metadata.blockentity;

import com.nukkitx.api.level.Level;
import com.nukkitx.api.metadata.Metadata;

public interface BlockEntity extends Metadata {

    Level getLevel();
}
