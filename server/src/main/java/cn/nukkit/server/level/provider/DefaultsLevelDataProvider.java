package cn.nukkit.server.level.provider;

import cn.nukkit.api.Configuration;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.level.NukkitLevelData;

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
