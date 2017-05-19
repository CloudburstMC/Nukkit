package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * Created by Pub4Game on 03.01.2015.
 */
public class BlockMushroomRed extends BlockFlowable {

    public BlockMushroomRed() {
        this(0);
    }

    public BlockMushroomRed(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Red Mushroom";
    }

    @Override
    public int getId() {
        return RED_MUSHROOM;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        System.out.println("light: " + this.level.getFullLight(this));

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
        if (item.getId() == Item.DYE && item.getDamage() == DyeColor.WHITE.getDyeData()) {
            if (this.level.rand.nextFloat() < 0.4) {
                this.grow();
            }

            this.level.addParticle(new BoneMealParticle(this));
            return true;
        }
        return false;
    }

    public boolean grow() {
        this.level.setBlock(this, new BlockAir(), true, false);

        BigMushroom generator = new BigMushroom(1);

        if (generator.generate(this.level, new NukkitRandom(), this)) {
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
}
