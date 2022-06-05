package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerExperienceChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private int exp;
    private int expLevel;

    public PlayerExperienceChangeEvent(Player player, int exp, int level) {
        this.player = player;
        this.exp = exp;
        this.expLevel = level;
    }

    public int getExperience() {
        return this.exp;
    }

    public void setExperience(int exp) {
        this.exp = exp;
    }

    public int getExperienceLevel() {
        return this.expLevel;
    }

    public void setExperienceLevel(int level) {
        this.expLevel = level;
    }

}
