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

package cn.nukkit.api;

import cn.nukkit.api.command.sender.CommandSender;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.permission.Permissible;
import cn.nukkit.api.util.Skin;
import com.flowpowered.math.vector.Vector3i;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public interface Player extends Session, CommandSender, Entity, Permissible {

    @Nonnull
    Optional<String> getDisplayName();

    void setDisplayName(@Nullable String name);

    @Nonnull
    Optional<InetSocketAddress> getRemoteAddress();

    @Nonnull
    Optional<UUID> getOfflineUuid();

    boolean isSneaking();

    void setSneaking(boolean sneaking);

    boolean isSprinting();

    void setSprinting(boolean sprinting);

    int getExperienceLevel();

    void disconnect();

    void disconnect(String reason);

    void chat(String message);

    void executeCommand(String command);

    void hideEntity(Entity entity);

    void showEntity(Entity entity);

    boolean canSee(Entity entity);

    void sendTitle(String title, String subtitle);

    void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);

    void resetTitle();

    /*int showFormWindow(FormWindow formWindow);

    int showFormWindow(FormWindow formWindow, int forceId);

    int addServerSettings(FormWindow formWindow);*/

    Locale getLocale();

    void setLocale(Locale locale);

    boolean transfer(@Nonnull InetSocketAddress address);

    boolean transfer(@Nonnull String address, @Nonnegative int port);

    boolean isBreakingBlock();

    void showXboxProfile(String xuid);

    void showXboxProfile(Player player);

    boolean getRemoveFormat();

    boolean setRemoveFormat(boolean value);

    boolean hasCommandsEnabled();

    void setCommandsEnabled(boolean value);

    Skin getSkin();

    void setSkin(Skin skin);

    void setButtonText(String text);

    String getButtonText();

    int getPing();

    void sleepOn(Vector3i position);

    boolean isSleeping();

    int getSleepTicks();

    void stopSleep();

    Entity getEntityPlayerLookingAt();

    void setViewDistance(int distance);

    int getViewDistance();

    void sendActionBar(String message);

    void sendActionBar(String message, int fadein, int duration, int fadeout);

    boolean dropItem(ItemInstance item);

    boolean isSpawned();

    @Override
    default boolean isOnline() {
        return true;
    }

    enum Animation {
        SWING_ARM,
        WAKE_UP,
        CRITICAL_HIT,
        MAGIC_CRITICAL_HIT,
        ROW_RIGHT,
        ROW_LEFT
    }
}
