package cn.nukkit.api.entity.component;

import cn.nukkit.api.GameMode;
import cn.nukkit.api.level.AdventureSettings;

import javax.annotation.Nonnull;

public interface PlayerData extends EntityComponent {

    @Nonnull
    AdventureSettings getAdventureSettings();

    boolean isSneaking();

    GameMode getGameMode();

    void setGamemode(GameMode gamemode);
}
