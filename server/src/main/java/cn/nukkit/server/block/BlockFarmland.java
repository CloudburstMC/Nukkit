package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockFarmland extends BlockTransparent {

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
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + 0.9375,
                this.z + 1
        );
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Vector3 v = new Vector3();

            if (this.level.getBlock(v.setComponents(x, this.y + 1, z)) instanceof BlockCrops) {
                return 0;
            }

            if (this.level.getBlock(v.setComponents(x, this.y + 1, z)).isSolid()) {
                this.level.setBlock(this, new BlockDirt(), true, true);

                return Level.BLOCK_UPDATE_RANDOM;
            }

            boolean found = false;

            if (this.level.isRaining()) {
                found = true;
            } else {
                for (int x = (int) this.x - 4; x <= this.x + 4; x++) {
                    for (int z = (int) this.z - 4; z <= this.z + 4; z++) {
                        for (int y = (int) this.y; y <= this.y + 1; y++) {
                            if (z == this.z && x == this.x && y == this.y) {
                                continue;
                            }

                            v.setComponents(x, y, z);
                            int block = this.level.getBlockIdAt(v.getFloorX(), v.getFloorY(), v.getFloorZ());

                            if (block == WATER || block == STILL_WATER) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }

            Block block = this.level.getBlock(v.setComponents(x, y - 1, z));
            if (found || block instanceof BlockWater) {
                if (this.meta < 7) {
                    this.meta = 7;
                    this.level.setBlock(this, this, true, false);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }

            if (this.meta > 0) {
                this.meta--;
                this.level.setBlock(this, this, true, false);
            } else {
                this.level.setBlock(this, new BlockDirt(), true, true);
            }

            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemBlock(new BlockDirt())
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
