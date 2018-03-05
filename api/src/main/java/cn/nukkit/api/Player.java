package cn.nukkit.api;

import cn.nukkit.api.command.sender.CommandSender;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.event.player.PlayerKickEvent;
import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.message.Message;
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
    Optional<String> getPlayerListName();

    void setPlayerListName(@Nullable String name);

    @Nonnull
    GameMode getGameMode();

    void setGameMode(@Nullable GameMode gameMode);

    @Nonnull
    Optional<InetSocketAddress> getRemoteAddress();

    boolean isSneaking();

    void setSneaking(boolean value);

    boolean isSprinting();

    void setSprinting(boolean value);

    int getExperienceLevel();

    void addWindow(Inventory inventory);

    void addWindow(Inventory inventory, int forceId);

    int getWindowId(Inventory inventory);

    Inventory getWindowById();

    void removeWindow(Inventory inventory);

    void removeAllWindows();

    void sendAllInventories();

    String getAddress();

    int getPort();

    void kick();

    void kick(String reason);

    void kick(PlayerKickEvent.Reason reason);

    void chat(String message);

    void saveData();

    void saveData(boolean async);

    void loadData();

    void addExp(int exp);

    int getExp();

    void addExpLevel(int level);

    int getExpLevel();

    boolean getAllowFlight();

    void setAllowFlight(boolean value);

    void hidePlayer(Player player);

    void showPlayer(Player player);

    boolean canSee(Player player);

    boolean isFlying();

    void setFlying(boolean value);

    void setMovementSpeed();

    int getMovementSpeed();

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

    boolean getRemoveFormat();

    boolean setRemoveFormat(boolean value);

    boolean isEnabledClientCommand();

    void setEnabledClientCommand(boolean value);

    boolean isConnected();

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

    void sendMessage(String message);

    void sendMessage(Message message);

    void sendTranslation(String message, String... parameters);

    void sendPopup(String popup);

    void sendTip(String tip);

    void sendActionBar(String message);

    void sendActionBar(String message, int fadein, int duration, int fadeout);

    boolean dropItem(ItemInstance item);

    enum Animation {
        SWING_ARM,
        WAKE_UP,
        CRITICAL_HIT,
        MAGIC_CRITICAL_HIT,
        ROW_RIGHT,
        ROW_LEFT
    }
}
