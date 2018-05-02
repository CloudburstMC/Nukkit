package com.nukkitx.api.entity.component;

import com.nukkitx.api.entity.Entity;

import javax.annotation.Nullable;
import java.util.Optional;

public interface Ownable extends EntityComponent {

    Optional<Entity> getOwner();

    void setOwner(@Nullable Entity entity);
}
