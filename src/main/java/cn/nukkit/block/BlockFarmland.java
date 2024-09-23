package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFarmland extends BlockTransparentMeta {

    public BlockFarmland() {
        this(0);
    }

    public BlockFarmland(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Farmland";
    }

    @Override
    public int getId() {
        return FARMLAND;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9375;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block up = this.up();

            if (up.isSolid()) {
                this.level.setBlock(this, Block.get(BlockID.DIRT), false, true);
                return Level.BLOCK_UPDATE_RANDOM;
            }

            boolean found = false;
            boolean unloadedChunk = false;

            if (this.level.isRaining()) {
                found = true;
            } else {
                for (int x = (int) this.x - 4; x <= this.x + 4; x++) {
                    for (int z = (int) this.z - 4; z <= this.z + 4; z++) {
                        for (int y = (int) this.y; y <= this.y + 1; y++) {
                            if (z == this.z && x == this.x && y == this.y) {
                                continue;
                            }

                            FullChunk chunk = this.level.getChunkIfLoaded(x >> 4, z >> 4);
                            if (chunk == null) {
                                unloadedChunk = true;
                                continue;
                            }

                            int block = this.level.getBlockIdAt(chunk, x, y, z);
                            if (Block.isWater(block) || block == FROSTED_ICE || this.level.isBlockWaterloggedAt(chunk, x, y, z)) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (!found && unloadedChunk) {
                return 0;
            }

            Block block;
            if (found || (block = this.down()) instanceof BlockWater || block instanceof BlockIceFrosted) {
                if (this.getDamage() < 7) {
                    this.setDamage(7);
                    this.level.setBlock(this, this, false, true);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }

            if (this.getDamage() > 0) {
                this.setDamage(this.getDamage() - 1);
                this.level.setBlock(this, this, false, true);
            } else if (!(up instanceof BlockCrops)) {
                this.level.setBlock(this, Block.get(Block.DIRT), false, true);
            }

            return Level.BLOCK_UPDATE_RANDOM;
        } else if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.up().isSolid()) {
                this.level.setBlock(this, Block.get(DIRT), false, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.DIRT));
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
