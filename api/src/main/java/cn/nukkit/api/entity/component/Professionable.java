package cn.nukkit.api.entity.component;

import cn.nukkit.api.util.data.Profession;

import javax.annotation.Nonnull;

public interface Professionable extends EntityComponent {

    Profession getProfession();

    void setProfession(@Nonnull Profession profession);
}
