package cn.nukkit.api.entity.component;

import cn.nukkit.api.entity.Entity;

public interface Breedable extends EntityComponent {

    boolean canMate(Entity entity);

    int getLoveTicks();

    // getLoveCause();
}
