package com.nukkitx.api.entity.component;

import com.nukkitx.api.permission.Abilities;
import com.nukkitx.api.permission.CommandPermission;
import com.nukkitx.api.permission.PlayerPermission;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.api.util.Skin;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public interface PlayerData extends EntityComponent {

    @Nonnull
    Abilities getAbilities();

    GameMode getGameMode();

    float getEffectiveSpeed();

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

    boolean isSprinting();

    void setSprinting(boolean sprinting);

    @Nonnull
    PlayerPermission getPlayerPermission();

    void setPlayerPermission(PlayerPermission playerPermission);

    @Nonnull
    CommandPermission getCommandPermission();

    void setCommandPermission(CommandPermission permission);
}
