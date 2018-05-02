package com.nukkitx.server.level.provider;

import com.nukkitx.api.Configuration;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.level.NukkitLevelData;

import java.nio.file.Path;

public class DefaultsLevelDataProvider extends NukkitLevelData implements LevelDataProvider {

    public DefaultsLevelDataProvider(NukkitServer server) {
        Configuration config = server.getConfiguration();
        this.setAchievementsDisabled(!config.getGeneral().isAchievementsEnabled());
        this.setGameMode(server.getConfiguration().getMechanics().getDefaultGamemode());
        this.setDifficulty(config.getMechanics().getDifficulty());
        this.setForceGameType(config.getMechanics().isGamemodeForced());
        this.setTexturepacksRequired(config.getGeneral().isForcingResourcePacks());
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(Path path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path getPath() {
        return null;
    }

    public void saveAsDefault() {
        NukkitLevelData.defaults = this;
    }
}
