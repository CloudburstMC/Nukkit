package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Identifier;

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
                getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (canStay()) {
            getLevel().setBlock(block, this, true, true);
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
        if (item.getId() == DYE && item.getDamage() == DyeColor.WHITE.getDyeData()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.decrementCount();
            }

            if (ThreadLocalRandom.current().nextFloat() < 0.4) {
                this.grow();
            }

            this.level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    public boolean grow() {
        this.level.setBlock(this, Block.get(AIR), true, false);

        BigMushroom generator = new BigMushroom(getType());

        if (generator.generate(this.level, new BedrockRandom(), this.asVector3i())) {
            return true;
        } else {
            this.level.setBlock(this, this, true, false);
            return false;
        }
    }

    public boolean canStay() {
        Block block = this.down();
        return block.getId() == MYCELIUM || block.getId() == PODZOL || (!block.isTransparent() && this.level.getFullLight(this) < 13);
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
