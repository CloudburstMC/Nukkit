package com.nukkitx.api;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.command.sender.CommandSender;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.permission.Permissible;
import com.nukkitx.api.util.Skin;

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
