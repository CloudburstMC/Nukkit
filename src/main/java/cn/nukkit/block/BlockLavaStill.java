package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.Random;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLavaStill extends BlockLava {

    public BlockLavaStill() {
        super(0);
    }

    public BlockLavaStill(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STILL_LAVA;
    }

    @Override
    public String getName() {
        return "Still Lava";
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return new BlockLavaStill(meta);
    }

    @Override
    public int onUpdate(int type) {
        int result = super.onUpdate(type);

        if (type == Level.BLOCK_UPDATE_RANDOM) {

            Random random = this.getLevel().rand;

            int i = random.nextInt(3);

            if (i > 0) {
                for (int k = 0; k < i; ++k) {
                    Vector3 v = this.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                    Block block = this.getLevel().getBlock(v);

                    if (block.getId() == AIR) {
                        if (this.isSurroundingBlockFlammable(block)) {
                            BlockFire fire = new BlockFire();
                            this.getLevel().setBlock(v, fire, true);
                            this.getLevel().scheduleUpdate(v, fire.tickRate());
                            return Level.BLOCK_UPDATE_RANDOM;
                        }
                    } else if (block.isSolid()) {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    Vector3 v = this.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                    Block block = this.getLevel().getBlock(v);

                    if (block.getSide(SIDE_UP).getId() == AIR && block.getBurnChance() > 0) {
                        BlockFire fire = new BlockFire();
                        this.getLevel().setBlock(v, fire, true);
                        this.getLevel().scheduleUpdate(v, fire.tickRate());
                    }
                }
            }

        }

        return result;
    }

    protected boolean isSurroundingBlockFlammable(Block block) {
        for (int side = 0; side <= 5; ++side) {
            if (block.getSide(side).getBurnChance() > 0) {
                return true;
            }
        }

        return false;
    }

}
