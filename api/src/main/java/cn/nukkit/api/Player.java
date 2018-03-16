package cn.nukkit.api;

import cn.nukkit.api.command.sender.CommandSender;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.permission.Permissible;
import cn.nukkit.api.util.GameMode;
import com.flowpowered.math.vector.Vector3d;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Optional;

public interface Player extends Session, CommandSender, Entity, MessageRecipient, Permissible {

    @Nonnull
    Optional<String> getDisplayName();

    void setDisplayName(@Nullable String name);

    @Nonnull
    GameMode getGameMode();

    void setGameMode(@Nullable GameMode gameMode);

    @Nonnull
    Optional<InetSocketAddress> getRemoteAddress();

    boolean isSneaking();

    void setSneaking(boolean sneaking);

    boolean isSprinting();

    void setSprinting(boolean sprinting);

    int getExperienceLevel();

    void disconnect();

    void disconnect(String reason);

    void chat(String message);

    boolean getAllowFlight();

    void setAllowFlight(boolean allowFlight);

    void hideEntity(Entity entity);

    void showEntity(Entity entity);

    boolean canSee(Entity entity);

    boolean isFlying();

    void setFlying(boolean value);

    void sendTitle(String title, String subtitle);

    void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);

    void resetTitle();

    /*int showFormWindow(FormWindow formWindow);

    int showFormWindow(FormWindow formWindow, int forceId);

    int addServerSettings(FormWindow formWindow);*/

    void setCheckMovement(boolean value);

    Locale getLocale();

    void setLocale(Locale locale);

    boolean transfer(@Nonnull InetSocketAddress address);

    boolean transfer(@Nonnull String address, @Nonnegative int port);

    boolean isBreakingBlock();

    void showXboxProfile(String xuid);

    void showXboxProfile(Player player);

    boolean getRemoveFormat();

    boolean setRemoveFormat(boolean value);

    boolean isEnabledClientCommand();

    void setEnabledClientCommand(boolean value);

    Skin getSkin();

    void setSkin(Skin skin);

    void setButtonText(String text);

    String getButtonText();

    int getPing();

    void sleepOn(Vector3d pos);

    boolean isSleeping();

    int getSleepTicks();

    void stopSleep();

    Entity getEntityPlayerLookingAt();

    void setViewDistance(int distance);

    int getViewDistance();

    void sendActionBar(String message);

    void sendActionBar(String message, int fadein, int duration, int fadeout);

    boolean dropItem(ItemInstance item);

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
