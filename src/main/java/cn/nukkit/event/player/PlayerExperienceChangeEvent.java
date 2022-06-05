package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerExperienceChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int oldExp;
    private final int oldExpLevel;
    private int newExp;
    private int newExpLevel;

    public PlayerExperienceChangeEvent(Player player, int oldExp, int oldLevel, int newExp, int newLevel) {
        this.player = player;
        this.oldExp = oldExp;
        this.oldExpLevel = oldLevel;
        this.newExp = newExp;
        this.newExpLevel = newLevel;
    }

    public int getOldExperience() {
        return this.oldExp;
    }

    public int getOldExperienceLevel() {
        return this.oldExpLevel;
    }

    public int getNewExperience() {
        return this.newExp;
    }

    public void setNewExperience(int exp) {
        this.newExp = exp;
    }

    public int getNewExperienceLevel() {
        return this.newExpLevel;
    }

    public void setNewExperienceLevel(int level) {
        this.newExpLevel = level;
    }

}
