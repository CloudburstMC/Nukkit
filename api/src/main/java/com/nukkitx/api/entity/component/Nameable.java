package com.nukkitx.api.entity.component;

import javax.annotation.Nullable;

public interface Nameable extends EntityComponent {

    boolean isAlwaysShowing();

    void setAlwaysShowing(boolean alwaysShowing);

    void setName(@Nullable String name);
}
