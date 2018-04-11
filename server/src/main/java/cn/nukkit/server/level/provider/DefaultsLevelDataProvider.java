/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

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
