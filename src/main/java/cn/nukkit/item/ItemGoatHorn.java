package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;

import java.util.concurrent.atomic.AtomicBoolean;

public class ItemGoatHorn extends Item {

    private final AtomicBoolean cooldown = new AtomicBoolean(false);

    public ItemGoatHorn() {
        this(0, 1);
    }

    public ItemGoatHorn(Integer meta) {
        this(meta, 1);
    }

    public ItemGoatHorn(Integer meta, int count) {
        super(GOAT_HORN, meta, count, "Goat Horn");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (!cooldown.getAndSet(true)) {
            this.playSound(player);
            Server.getInstance().getScheduler().scheduleDelayedTask(null, () -> cooldown.set(false), 140);
            player.startItemCooldown(140, "goat_horn");
            return true;
        } else {
            return false;
        }
    }

    public void playSound(Player player) {
        switch (this.getDamage()) {
            case 0: player.getLevel().addSound(player, Sound.HORN_CALL_0);
                break;
            case 1: player.getLevel().addSound(player, Sound.HORN_CALL_1);
                break;
            case 2: player.getLevel().addSound(player, Sound.HORN_CALL_2);
                break;
            case 3: player.getLevel().addSound(player, Sound.HORN_CALL_3);
                break;
            case 4: player.getLevel().addSound(player, Sound.HORN_CALL_4);
                break;
            case 5: player.getLevel().addSound(player, Sound.HORN_CALL_5);
                break;
            case 6: player.getLevel().addSound(player, Sound.HORN_CALL_6);
                break;
            case 7: player.getLevel().addSound(player, Sound.HORN_CALL_7);
                break;
        }
    }
}
