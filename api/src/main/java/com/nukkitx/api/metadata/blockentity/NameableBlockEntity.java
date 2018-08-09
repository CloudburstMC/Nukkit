package com.nukkitx.api.metadata.blockentity;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author CreeperFace
 */
public interface NameableBlockEntity extends BlockEntity {

    Optional<String> getCustomName();

    void setCustomName(@Nullable String name);

    Optional<String> getLock();

    void setLock(@Nullable String name);
}
