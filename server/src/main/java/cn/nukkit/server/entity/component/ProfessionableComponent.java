package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Professionable;
import cn.nukkit.api.util.data.Profession;
import com.google.common.base.Preconditions;

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
