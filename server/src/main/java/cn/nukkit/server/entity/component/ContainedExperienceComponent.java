package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.ContainedExperience;

import javax.annotation.Nonnegative;

public class ContainedExperienceComponent implements ContainedExperience {
    private final int experience;

    public ContainedExperienceComponent(@Nonnegative int experience) {
        this.experience = experience;
    }

    @Override
    public int getExperience() {
        return experience;
    }
}
