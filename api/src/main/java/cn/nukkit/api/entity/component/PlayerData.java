package cn.nukkit.api.entity.component;

import cn.nukkit.api.level.AdventureSettings;
import cn.nukkit.api.util.GameMode;

import javax.annotation.Nonnull;

public interface PlayerData extends EntityComponent {

    @Nonnull
    AdventureSettings getAdventureSettings();

    boolean isSneaking();

    GameMode getGameMode();

    void setGamemode(GameMode gamemode);
}
