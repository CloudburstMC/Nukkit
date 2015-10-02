package cn.nukkit;

import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Human;
import cn.nukkit.event.TextContainer;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.ChunkLoader;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.utils.TextFormat;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Player extends Human implements CommandSender, InventoryHolder, ChunkLoader, IPlayer {

    public static final int SURVIVAL = 0;
    public static final int CREATIVE = 1;
    public static final int ADVENTURE = 2;
    public static final int SPECTATOR = 3;
    public static final int VIEW = SPECTATOR;

    public static final int SURVIVAL_SLOTS = 36;
    public static final int CREATIVE_SLOTS = 112;

    protected SourceInterface interfaz;

    public boolean spawned = false;
    public boolean loggedIn = false;
    public byte gamemode;
    public long lastBreak;

    protected String ip;

    protected String username;
    protected String iusername;
    protected String displayName;

    public TranslationContainer getLeaveMessage() {
        return new TranslationContainer(TextFormat.YELLOW + "%multiplayer.player.left", this.getDisplayName());
    }

    public String getName() {
        return this.username;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Player getPlayer() {
        return this;
    }

    public boolean isOnline() {
        //todo !!!
        return false;
    }

    public String getAddress() {
        return this.ip;
    }

    public boolean isSurvival() {
        return (this.gamemode & 0x01) == 0;
    }

    public boolean isCreative() {
        return (this.gamemode & 0x01) > 0;
    }

    public boolean isSpectator() {
        return this.gamemode == 3;
    }

    public boolean isAdventure() {
        return (this.gamemode & 0x02) > 0;
    }

    public void handleDataPacket(DataPacket packet) {
        //todo
    }

    @Override
    public void close() {
        this.close("");
    }

    public void close(String message) {
        this.close(message, "generic");
    }

    public void close(String message, String reason) {
        this.close(message, reason, true);
    }

    public void close(String message, String reason, boolean notify) {

    }

    public void close(TextContainer message) {
        this.close(message, "generic");
    }

    public void close(TextContainer message, String reason) {
        this.close(message, reason, true);
    }

    public void close(TextContainer message, String reason, boolean notify) {

    }


}
