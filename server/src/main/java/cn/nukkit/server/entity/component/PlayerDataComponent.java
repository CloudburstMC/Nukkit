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

package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.PlayerData;
import cn.nukkit.api.permission.CommandPermission;
import cn.nukkit.api.permission.PlayerPermission;
import cn.nukkit.api.util.GameMode;
import cn.nukkit.api.util.Skin;
import cn.nukkit.server.entity.Attribute;
import cn.nukkit.server.network.minecraft.session.PlayerSession;
import cn.nukkit.server.network.minecraft.session.data.ClientData;
import cn.nukkit.server.permission.NukkitAbilities;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PlayerDataComponent implements PlayerData {
    private final PlayerSession session;

    private volatile boolean gamemodeTouched = false;

    private final NukkitAbilities abilities;
    private volatile PlayerPermission playerPermission = PlayerPermission.MEMBER;
    private volatile CommandPermission commandPermission = CommandPermission.NORMAL;
    private volatile Skin skin;
    private volatile GameMode gameMode = GameMode.SURVIVAL;
    private volatile boolean sprinting = false;
    private volatile float speed = 0.1f;
    private volatile int hunger = 20;
    private volatile float saturation = 20f;
    private volatile float exhaustion = 0f;
    private volatile int experience;

    public PlayerDataComponent(PlayerSession session) {
        this.session = session;
        abilities = new NukkitAbilities(session.getServer().getDefaultAbilities());
        ClientData data = session.getMinecraftSession().getClientData();
        skin = new Skin(data.getSkinId(), data.getSkinData(), data.getCapeData(), data.getSkinGeometryName(), data.getSkinGeometry());
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    @Nonnull
    @Override
    public NukkitAbilities getAbilities() {
        return abilities;
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public float getEffectiveSpeed() {
        return sprinting ? (float) Math.min(0.5f, speed * 1.3) : speed;
    }

    @Override
    public void setGamemode(GameMode gamemode) {
        if (this.gameMode != gameMode) {
            this.gameMode = gamemode;
        }
    }

    @Override
    public Skin getSkin() {
        return skin;
    }

    @Override
    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(@Nonnegative float speed) {
        Preconditions.checkArgument(speed >= 0, "speed cannot be negative");
        if (this.speed != speed) {
            this.speed = speed;
            session.onAttributeUpdate(new Attribute("minecraft:movement", getEffectiveSpeed(), 0.1f, Float.MAX_VALUE, 0.1f));
        }
    }

    @Override
    public int getHunger() {
        return hunger;
    }

    @Override
    public void setHunger(@Nonnegative int hunger) {
        Preconditions.checkArgument(hunger >= 0, "hunger cannot be negative");
        if (this.hunger != hunger) {
            this.hunger = hunger;
            session.onAttributeUpdate(new Attribute("minecraft:player.hunger", hunger, 0f, 20f, 20f));
        }
    }

    @Override
    public float getSaturation() {
        return saturation;
    }

    @Override
    public void setSaturation(@Nonnegative float saturation) {
        Preconditions.checkArgument(saturation >= 0, "saturation cannot be negative");
        if (this.saturation != saturation) {
            this.saturation = saturation;
            session.onAttributeUpdate(new Attribute("minecraft:player.saturation", saturation, 0f, 20f, 20f));
        }
    }

    @Override
    public float getExhaustion() {
        return exhaustion;
    }

    @Override
    public void setExhaustion(float exhaustion) {
        Preconditions.checkArgument(exhaustion >= 0, "exhaustion cannot be negative");
        if (this.exhaustion != exhaustion) {
            this.exhaustion = exhaustion;
            session.onAttributeUpdate(new Attribute("minecraft:player.exhaustion", exhaustion, 0f, 4f, 4f));
        }
    }

    @Override
    public int getExperience() {
        return experience;
    }

    @Override
    public void setExperience(int experience) {
        Preconditions.checkArgument(experience >= 0, "experience cannot be negative");
        this.experience = experience;
        //TODO
    }

    @Nonnull
    @Override
    public PlayerPermission getPlayerPermission() {
        return playerPermission;
    }

    @Override
    public void setPlayerPermission(PlayerPermission playerPermission) {
        this.playerPermission = playerPermission;
    }

    @Nonnull
    @Override
    public CommandPermission getCommandPermission() {
        return commandPermission;
    }

    @Override
    public void setCommandPermission(CommandPermission permission) {
        this.commandPermission = permission;
    }

    public List<Attribute> getAttributes() {
        List<Attribute> list = new ArrayList<>();
        list.add(new Attribute("minecraft:movement", getEffectiveSpeed(), 0, 0.5f, 0.1f));
        list.add(new Attribute("minecraft:player.hunger", hunger, 0f, 20f, 20f));
        list.add(new Attribute("minecraft:player.saturation", saturation, 0f, 20f, 20f));
        list.add(new Attribute("minecraft:player.exhaustion", exhaustion, 0f, 4f, 4f));
        return list;
    }
}
