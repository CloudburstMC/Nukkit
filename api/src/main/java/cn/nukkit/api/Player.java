package cn.nukkit.api;

import cn.nukkit.api.command.CommandExecutorSource;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.HumanEntity;
import cn.nukkit.api.event.player.PlayerKickEvent;
import cn.nukkit.api.event.player.PlayerTeleportEvent.TeleportCause;
import cn.nukkit.api.form.window.FormWindow;
import cn.nukkit.api.inventory.Inventory;
import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.message.Message;
import cn.nukkit.api.util.LoginChainData;
import com.flowpowered.math.vector.Vector3d;

import java.net.InetSocketAddress;
import java.util.Locale;

public interface Player extends Session, HumanEntity, CommandExecutorSource {

    String getDisplayName();

    void setDisplayName(String name);

    String getPlayerListName();

    void setPlayerListName(String name);

    GameMode getGameMode();

    void setGameMode(GameMode gameMode);

    boolean isSneaking();

    void setSneaking(boolean value);

    boolean isSprinting();

    void setSprinting(boolean value);

    int getExpToLevel();

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

    int showFormWindow(FormWindow formWindow);

    int showFormWindow(FormWindow formWindow, int forceId);

    int addServerSettings(FormWindow formWindow);

    void setCheckMovement(boolean value);

    Locale getLocale();

    void setLocale(Locale locale);

    void transfer(InetSocketAddress address);

    boolean isBreakingBlock();

    void showXboxProfile(String xuid);

    boolean getRemoveFormat();

    boolean setRemoveFormat(boolean value);

    boolean isEnabledClientCommand();

    void setEnabledClientCommand(boolean value);

    default boolean isPlayer() {
        return true;
    }

    boolean isConnected();

    Skin getSkin();

    void setSkin(Skin skin);

    void setButtonText(String text);

    String getButtonText();

    Location getSpawn();

    void setSpawn(Location location);

    int getPing();

    void sleepOn(Vector3d pos);

    boolean isSleeping();

    int getSleepTicks();

    void stopSleep();

    boolean isSurvival();

    boolean isCreative();

    boolean isAdventure();

    boolean isSpectator();

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

    boolean dropItem(ItemStack item);

    void teleportImmediate(Location location);

    void teleportImmediate(Location location, TeleportCause cause);

    LoginChainData getLoginChainData();
}
