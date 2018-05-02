package com.nukkitx.api.entity.component;

import javax.annotation.Nonnegative;

public interface ContainedExperience extends EntityComponent {

    @Nonnegative
    int getExperience();
}
