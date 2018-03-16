package cn.nukkit.api.entity.component;

import cn.nukkit.api.permission.Abilities;
import cn.nukkit.api.permission.CommandPermission;
import cn.nukkit.api.permission.PlayerPermission;
import cn.nukkit.api.util.GameMode;
import cn.nukkit.api.util.Skin;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public interface PlayerData extends EntityComponent {

    @Nonnull
    Abilities getAbilities();

    GameMode getGameMode();

    void setGamemode(GameMode gamemode);

    Skin getSkin();

    void setSkin(Skin skin);

    float getSpeed();

    void setSpeed(@Nonnegative float speed);

    int getHunger();

    void setHunger(@Nonnegative int hunger);

    float getSaturation();

    void setSaturation(@Nonnegative float saturation);

    float getExhaustion();

    void setExhaustion(@Nonnegative float exhaustion);

    int getExperience();

    void setExperience(@Nonnegative int experience);

    @Nonnull
    PlayerPermission getPlayerPermission();

    void setPlayerPermission(PlayerPermission playerPermission);

    @Nonnull
    CommandPermission getCommandPermission();

    void setCommandPermission(CommandPermission permission);
}
