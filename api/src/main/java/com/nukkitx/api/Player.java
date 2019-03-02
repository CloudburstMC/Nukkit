package com.nukkitx.api;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.command.sender.CommandSender;
import com.nukkitx.api.container.Container;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.inventory.Inventory;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.permission.Permissible;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.api.util.Skin;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Optional;

public interface Player extends Session, CommandSender, Entity, Permissible {

    @Nonnull
    Optional<String> getDisplayName();

    void setDisplayName(@Nullable String name);

    @Nonnull
    Optional<InetSocketAddress> getRemoteAddress();

    int getExperienceLevel();

    GameMode getGameMode();

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

    void sleepOn(Vector3i position);

    boolean isSleeping();

    int getSleepTicks();

    void stopSleep();

    void setViewDistance(int distance);

    int getViewDistance();

    void sendActionBar(String message);

    void sendActionBar(String message, int fadein, int duration, int fadeout);

    boolean drop(@Nonnull ItemStack item);

    boolean isSpawned();

    Inventory getInventory();

    Optional<Container> getOpenContainer();

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
