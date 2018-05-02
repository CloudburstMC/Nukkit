package com.nukkitx.server.entity.component;

import com.google.common.base.Preconditions;
import com.nukkitx.api.entity.component.Professionable;
import com.nukkitx.api.util.data.Profession;

import javax.annotation.Nonnull;

public class ProfessionableComponent implements Professionable {
    private Profession profession;

    public ProfessionableComponent() {
        this(Profession.GENERIC);
    }

    public ProfessionableComponent(Profession profession) {
        this.profession = profession;
    }

    @Nonnull
    @Override
    public Profession getProfession() {
        return profession;
    }

    @Override
    public void setProfession(@Nonnull Profession profession) {
        this.profession = Preconditions.checkNotNull(profession);
    }
}
