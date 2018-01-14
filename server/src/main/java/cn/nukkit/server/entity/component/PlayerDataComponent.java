package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.PlayerData;
import cn.nukkit.api.level.AdventureSettings;

import javax.annotation.Nonnull;

public class PlayerDataComponent implements PlayerData {
    private volatile AdventureSettings adventureSettings;

    private volatile boolean sneaking;

    @Nonnull
    @Override
    public AdventureSettings getAdventureSettings() {
        return adventureSettings;
    }

    @Override
    public boolean isSneaking() {
        return sneaking;
    }
}
