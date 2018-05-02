package com.nukkitx.api.entity.component;

import com.nukkitx.api.entity.Entity;

public interface Breedable extends EntityComponent {

    boolean canMate(Entity entity);

    int getLoveTicks();

    // getLoveCause();
}
