package cn.nukkit.api.entity.component;

import java.util.OptionalInt;

public interface Shearable extends EntityComponent {

    boolean isSheared();

    void setSheared(boolean sheared);

    OptionalInt ticksSinceSheared();
}
