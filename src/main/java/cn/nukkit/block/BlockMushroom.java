package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.feature.WorldFeature;
import cn.nukkit.level.feature.tree.TreeSpecies;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import net.daporkchop.lib.random.impl.ThreadLocalPRandom;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.DYE;

public abstract class BlockMushroom extends FloodableBlock {

    public BlockMushroom(Identifier id) {
        super(id);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canStay()) {
                getLevel().useBreakOn(this.getPosition());

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (canStay()) {
            getLevel().setBlock(block.getPosition(), this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == DYE && item.getMeta() == DyeColor.WHITE.getDyeData()) {
            if (player != null && (player.getGamemode() & 0x01) == 0) {
                item.decrementCount();
            }

            if (ThreadLocalRandom.current().nextFloat() < 0.4) {
                this.grow();
            }

            this.level.addParticle(new BoneMealParticle(this.getPosition()));
            return true;
        }
        return false;
    }

    public boolean grow() {
        this.level.setBlock(this.getPosition(), Block.get(AIR), true, false);

        WorldFeature feature = TreeSpecies.fromItem(this.getId(), this.getMeta()).getDefaultGenerator();

        if (feature.place(this.level, ThreadLocalPRandom.current(), this.getX(), this.getY(), this.getZ())) {
            return true;
        } else {
            this.level.setBlock(this.getPosition(), this, true, false);
            return false;
        }
    }

    public boolean canStay() {
        Block block = this.down();
        return block.getId() == MYCELIUM || block.getId() == PODZOL ||
                (!block.isTransparent() && this.level.getFullLight(this.getPosition()) < 13);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    protected abstract int getType();
}
