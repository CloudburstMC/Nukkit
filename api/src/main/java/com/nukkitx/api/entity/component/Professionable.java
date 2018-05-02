package com.nukkitx.api.entity.component;

import com.nukkitx.api.util.data.Profession;

import javax.annotation.Nonnull;

public interface Professionable extends EntityComponent {

    Profession getProfession();

    void setProfession(@Nonnull Profession profession);
}
