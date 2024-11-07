package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

public class ItemFishingRod extends ItemTool {

    public ItemFishingRod() {
        this(0, 1);
    }

    public ItemFishingRod(Integer meta) {
        this(meta, 1);
    }

    public ItemFishingRod(Integer meta, int count) {
        super(FISHING_ROD, meta, count, "Fishing Rod");
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.fishing != null) {
            if (player.fishing.getTarget() > 0) {
                this.meta = this.meta + 2;
            } else {
                this.meta++;
            }

            player.stopFishing(true);
		} else {
			player.startFishing(this);
        }

        return true;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_FISHING_ROD;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        return true;
    }
}
