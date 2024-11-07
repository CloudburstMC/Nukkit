package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockFungus extends BlockFlowable {

    protected BlockFungus() {
        super(0);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isValidSupport(this.down())) {
            return false;
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL && !isValidSupport(down())) {
            this.level.useBreakOn(this);
            return type;
        }
        return 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.isNull()) {
            return false;
        }

        if (!(item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL)) {
            return false;
        }

        this.level.addParticle(new BoneMealParticle(this));
        if (player != null && !player.isCreative()) {
            item.count--;
        }

        Block down = this.down();
        if (!this.isValidSupport(down)) {
            this.level.useBreakOn(this);
            return true;
        }

        if (!this.canGrowOn(down) || ThreadLocalRandom.current().nextFloat() >= 0.4) {
            return true;
        }

        this.grow(player);
        return true;
    }

    protected abstract boolean canGrowOn(Block support);

    protected boolean isValidSupport(Block support) {
        switch (support.getId()) {
            case GRASS:
            case DIRT:
            case PODZOL:
            case FARMLAND:
            case CRIMSON_NYLIUM:
            case WARPED_NYLIUM:
            case SOUL_SOIL:
            case MYCELIUM:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public abstract boolean grow(Player cause);
}
